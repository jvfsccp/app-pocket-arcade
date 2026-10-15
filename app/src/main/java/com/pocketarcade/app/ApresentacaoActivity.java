package com.pocketarcade.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.pocketarcade.app.databinding.ActivityApresentacaoBinding;

/**
 * Tela 1: apresentação dos integrantes e identificação do jogador por apelido.
 * Também recebe o retorno de "Encerrar sessão" em onNewIntent().
 */
public class ApresentacaoActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela1";
    private static final int TAMANHO_MINIMO_APELIDO = 3;

    private ActivityApresentacaoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityApresentacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        preencherIntegrantes();

        binding.btnEntrar.setOnClickListener(v -> tentarEntrar());
        binding.etApelido.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tentarEntrar();
                return true;
            }
            return false;
        });
    }

    /** Mostra os dados do primeiro integrante e, se houver, do segundo (dupla). */
    private void preencherIntegrantes() {
        binding.tvIntegrante1Nome.setText(getString(R.string.rotulo_nome, getString(R.string.integrante1_nome)));
        binding.tvIntegrante1Matricula.setText(
                getString(R.string.rotulo_matricula, getString(R.string.integrante1_matricula)));
        binding.tvIntegrante1Curso.setText(getString(R.string.rotulo_curso, getString(R.string.integrante1_curso)));

        String nome2 = getString(R.string.integrante2_nome).trim();
        if (!nome2.isEmpty()) {
            binding.blocoIntegrante2.setVisibility(android.view.View.VISIBLE);
            binding.tvIntegrante2Nome.setText(getString(R.string.rotulo_nome, nome2));
            binding.tvIntegrante2Matricula.setText(
                    getString(R.string.rotulo_matricula, getString(R.string.integrante2_matricula)));
            binding.tvIntegrante2Curso.setText(
                    getString(R.string.rotulo_curso, getString(R.string.integrante2_curso)));
        }
    }

    private void tentarEntrar() {
        String apelido = String.valueOf(binding.etApelido.getText()).trim();

        if (apelido.length() < TAMANHO_MINIMO_APELIDO) {
            binding.tilApelido.setError(getString(R.string.erro_apelido_curto));
            binding.tilApelido.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            return;
        }
        binding.tilApelido.setError(null);
        animarBotaoEAbrirCentral(apelido);
    }

    /** Pulso no botão com ViewPropertyAnimator; ao terminar, abre a Central por Intent explícita. */
    private void animarBotaoEAbrirCentral(String apelido) {
        binding.btnEntrar.setEnabled(false);
        binding.btnEntrar.animate()
                .scaleX(1.12f)
                .scaleY(1.12f)
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.btnEntrar.animate().setListener(null);
                        binding.btnEntrar.setScaleX(1f);
                        binding.btnEntrar.setScaleY(1f);
                        binding.btnEntrar.setEnabled(true);
                        abrirCentral(apelido);
                    }
                })
                .start();
    }

    private void abrirCentral(String apelido) {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(Navegacao.EXTRA_APELIDO, apelido);
        startActivity(intent);
    }

    /**
     * Chamado quando o jogador encerra a sessão em um desafio: a instância desta Tela
     * é reaproveitada (CLEAR_TOP | SINGLE_TOP) e informa o último módulo usado.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String modulo = intent.getStringExtra(Navegacao.EXTRA_ULTIMO_MODULO);
        Log.d(TAG, "onNewIntent, ultimoModulo=" + modulo);

        if (modulo != null) {
            binding.tvUltimoModulo.setText(getString(R.string.ultimo_modulo, modulo));
            binding.tvUltimoModulo.setVisibility(android.view.View.VISIBLE);
            binding.tvUltimoModulo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
        }
        binding.etApelido.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}

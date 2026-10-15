package com.pocketarcade.app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pocketarcade.app.databinding.ActivityCodigoSecretoBinding;
import com.pocketarcade.app.databinding.ItemTentativaBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Tela 5: Cofre do Código Secreto. Quatro dígitos diferentes, até oito tentativas.
 */
public class CodigoSecretoActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela5";

    private static final int TAMANHO_CODIGO = 4;
    private static final int MAX_TENTATIVAS = 8;

    private ActivityCodigoSecretoBinding binding;

    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;

    private int[] segredo;
    private int tentativasFeitas;
    private long inicioMs;
    private boolean partidaEncerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityCodigoSecretoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        if (Navegacao.lerApelido(getIntent()) == null) {
            Toast.makeText(this, R.string.erro_apelido_ausente, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);

        binding.btnTentar.setOnClickListener(v -> tentar());
        binding.etTentativa.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tentar();
                return true;
            }
            return false;
        });
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.navegacao.btnVoltarMenu.setOnClickListener(v -> finish());
        binding.navegacao.btnEncerrarSessao.setOnClickListener(
                v -> Navegacao.encerrarSessao(this, getString(R.string.modulo_codigo)));

        novaPartida();
    }

    /** Sorteia quatro dígitos sem repetição embaralhando 0..9 e pegando os quatro primeiros. */
    private void novaPartida() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos);
        segredo = new int[TAMANHO_CODIGO];
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            segredo[i] = digitos.get(i);
        }

        tentativasFeitas = 0;
        partidaEncerrada = false;
        inicioMs = System.currentTimeMillis();

        binding.llHistorico.removeAllViews();
        binding.painelFinal.setVisibility(View.GONE);
        binding.etTentativa.setText("");
        binding.tilTentativa.setError(null);
        definirEntradaHabilitada(true);
        atualizarContador();
    }

    private void tentar() {
        if (partidaEncerrada) {
            return;
        }
        String tentativa = String.valueOf(binding.etTentativa.getText()).trim();

        String erro = validar(tentativa);
        if (erro != null) {
            binding.tilTentativa.setError(erro);
            binding.tilTentativa.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            return;
        }
        binding.tilTentativa.setError(null);
        binding.etTentativa.setText("");

        tentativasFeitas++;
        int certos = registrarTentativa(tentativa);

        if (certos == TAMANHO_CODIGO) {
            encerrarPartida(true);
        } else if (tentativasFeitas >= MAX_TENTATIVAS) {
            encerrarPartida(false);
        } else {
            atualizarContador();
        }
    }

    /** Campo não vazio, exatamente quatro números e nenhum dígito repetido. */
    private String validar(String tentativa) {
        if (tentativa.isEmpty()) {
            return getString(R.string.erro_vazio);
        }
        if (!tentativa.matches("\\d{" + TAMANHO_CODIGO + "}")) {
            return getString(R.string.erro_tamanho);
        }
        for (int i = 0; i < tentativa.length(); i++) {
            if (tentativa.indexOf(tentativa.charAt(i)) != tentativa.lastIndexOf(tentativa.charAt(i))) {
                return getString(R.string.erro_repetido);
            }
        }
        return null;
    }

    /**
     * Compara a tentativa com o segredo, desenha a linha no histórico (mais recente no topo)
     * e devolve quantos dígitos estão na posição certa.
     */
    private int registrarTentativa(String tentativa) {
        ItemTentativaBinding linha = ItemTentativaBinding.inflate(getLayoutInflater(), binding.llHistorico, false);
        TextView[] casas = {linha.tvDigito1, linha.tvDigito2, linha.tvDigito3, linha.tvDigito4};

        int certos = 0;
        int posicaoErrada = 0;
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            int digito = tentativa.charAt(i) - '0';
            casas[i].setText(String.valueOf(digito));

            if (segredo[i] == digito) {
                certos++;
                casas[i].setBackgroundResource(R.drawable.bg_clue_certo);
            } else if (existeNoSegredo(digito)) {
                posicaoErrada++;
                casas[i].setBackgroundResource(R.drawable.bg_clue_posicao);
            } else {
                casas[i].setBackgroundResource(R.drawable.bg_clue_ausente);
            }
        }

        linha.tvResumo.setText(getString(R.string.resumo_tentativa,
                tentativasFeitas, tentativa, certos, posicaoErrada));
        binding.llHistorico.addView(linha.getRoot(), 0);
        return certos;
    }

    private boolean existeNoSegredo(int digito) {
        for (int valor : segredo) {
            if (valor == digito) {
                return true;
            }
        }
        return false;
    }

    private void encerrarPartida(boolean vitoria) {
        partidaEncerrada = true;
        long fimMs = System.currentTimeMillis();
        long decorridoSegundos = (fimMs - inicioMs) / 1000L;

        definirEntradaHabilitada(false);
        binding.tvTentativas.setText(getString(R.string.tentativas_restantes, tentativasFeitas, MAX_TENTATIVAS));

        if (vitoria) {
            binding.tvResultadoFinal.setText(R.string.codigo_vitoria);
            binding.painelFinal.setVisibility(View.VISIBLE);
            binding.painelFinal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
            tocar(somVitoria);
        } else {
            binding.tvResultadoFinal.setText(getString(R.string.codigo_derrota, codigoSecretoComoTexto()));
            binding.painelFinal.setVisibility(View.VISIBLE);
            binding.painelFinal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            tocar(somDerrota);
        }

        String tempo = String.format(Locale.getDefault(), "%02d:%02d",
                decorridoSegundos / 60, decorridoSegundos % 60);
        binding.tvEstatisticas.setText(getString(R.string.codigo_estatisticas, tentativasFeitas, tempo));
    }

    private String codigoSecretoComoTexto() {
        StringBuilder texto = new StringBuilder();
        for (int digito : segredo) {
            texto.append(digito);
        }
        return texto.toString();
    }

    private void atualizarContador() {
        binding.tvTentativas.setText(
                getString(R.string.tentativas_restantes, tentativasFeitas + 1, MAX_TENTATIVAS));
    }

    private void definirEntradaHabilitada(boolean habilitada) {
        binding.etTentativa.setEnabled(habilitada);
        binding.btnTentar.setEnabled(habilitada);
    }

    private void tocar(MediaPlayer player) {
        if (player == null) {
            return;
        }
        player.seekTo(0);
        player.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /** Ao sair da tela o áudio é pausado (nunca stop()). */
    @Override
    protected void onPause() {
        super.onPause();
        if (somVitoria != null && somVitoria.isPlaying()) {
            somVitoria.pause();
        }
        if (somDerrota != null && somDerrota.isPlaying()) {
            somDerrota.pause();
        }
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
        if (somVitoria != null) {
            somVitoria.release();
            somVitoria = null;
        }
        if (somDerrota != null) {
            somDerrota.release();
            somDerrota = null;
        }
    }
}

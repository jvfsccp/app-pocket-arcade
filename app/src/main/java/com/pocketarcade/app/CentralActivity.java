package com.pocketarcade.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pocketarcade.app.databinding.ActivityCentralBinding;

/**
 * Tela 2: Central de Desafios, um menu rolável com quatro painéis.
 */
public class CentralActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela2";
    private static final long ATRASO_ENTRE_PAINEIS_MS = 150L;

    private ActivityCentralBinding binding;
    private String apelido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        apelido = Navegacao.lerApelido(getIntent());
        if (apelido == null) {
            Toast.makeText(this, R.string.erro_apelido_ausente, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.tvSaudacao.setText(getString(R.string.saudacao, apelido));

        binding.btnScanner.setOnClickListener(v -> abrir(ScannerActivity.class));
        binding.btnArena.setOnClickListener(v -> abrir(ArenaActivity.class));
        binding.btnCodigo.setOnClickListener(v -> abrir(CodigoSecretoActivity.class));
        binding.btnRandomizador.setOnClickListener(v -> abrir(RandomizadorActivity.class));

        animarPaineis();
    }

    /** Animação XML de entrada (fade + translate), escalonada painel a painel. */
    private void animarPaineis() {
        View[] paineis = {
                binding.painelScanner,
                binding.painelArena,
                binding.painelCodigo,
                binding.painelRandomizador
        };
        for (int i = 0; i < paineis.length; i++) {
            Animation animacao = AnimationUtils.loadAnimation(this, R.anim.painel_entrada);
            animacao.setStartOffset(i * ATRASO_ENTRE_PAINEIS_MS);
            paineis[i].startAnimation(animacao);
        }
    }

    private void abrir(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.putExtra(Navegacao.EXTRA_APELIDO, apelido);
        startActivity(intent);
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

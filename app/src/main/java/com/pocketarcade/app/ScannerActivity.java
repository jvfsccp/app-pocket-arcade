package com.pocketarcade.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pocketarcade.app.databinding.ActivityScannerBinding;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Tela 3: Scanner de Mensagens. Analisa um texto em cinco medidas.
 */
public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela3";
    private static final int TAMANHO_MINIMO = 3;

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        if (Navegacao.lerApelido(getIntent()) == null) {
            Toast.makeText(this, R.string.erro_apelido_ausente, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.btnAnalisar.setOnClickListener(v -> analisar());
        binding.btnLimpar.setOnClickListener(v -> limpar());
        binding.navegacao.btnVoltarMenu.setOnClickListener(v -> finish());
        binding.navegacao.btnEncerrarSessao.setOnClickListener(
                v -> Navegacao.encerrarSessao(this, getString(R.string.modulo_scanner)));
    }

    private void analisar() {
        String texto = String.valueOf(binding.etMensagem.getText()).trim();

        if (texto.length() < TAMANHO_MINIMO) {
            binding.tilMensagem.setError(getString(R.string.erro_mensagem_curta));
            binding.tilMensagem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            binding.painelResultado.setVisibility(View.GONE);
            return;
        }
        binding.tilMensagem.setError(null);

        String semEspacos = texto.replaceAll("\\s+", "");
        int caracteres = semEspacos.codePointCount(0, semEspacos.length());
        int palavras = texto.split("\\s+").length;
        int vogais = contarVogais(texto);
        String invertido = new StringBuilder(texto).reverse().toString();
        boolean palindromo = ehPalindromo(semEspacos);

        binding.tvCaracteres.setText(String.valueOf(caracteres));
        binding.tvPalavras.setText(String.valueOf(palavras));
        binding.tvVogais.setText(String.valueOf(vogais));
        binding.tvInvertido.setText(invertido);
        binding.tvPalindromo.setText(palindromo ? R.string.sim : R.string.nao);

        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
    }

    /** Conta a, e, i, o, u ignorando maiúsculas e acentos (á, ã, ê...). */
    private int contarVogais(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        int total = 0;
        for (char c : semAcentos.toLowerCase(Locale.ROOT).toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                total++;
            }
        }
        return total;
    }

    /** Ignora espaços e diferenças entre maiúsculas e minúsculas. */
    private boolean ehPalindromo(String semEspacos) {
        String normalizado = semEspacos.toLowerCase(Locale.ROOT);
        String invertido = new StringBuilder(normalizado).reverse().toString();
        return normalizado.equals(invertido);
    }

    private void limpar() {
        binding.etMensagem.setText("");
        binding.tilMensagem.setError(null);
        binding.painelResultado.setVisibility(View.GONE);
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

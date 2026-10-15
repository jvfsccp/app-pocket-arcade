package com.pocketarcade.app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pocketarcade.app.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tela 6: Randomizador. Cadastra palavras por categoria e sorteia dentro da categoria escolhida.
 */
public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela6";
    private static final int COLUNAS_GRADE = 2;

    private ActivityRandomizadorBinding binding;
    private final Random sorteador = new Random();

    private final List<ItemSorteio> itens = new ArrayList<>();
    private ItemSorteioAdapter adapter;
    private boolean modoGrade;

    private MediaPlayer somSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        if (Navegacao.lerApelido(getIntent()) == null) {
            Toast.makeText(this, R.string.erro_apelido_ausente, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        somSorteio = MediaPlayer.create(this, R.raw.som_sorteio);

        ArrayAdapter<String> categorias = new ArrayAdapter<>(this, R.layout.spinner_item,
                getResources().getStringArray(R.array.categorias));
        categorias.setDropDownViewResource(R.layout.spinner_item);
        binding.spCategoria.setAdapter(categorias);

        adapter = new ItemSorteioAdapter(itens, this::excluirItem);
        binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItens.setAdapter(adapter);

        binding.btnAdicionar.setOnClickListener(v -> adicionar());
        binding.etValor.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                adicionar();
                return true;
            }
            return false;
        });
        binding.btnSortear.setOnClickListener(v -> sortear());
        binding.btnAlternar.setOnClickListener(v -> alternarLayout());
        binding.navegacao.btnVoltarMenu.setOnClickListener(v -> finish());
        binding.navegacao.btnEncerrarSessao.setOnClickListener(
                v -> Navegacao.encerrarSessao(this, getString(R.string.modulo_randomizador)));
    }

    private String categoriaSelecionada() {
        return String.valueOf(binding.spCategoria.getSelectedItem());
    }

    private void adicionar() {
        String valor = String.valueOf(binding.etValor.getText()).trim();
        if (valor.isEmpty()) {
            binding.tilValor.setError(getString(R.string.erro_valor_vazio));
            binding.tilValor.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            return;
        }
        binding.tilValor.setError(null);

        ItemSorteio novo = new ItemSorteio(valor, categoriaSelecionada());
        itens.add(novo);
        int posicao = itens.size() - 1;
        adapter.notifyItemInserted(posicao);
        binding.rvItens.scrollToPosition(posicao);

        binding.etValor.setText("");
        Toast.makeText(this, getString(R.string.item_adicionado, novo.getCategoria()), Toast.LENGTH_SHORT).show();
    }

    /** Clique longo: remove o item e avisa o Adapter com notifyItemRemoved(). */
    private void excluirItem(int posicao) {
        ItemSorteio removido = itens.remove(posicao);
        adapter.limparDestaqueSe(removido);
        adapter.notifyItemRemoved(posicao);
        adapter.notifyItemRangeChanged(posicao, itens.size() - posicao);
        Toast.makeText(this, getString(R.string.item_removido, removido.getPalavra()), Toast.LENGTH_SHORT).show();
    }

    /** Sorteia apenas entre os itens da categoria selecionada. */
    private void sortear() {
        String categoria = categoriaSelecionada();
        List<ItemSorteio> candidatos = new ArrayList<>();
        for (ItemSorteio item : itens) {
            if (item.getCategoria().equals(categoria)) {
                candidatos.add(item);
            }
        }

        if (candidatos.isEmpty()) {
            binding.tvSorteado.setText(getString(R.string.erro_sem_itens, categoria));
            binding.tvSorteado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            return;
        }

        ItemSorteio sorteado = candidatos.get(sorteador.nextInt(candidatos.size()));
        adapter.destacar(sorteado);
        binding.rvItens.smoothScrollToPosition(itens.indexOf(sorteado));

        binding.tvSorteado.setText(getString(R.string.sorteado_formato, sorteado.getPalavra(), sorteado.getCategoria()));
        binding.tvSorteado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
        tocar(somSorteio);
    }

    /** Alterna entre LinearLayoutManager (lista) e GridLayoutManager (grade). */
    private void alternarLayout() {
        modoGrade = !modoGrade;
        if (modoGrade) {
            binding.rvItens.setLayoutManager(new GridLayoutManager(this, COLUNAS_GRADE));
        } else {
            binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
        }
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
        if (somSorteio != null && somSorteio.isPlaying()) {
            somSorteio.pause();
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
        if (somSorteio != null) {
            somSorteio.release();
            somSorteio = null;
        }
    }
}

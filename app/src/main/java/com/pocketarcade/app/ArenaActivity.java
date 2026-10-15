package com.pocketarcade.app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pocketarcade.app.databinding.ActivityArenaBinding;

import java.util.Random;

/**
 * Tela 4: Arena de Pedra, Papel e Tesoura, melhor de três contra o dispositivo.
 */
public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade.Tela4";

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;
    private static final int VITORIAS_PARA_GANHAR = 2;

    private static final long DURACAO_ESCOLHA_JOGADOR_MS = 400L;
    private static final long DURACAO_ESCOLHA_DISPOSITIVO_MS = 600L;
    private static final long ATRASO_DISPOSITIVO_MS = 250L;

    private static final int[] IMAGENS = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};

    private ActivityArenaBinding binding;
    private final Random sorteador = new Random();

    // Criados uma única vez em onCreate() e reutilizados a cada rodada.
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    private MediaPlayer somEmpate;

    private int placarJogador;
    private int placarDispositivo;
    private boolean partidaEncerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JanelaUtil.aplicarInsets(binding.getRoot());

        if (Navegacao.lerApelido(getIntent()) == null) {
            Toast.makeText(this, R.string.erro_apelido_ausente, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);
        somEmpate = MediaPlayer.create(this, R.raw.som_empate);

        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.navegacao.btnVoltarMenu.setOnClickListener(v -> finish());
        binding.navegacao.btnEncerrarSessao.setOnClickListener(
                v -> Navegacao.encerrarSessao(this, getString(R.string.modulo_arena)));

        atualizarPlacar();
    }

    private void jogar(int jogadaJogador) {
        if (partidaEncerrada) {
            return;
        }
        int jogadaDispositivo = sorteador.nextInt(3);

        binding.ivEscolhaJogador.setImageResource(IMAGENS[jogadaJogador]);
        binding.ivEscolhaDispositivo.setImageResource(IMAGENS[jogadaDispositivo]);
        animarEscolha(binding.ivEscolhaJogador, DURACAO_ESCOLHA_JOGADOR_MS, 0L);
        animarEscolha(binding.ivEscolhaDispositivo, DURACAO_ESCOLHA_DISPOSITIVO_MS, ATRASO_DISPOSITIVO_MS);

        int mensagem;
        MediaPlayer som;
        if (jogadaJogador == jogadaDispositivo) {
            mensagem = R.string.rodada_empate;
            som = somEmpate;
        } else if (venceu(jogadaJogador, jogadaDispositivo)) {
            placarJogador++;
            mensagem = R.string.rodada_vitoria;
            som = somVitoria;
        } else {
            placarDispositivo++;
            mensagem = R.string.rodada_derrota;
            som = somDerrota;
        }

        binding.tvResultadoRodada.setText(mensagem);
        binding.tvResultadoRodada.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
        if (mensagem == R.string.rodada_derrota) {
            binding.painelRodada.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
        }
        tocar(som);
        atualizarPlacar();
        verificarFimDaPartida();
    }

    /** Pedra vence tesoura, tesoura vence papel e papel vence pedra. */
    private boolean venceu(int a, int b) {
        return (a - b + 3) % 3 == 1;
    }

    /** Escolha entra girando e crescendo, com ViewPropertyAnimator (300 a 700 ms). */
    private void animarEscolha(ImageView imagem, long duracao, long atraso) {
        imagem.setScaleX(0f);
        imagem.setScaleY(0f);
        imagem.setAlpha(0f);
        imagem.setRotation(-180f);
        imagem.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .rotation(0f)
                .setStartDelay(atraso)
                .setDuration(duracao)
                .start();
    }

    private void atualizarPlacar() {
        binding.tvPlacar.setText(getString(R.string.placar_formato, placarJogador, placarDispositivo));
    }

    private void verificarFimDaPartida() {
        boolean jogadorGanhou = placarJogador >= VITORIAS_PARA_GANHAR;
        boolean dispositivoGanhou = placarDispositivo >= VITORIAS_PARA_GANHAR;
        if (!jogadorGanhou && !dispositivoGanhou) {
            return;
        }
        partidaEncerrada = true;
        definirBotoesHabilitados(false);

        binding.tvResultadoFinal.setText(jogadorGanhou ? R.string.final_vitoria : R.string.final_derrota);
        binding.tvResultadoFinal.setVisibility(View.VISIBLE);
        binding.tvResultadoFinal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
    }

    private void novaPartida() {
        placarJogador = 0;
        placarDispositivo = 0;
        partidaEncerrada = false;
        definirBotoesHabilitados(true);

        binding.ivEscolhaJogador.setImageResource(R.drawable.ic_logo);
        binding.ivEscolhaDispositivo.setImageResource(R.drawable.ic_logo);
        binding.tvResultadoRodada.setText(R.string.aguardando_jogada);
        binding.tvResultadoFinal.setVisibility(View.GONE);
        atualizarPlacar();
    }

    private void definirBotoesHabilitados(boolean habilitados) {
        float opacidade = habilitados ? 1f : 0.4f;
        View[] botoes = {binding.btnPedra, binding.btnPapel, binding.btnTesoura};
        for (View botao : botoes) {
            botao.setEnabled(habilitados);
            botao.setAlpha(opacidade);
        }
    }

    /** Reaproveita a instância: volta ao início e toca de novo. */
    private void tocar(MediaPlayer player) {
        if (player == null) {
            return;
        }
        player.seekTo(0);
        player.start();
    }

    private void pausar(MediaPlayer player) {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
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
        pausar(somVitoria);
        pausar(somDerrota);
        pausar(somEmpate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /** release() libera os recursos nativos e leva o player ao estado End. */
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
        if (somEmpate != null) {
            somEmpate.release();
            somEmpate = null;
        }
    }
}

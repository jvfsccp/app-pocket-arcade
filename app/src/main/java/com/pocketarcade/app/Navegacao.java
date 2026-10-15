package com.pocketarcade.app;

import android.app.Activity;
import android.content.Intent;

/**
 * Constantes das chaves de extras e atalhos de navegação entre as Activities.
 */
public final class Navegacao {

    /** Apelido do jogador, enviado da Tela 1 para todas as demais telas. */
    public static final String EXTRA_APELIDO = "com.pocketarcade.app.EXTRA_APELIDO";

    /** Nome do último módulo usado, enviado de volta à Tela 1 ao encerrar a sessão. */
    public static final String EXTRA_ULTIMO_MODULO = "com.pocketarcade.app.EXTRA_ULTIMO_MODULO";

    private Navegacao() {
    }

    /**
     * Lê e valida o apelido recebido por Intent.
     *
     * @return o apelido sem espaços nas pontas, ou null se estiver ausente/vazio.
     */
    public static String lerApelido(Intent intent) {
        if (intent == null) {
            return null;
        }
        String apelido = intent.getStringExtra(EXTRA_APELIDO);
        if (apelido == null || apelido.trim().isEmpty()) {
            return null;
        }
        return apelido.trim();
    }

    /**
     * Volta para a Tela 1 reaproveitando a instância que já está na pilha.
     * CLEAR_TOP remove as telas acima dela e SINGLE_TOP entrega a Intent em onNewIntent().
     */
    public static void encerrarSessao(Activity origem, String nomeDoModulo) {
        Intent intent = new Intent(origem, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(EXTRA_ULTIMO_MODULO, nomeDoModulo);
        origem.startActivity(intent);
    }
}

package com.pocketarcade.app;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Ajusta o conteúdo às barras do sistema e ao teclado (tela de ponta a ponta no Android 15+).
 */
public final class JanelaUtil {

    private JanelaUtil() {
    }

    public static void aplicarInsets(View raiz) {
        final int esquerda = raiz.getPaddingLeft();
        final int topo = raiz.getPaddingTop();
        final int direita = raiz.getPaddingRight();
        final int base = raiz.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (view, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            view.setPadding(esquerda + barras.left, topo + barras.top,
                    direita + barras.right, base + barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}

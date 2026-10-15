# Pocket Arcade — Central de Desafios

Trabalho avaliativo (APP05) de Desenvolvimento de Aplicações para Dispositivos Móveis.
Android nativo em **Java + XML**, com **View Binding**, sem banco de dados, internet, Handler/Thread/Runnable, `findViewById`, `android:onClick` ou Jetpack Compose.

## Como personalizar (antes de entregar)

Tudo fica em `app/src/main/res/values/strings.xml`:

| Chave | O que colocar |
|---|---|
| `integrante1_nome`, `integrante1_matricula`, `integrante1_curso` | Seus dados |
| `integrante2_*` | Dados do segundo integrante. **Em trabalho individual, deixe `integrante2_nome` vazio** e o bloco some da Tela 1 |
| `app_name`, `subtitulo_app` | Nome e subtítulo do app |
| `arrays.xml` → `categorias` | Categorias do Randomizador (mínimo: Filmes, Comidas, Lugares, Desafios) |

Cores em `values/colors.xml`, dimensões em `values/dimens.xml`, ícones em `res/drawable/`.
Para mudar o visual, troque a paleta em `colors.xml` e os vetores `ic_*.xml`.

## Como compilar e rodar

Pelo Android Studio: abra a pasta do projeto e rode (Run).

Pela linha de comando:

```bash
./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Versões: AGP 9.3.1, Gradle 9.5.0, `compileSdk`/`targetSdk` 37, `minSdk` 26, Java 17.

## Mapa das telas

| # | Activity | Conteúdo |
|---|---|---|
| 1 | `ApresentacaoActivity` | Dados dos integrantes, logo, apelido (mín. 3 caracteres), botão animado com `ViewPropertyAnimator`, Intent explícita; `onNewIntent()` mostra o último módulo usado |
| 2 | `CentralActivity` | `NestedScrollView` + um `LinearLayout` + `fillViewport`; saudação com o apelido; 4 painéis com animação XML de entrada |
| 3 | `ScannerActivity` | Caracteres sem espaços, palavras, vogais, texto invertido, palíndromo; Analisar/Limpar; validação; animação do resultado |
| 4 | `ArenaActivity` | Pedra, Papel e Tesoura melhor de três; placar e resultado na mesma tela; animações de 400–600 ms; 3 efeitos sonoros |
| 5 | `CodigoSecretoActivity` | 4 dígitos sem repetição, 8 tentativas, validação, cores verde/amarelo/cinza, histórico, tempo com `System.currentTimeMillis()` |
| 6 | `RandomizadorActivity` | `RecyclerView` + `Adapter` + `ViewHolder` + modelo `ItemSorteio`; `notifyItemInserted/Removed`; lista/grade; sorteio por categoria com som |

Classes de apoio: `Navegacao` (chaves de extras e `encerrarSessao`), `JanelaUtil` (insets/edge-to-edge), `ItemSorteio`, `ItemSorteioAdapter`.

## Pontos técnicos para a demonstração

- **Pilha de Activities:** "Voltar ao menu" usa só `finish()`. "Encerrar sessão" usa `FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP` (ver `Navegacao.encerrarSessao`) e a Tela 1 recebe em `onNewIntent()`.
- **Extras:** chaves em constantes (`Navegacao.EXTRA_APELIDO`, `EXTRA_ULTIMO_MODULO`); cada destino valida o apelido recebido.
- **MediaPlayer:** criado em `onCreate()` e reutilizado (`seekTo(0)` + `start()`), `pause()` em `onPause()` (nunca `stop()`), `release()` em `onDestroy()`.
- **Ciclo de vida:** `Log.d` em `onCreate`, `onResume`, `onStop` e `onDestroy` de todas as telas. Para ver no Logcat, filtre por `PocketArcade`:

  ```bash
  adb logcat -s PocketArcade.Tela1 PocketArcade.Tela2 PocketArcade.Tela3 PocketArcade.Tela4 PocketArcade.Tela5 PocketArcade.Tela6
  ```

- **Tempo do Código Secreto:** diferença de `System.currentTimeMillis()` entre início e fim, sem Handler/Thread/Runnable.
- **Orientação:** telas travadas em retrato (`screenOrientation="portrait"`).

## Créditos de imagens e sons

- Ícones e logotipo: vetores (XML) desenhados para este projeto.
- Efeitos sonoros (`res/raw/som_*.wav`): sintetizados por código em `tools/gerar_sons.js` (`node tools/gerar_sons.js`), sem material de terceiros.

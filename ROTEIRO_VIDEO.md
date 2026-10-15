# Roteiro do vídeo — Pocket Arcade

Vídeo curto (cerca de 5 a 7 minutos) mostrando as seis telas, o funcionamento e as decisões técnicas.
O PDF pede que o vídeo torne visível: **validações, rolagem, navegação e Voltar, animações e sons, os dois jogos, RecyclerView, flags de Intent e ciclo de vida no Logcat**.

## Antes de gravar

- [ ] Abrir o app no emulador ou no celular e deixar o **Logcat** visível (Android Studio ou terminal) com o filtro `PocketArcade`.
- [ ] Ligar o som do dispositivo (os efeitos sonoros precisam aparecer no vídeo).
- [ ] Deixar o código aberto em segundo plano para mostrar trechos quando citar uma decisão técnica.
- [ ] Gravar a tela com áudio do sistema e microfone.

Filtro do Logcat:

```bash
adb logcat -s PocketArcade.Tela1 PocketArcade.Tela2 PocketArcade.Tela3 PocketArcade.Tela4 PocketArcade.Tela5 PocketArcade.Tela6
```

---

## 1. Abertura (0:00 – 0:30)

**Mostrar:** Tela 1 do app.

**Falar:**
- "Sou o João Victor Fernandes Castro, matrícula 20251012000180, curso de Análise e Desenvolvimento de Sistemas."
- "Este é o Pocket Arcade, a Central de Desafios: Java, XML, View Binding, seis Activities e quatro desafios interativos."

**Destacar:** nome, matrícula, curso e o logotipo na tela; layout em `ConstraintLayout` dentro de um `NestedScrollView`.

## 2. Tela 1 — Apresentação e validação (0:30 – 1:30)

**Fazer:**
1. Digitar um apelido curto (2 letras, ex.: `Jo`) e tocar em **Entrar** → mensagem de erro e tremor do campo.
2. Digitar um apelido válido (ex.: `Joao`) e tocar em **Entrar** → botão pulsa e a Central abre.

**Falar:**
- "O apelido precisa ter ao menos três caracteres; a validação mostra erro no próprio campo."
- "O botão é animado com `ViewPropertyAnimator` e, ao fim da animação, abre a próxima tela por **Intent explícita** levando o apelido como extra."

**Mostrar no código:** `ApresentacaoActivity.animarBotaoEAbrirCentral()` e a constante `Navegacao.EXTRA_APELIDO`.

## 3. Tela 2 — Central de Desafios e rolagem (1:30 – 2:15)

**Fazer:** deixar a animação de entrada rodar, depois **rolar** a tela até o fim.

**Falar:**
- "A saudação usa o apelido recebido pelo Intent."
- "A raiz é um `NestedScrollView` com `fillViewport` e um único `LinearLayout` filho, porque o ScrollView aceita só um filho direto."
- "Cada painel tem imagem, título, descrição e botão, e entra com animação XML de fade + translate."

**Mostrar no código:** `activity_central.xml` (raiz e filho único) e `res/anim/painel_entrada.xml`.

## 4. Tela 3 — Scanner de Mensagens (2:15 – 3:15)

**Fazer:**
1. Tocar em **Analisar** com o campo vazio/curto → mensagem de erro.
2. Digitar `Ana` → Analisar: 3 caracteres, 1 palavra, 2 vogais, `anA`, palíndromo **Sim**.
3. Digitar `A sacada da casa` → Analisar: 13 caracteres, 4 palavras, 7 vogais, palíndromo **Sim**.
4. Digitar uma frase que não seja palíndromo (ex.: `Pocket Arcade`) → **Não**.
5. Tocar em **Limpar**.

**Falar:**
- "Caracteres sem espaços, palavras, vogais, texto invertido e palíndromo. Para o palíndromo, ignoro espaços e diferenças entre maiúsculas e minúsculas."
- "O painel de resultado aparece com animação."

**Mostrar no código:** `ScannerActivity.analisar()`, `contarVogais()` e `ehPalindromo()`.

## 5. Tela 4 — Arena: Pedra, Papel e Tesoura (3:15 – 4:15)

**Fazer:**
1. Jogar rodadas até alguém chegar a **duas vitórias** (mostrar vitória, derrota e, se aparecer, empate sem pontuar).
2. Mostrar o placar e a mensagem final na mesma tela; botões desabilitam.
3. Tocar em **Nova partida** → placar reinicia.

**Falar:**
- "O aplicativo sorteia a jogada do dispositivo e mostra as duas escolhas, com animações entre 400 e 600 ms."
- "Cada resultado tem um som: vitória, derrota e empate, todos em `res/raw`."
- "Os `MediaPlayer` são criados uma vez no `onCreate()` e reutilizados; ao sair da tela eu **pauso** (não uso `stop()`), e no `onDestroy()` chamo `release()`."

**Mostrar no código:** `ArenaActivity` (`onCreate`, `tocar()`, `onPause`, `onDestroy`) e `venceu()`.

## 6. Tela 5 — Cofre do Código Secreto (4:15 – 5:30)

**Fazer:**
1. Tocar em **Tentar** vazio → erro. Digitar `12` → erro de tamanho. Digitar `1123` → erro de dígito repetido.
2. Fazer tentativas válidas e mostrar as cores: **verde** (posição certa), **amarelo** (existe em outra posição), **cinza** (não existe).
3. Mostrar o histórico abaixo do campo e o contador de tentativas.
4. Concluir a partida (vitória ou derrota nas 8 tentativas): mostrar tentativas usadas e **tempo**, com animação.
5. Tocar em **Nova partida**.

**Falar:**
- "O código tem quatro dígitos sem repetição e o jogador tem no máximo oito tentativas."
- "Antes de aceitar, valido campo vazio, exatamente quatro números e ausência de dígitos repetidos."
- "O tempo é a diferença entre dois `System.currentTimeMillis()`, sem Handler, Thread ou Runnable."

**Mostrar no código:** `CodigoSecretoActivity.validar()`, `registrarTentativa()` e `encerrarPartida()`.

## 7. Tela 6 — Randomizador e RecyclerView (5:30 – 6:45)

**Fazer:**
1. Tentar **Adicionar** com o campo vazio → erro.
2. Adicionar itens em categorias diferentes (Filmes, Comidas, Lugares, Desafios) e mostrar que a lista rola até o novo item.
3. Tocar em **Lista/grade** para alternar entre `LinearLayoutManager` e `GridLayoutManager`.
4. Escolher uma categoria e tocar em **Sortear** → item destacado, animação e som.
5. Tentar sortear em uma categoria sem itens → mensagem.
6. **Clique longo** em um item para excluí-lo.

**Falar:**
- "Cada item mostra palavra e categoria. O Adapter fornece as views e o ViewHolder as guarda para reutilização."
- "Ao cadastrar uso `notifyItemInserted()` e rolo até o novo item; ao excluir uso `notifyItemRemoved()`."
- "O sorteio considera apenas a categoria selecionada."

**Mostrar no código:** `ItemSorteio` (modelo), `ItemSorteioAdapter`, `RandomizadorActivity.adicionar()`, `excluirItem()`, `sortear()` e `alternarLayout()`.

## 8. Navegação, pilha e flags de Intent (6:45 – 7:45)

**Fazer:**
1. Em um desafio, tocar em **Voltar ao menu** → volta para a Central sem abrir outra instância.
2. Abrir outro desafio e tocar em **Encerrar sessão** → volta direto à Tela 1, que mostra "Último módulo usado: …".
3. Pressionar o botão Voltar do sistema na Central e na Tela 1 para mostrar que não há telas duplicadas.

**Falar:**
- "`finish()` fecha a Activity atual e revela a anterior que já está na pilha; nunca abro uma nova instância da Central."
- "Encerrar sessão usa `FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP`: reaproveita a Tela 1 e entrega a Intent em `onNewIntent()`, que informa o último módulo usado."

**Mostrar no código:** `Navegacao.encerrarSessao()` e `ApresentacaoActivity.onNewIntent()`.

## 9. Ciclo de vida no Logcat (7:45 – 8:30)

**Fazer:** com o Logcat visível, navegar entre telas e mostrar as linhas `onCreate`, `onResume`, `onStop` e `onDestroy`; destacar que ao abrir a Central a Tela 1 recebe `onStop` e que, ao encerrar a sessão, as telas acima recebem `onDestroy` e a Tela 1 recebe `onNewIntent`.

**Falar:**
- "Registro `Log.d` em `onCreate`, `onResume`, `onStop` e `onDestroy` de todas as telas para observar o ciclo de vida."
- "O áudio é pausado em `onPause` e liberado com `release()` em `onDestroy`, por isso não vaza recurso."

## 10. Encerramento (8:30 – 9:00)

**Falar:**
- "Requisitos técnicos: Java e XML, seis Activities, View Binding, ConstraintLayout, LinearLayout e NestedScrollView, RecyclerView com Adapter, ViewHolder e modelo, Intents, MediaPlayer local, animação XML e ViewPropertyAnimator."
- "Não uso `findViewById`, `android:onClick`, banco de dados, internet, Handler/Thread/Runnable nem Jetpack Compose."
- "O código está no GitHub e o APK, no Google Drive. Obrigado!"

---

## Checklist final de entrega

- [ ] Código no GitHub: https://github.com/jvfsccp/app-pocket-arcade
- [ ] APK enviado por link do Google Drive no Teams (`app/build/outputs/apk/debug/app-debug.apk`)
- [ ] Vídeo curto das seis telas
- [ ] Créditos de imagens e sons (no `README.md`: ícones desenhados em XML e sons sintetizados por código)
- [ ] Projeto compila (`./gradlew assembleDebug`) — **app que não compila não recebe pontuação de funcionamento**

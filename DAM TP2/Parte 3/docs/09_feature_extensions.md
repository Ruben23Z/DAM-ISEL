# Feature Extensions

> Ficheiro requerido pela secção 3.10.2 do enunciado.  
> Novas funcionalidades identificadas após a conclusão do plano base (`08_implementation_plan.md`).

---

## Extensão 1 — MVVM Pattern (já implementado na base)

**Descrição:** Garantir que toda a lógica de negócio reside no ViewModel e nunca na Activity.

**Tarefas:**
- [x] Criar `ImageViewModel` com LiveData para imagens, loading e erros
- [x] `MainActivity` apenas observa — sem chamadas de rede diretas

**Alterações UI:** Nenhuma — arquitetura interna.

---

## Extensão 2 — Loading Indicator

**Descrição:** Mostrar uma ProgressBar enquanto as imagens são carregadas da API.

**Tarefas:**
- [x] `_isLoading` LiveData no ViewModel
- [x] ProgressBar global em `activity_main.xml` (visível durante fetch)
- [x] ProgressBar por item em `item_image_card.xml` (visível enquanto Glide carrega a imagem)

**Alterações UI:** ProgressBar centralizada sobre o ViewPager2; spinner por card.

---

## Extensão 3 — Image Details Screen

**Descrição:** Ecrã separado com informação detalhada sobre uma imagem (autor, descrição, likes, tags).

**Tarefas:**
- [ ] Criar `ImageDetailsActivity.kt`
- [ ] Criar `activity_image_details.xml`
- [ ] Passar `ImageItem` via Intent (serialized) ao clicar num card
- [ ] Actualizar `docs/05_navigation.md`

**Alterações UI:** Novo ecrã com imagem em destaque, avatar do utilizador, estatísticas.

---

## Extensão 4 — Favourite Items (FIFO queue, máx. 5)

**Descrição:** O utilizador pode guardar até 5 imagens favoritas. Ao guardar a 6.ª, a mais antiga é removida (FIFO).

**Tarefas:**
- [x] `FavoritesManager.kt` — lógica FIFO com `MutableList` (máx. 5)
- [x] Persistência local com `SharedPreferences` + Gson
- [x] Botão "Guardar" em `bottomActionsBar` liga ao manager
- [ ] Barra de thumbnails de favoritos acessível de qualquer ecrã
- [x] Toast "Favoritos cheios! O mais antigo foi removido" quando a fila está cheia (implícito no Toast de confirmação)

**Alterações UI:** Fila de 5 thumbnails horizontais no topo ou como overlay.

---

## Extensão 5 — Cache de até 50 Itens

**Descrição:** Manter uma cache local de até 50 imagens (excluindo favoritos), com pelo menos 10 itens à frente e 10 atrás da posição atual.

**Tarefas:**
- [ ] `ImageCache.kt` — cache em memória com `LinkedHashMap` (máx. 50, FIFO de evicção)
- [ ] Pré-carregar 10 itens durante navegação no `ImageViewModel`
- [ ] ProgressBar por item indica se a imagem vem da cache ou da rede

**Alterações UI:** Loading indicator relativo ao item em carregamento, não global.

---

## Extensão 6 — Acesso Offline

**Descrição:** Mostrar imagens cached quando não há ligação à rede. Banner "Sem ligação" visível.

**Tarefas:**
- [ ] `NetworkMonitor.kt` — detecta conectividade com `ConnectivityManager`
- [ ] `ImageRepository` devolve dados da cache quando offline
- [ ] Banner `offlineBanner` em `activity_main.xml` torna-se visível

**Alterações UI:** Banner cinzento translúcido acima da barra de ações.

---

## Extensão 7 — Tratamento de Erros da API

**Descrição:** Não fechar a app em caso de falha de rede ou resposta inválida.

**Tarefas:**
- [x] try/catch em `ImageViewModel.loadImages()`
- [x] `_errorMessage` LiveData expõe a mensagem
- [x] `MainActivity` mostra Toast com a mensagem de erro
- [ ] Botão "Tentar de novo" substitui o Toast por um Snackbar com ação

**Alterações UI:** Snackbar persistente com botão de retry.

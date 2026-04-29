# Registo de Prompts IA — DogFeed App

> Ficheiro: `docs/prompts_log.md`  
> Projeto: DogFeed — App Android estilo TikTok  
> Ferramenta IA: AntiGravity  

---

## Prompt 1

**Objetivo:** Iniciar o projeto e configurar o ambiente

**Prompt:**
Lê toda a documentação dentro de `/docs` e segue as regras do `agents.md`.
Começa a implementar o Passo 1 e o Passo 2 do plano de implementação.
Cria:
- Configuração do projeto Android (Kotlin + XML Views)
- Adiciona a permissão INTERNET no AndroidManifest
Não geres ficheiros grandes.
Explica o que estás a fazer.

**Resultado:** Projeto Android criado com Kotlin, dependências adicionadas ao `build.gradle` e permissão INTERNET no `AndroidManifest.xml`.

---

## Prompt 2

**Objetivo:** Criar o modelo de dados

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 3:
Cria o modelo de dados `ImageItem` com base em `docs/04_data_model.md`.
Mantém o código simples e limpo.

**Resultado:** Data class `ImageItem.kt` criada com os campos necessários (id, url, breed).

---

## Prompt 3

**Objetivo:** Criar o serviço de API

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 4:
Cria um serviço de API para ir buscar imagens à API definida em `docs/07_api_usage.md`.
Usa:
- Pedido HTTP com Retrofit
- Gson para fazer o parsing do JSON
Não implementes UI ainda.

**Resultado:** `ApiService.kt` criado com Retrofit, endpoint configurado e mapeamento de resposta para `ImageItem`.

---

## Prompt 4

**Objetivo:** Criar a camada Repository

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 5:
Cria uma classe Repository que:
- Chama o serviço de API
- Devolve os dados das imagens
Segue a arquitetura MVVM de forma rigorosa.

**Resultado:** `ImageRepository.kt` criado, isola a lógica de acesso a dados e expõe os resultados ao ViewModel.

---

## Prompt 5

**Objetivo:** Criar o ViewModel

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 6:
Cria um ViewModel que:
- Usa LiveData
- Chama o Repository
- Expõe a lista de imagens à UI
Mantém a lógica dentro do ViewModel (não na Activity).

**Resultado:** `FeedViewModel.kt` criado com LiveData para imagens, estado de carregamento e erros.

---

## Prompt 6

**Objetivo:** Criar o layout principal

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 7:
Cria o `activity_main.xml` com:
- RecyclerView (ecrã completo)
- SwipeRefreshLayout
- ProgressBar
Cada item deve ocupar a altura total do ecrã.

**Resultado:** `activity_main.xml` criado com ViewPager2 vertical fullscreen, barra de ações inferior (Like, Guardar, Baixar) e ProgressBar.

---

## Prompt 7

**Objetivo:** Criar o adaptador do RecyclerView

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 8:
Cria o Adapter do RecyclerView:
- Uma imagem por item a ecrã inteiro
- Usa ImageView
- Carrega imagens a partir do URL com Glide
Mantém o código simples e legível.

**Resultado:** `ImageFeedAdapter.kt` criado com ViewHolder que carrega imagens via Glide e mostra ProgressBar durante o carregamento.

---

## Prompt 8

**Objetivo:** Ligar o ViewModel à MainActivity

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 9:
Liga o ViewModel à MainActivity:
- Observa o LiveData
- Atualiza o RecyclerView
Segue o MVVM corretamente (sem lógica de negócio na Activity).

**Resultado:** `MainActivity.kt` atualizada para observar LiveData do ViewModel e atualizar o adaptador. Botões da barra inferior ligados às ações corretas.

---

## Prompt 9

**Objetivo:** Comportamento estilo TikTok (snap vertical)

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 10:
Faz o RecyclerView comportar-se como o TikTok:
- Scroll vertical
- Uma imagem por ecrã
- Snap em cada item
Usa o LayoutManager ou SnapHelper adequado.

**Resultado:** ViewPager2 com `orientation="vertical"` configurado. Cada slide ocupa o ecrã completo com transição suave entre imagens.

---

## Prompt 10

**Objetivo:** Adicionar swipe-to-refresh

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 11:
Adiciona o swipe-to-refresh:
- Puxar para baixo carrega novas imagens
- Liga ao ViewModel

**Resultado:** `SwipeRefreshLayout` ligado ao ViewModel. O gesto de puxar para baixo chama `viewModel.loadMore()` e reinicia a lista.

---

## Prompt 11

**Objetivo:** ProgressBar e tratamento de erros

**Prompt:**
Continua a seguir o plano de implementação.
Implementa o Passo 12 e o Passo 13:
- Mostra o ProgressBar enquanto carrega
- Trata os erros da API de forma adequada
A app não deve fechar com erros.

**Resultado:** ProgressBar ligado ao LiveData `isLoading`. Erros capturados com try/catch e apresentados via Toast/Snackbar.

---

## Prompt 14 (Extensão)

**Objetivo:** Implementar Animação de Like (TikTok style)

**Prompt:**
Adiciona uma animação de coração ao centro da imagem quando o utilizador faz double-tap.
- Usa um GestureDetector no adaptador.
- O coração deve aparecer com escala e alpha, e depois desaparecer.
- Sincroniza o estado de "isLiked" com a MainActivity.

**Resultado:** Implementado no `ImageFeedAdapter` com animação suave e callback para a UI principal.

---

## Prompt 15 (Extensão)

**Objetivo:** Implementar Favoritos FIFO (Máx. 5) e Cache Offline

**Prompt:**
Implementa a lógica de favoritos:
- Máximo de 5 itens (FIFO).
- Persistência com SharedPreferences e Gson.
- Implementa também um `ImageCache` para guardar até 50 imagens e permitir acesso offline.
- Adiciona um `NetworkMonitor` para avisar o utilizador quando não há internet.

**Resultado:** Criados `FavoritesManager`, `ImageCache` e `NetworkMonitor`. A app agora funciona sem rede usando os dados locais.

---

## Prompt 16 (Extensão)

**Objetivo:** Criar a Barra de Favoritos (Thumbnails)

**Prompt:**
Adiciona uma barra horizontal no topo da MainActivity para mostrar os 5 favoritos.
- Usa uma RecyclerView pequena com thumbnails circulares.
- Atualiza a barra em tempo real quando o utilizador clica no botão "Guardar".

**Resultado:** Criado `FavoritesAdapter` e integrado no layout principal. A UI reflete instantaneamente as mudanças nos favoritos.

---

## Prompt 17 (Extensão)

**Objetivo:** Implementar Ecrã de Detalhes e Partilha

**Prompt:**
Cria a `ImageDetailsActivity`:
- Mostra a imagem em grande plano.
- Mostra a raça do cão e a fonte (URL).
- Adiciona um botão para partilhar o link da imagem via Intent.ACTION_SEND.
- Permite abrir este ecrã clicando na imagem do feed ou nos thumbnails de favoritos.

**Resultado:** Ecrã de detalhes funcional com suporte a partilha externa e navegação fluida.

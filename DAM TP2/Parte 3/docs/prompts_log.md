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

**Resultado:** ProgressBar ligado ao LiveData `isLoading`. Erros capturados com try/catch e apresentados via Snackbar com botão "Tentar de novo". A app não fecha em caso de falha de rede.

---

## Prompt 12

**Objetivo:** Criar recursos drawable em falta

**Prompt:**

Continue. Cria os drawables em falta que o layout XML referencia:
- `gradient_bottom_overlay` — gradiente transparente→preto para legibilidade do texto
- `tab_active_underline` — sublinhado branco para o separador activo
- `ic_heart_outline` — ícone de coração (botão de like)
- `ic_star_outline` — ícone de estrela (botão de guardar)
- `ic_download` — ícone de descarregar

**Resultado:** Todos os cinco ficheiros XML de drawable criados como vectores/shapes. O projeto compila sem erros de recursos.

---

## Prompt 13

**Objetivo:** Corrigir erros de build e validar compilação

**Prompt:**

Continue. Corre `./gradlew assembleDebug` e corrige todos os erros de compilação:
- Plugin Kotlin duplicado (AGP 9.1 inclui Kotlin nativamente)
- `kotlinOptions` não disponível sem plugin Kotlin explícito

**Resultado:** BUILD SUCCESSFUL em 2m 51s. APK de debug gerado corretamente. 37 tarefas executadas sem erros.
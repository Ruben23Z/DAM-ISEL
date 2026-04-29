# Plano de Implementação - DogFeed Modular

Este documento descreve as etapas para a modularização da aplicação DogFeed.

## Etapa 1: Estrutura Multi-Módulo
- Configurar `settings.gradle.kts` para incluir `:core`, `:app-xml` e `:app-compose`.
- Criar os diretórios e ficheiros `build.gradle.kts` iniciais.

## Etapa 2: Extração para o Módulo `:core`
- Mover classes de dados (`ImageItem`).
- Mover lógica de rede (`DogApiService`, `NetworkMonitor`).
- Mover lógica de persistência (`FavoritesManager`).
- Criar `ImageRepository` consolidado.

## Etapa 3: Refatoração do Módulo `:app-xml`
- Adaptar o código original para utilizar as classes do módulo `:core`.
- Garantir que a `MainActivity` e `ImageDetailsActivity` funcionam corretamente com a nova estrutura.

## Etapa 4: Implementação do Módulo `:app-compose`
- Criar a interface de utilizador utilizando Jetpack Compose.
- Implementar o `VerticalPager` para navegação estilo TikTok.
- Criar componentes para botões de ação e barra de favoritos.

## Etapa 5: Funcionalidade Exclusiva Compose
- Adicionar animações fluidas com `AnimatedVisibility`.
- Implementar feedback visual animado para a ação de "Gosto".
- Garantir suporte completo a temas dinâmicos (Light/Dark mode).

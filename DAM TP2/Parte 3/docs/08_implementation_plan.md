# Plano de Implementação

O presente documento descreve o roteiro metodológico para o desenvolvimento da aplicação DogFeed, estruturado em etapas sequenciais para garantir a integridade da arquitetura MVVM.

## Etapa 1: Configuração do Ambiente e Estrutura Inicial
- Criação do projeto Android utilizando Kotlin e XML Views.
- Configuração das dependências necessárias (Retrofit, Glide, ViewModel, LiveData) no ficheiro `build.gradle.kts`.

## Etapa 2: Definição de Permissões e Segurança
- Declaração da permissão `INTERNET` no `AndroidManifest.xml`.
- Configuração da permissão `ACCESS_NETWORK_STATE` para a monitorização de conetividade.

## Etapa 3: Modelação de Dados
- Implementação da classe de dados `ImageItem`, adaptada para a estrutura de resposta da Dog CEO API.
- Inclusão de lógica para extração de metadados (raça) a partir dos URLs das imagens.

## Etapa 4: Camada de Rede (Network)
- Implementação do serviço API utilizando a biblioteca Retrofit.
- Configuração do conversor GSON para a desserialização de objetos JSON.

## Etapa 5: Camada de Dados (Repository)
- Criação da classe `ImageRepository` para abstração da fonte de dados.
- Implementação da lógica de decisão entre dados remotos e cache local.

## Etapa 6: Lógica de Negócio e Estado da UI (ViewModel)
- Implementação do `ImageViewModel` com recurso a `LiveData`.
- Gestão de estados de carregamento, sucesso e erro.

## Etapa 7: Desenho da Interface Principal (Layout)
- Conceção do `activity_main.xml` com `ViewPager2` para navegação vertical.
- Implementação de indicadores de progresso e painéis de ação.

## Etapa 8: Adaptadores e Renderização de Listas
- Criação do `ImageFeedAdapter` para a gestão do fluxo de imagens em ecrã total.
- Integração da biblioteca Glide para o carregamento assíncrono de recursos visuais.

## Etapa 9: Integração UI-ViewModel
- Estabelecimento da ligação entre a `MainActivity` e o `ImageViewModel`.
- Implementação de observadores para atualização dinâmica da interface.

## Etapa 10: Refinamento da Experiência de Utilizador
- Implementação do comportamento de "snap" vertical.
- Adição da funcionalidade de atualização por gesto (*Swipe-to-Refresh*).

## Etapa 11: Validação e Testes
- Realização de testes funcionais para verificar a recuperação de dados e a estabilidade da aplicação em diferentes condições de rede.

# DogFeed — Galeria Canina Dinâmica

> **Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
> **Projeto:** MIP-2 — Desenvolvimento Android Assistido por IA  
> **Instituição:** ISEL · 2025/2026  
> **Discente:** A51388

---

## Descrição do Projeto

O **DogFeed** é uma aplicação móvel desenvolvida para a plataforma Android que implementa uma galeria de imagens dinâmica com navegação vertical imersiva. A aplicação adota o paradigma de interface popularizado por redes sociais contemporâneas (estilo *TikTok*), onde cada item multimédia ocupa a totalidade do viewport, permitindo uma interação fluida através de gestos de deslizamento (*swiping*).

### Objetivo e Propósito
O propósito fundamental deste projeto assenta na demonstração de competências de engenharia de software móvel, nomeadamente:
- Implementação do padrão arquitetural **MVVM** (*Model-View-ViewModel*).
- Gestão de estados de rede e persistência de dados local (Cache e Favoritos).
- Consumo de serviços Web RESTful em ambiente assíncrono.
- Utilização de ferramentas de IA para aceleração do ciclo de desenvolvimento, mantendo o rigor técnico e a validação humana.

---

## Interface de Programação de Aplicações (API)

A aplicação consome a **Dog CEO API**, uma interface de programação pública e gratuita dedicada à catalogação de imagens de canídeos.

| Especificação | Detalhe |
|---|---|
| **Ponto de Extremidade (Endpoint)** | `GET https://dog.ceo/api/breeds/image/random/10` |
| **Formato de Dados** | JSON |
| **Autenticação** | Isento (Acesso Público) |
| **Documentação** | [dog.ceo/dog-api/](https://dog.ceo/dog-api/) |

---

## Capturas de Ecrã (Screenshots)

*Nota: As imagens representam a interface final da aplicação em ambiente de execução.*

| Feed Principal | Detalhes do Item | Modo Offline |
|:---:|:---:|:---:|
| ![Feed](docs/screenshots/feed.png) | ![Detalhes](docs/screenshots/details.png) | ![Offline](docs/screenshots/offline.png) |

---

## Instruções de Execução

Para compilar e executar o projeto em ambiente de desenvolvimento ou produção, siga os procedimentos descritos abaixo:

### Pré-requisitos Técnicos
- **Android Studio:** Versão Ladybug (ou superior).
- **Android SDK:** API Level 24 (Android 7.0) como requisito mínimo.
- **Conetividade:** Acesso à Internet para o carregamento inicial de dados (suporta modo offline após cache).

### Procedimento de Instalação e Corrida
1. Efetue o clone ou descarregamento do repositório para o sistema local.
2. Abra o projeto no **Android Studio**.
3. Sincronize o sistema de build **Gradle** para garantir a resolução de todas as dependências (Retrofit, Glide, Gson).
4. Execute o comando de compilação via terminal:
   ```bash
   ./gradlew assembleDebug
   ```
5. Implemente a aplicação num dispositivo físico ou emulador através do botão **Run** (▶) no IDE.

---

## Arquitetura e Estrutura

A aplicação está estruturada de forma a garantir a escalabilidade e a manutenção do código, separando a lógica de negócio da interface de utilizador:

- **Camada View:** Atividades e layouts XML (`MainActivity`, `ImageDetailsActivity`).
- **Camada ViewModel:** Gestão de estado e lógica de UI (`ImageViewModel`).
- **Camada Repository:** Gestão de fontes de dados e cache (`ImageRepository`).
- **Persistência:** `SharedPreferences` para favoritos (lógica FIFO) e cache em memória/disco.

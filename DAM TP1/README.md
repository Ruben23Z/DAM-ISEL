# Relatório do Trabalho Prático 1

**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Estudante:** Ruben Zhang  
**Data:** Março de 2026  
**Instituição:** Instituto Superior de Engenharia de Lisboa (ISEL)  
**Repositório:** [GitHub - Ruben23Z/DAM-TP1](https://github.com/Ruben23Z/DAM-TP1)

---

## 1. Introdução

O presente repositório consubstancia o culminar do **TP1** da unidade curricular de Desenvolvimento de Aplicações Móveis. O objetivo primordial deste trabalho incidiu na imersão profunda no ecossistema de desenvolvimento nativo para Android, utilizando a linguagem **Kotlin**, privilegiando a segurança, a expressividade sintática e a interoperabilidade.

A trajetória pedagógica percorreu desde os fundamentos sintáticos e estruturas de controlo da linguagem até à implementação de arquiteturas modernas (MVVM). O fito deste projeto não foi apenas o produto final, mas fortemente a escalabilidade do código e a obediência aos padrões modernos de programação.

Este relatório aborda o desenvolvimento de múltiplas aplicações:
- Um **Sistema de Gestão de Biblioteca** centrado em POO e Programação Funcional.
- A aplicação **SysInfoAPP** para extração de metadados de hardware.
- A aplicação de alta performance **Table Tennis Score**, focada em persistência e reatividade.
- A aplicação **Memorias do ISEL** que permite a criação de uma lista de memórias e a sua visualização através de um mural interativo.

---

## 2. Visão Geral do Sistema

O sistema modular é composto por cinco domínios tecnológicos distintos:

### 2.1 Módulo DAM (Core Kotlin)
Algoritmos de consola para resolução de problemas matemáticos (Quadrados Perfeitos, Sequências de Bouncing e Calculadora), focando-se na transição rumo a ecossistemas que privilegiam a segurança intrínseca.

### 2.2 Virtual Library Management System
Simula o funcionamento logístico de uma biblioteca, gerindo um catálogo diversificado, ciclos de requisição e devolução, e o escrutínio pericial das métricas das publicações.

### 2.3 SysInfoAPP
Extração e exibição de metadados referentes ao hardware e software do dispositivo via `android.os.Build`, com interface responsiva e adaptativa.

### 2.4 Table Tennis Score
Ferramenta para contagem de pontos em partidas de ténis de mesa, com rotação de serviço, deteção de Deuce, histórico via Room e análise estatística.

### 2.5 Memorias do ISEL (DAMAPP)
Mural fotográfico interativo que permite ao utilizador adicionar fotos com legendas, aplicar filtros de imagem em tempo real e manipular as imagens através de gestos multi-toque (escala e rotação).

---

## 3. Arquitetura e Design

### 3.1 Arquitetura de Software
- **MVVM & Clean Architecture**: Aplicado no Ténis de Mesa para separação de preocupações.
- **Single-Activity Architecture**: Utilizada nas apps mobile para gestão eficiente de recursos.
- **Injeção de Dependências**: Implementação de **Hilt** para desacoplamento de componentes.

### 3.2 Design de UI
- **Material Design 3**: Uso de `MaterialCardView`, `ExtendedFloatingActionButton` e `MaterialSwitch` para uma experiência moderna.
- **Responsividade**: Uso de `ConstraintLayout` e gestão dinâmica de `WindowInsets`.

---

## 4. Implementação Detalhada

### 4.1 Lógica de Programação e POO (Virtual Library)
O artefacto nuclear é a classe abstrata `Book`.
- **Custom Setters**: Validação de integridade de data (stock não negativo).
- **Polimorfismo**: Método abstrato `getStorageInfo()` implementado de forma distinta em `DigitalBook` e `PhysicalBook`.

### 4.2 Lógica Interativa e Multitouch (Memorias do ISEL)
A aplicação [DAMAPP](file:///c:/Users/ruben/OneDrive%20-%20Instituto%20Polit%C3%A9cnico%20de%20Lisboa/UNIVERSIDADE/DAM/DAM%20TP1/DAMAPP/app/src/main/java/dam_a51388/hellowordl/MainActivity.kt) destaca-se pela manipulação dinâmica de vistas:
- **Gesture Detection**: Implementação combinada de `ScaleGestureDetector` e `GestureDetector` para permitir o redimensionamento e a rotação de fotografias no mural.
- **Filtros de Cor**: Uso de `ColorMatrixColorFilter` para aplicar efeitos de Greyscale, Sepia e Inversão de cores em tempo real sobre imagens.
- **Persistência de Estado**: Uso de `onSaveInstanceState` com a data class `PhotoState` (Serializable) para garantir que as memórias e as suas transformações espaciais sobrevivem a mudanças de configuração (ex: rotação de ecrã ou modo noite).

### 4.3 Persistência e Reatividade (Table Tennis)
- **Room Database**: Subscrições via `Flow` permitem que a UI reaja instantaneamente a mudanças na base de dados.
- **Undo Logic**: Uso de `ArrayDeque` para gerir uma pilha de estados imutáveis, garantindo reversibilidade sem custo computacional elevado.

### 4.4 Metadados e UI Imersiva (SysInfoAPP)
Extração de dados via `Build` e implementação de `enableEdgeToEdge()` para uma experiência de ecrã total.

---

## 5. Testes e Validação

- **Testes Unitários**: Validação das regras de `TableTennisRules`.
- **Garantia de Estado**: Verificação da restauração de UI na app Memorias do ISEL após mudança de tema.
- **Feedback Visual**: Integração de animações **Lottie** para celebrar vitórias e transições de ecrã.

---

# Secções de Engenharia Autónoma (IA)

## 7. Estratégia de Prompting
- Uso de **Chain-of-Thought** para estruturação de módulos complexos.
- Especificação de padrões de design (MVVM, Material 3) em prompts iniciais.

## 8. Workflow de Agente Autónomo
Workflow via **Google Antigravity** focado em planificação exaustiva (`implementation_plan.md`) antes da execução, garantindo que o código gerado segue as normas do professor.

## 9. Verificação de Artefactos de IA
- Revisão manual de cada bloco de código.
- Testes de integração para validar a injeção de dependências via Hilt.

## 10. Contribuição Humana vs IA
- **Humana (70%)**: Lógica de negócio, arquitetura, design de UI e validação final.
- **IA (30%)**: Geração de código repetitivo.

---

# Processo de Desenvolvimento

## 12. Controlo de Versões
Histórico de commits atómicos e descritivos em conformidade com as diretrizes curriculares do ISEL.

## 13. Dificuldades e Lições Aprendidas
- Gestão de permissões de URI persistentes na app de Memórias.
- Valor da arquitetura desacoplada para manutenção a longo prazo.

## 14. Conclusão

A concretização deste Trabalho Prático permitiu uma síntese holística de conceitos avançados de programação aplicados ao ecossistema Android. A transição de scripts de consola puristas em **Kotlin** para sistemas móveis reativos e persistentes evidenciou a versatilidade da linguagem e a robustez das bibliotecas modernas (Room, Hilt, Navigation). 

As soluções desenvolvidas — desde a gestão de bibliotecas até ao mural de memórias interativo e à gestão desportiva — demonstram uma capacidade sólida de aplicar padrões de arquitetura (MVVM) e princípios de UI/UX modernos. O projeto não só cumpre os requisitos pedagógicos estabelecidos, como estabelece uma base escalável para futuras extensões, consolidando o conhecimento necessário para o desenvolvimento de aplicações de nível profissional.
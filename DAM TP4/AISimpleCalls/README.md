# LLM Assistant Application

This application provides a simple command-line interface to interact with AI language models, supporting both OpenAI
and Google Gemini models.

## Features

- Support for both OpenAI and Google Gemini AI models
- Extensible design with AIAssistant interface for easy addition of new AI providers
- Factory pattern for creating the appropriate assistant based on configuration
- Configurable model selection with efficient properties caching
- Robust error handling with retry mechanism
- Structured logging with configurable log levels

## Setup

1. Clone the repository
2. Create a `config.properties` file in one of these locations:
    - In the project root directory (recommended for development)
    - In the `src/main/resources` directory (for packaged applications)
    - In your user's working directory
3. Configure your API keys in the `config.properties` file:
    - For OpenAI: Get an API key from [OpenAI Platform](https://platform.openai.com/)
    - For Gemini: Get an API key from [Google AI Studio](https://ai.google.dev/)
4. Build the project using Gradle:
   ```
   ./gradlew build
   ```
5. Run the application:
   ```
   ./gradlew run
   ```

## Configuration

The `config.properties` file may contain the following settings:

- `OPENAI_API_KEY=...`
- `GEMINI_API_KEY=...`
- `AI_LLM=...` <span style="color: gray;">// Possible values: OPENAI, GEMINI; defaults to OPENAI</span>
- `LOG_LEVEL=...` <span style="color: gray;">// Possible values: OFF, ERROR, WARN, INFO, DEBUG, TRACE; defaults to
  OFF</span>






---------------------------------------------------------------------------------------------

# Testes de Temperatura — Tarefa 3.3

## Modelo Utilizado

NVIDIA API — `google/gemma-2-2b-it`

---

## Configuração dos Testes

Foram utilizados dois prompts distintos, testados com três valores de temperatura: **0,1**, **0,5** e **1,0**.

O objetivo é demonstrar de que forma a temperatura influencia a variabilidade das respostas geradas pelo modelo.

---

## Prompt 1 — Factual (baixa variância esperada)

> "What is 15 multiplied by 8?"

| Temperatura | Resposta                     |
|-------------|------------------------------|
| 0,0         | "15 multiplied by 8 is 120." |
| 0,5         | "15 multiplied by 8 is 120." |
| 1,0         | "15 multiplied by 8 is 120." |

**Conclusão:** Prompts de natureza factual produzem respostas idênticas independentemente da temperatura. A resposta
correta domina de forma tão expressiva a distribuição de probabilidade dos tokens que a aleatoriedade introduzida pela
temperatura não tem qualquer efeito observável.

---

## Prompt 2 — Criativo (alta variância esperada)

> "Escreve um pequeno poema sobre Lisboa."

### Temperatura 0,0

```
Lisbon, a city of sun and sea,
Where history whispers, "Come, be free!"
The trams hum softly, a gentle beat,
As Fado's melody, bittersweet,
Flows from the heart of every soul,
A story of love, a story of gold.
From Belém's towers, a glimpse of the past,
To Alfama's streets, where legends amassed.
The Tagus flows, a ribbon of blue,
Reflecting the city, forever anew.
Lisbon, a gem, a treasure untold,
A city of beauty, both young and old.
```

### Temperatura 0,5

```
Lisbon, a city of sun and sea,
Where history whispers, wild and free.
Fado's music, a soulful sound,
On cobblestone streets, it's all around.
The Tram rides up hills, a joyous flight,
Past Belém's towers, bathed in golden light.
Jerónimos, with its grandeur bold,
Lisbon's story, forever told.
So come, dear traveler, explore and see,
The magic of Lisbon, for you and me.
```

### Temperatura 1,0

```
The Tagus flows, a silver thread,
Through Lisbon's heart, a story spread.
From Alfama's streets, a melody,
Of fado's soul, for all to see.
The trams they rumble, a rhythmic beat,
Across the hills, a vibrant feat.
Past Jerónimos, a monument grand,
To explorers bold, across the land.
The sun descends, a fiery glow,
On Belém Tower, where stories flow.
From pastel-colored houses, bright and bold,
Lisbon's charm, a story told.
```

---

## Análise Comparativa

| Temperatura | Características Observadas                                                                                                                                                                |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0,0         | Estrutura mais formulaica e previsível. Escolha de palavras conservadora. O poema termina com um dístico de síntese, padrão recorrente em respostas deterministas.                        |
| 0,5         | Vocabulário ligeiramente mais variado. Introdução de referências adicionais ("Jerónimos") e de uma interpelação directa ao leitor ("dear traveler"), conferindo maior dinamismo ao texto. |
| 1,0         | Perspectiva de abertura distinta — o poema inicia com o Tejo em vez de uma descrição geral da cidade. Imagética mais diversificada e estrutura de verso com maior variação.               |

---

## Conclusão Geral

Os resultados obtidos demonstram que a temperatura influencia a variabilidade das respostas geradas, ainda que o efeito
seja mais subtil em modelos de menor dimensão, como o `gemma-2-2b-it`. Nos três poemas produzidos, mantêm-se constantes
os temas centrais (Tejo, Alfama, Belém, fado, eléctricos) e o esquema de rima AABB, o que sugere que o espaço de geração
do modelo é relativamente restrito para este tipo de prompt.

A temperatura mais baixa (0.1) favorece respostas deterministas e repetíveis, adequadas a contextos que exigem
consistência. A temperatura mais elevada (1,0) introduz maior diversidade lexical e estrutural, sendo mais apropriada
para tarefas criativas. Modelos de maior capacidade evidenciariam divergências mais pronunciadas entre os extremos da
escala de temperatura.
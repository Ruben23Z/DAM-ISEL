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

# Configuração dos Testes

Foi testado uma prompt de escrita de um peoma sobre lisboa com três valores de temperatura: 0.1(não permite 0), 0.5 e 1.0, com o objetivo de observar como a
temperatura afeta as respostas do modelo.

# Prompt

"Escreve um pequeno poema sobre Lisboa."

## Temperatura 0,1

~~~~
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
~~~~

### Temperatura 0,5

~~~~
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
~~~~

### Temperatura 1,0

~~~~
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
~~~~


## Análise observadas

Comparando os três poemas, é possível ver algumas diferenças. Com temperatura 0.1 a resposta é mais previsível, pois possui uma 
estrutura mais simples e vocabulário mais previsivel(como fado, belem,jeronimos, etc). Com 0.5 já se nota uma variação nas palavras, como a referência a Jerónimos e uma
interação direta ao leitor("dear traveler").
Com 1.0 o poema começa de forma diferente, abrindo com o Tejo em vez de
uma descrição geral da cidade, onde apresenta imagens mais variadas. Mas continua a menção aos temas centrais como o Tejo, Alfama, Belém,e
fado, com o esquema de rimas AABB a manter-se nos três casos, demonstrado uma limitação do modelo.
# Modelo de Dados

## Entidade: ImageItem
Representação interna de cada registo de imagem.
- `id`(String): Identificador único (gerado via hash do URL).
- `url`(String): Endereço absoluto da imagem remota.
- `breed`(String): Identificação da raça (extraída do caminho do URL).
- `isLiked`(boolean): Estado booleano de preferência imediata.
- `isFavorite`(boolean): Estado booleano de inclusão na fila de favoritos.

## Resposta da API
Mapeamento do objeto JSON recebido para a estrutura de dados Kotlin através da biblioteca GSON.
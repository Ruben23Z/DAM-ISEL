# Modelo de Dados

## Entidade: ImageItem
Representação interna de cada registo de imagem.
- `id`: Identificador único (gerado via hash do URL).
- `url`: Endereço absoluto da imagem remota.
- `breed`: Identificação da raça (extraída do caminho do URL).
- `isLiked`: Estado booleano de preferência imediata.
- `isFavorite`: Estado booleano de inclusão na fila de favoritos.

## Resposta da API
Mapeamento do objeto JSON recebido para a estrutura de dados Kotlin através da biblioteca GSON.
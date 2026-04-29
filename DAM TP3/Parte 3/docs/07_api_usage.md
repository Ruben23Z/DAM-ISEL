# Utilização da Interface de Programação de Aplicações (API)

## Descrição da API

**Dog CEO API**

Trata-se de uma API pública e gratuita que faculta o acesso a uma vasta base de dados de imagens de
cães, permitindo a filtragem por raças e a recuperação de registos aleatórios.

**Documentação Oficial:**
[https://dog.ceo/dog-api/](https://dog.ceo/dog-api/)

## Exemplo de Endpoint

A aplicação utiliza o seguinte endpoint para a recuperação de um lote de dez imagens aleatórias:
`GET https://dog.ceo/api/breeds/image/random/10`

## Cabeçalhos (Headers)

Não é necessária qualquer forma de autenticação ou cabeçalhos específicos para o consumo desta API
pública.

## Exemplo de Resposta (JSON)

O formato de resposta segue a estrutura padrão da API, onde o campo `message` contém a lista de URLs
das imagens e o campo `status` indica o sucesso da operação.

```json
{
  "message": [
    "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
    "https://images.dog.ceo/breeds/retriever-golden/n02099601_100.jpg"
  ],
  "status": "success"
}
```

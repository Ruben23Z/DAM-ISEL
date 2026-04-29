# Visão Geral do Projeto - DogFeed Modular

O projeto DogFeed é uma aplicação Android evoluída para uma arquitetura multi-módulo, demonstrando a coexistência de interfaces imperativas (XML) e declarativas (Jetpack Compose) sobre uma camada de lógica partilhada.

## Objetivo
A presente aplicação tem como propósito o desenvolvimento de uma galeria de imagens de cães, com navegação vertical em estilo TikTok, consumindo dados da "Dog CEO API".

## Utilizadores-Alvo
- Entusiastas de conteúdos multimédia e amantes de animais.
- Utilizadores que procuram uma experiência de navegação fluida e moderna.

## Descrição do Sistema
O sistema utiliza uma arquitetura multi-módulo para separar a lógica de negócio (módulo `:core`) das interfaces de utilizador. A interface XML (`:app-xml`) oferece a experiência clássica, enquanto a interface Compose (`:app-compose`) introduz animações e gestos avançados.

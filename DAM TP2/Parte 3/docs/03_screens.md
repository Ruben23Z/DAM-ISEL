# Descrição dos Ecrãs

## Ecrã Principal (Feed)
Componente central da aplicação que apresenta o fluxo de imagens.
- **ViewPager2**: Gere o scroll vertical em modo de ecrã total.
- **Barra de Favoritos**: Recipiente horizontal de miniaturas circulares para acesso rápido aos itens guardados.
- **Painel de Ações**: Sobreposição inferior com funcionalidades de "Like", "Save" e "Download".
- **Indicadores de Estado**: Faixa de aviso de ausência de rede e barramento de progresso (*ProgressBar*).

## Ecrã de Detalhes
Ecrã secundário invocado para a visualização detalhada de um espécime.
- **Visualização Ampliada**: Exibição da imagem com suporte a diferentes resoluções.
- **Informação de Origem**: Apresentação da raça e do servidor de origem.
- **Partilha Externa**: Integração com o sistema de partilha do SO Android para disseminação do URL da imagem.
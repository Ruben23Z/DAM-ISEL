# Arquitetura de Software

## Padrão: MVVM (Model-View-ViewModel)
O projeto segue rigorosamente o padrão de arquitetura recomendado pela Google para garantir a robustez do software.

### Camadas
- **UI (View)**: Atividades e ficheiros XML responsáveis pela renderização e captura de eventos.
- **ViewModel**: Gestão do estado da UI e sobrevivência a mudanças de configuração. Expõe dados através de `LiveData`.
- **Repository**: Ponto único de verdade que decide se os dados provêm da API ou da cache local.
- **Network/Storage**: Implementações técnicas de Retrofit e SharedPreferences.
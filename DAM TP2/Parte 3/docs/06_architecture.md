# Arquitetura do Software

## Padrão: MVVM (Model-View-ViewModel)

O projeto segue rigorosamente o padrão de arquitetura MVVM para garantir a
robustez da aplicação.

### Camadas

- **UI (View)**: Atividades e ficheiros XML responsáveis pela renderização e captura de eventos.
- **ViewModel**: Gestão do estado da UI e sobrevivência a mudanças de configuração. Expõe dados
  através de `LiveData`.
- **Repository**: Ponto único de verdade que decide se os dados provêm da API ou da cache local.
- **Storage**: Implementações de download

### Layers:
UI → ViewModel → Repository && Storage → API Service 


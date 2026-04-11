# Architecture

## Pattern: MVVM

### Layers

UI (Activity + XML)
↓
ViewModel
↓
Repository
↓
API Service

## Responsibilities

### UI
- Displays data
- Observes LiveData
- Handles user input

### ViewModel
- Holds UI state
- Calls repository
- Exposes LiveData

### Repository
- Handles data fetching
- Abstracts API layer

### API Service
- Makes HTTP requests to Dog CEO API
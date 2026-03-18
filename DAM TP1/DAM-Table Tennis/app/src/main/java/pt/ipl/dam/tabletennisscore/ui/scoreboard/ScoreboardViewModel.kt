package pt.ipl.dam.tabletennisscore.ui.scoreboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet
import pt.ipl.dam.tabletennisscore.domain.rules.TableTennisRules
import pt.ipl.dam.tabletennisscore.domain.usecase.SaveMatchUseCase
import javax.inject.Inject

// [ViewModel] O "Cérebro" do marcador. Gere o estado da pontuação usando regras de domínio.
@HiltViewModel
class ScoreboardViewModel @Inject constructor(
    private val saveMatchUseCase: SaveMatchUseCase // Injetado via Hilt
) : ViewModel() {

    // _state: O estado interno (mutável). state: O estado público (apenas leitura).
    private val _state = MutableStateFlow(ScoreState())
    val state: StateFlow<ScoreState> = _state.asStateFlow()

    // [Undo Logic] Uma fila que guarda "fotocópias" (snapshots) de estados anteriores.
    private val pointHistory = ArrayDeque<ScoreState>()
    private var matchStartTime = System.currentTimeMillis()

    /** Configura uma nova partida com os dados vindos do SetupMatchFragment */
    fun setupMatch(p1: String, p2: String, bestOf: Int, pointsPerSet: Int) {
        matchStartTime = System.currentTimeMillis()
        _state.value = ScoreState(
            player1Name = p1,
            player2Name = p2,
            bestOf = bestOf,
            pointsPerSet = pointsPerSet
        )
        pointHistory.clear() // Limpa o histórico de undo para a nova partida
    }

    /** Ponto de entrada para todos os eventos vindos da UI (Padrão MVI/Redux) */
    fun onEvent(event: ScoreEvent) {
        when (event) {
            is ScoreEvent.AddPoint -> addPoint(event.playerIndex)
            is ScoreEvent.UndoLastPoint -> undoLastPoint()
            is ScoreEvent.AcknowledgeSetEnd -> acknowledgeSetEnd()
            is ScoreEvent.AcknowledgeMatchEnd -> { /* Gerido no fragmento */ }
        }
    }

    /** Adiciona um ponto a um dos jogadores e recalcula o estado (serviço, fim de set, deuce) */
    private fun addPoint(playerIndex: Int) {
        val cur = _state.value
        if (cur.matchFinished || cur.setJustFinished) return // Ignora se o jogo já acabou

        // Guarda o estado atual no histórico antes de o alterar (para o botão Undo)
        pointHistory.addLast(cur)

        // Calcula a nova pontuação
        val newScore1 = if (playerIndex == 0) cur.score1 + 1 else cur.score1
        val newScore2 = if (playerIndex == 1) cur.score2 + 1 else cur.score2
        
        // Usa as regras de domínio (Domain Layer) para validações matemáticas
        val deuce = TableTennisRules.isDeuce(newScore1, newScore2, cur.pointsPerSet)
        val server = TableTennisRules.currentServer(newScore1 + newScore2, deuce)

        val p1WonSet = TableTennisRules.hasWonSet(newScore1, newScore2, cur.pointsPerSet)
        val p2WonSet = TableTennisRules.hasWonSet(newScore2, newScore1, cur.pointsPerSet)

        // Se alguém ganhou o set
        if (p1WonSet || p2WonSet) {
            val newSets1 = if (p1WonSet) cur.sets1 + 1 else cur.sets1
            val newSets2 = if (p2WonSet) cur.sets2 + 1 else cur.sets2
            val setWinner = if (p1WonSet) cur.player1Name else cur.player2Name
            
            // Adiciona o set finalizado à lista para o histórico
            val completedSets = cur.completedSets + CompletedSet(cur.currentSet, newScore1, newScore2, setWinner)

            // Verifica se a partida inteira acabou
            val matchWon = TableTennisRules.hasWonMatch(if (p1WonSet) newSets1 else newSets2, cur.bestOf)
            
            _state.update { it.copy(
                score1 = newScore1, score2 = newScore2,
                sets1 = newSets1, sets2 = newSets2,
                isDeuce = deuce, server = server,
                setJustFinished = true,
                setWinnerName = setWinner,
                matchFinished = matchWon,
                matchWinnerName = if (matchWon) setWinner else "",
                completedSets = completedSets
            )}
        } else {
            // Apenas atualiza o ponto flutuante
            _state.update { it.copy(
                score1 = newScore1, score2 = newScore2,
                isDeuce = deuce, server = server
            )}
        }
    }

    /** Remove a última "fotocópia" do estado e restaura-a (Anula o ponto) */
    private fun undoLastPoint() {
        if (pointHistory.isEmpty()) return
        _state.value = pointHistory.removeLast()
    }

    /** Prepara o marcador para o próximo set (limpa pontos mas mantém os sets ganhos) */
    private fun acknowledgeSetEnd() {
        val cur = _state.value
        if (cur.matchFinished) return
        _state.update { it.copy(
            score1 = 0, score2 = 0,
            currentSet = it.currentSet + 1,
            setJustFinished = false,
            setWinnerName = ""
        )}
        pointHistory.clear() // O Undo não deve permitir voltar ao set anterior por simplicidade
    }

    /** Converte o estado atual da UI num objeto da DB (Match e Lista de MatchSets) e guarda */
    fun saveCompletedMatch() {
        val cur = _state.value
        viewModelScope.launch {
            val match = Match(
                player1Name = cur.player1Name,
                player2Name = cur.player2Name,
                player1Id = null, player2Id = null,
                startedAt = matchStartTime,
                finishedAt = System.currentTimeMillis(),
                winnerPlayerId = null,
                winnerName = cur.matchWinnerName,
                bestOf = cur.bestOf,
                pointsPerSet = cur.pointsPerSet
            )
            val sets = cur.completedSets.map { s ->
                MatchSet(matchId = 0, setNumber = s.setNumber, score1 = s.score1, score2 = s.score2, winnerName = s.winnerName)
            }
            saveMatchUseCase(match, sets) // Chama o UseCase para gravar no Room
        }
    }
}


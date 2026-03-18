package pt.ipl.dam.tabletennisscore.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet

// [Relação] Representa uma partida juntamente com todos os seus sets (1-para-Muitos)
data class MatchWithSets(
    @Embedded val match: Match, // "Embebe" os dados da partida principal
    @Relation(
        parentColumn = "id", // Chave primária na tabela 'matches'
        entityColumn = "matchId" // Chave estrangeira na tabela 'match_sets'
    )
    val sets: List<MatchSet> // Lista de todos os sets que pertencem a esta partida
)

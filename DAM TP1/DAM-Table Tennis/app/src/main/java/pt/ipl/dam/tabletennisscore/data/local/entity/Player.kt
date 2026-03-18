package pt.ipl.dam.tabletennisscore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// [Entidade] Representa um Jogador no perfil da App
@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID do jogador
    val name: String, // Nome do jogador
    val createdAt: Long = System.currentTimeMillis() // Data de registo do perfil
)


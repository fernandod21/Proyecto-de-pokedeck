package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PokemonDetail
import com.example.data.model.PokemonItem

@Entity(tableName = "favorite_pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val heightM: Float,
    val weightKg: Float,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int,
    val description: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toPokemonDetail(): PokemonDetail = PokemonDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        heightMeters = heightM,
        weightKg = weightKg,
        types = if (types.isBlank()) emptyList() else types.split(","),
        hp = hp,
        attack = attack,
        defense = defense,
        spAtk = spAtk,
        spDef = spDef,
        speed = speed,
        description = description,
        isFavorite = true
    )

    fun toPokemonItem(): PokemonItem = PokemonItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = if (types.isBlank()) emptyList() else types.split(","),
        isFavorite = true
    )

    companion object {
        fun fromDetail(detail: PokemonDetail): PokemonEntity = PokemonEntity(
            id = detail.id,
            name = detail.name,
            imageUrl = detail.imageUrl,
            types = detail.types.joinToString(","),
            heightM = detail.heightMeters,
            weightKg = detail.weightKg,
            hp = detail.hp,
            attack = detail.attack,
            defense = detail.defense,
            spAtk = detail.spAtk,
            spDef = detail.spDef,
            speed = detail.speed,
            description = detail.description
        )

        fun fromItem(item: PokemonItem): PokemonEntity = PokemonEntity(
            id = item.id,
            name = item.name,
            imageUrl = item.imageUrl,
            types = item.types.joinToString(","),
            heightM = 0.5f,
            weightKg = 5.0f,
            hp = 50,
            attack = 50,
            defense = 50,
            spAtk = 50,
            spDef = 50,
            speed = 50,
            description = "Pokémon registrado en la colección de favoritos."
        )
    }
}

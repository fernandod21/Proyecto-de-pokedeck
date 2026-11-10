package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class PokemonType(
    val apiName: String,
    val displayName: String,
    val color: Color
) {
    NORMAL("normal", "Normal", TypeNormal),
    FIRE("fire", "Fuego", TypeFire),
    WATER("water", "Agua", TypeWater),
    GRASS("grass", "Planta", TypeGrass),
    ELECTRIC("electric", "Eléctrico", TypeElectric),
    ICE("ice", "Hielo", TypeIce),
    FIGHTING("fighting", "Lucha", TypeFighting),
    POISON("poison", "Veneno", TypePoison),
    GROUND("ground", "Tierra", TypeGround),
    FLYING("flying", "Volador", TypeFlying),
    PSYCHIC("psychic", "Psíquico", TypePsychic),
    BUG("bug", "Bicho", TypeBug),
    ROCK("rock", "Roca", TypeRock),
    GHOST("ghost", "Fantasma", TypeGhost),
    DRAGON("dragon", "Dragón", TypeDragon),
    STEEL("steel", "Acero", TypeSteel),
    FAIRY("fairy", "Hada", TypeFairy),
    DARK("dark", "Siniestro", TypeDark);

    companion object {
        fun fromApiName(name: String): PokemonType? {
            val lower = name.lowercase().trim()
            return entries.find { it.apiName.equals(lower, ignoreCase = true) }
        }
    }
}

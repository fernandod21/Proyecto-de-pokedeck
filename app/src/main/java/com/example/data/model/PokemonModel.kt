package com.example.data.model

data class PokemonItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val formattedId: String
        get() = String.format("#%03d", id)

    val capitalizedName: String
        get() = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    val primaryType: PokemonType?
        get() = types.firstOrNull()?.let { PokemonType.fromApiName(it) }
}

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val heightMeters: Float,
    val weightKg: Float,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int,
    val description: String,
    val isFavorite: Boolean = false
) {
    val formattedId: String
        get() = String.format("#%03d", id)

    val capitalizedName: String
        get() = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    val primaryType: PokemonType?
        get() = types.firstOrNull()?.let { PokemonType.fromApiName(it) }

    val totalStats: Int
        get() = hp + attack + defense + spAtk + spDef + speed

    fun toItem(): PokemonItem = PokemonItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types,
        isFavorite = isFavorite
    )
}

enum class SortOption(val label: String) {
    NUMBER_ASC("Número: Menor a mayor"),
    NUMBER_DESC("Número: Mayor a menor"),
    NAME_ASC("Nombre: A - Z"),
    NAME_DESC("Nombre: Z - A")
}

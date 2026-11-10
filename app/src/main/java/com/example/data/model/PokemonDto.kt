package com.example.data.model

import com.squareup.moshi.Json

data class PokemonListResponseDto(
    @Json(name = "count") val count: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "previous") val previous: String?,
    @Json(name = "results") val results: List<NamedResourceDto>
)

data class NamedResourceDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

data class PokemonDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "height") val height: Int, // in decimetres
    @Json(name = "weight") val weight: Int, // in hectograms
    @Json(name = "types") val types: List<TypeSlotDto>,
    @Json(name = "stats") val stats: List<StatSlotDto>,
    @Json(name = "sprites") val sprites: SpritesDto
)

data class TypeSlotDto(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: NamedResourceDto
)

data class StatSlotDto(
    @Json(name = "base_stat") val baseStat: Int,
    @Json(name = "stat") val stat: NamedResourceDto
)

data class SpritesDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "other") val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @Json(name = "front_default") val frontDefault: String?
)

data class SpeciesDto(
    @Json(name = "flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>?
)

data class FlavorTextEntryDto(
    @Json(name = "flavor_text") val flavorText: String,
    @Json(name = "language") val language: NamedResourceDto
)

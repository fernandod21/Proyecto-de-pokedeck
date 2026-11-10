package com.example.data.network

import com.example.data.model.PokemonDetailDto
import com.example.data.model.PokemonListResponseDto
import com.example.data.model.SpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 151,
        @Query("offset") offset: Int = 0
    ): PokemonListResponseDto

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: String
    ): PokemonDetailDto

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: String
    ): SpeciesDto
}

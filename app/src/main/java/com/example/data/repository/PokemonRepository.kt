package com.example.data.repository

import com.example.data.database.PokemonDao
import com.example.data.database.PokemonEntity
import com.example.data.model.PokemonDetail
import com.example.data.model.PokemonItem
import com.example.data.network.PokeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class PokemonRepository(
    private val apiService: PokeApiService,
    private val pokemonDao: PokemonDao
) {
    // In-memory caches to make UI fast and responsive
    private val cachedItems = ConcurrentHashMap<Int, PokemonItem>()
    private val cachedDetails = ConcurrentHashMap<Int, PokemonDetail>()

    val favoritePokemonFlow: Flow<List<PokemonItem>> =
        pokemonDao.getAllFavorites().map { entities ->
            entities.map { it.toPokemonItem() }
        }

    fun isFavoriteFlow(id: Int): Flow<Boolean> = pokemonDao.isFavorite(id)

    suspend fun getPokemonCatalog(limit: Int = 151, offset: Int = 0): Result<List<PokemonItem>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPokemonList(limit, offset)
                val baseItems = response.results.mapNotNull { resource ->
                    val id = extractIdFromUrl(resource.url) ?: return@mapNotNull null
                    val imageUrl = getOfficialArtworkUrl(id)
                    PokemonItem(
                        id = id,
                        name = resource.name,
                        imageUrl = imageUrl,
                        types = cachedItems[id]?.types ?: emptyList()
                    )
                }

                // Cache base items
                baseItems.forEach { cachedItems[it.id] = it }

                // Asynchronously enrich types for smooth filtering
                enrichTypesInBackground(baseItems)

                Result.success(cachedItems.values.sortedBy { it.id })
            } catch (e: Exception) {
                // If network fails, check if we have any cached or room favorites
                if (cachedItems.isNotEmpty()) {
                    Result.success(cachedItems.values.sortedBy { it.id })
                } else {
                    Result.failure(e)
                }
            }
        }

    private suspend fun enrichTypesInBackground(items: List<PokemonItem>) = coroutineScope {
        // Enrich in small concurrent batches to respect PokeAPI rate limits
        items.chunked(15).forEach { chunk ->
            val deferred = chunk.map { item ->
                async {
                    if (cachedItems[item.id]?.types.isNullOrEmpty()) {
                        try {
                            val detail = apiService.getPokemonDetail(item.id.toString())
                            val types = detail.types.map { it.type.name }
                            val updated = item.copy(types = types)
                            cachedItems[item.id] = updated
                        } catch (_: Exception) {
                            // Non-critical, graceful fallback
                        }
                    }
                }
            }
            deferred.awaitAll()
        }
    }

    suspend fun getPokemonDetail(id: Int): Result<PokemonDetail> = withContext(Dispatchers.IO) {
        try {
            // First check memory cache
            cachedDetails[id]?.let {
                val isFav = pokemonDao.getFavoriteById(id) != null
                return@withContext Result.success(it.copy(isFavorite = isFav))
            }

            // Next check Room database
            val localEntity = pokemonDao.getFavoriteById(id)

            val detailDto = apiService.getPokemonDetail(id.toString())
            val speciesDto = try {
                apiService.getPokemonSpecies(id.toString())
            } catch (_: Exception) {
                null
            }

            // Extract description: prefer Spanish, fallback to English or default
            val description = speciesDto?.flavorTextEntries
                ?.firstOrNull { it.language.name == "es" }?.flavorText
                ?: speciesDto?.flavorTextEntries
                    ?.firstOrNull { it.language.name == "en" }?.flavorText
                ?: "Un Pokémon fascinante con habilidades únicas y gran espíritu de combate."

            val cleanedDesc = description
                .replace("\n", " ")
                .replace("\u000c", " ")
                .trim()

            val statsMap = detailDto.stats.associate { it.stat.name to it.baseStat }
            val types = detailDto.types.map { it.type.name }
            val imageUrl = detailDto.sprites.other?.officialArtwork?.frontDefault
                ?: detailDto.sprites.frontDefault
                ?: getOfficialArtworkUrl(id)

            val detail = PokemonDetail(
                id = detailDto.id,
                name = detailDto.name,
                imageUrl = imageUrl,
                heightMeters = detailDto.height / 10f, // dm to meters
                weightKg = detailDto.weight / 10f, // hg to kg
                types = types,
                hp = statsMap["hp"] ?: 45,
                attack = statsMap["attack"] ?: 49,
                defense = statsMap["defense"] ?: 49,
                spAtk = statsMap["special-attack"] ?: 65,
                spDef = statsMap["special-defense"] ?: 65,
                speed = statsMap["speed"] ?: 45,
                description = cleanedDesc,
                isFavorite = localEntity != null
            )

            // Cache in memory
            cachedDetails[id] = detail
            cachedItems[id] = detail.toItem()

            Result.success(detail)
        } catch (e: Exception) {
            // If offline, attempt to serve from Room
            val localEntity = pokemonDao.getFavoriteById(id)
            if (localEntity != null) {
                Result.success(localEntity.toPokemonDetail())
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun toggleFavorite(pokemon: PokemonDetail) = withContext(Dispatchers.IO) {
        val exists = pokemonDao.getFavoriteById(pokemon.id) != null
        if (exists) {
            pokemonDao.deleteFavoriteById(pokemon.id)
            cachedDetails[pokemon.id] = pokemon.copy(isFavorite = false)
        } else {
            pokemonDao.insertFavorite(PokemonEntity.fromDetail(pokemon))
            cachedDetails[pokemon.id] = pokemon.copy(isFavorite = true)
        }
    }

    suspend fun toggleFavoriteItem(item: PokemonItem) = withContext(Dispatchers.IO) {
        val exists = pokemonDao.getFavoriteById(item.id) != null
        if (exists) {
            pokemonDao.deleteFavoriteById(item.id)
        } else {
            pokemonDao.insertFavorite(PokemonEntity.fromItem(item))
        }
    }

    suspend fun removeFavorite(id: Int) = withContext(Dispatchers.IO) {
        pokemonDao.deleteFavoriteById(id)
        cachedDetails[id]?.let {
            cachedDetails[id] = it.copy(isFavorite = false)
        }
    }

    private fun extractIdFromUrl(url: String): Int? {
        val trimmed = url.trimEnd('/')
        return trimmed.substringAfterLast('/').toIntOrNull()
    }

    fun getOfficialArtworkUrl(id: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}

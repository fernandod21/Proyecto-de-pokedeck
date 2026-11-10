package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM favorite_pokemon ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<PokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_pokemon WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: Int): PokemonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: PokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
}

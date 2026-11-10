package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.PokemonDatabase
import com.example.data.database.PokemonEntity
import com.example.data.model.PokemonItem
import com.example.data.model.PokemonType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: PokemonDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PokemonDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PokéDex", appName)
    }

    @Test
    fun `room database insert and query favorite pokemon`() = runBlocking {
        val entity = PokemonEntity(
            id = 25,
            name = "pikachu",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            types = "electric",
            heightM = 0.4f,
            weightKg = 6.0f,
            hp = 35,
            attack = 55,
            defense = 40,
            spAtk = 50,
            spDef = 50,
            speed = 90,
            description = "Cuando se enfada, este Pokémon descarga de forma instantánea la energía que almacena en el interior de sus mejillas."
        )

        database.pokemonDao().insertFavorite(entity)

        val favorites = database.pokemonDao().getAllFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("pikachu", favorites[0].name)
        assertEquals(25, favorites[0].id)

        val isFav = database.pokemonDao().isFavorite(25).first()
        assertTrue(isFav)

        // Delete test
        database.pokemonDao().deleteFavoriteById(25)
        val emptyList = database.pokemonDao().getAllFavorites().first()
        assertTrue(emptyList.isEmpty())
    }

    @Test
    fun `pokemon model formatting and types test`() {
        val item = PokemonItem(
            id = 1,
            name = "bulbasaur",
            imageUrl = "url",
            types = listOf("grass", "poison")
        )

        assertEquals("#001", item.formattedId)
        assertEquals("Bulbasaur", item.capitalizedName)
        assertEquals(PokemonType.GRASS, item.primaryType)
        assertEquals("Planta", item.primaryType?.displayName)
    }
}

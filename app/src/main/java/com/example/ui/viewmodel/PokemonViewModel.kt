package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.PokemonDatabase
import com.example.data.model.PokemonDetail
import com.example.data.model.PokemonItem
import com.example.data.model.PokemonType
import com.example.data.model.SortOption
import com.example.data.network.NetworkClient
import com.example.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PokedexScreen {
    CATALOG,
    FAVORITES,
    DETAIL
}

data class PokemonUiState(
    val allPokemon: List<PokemonItem> = emptyList(),
    val filteredPokemon: List<PokemonItem> = emptyList(),
    val searchQuery: String = "",
    val selectedType: PokemonType? = null,
    val sortOption: SortOption = SortOption.NUMBER_ASC,
    val isLoadingCatalog: Boolean = false,
    val catalogError: String? = null,
    val favorites: List<PokemonItem> = emptyList(),
    val selectedDetail: PokemonDetail? = null,
    val isLoadingDetail: Boolean = false,
    val detailError: String? = null,
    val currentScreen: PokedexScreen = PokedexScreen.CATALOG,
    val previousScreen: PokedexScreen = PokedexScreen.CATALOG
)

class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        loadCatalog()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.favoritePokemonFlow.collectLatest { favList ->
                val favIds = favList.map { it.id }.toSet()
                _uiState.update { current ->
                    val updatedAll = current.allPokemon.map { it.copy(isFavorite = favIds.contains(it.id)) }
                    val updatedDetail = current.selectedDetail?.let {
                        it.copy(isFavorite = favIds.contains(it.id))
                    }
                    current.copy(
                        favorites = favList,
                        allPokemon = updatedAll,
                        filteredPokemon = applyFilters(updatedAll, current.searchQuery, current.selectedType, current.sortOption),
                        selectedDetail = updatedDetail
                    )
                }
            }
        }
    }

    fun loadCatalog() {
        _uiState.update { it.copy(isLoadingCatalog = true, catalogError = null) }
        viewModelScope.launch {
            val result = repository.getPokemonCatalog(limit = 151, offset = 0)
            result.fold(
                onSuccess = { list ->
                    val favIds = _uiState.value.favorites.map { it.id }.toSet()
                    val enriched = list.map { it.copy(isFavorite = favIds.contains(it.id)) }
                    _uiState.update { current ->
                        current.copy(
                            allPokemon = enriched,
                            filteredPokemon = applyFilters(enriched, current.searchQuery, current.selectedType, current.sortOption),
                            isLoadingCatalog = false,
                            catalogError = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingCatalog = false,
                            catalogError = "No se pudo cargar la Pokédex. Verifica tu conexión a Internet."
                        )
                    }
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredPokemon = applyFilters(current.allPokemon, query, current.selectedType, current.sortOption)
            )
        }
    }

    fun selectTypeFilter(type: PokemonType?) {
        _uiState.update { current ->
            val newType = if (current.selectedType == type) null else type
            current.copy(
                selectedType = newType,
                filteredPokemon = applyFilters(current.allPokemon, current.searchQuery, newType, current.sortOption)
            )
        }
    }

    fun setSortOption(sortOption: SortOption) {
        _uiState.update { current ->
            current.copy(
                sortOption = sortOption,
                filteredPokemon = applyFilters(current.allPokemon, current.searchQuery, current.selectedType, sortOption)
            )
        }
    }

    fun selectPokemon(id: Int) {
        _uiState.update {
            it.copy(
                previousScreen = if (it.currentScreen != PokedexScreen.DETAIL) it.currentScreen else it.previousScreen,
                currentScreen = PokedexScreen.DETAIL,
                isLoadingDetail = true,
                detailError = null,
                selectedDetail = null
            )
        }
        viewModelScope.launch {
            val result = repository.getPokemonDetail(id)
            result.fold(
                onSuccess = { detail ->
                    val isFav = _uiState.value.favorites.any { it.id == detail.id }
                    _uiState.update {
                        it.copy(
                            selectedDetail = detail.copy(isFavorite = isFav),
                            isLoadingDetail = false,
                            detailError = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingDetail = false,
                            detailError = "Error al obtener detalles del Pokémon."
                        )
                    }
                }
            )
        }
    }

    fun navigateTo(screen: PokedexScreen) {
        _uiState.update {
            it.copy(
                currentScreen = screen,
                previousScreen = if (screen != PokedexScreen.DETAIL) screen else it.previousScreen
            )
        }
    }

    fun navigateBack() {
        _uiState.update {
            it.copy(
                currentScreen = it.previousScreen,
                selectedDetail = null,
                detailError = null
            )
        }
    }

    fun toggleFavorite(detail: PokemonDetail) {
        viewModelScope.launch {
            repository.toggleFavorite(detail)
        }
    }

    fun toggleFavoriteItem(item: PokemonItem) {
        viewModelScope.launch {
            repository.toggleFavoriteItem(item)
        }
    }

    fun removeFavorite(id: Int) {
        viewModelScope.launch {
            repository.removeFavorite(id)
        }
    }

    private fun applyFilters(
        list: List<PokemonItem>,
        query: String,
        type: PokemonType?,
        sortOption: SortOption
    ): List<PokemonItem> {
        var result = list

        if (query.isNotBlank()) {
            val cleanQuery = query.trim().lowercase()
            result = result.filter { item ->
                item.name.lowercase().contains(cleanQuery) ||
                        item.id.toString().contains(cleanQuery) ||
                        item.formattedId.lowercase().contains(cleanQuery)
            }
        }

        if (type != null) {
            result = result.filter { item ->
                item.types.any { it.equals(type.apiName, ignoreCase = true) }
            }
        }

        return when (sortOption) {
            SortOption.NUMBER_ASC -> result.sortedBy { it.id }
            SortOption.NUMBER_DESC -> result.sortedByDescending { it.id }
            SortOption.NAME_ASC -> result.sortedBy { it.name }
            SortOption.NAME_DESC -> result.sortedByDescending { it.name }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = PokemonDatabase.getDatabase(context)
                    val repo = PokemonRepository(NetworkClient.apiService, db.pokemonDao())
                    return PokemonViewModel(repo) as T
                }
            }
    }
}

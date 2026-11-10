package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.PokedexBottomNav
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.theme.PokedexTheme
import com.example.ui.viewmodel.PokedexScreen
import com.example.ui.viewmodel.PokemonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                PokedexApp()
            }
        }
    }
}

@Composable
fun PokedexApp(
    viewModel: PokemonViewModel = viewModel(
        factory = PokemonViewModel.provideFactory(androidx.compose.ui.platform.LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (uiState.currentScreen != PokedexScreen.DETAIL) {
                PokedexBottomNav(
                    currentScreen = uiState.currentScreen,
                    favoritesCount = uiState.favorites.size,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = uiState.currentScreen,
                label = "pokedex_screen_transition"
            ) { screen ->
                when (screen) {
                    PokedexScreen.CATALOG -> {
                        CatalogScreen(
                            pokemonList = uiState.filteredPokemon,
                            searchQuery = uiState.searchQuery,
                            selectedType = uiState.selectedType,
                            sortOption = uiState.sortOption,
                            isLoading = uiState.isLoadingCatalog,
                            errorMessage = uiState.catalogError,
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            onTypeSelect = { viewModel.selectTypeFilter(it) },
                            onSortSelect = { viewModel.setSortOption(it) },
                            onPokemonClick = { id -> viewModel.selectPokemon(id) },
                            onFavoriteToggle = { item -> viewModel.toggleFavoriteItem(item) },
                            onRetry = { viewModel.loadCatalog() }
                        )
                    }

                    PokedexScreen.FAVORITES -> {
                        FavoritesScreen(
                            favorites = uiState.favorites,
                            onPokemonClick = { id -> viewModel.selectPokemon(id) },
                            onRemoveFavorite = { item -> viewModel.removeFavorite(item.id) },
                            onExploreClick = { viewModel.navigateTo(PokedexScreen.CATALOG) }
                        )
                    }

                    PokedexScreen.DETAIL -> {
                        DetailScreen(
                            detail = uiState.selectedDetail,
                            isLoading = uiState.isLoadingDetail,
                            errorMessage = uiState.detailError,
                            onBackClick = { viewModel.navigateBack() },
                            onFavoriteToggle = { detail -> viewModel.toggleFavorite(detail) },
                            onRetry = {
                                uiState.selectedDetail?.let {
                                    viewModel.selectPokemon(it.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

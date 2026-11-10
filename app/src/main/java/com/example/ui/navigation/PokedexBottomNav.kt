package com.example.ui.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PokedexRed
import com.example.ui.viewmodel.PokedexScreen

@Composable
fun PokedexBottomNav(
    currentScreen: PokedexScreen,
    favoritesCount: Int,
    onNavigate: (PokedexScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    NavigationBar(
        modifier = modifier
            .then(if (isLandscape) Modifier.height(54.dp) else Modifier)
            .testTag("pokedex_bottom_nav"),
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Tab 1: Catálogo
        NavigationBarItem(
            selected = currentScreen == PokedexScreen.CATALOG,
            onClick = { onNavigate(PokedexScreen.CATALOG) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == PokedexScreen.CATALOG) Icons.Filled.FormatListNumbered else Icons.Outlined.FormatListNumbered,
                    contentDescription = "Catálogo",
                    modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp)
                )
            },
            label = {
                Text(
                    text = "Catálogo",
                    fontWeight = if (currentScreen == PokedexScreen.CATALOG) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isLandscape) 11.sp else 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PokedexRed,
                indicatorColor = PokedexRed,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_catalog")
        )

        // Tab 2: Favoritos
        NavigationBarItem(
            selected = currentScreen == PokedexScreen.FAVORITES,
            onClick = { onNavigate(PokedexScreen.FAVORITES) },
            icon = {
                BadgedBox(
                    badge = {
                        if (favoritesCount > 0) {
                            Badge(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = favoritesCount.toString(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == PokedexScreen.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritos",
                        modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Favoritos",
                    fontWeight = if (currentScreen == PokedexScreen.FAVORITES) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isLandscape) 11.sp else 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = PokedexRed,
                indicatorColor = PokedexRed,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_favorites")
        )
    }
}

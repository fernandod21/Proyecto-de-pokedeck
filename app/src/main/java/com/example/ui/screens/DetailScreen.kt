package com.example.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.PokemonDetail
import com.example.ui.components.StatBar
import com.example.ui.components.TypeBadge
import com.example.ui.theme.*

@Composable
fun DetailScreen(
    detail: PokemonDetail?,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onFavoriteToggle: (PokemonDetail) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val primaryColor = detail?.primaryType?.color ?: PokedexRed

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && detail == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PokedexRed, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando información del Pokémon...", fontWeight = FontWeight.Bold)
                }
            }
            return
        }

        if (errorMessage != null && detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PokedexRed)) {
                        Text("Reintentar")
                    }
                }
            }
            return
        }

        if (detail == null) return

        if (isLandscape) {
            // LANDSCAPE: Clean two-pane side-by-side layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Left Pane: Hero Visual & Artwork
                Box(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    // Decorative circle
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Top Bar inside left pane
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.2f))
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar al catálogo",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = detail.formattedId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Centered artwork scaled to available height
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(detail.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Arte oficial de ${detail.capitalizedName}",
                        modifier = Modifier
                            .size(175.dp)
                            .align(Alignment.Center)
                            .testTag("detail_pokemon_image"),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    )

                    // Bottom info: Name & Favorite FAB
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = detail.capitalizedName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        FloatingActionButton(
                            onClick = { onFavoriteToggle(detail) },
                            containerColor = if (detail.isFavorite) Color(0xFFE53935) else Color.White,
                            contentColor = if (detail.isFavorite) Color.White else Color(0xFFE53935),
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("detail_favorite_fab"),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = if (detail.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (detail.isFavorite) "Eliminar de favoritos" else "Guardar en favoritos",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Right Pane: Scrollable Details Sheet
                Card(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .padding(start = 4.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Types Row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            detail.types.forEach { type ->
                                TypeBadge(typeName = type)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Physical Attributes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PhysicalMetric(icon = Icons.Default.Height, title = "Altura", value = "${detail.heightMeters} m")
                            PhysicalMetric(icon = Icons.Default.Scale, title = "Peso", value = "${detail.weightKg} kg")
                            PhysicalMetric(icon = Icons.Default.Star, title = "Total Stats", value = "${detail.totalStats} pts")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Descripción de la Pokédex",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = detail.description,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Estadísticas Base",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        StatBar(name = "HP", value = detail.hp, barColor = StatHp)
                        StatBar(name = "Ataque", value = detail.attack, barColor = StatAtk)
                        StatBar(name = "Defensa", value = detail.defense, barColor = StatDef)
                        StatBar(name = "Atq. Esp.", value = detail.spAtk, barColor = StatSpAtk)
                        StatBar(name = "Def. Esp.", value = detail.spDef, barColor = StatSpDef)
                        StatBar(name = "Velocidad", value = detail.speed, barColor = StatSpeed)
                    }
                }
            }
        } else {
            // PORTRAIT: Original vertical scroll layout
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Hero Top Section with Type Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.82f)
                                )
                            )
                        )
                        .statusBarsPadding()
                ) {
                    // Background Pokéball decorative circle
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Top Bar Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.2f))
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar al catálogo",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = detail.formattedId,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Official Artwork Centered
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(detail.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Arte oficial de ${detail.capitalizedName}",
                        modifier = Modifier
                            .size(210.dp)
                            .align(Alignment.BottomCenter)
                            .testTag("detail_pokemon_image"),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    )
                }

                // Pokemon Content Details Card Sheet
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Name & Types
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = detail.capitalizedName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Floating Favorite Button inside card header
                            FloatingActionButton(
                                onClick = { onFavoriteToggle(detail) },
                                containerColor = if (detail.isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (detail.isFavorite) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(46.dp)
                                    .testTag("detail_favorite_fab"),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = if (detail.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (detail.isFavorite) "Eliminar de favoritos" else "Guardar en favoritos",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Types Row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            detail.types.forEach { type ->
                                TypeBadge(typeName = type)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Physical Attributes Row (Height, Weight, Total Base Stats)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PhysicalMetric(
                                icon = Icons.Default.Height,
                                title = "Altura",
                                value = "${detail.heightMeters} m"
                            )
                            PhysicalMetric(
                                icon = Icons.Default.Scale,
                                title = "Peso",
                                value = "${detail.weightKg} kg"
                            )
                            PhysicalMetric(
                                icon = Icons.Default.Star,
                                title = "Total Stats",
                                value = "${detail.totalStats} pts"
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Description Section
                        Text(
                            text = "Descripción de la Pokédex",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = detail.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Base Stats Section
                        Text(
                            text = "Estadísticas Base",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        StatBar(name = "HP", value = detail.hp, barColor = StatHp)
                        StatBar(name = "Ataque", value = detail.attack, barColor = StatAtk)
                        StatBar(name = "Defensa", value = detail.defense, barColor = StatDef)
                        StatBar(name = "Atq. Esp.", value = detail.spAtk, barColor = StatSpAtk)
                        StatBar(name = "Def. Esp.", value = detail.spDef, barColor = StatSpDef)
                        StatBar(name = "Velocidad", value = detail.speed, barColor = StatSpeed)

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PhysicalMetric(
    icon: ImageVector,
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

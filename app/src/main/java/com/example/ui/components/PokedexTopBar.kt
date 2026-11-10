package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PokedexBlueIndicator
import com.example.ui.theme.PokedexRed
import com.example.ui.theme.PokedexYellow

@Composable
fun PokedexTopBar(
    title: String = "PokéDex",
    subtitle: String = "Kanto • 151 Pokémon",
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val verticalPadding = if (isLandscape) 6.dp else 14.dp
    val lensSize = if (isLandscape) 34.dp else 46.dp
    val innerLensSize = if (isLandscape) 28.dp else 40.dp
    val miniSize = if (isLandscape) 10.dp else 13.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .background(PokedexRed)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = verticalPadding)
            .testTag("pokedex_top_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Authentic Pokédex Sensor Lights
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Big Glowing Blue Lens
                Box(
                    modifier = Modifier
                        .size(lensSize)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(innerLensSize)
                            .clip(CircleShape)
                            .background(PokedexBlueIndicator)
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    ) {
                        // Glossy shine reflection
                        Box(
                            modifier = Modifier
                                .size(if (isLandscape) 8.dp else 12.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(1.dp)
                                .align(Alignment.TopStart)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Three mini indicator lights: Red, Yellow, Green
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    MiniIndicator(color = Color(0xFFFF5252), size = miniSize)
                    MiniIndicator(color = PokedexYellow, size = miniSize)
                    MiniIndicator(color = Color(0xFF4CAF50), size = miniSize)
                }
            }

            // Title & Subtitle
            if (isLandscape) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "• $subtitle",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniIndicator(color: Color, size: androidx.compose.ui.unit.Dp = 13.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF330000))
            .padding(1.5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size - 3.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

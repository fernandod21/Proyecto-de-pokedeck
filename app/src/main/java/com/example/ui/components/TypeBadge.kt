package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PokemonType

@Composable
fun TypeBadge(
    typeName: String,
    modifier: Modifier = Modifier
) {
    val pokemonType = PokemonType.fromApiName(typeName)
    val color = pokemonType?.color ?: Color(0xFF888888)
    val displayName = pokemonType?.displayName ?: typeName.replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .testTag("type_badge_${typeName.lowercase()}")
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayName,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

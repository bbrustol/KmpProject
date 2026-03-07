package com.bbrustol.feature.animelist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModel
import com.bbrustol.feature.animelist.presentation.AnimeDetailsEvent.NavigateBack
import com.bbrustol.feature.animelist.presentation.AnimeDetailsUiState.AnimeDetails
import com.bbrustol.feature.animelist.presentation.AnimeDetailsUiState.Idle
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson

@Composable
internal fun AnimeDetailsScreen(
    uiState: AnimeDetailsUiState,
    onEvent: (AnimeDetailsEvent) -> Unit,
) {
    when (uiState) {
        Idle -> { /* waiting for shared state */ }
        is AnimeDetails -> AnimeDetailContent(
            anime = uiState.anime,
            onBack = { onEvent(NavigateBack) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeDetailContent(anime: AnimeUiModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = anime.title,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            AsyncImage(
                model = anime.largeImageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = anime.titleEnglish,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    InfoItem(label = "Score", value = anime.score)
                    InfoItem(label = "Rank", value = anime.rank)
                    InfoItem(label = "Episodes", value = anime.episodes)
                }

                if (anime.type.isNotBlank() || anime.status.isNotBlank() || anime.season.isNotBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (anime.type.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(anime.type) })
                        }
                        if (anime.status.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(anime.status) })
                        }
                        if (anime.season.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(anime.season) })
                        }
                    }
                }

                if (anime.genres.isNotEmpty()) {
                    Text(
                        text = "Genres",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        anime.genres.forEach { genre ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(genre, style = MaterialTheme.typography.labelSmall) },
                            )
                        }
                    }
                }

                if (anime.synopsis.isNotBlank()) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = anime.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AnimeDetailsScreenPreview() {
    val anime = previewJson
        .decodeFromString<AnimeListResponse>(animeListMock)
        .data.first()
        .toDomainModel()
        .toUiModel()
    MaterialTheme {
        AnimeDetailsScreen(
            uiState = AnimeDetailsUiState.AnimeDetails(anime = anime),
            onEvent = {},
        )
    }
}

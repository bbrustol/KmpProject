package com.bbrustol.feature.animelist.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.bbrustol.core.ui.components.ErrorContent
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModels
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.GetDetails
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.LoadMore
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.LoadTopAnime
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.AnimeList
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.Idle
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.ShowError
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.ShowException
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.ShowNoInternet
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson

@Composable
internal fun AnimeListScreen(
    uiState: AnimeListUiState,
    onEvent: (AnimeListEvent) -> Unit,
) {
    when (uiState) {
        Idle -> { /* waiting for first load */ }
        is AnimeList -> AnimeGrid(uiState = uiState, onEvent = onEvent)
        is ShowError -> ErrorContent(
            message = uiState.message ?: "Error ${uiState.code}",
            onRetry = { onEvent(LoadTopAnime) }
        )
        is ShowException -> ErrorContent(
            message = uiState.throwable?.message ?: "Unknown error",
            onRetry = { onEvent(LoadTopAnime) }
        )
        ShowNoInternet -> ErrorContent(
            message = "No internet connection",
            onRetry = { onEvent(LoadTopAnime) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeGrid(
    uiState: AnimeList,
    onEvent: (AnimeListEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Top Anime") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = uiState.list.size,
                    key = { uiState.list[it].malId },
                ) { index ->
                    AnimeCard(
                        anime = uiState.list[index],
                        onClick = { onEvent(GetDetails(uiState.list[index])) },
                    )
                }

                if (uiState.hasNextPage && !uiState.isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Button(onClick = { onEvent(LoadMore) }) {
                                Text("Load More")
                            }
                        }
                    }
                }

                if (uiState.isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeCard(
    anime: AnimeUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.height(220.dp)) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "★ ${anime.score}", style = MaterialTheme.typography.labelSmall)
                    Text(text = anime.rank, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AnimeListScreenPreview() {
    val domain = previewJson.decodeFromString<AnimeListResponse>(animeListMock).toDomainModel()
    MaterialTheme {
        AnimeListScreen(
            uiState = AnimeListUiState.AnimeList(
                list = domain.toUiModels(),
                hasNextPage = domain.hasNextPage,
                isLoading = false,
            ),
            onEvent = {},
        )
    }
}

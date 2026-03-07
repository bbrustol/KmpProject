package com.bbrustol.feature.animelist.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bbrustol.feature.animelist.presentation.AnimeDetailsEvent
import com.bbrustol.feature.animelist.presentation.AnimeDetailsPresenter
import com.bbrustol.feature.animelist.presentation.AnimeDetailsSideEffect
import com.bbrustol.feature.animelist.presentation.AnimeDetailsScreen
import com.bbrustol.feature.animelist.presentation.AnimeListEvent
import com.bbrustol.feature.animelist.presentation.AnimeListPresenter
import com.bbrustol.feature.animelist.presentation.AnimeListScreen
import com.bbrustol.feature.animelist.presentation.AnimeListSideEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavAnime() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "anime_list") {
        composable("anime_list") {
            val listPresenter: AnimeListPresenter = koinViewModel()
            val listUiState by listPresenter.uiState.collectAsState()

            LaunchedEffect(Unit) {
                listPresenter.dispatch(AnimeListEvent.LoadTopAnime)
            }

            LaunchedEffect(Unit) {
                listPresenter.sideEffect.collect { effect ->
                    when (effect) {
                        AnimeListSideEffect.GotoDetails -> navController.navigate("anime_details")
                    }
                }
            }

            AnimeListScreen(
                uiState = listUiState,
                onEvent = { listPresenter.dispatch(it) },
            )
        }

        composable("anime_details") {
            val detailsPresenter: AnimeDetailsPresenter = koinViewModel()
            val detailsUiState by detailsPresenter.uiState.collectAsState()

            LaunchedEffect(Unit) {
                detailsPresenter.dispatch(AnimeDetailsEvent.LoadAnime)
                detailsPresenter.sideEffect.collect { effect ->
                    when (effect) {
                        AnimeDetailsSideEffect.GoBack -> navController.popBackStack()
                    }
                }
            }

            AnimeDetailsScreen(
                uiState = detailsUiState,
                onEvent = { detailsPresenter.dispatch(it) },
            )
        }
    }
}

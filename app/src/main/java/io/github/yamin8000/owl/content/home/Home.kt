/*
 *     Owl: an android app for Owlbot Dictionary API
 *     Home.kt Created by Yamin Siahmargooei at 2022/8/22
 *     This file is part of Owl.
 *     Copyright (C) 2022  Yamin Siahmargooei
 *
 *     Owl is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Owl is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Owl.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.yamin8000.owl.content.home

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.content.MainBottomBar
import io.github.yamin8000.owl.content.MainTopBar
import io.github.yamin8000.owl.ui.composable.*
import io.github.yamin8000.owl.ui.theme.PreviewTheme
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    searchTerm: String?,
    onHistoryClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onInfoClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = rememberHomeState()

        InternetAwareComposable { state.isOnline.value = it }

        val locale = if (state.ttsLang.value.isEmpty())
            Locale.US else Locale.forLanguageTag(state.ttsLang.value)

        if (searchTerm != null)
            state.searchText = searchTerm
        LaunchedEffect(state.isOnline.value) {
            if (state.isFirstTimeOpening)
                state.searchText = "Owl"
            if (state.searchText.isNotBlank())
                state.searchForDefinition()
        }

        if (state.searchResult.value.item.isNotEmpty() && state.rawWordSearchBody.value != null && state.isSharing.value)
            state.handleShareIntent()

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = {
                SnackbarHost(state.snackbarHostState) { data ->
                    MySnackbar {
                        PersianText(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = data.visuals.message
                        )
                    }
                }
            },
            topBar = {
                MainTopBar(
                    scrollBehavior = scrollBehavior,
                    onHistoryClick = onHistoryClick,
                    onFavouritesClick = onFavouritesClick,
                    onInfoClick = onInfoClick,
                    onSettingsClick = onSettingsClick,
                    onRandomWordClick = { state.scope.launch { state.searchForRandomWord() } }
                )
            },
            bottomBar = {
                MainBottomBar(
                    searchTerm = searchTerm,
                    suggestions = state.searchSuggestions.value,
                    isSearching = state.isSearching.value,
                    onSearchTermChanged = {
                        state.searchText = it
                        state.scope.launch { state.handleSuggestions() }
                        if (state.isWordSelectedFromKeyboardSuggestions) {
                            state.scope.launch { state.searchForDefinitionHandler() }
                            state.clearSuggestions()
                        }
                    },
                    onSuggestionClick = {
                        state.searchText = it
                        state.lifecycleOwner.lifecycleScope.launch { state.searchForDefinitionHandler() }
                    },
                    onSearch = {
                        state.searchText = it
                        state.lifecycleOwner.lifecycleScope.launch { state.searchForDefinitionHandler() }
                    }
                )
            },
            content = { contentPadding ->
                val onShareWord = remember { { state.isSharing.value = true } }

                Column(
                    modifier = Modifier.padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = !state.isOnline.value,
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        PersianText(
                            text = stringResource(R.string.general_net_error),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    val addedToFavourites = stringResource(R.string.added_to_favourites)

                    if (state.rawWordSearchBody.value != null || state.searchResult.value.item.isNotEmpty()) {
                        state.rawWordSearchBody.value?.let { word ->
                            WordCard(
                                locale.toLanguageTag(),
                                word.word,
                                word.pronunciation,
                                onShareWord = onShareWord,
                                onAddToFavourite = {
                                    state.scope.launch {
                                        state.addToFavourite(word.word)
                                        state.snackbarHostState.showSnackbar(addedToFavourites)
                                    }
                                }
                            )
                        }

                        WordDefinitionsList(
                            locale.toLanguageTag(),
                            state.listState,
                            state.searchResult.value,
                            onWordChipClick = {
                                state.searchText = it
                                state.lifecycleOwner.lifecycleScope.launch { state.searchForDefinitionHandler() }
                            }
                        )
                    } else EmptyList()
                }
            })
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun Preview() {
    PreviewTheme { HomeContent(null, {}, {}, {}, {}) }
}
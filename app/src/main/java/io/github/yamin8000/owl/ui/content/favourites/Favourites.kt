/*
 *     freeDictionaryApp/freeDictionaryApp.app.main
 *     Favourites.kt Copyrighted by Yamin Siahmargooei at 2023/8/26
 *     Favourites.kt Last modified at 2023/8/26
 *     This file is part of freeDictionaryApp/freeDictionaryApp.app.main.
 *     Copyright (C) 2023  Yamin Siahmargooei
 *
 *     freeDictionaryApp/freeDictionaryApp.app.main is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     freeDictionaryApp/freeDictionaryApp.app.main is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with freeDictionaryApp.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.yamin8000.owl.ui.content.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.ui.composable.EmptyList
import io.github.yamin8000.owl.ui.composable.RemovableCard
import io.github.yamin8000.owl.ui.composable.ScaffoldWithTitle
import io.github.yamin8000.owl.util.list.ListSatiation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavouritesContent(
    onFavouritesItemClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state = rememberFavouritesState()

    ScaffoldWithTitle(
        title = stringResource(R.string.favourites),
        onBackClick = onBackClick,
        content = {
            when (state.listSatiation) {
                ListSatiation.Empty -> EmptyList()
                ListSatiation.Partial -> {
                    FavouritesGrid(
                        favourites = state.favourites.value.toList(),
                        onItemClick = onFavouritesItemClick,
                        onItemLongClick = { favourite ->
                            state.scope.launch { state.removeFavourite(favourite) }
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun FavouritesGrid(
    favourites: List<String>,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit
) {
    val span = rememberSaveable { if (favourites.size == 1) 1 else 2 }
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Fixed(span),
        content = {
            items(
                items = favourites,
                itemContent = { favourite ->
                    val onLongClick = remember(onItemLongClick, favourite) {
                        { onItemLongClick(favourite) }
                    }
                    FavouriteItem(
                        favourite = favourite,
                        onClick = onItemClick,
                        onLongClick = onLongClick
                    )
                }
            )
        }
    )
}

@Composable
private fun FavouriteItem(
    favourite: String,
    onClick: (String) -> Unit,
    onLongClick: () -> Unit
) {
    RemovableCard(
        item = favourite,
        onClick = { onClick(favourite) },
        onLongClick = onLongClick
    )
}
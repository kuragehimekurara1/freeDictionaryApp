/*
 *     freeDictionaryApp/freeDictionaryApp.app.main
 *     MainTopBar.kt Copyrighted by Yamin Siahmargooei at 2023/8/26
 *     MainTopBar.kt Last modified at 2023/8/26
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

package io.github.yamin8000.owl.ui.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Casino
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.History
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.yamin8000.owl.R
import io.github.yamin8000.owl.ui.composable.AppIcon
import io.github.yamin8000.owl.ui.composable.ClickableIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onTopBarClick: (TopBarItem) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        content = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { AppIcon() },
                actions = {
                    ClickableIcon(
                        imageVector = Icons.TwoTone.History,
                        contentDescription = stringResource(R.string.search_history),
                        onClick = { onTopBarClick(TopBarItem.History) },
                    )
                    ClickableIcon(
                        imageVector = Icons.TwoTone.Favorite,
                        contentDescription = stringResource(R.string.favourites),
                        onClick = { onTopBarClick(TopBarItem.Favourites) },
                    )
                    ClickableIcon(
                        imageVector = Icons.TwoTone.Casino,
                        contentDescription = stringResource(R.string.random_word),
                        onClick = { onTopBarClick(TopBarItem.Random) },
                    )
                    ClickableIcon(
                        imageVector = Icons.TwoTone.Settings,
                        contentDescription = stringResource(R.string.settings),
                        onClick = { onTopBarClick(TopBarItem.Settings) }
                    )
                    ClickableIcon(
                        imageVector = Icons.TwoTone.Info,
                        contentDescription = stringResource(R.string.about_app),
                        onClick = { onTopBarClick(TopBarItem.Info) }
                    )
                }
            )
        }
    )
}
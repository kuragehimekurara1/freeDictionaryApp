/*
 *     freeDictionaryApp/freeDictionaryApp.app.main
 *     MainActivity.kt Copyrighted by Yamin Siahmargooei at 2023/8/27
 *     MainActivity.kt Last modified at 2023/8/27
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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import io.github.yamin8000.owl.data.DataStoreRepository
import io.github.yamin8000.owl.data.db.AppDatabase
import io.github.yamin8000.owl.ui.content.favourites.FavouritesContent
import io.github.yamin8000.owl.ui.content.favourites.FavouritesViewModel
import io.github.yamin8000.owl.ui.content.history.HistoryContent
import io.github.yamin8000.owl.ui.content.history.HistoryViewModel
import io.github.yamin8000.owl.ui.content.home.HomeContent
import io.github.yamin8000.owl.ui.content.settings.SettingsContent
import io.github.yamin8000.owl.ui.content.settings.SettingsViewModel
import io.github.yamin8000.owl.ui.content.settings.ThemeSetting
import io.github.yamin8000.owl.ui.favouritesDataStore
import io.github.yamin8000.owl.ui.historyDataStore
import io.github.yamin8000.owl.ui.navigation.Nav
import io.github.yamin8000.owl.ui.settingsDataStore
import io.github.yamin8000.owl.ui.theme.OwlTheme
import io.github.yamin8000.owl.util.Constants
import io.github.yamin8000.owl.util.log
import io.github.yamin8000.owl.util.viewModelFactory
import kotlinx.coroutines.runBlocking

internal class MainActivity : ComponentActivity() {

    private var outsideInput: String? = null

    private var theme: ThemeSetting = ThemeSetting.System

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Constants.db = createDb()
        outsideInput = handleOutsideInputIntent()

        try {
            runBlocking { theme = getCurrentTheme() }
        } catch (e: InterruptedException) {
            log(e.stackTraceToString())
        }

        setContent {
            var currentTheme by remember { mutableStateOf(theme) }
            MainContent(
                currentTheme = currentTheme,
                content = {
                    Scaffold {
                        val onThemeChanged: (ThemeSetting) -> Unit = remember {
                            {
                                currentTheme = it
                            }
                        }
                        MainNav(onThemeChanged = onThemeChanged)
                    }
                }
            )
        }
    }

    private suspend fun getCurrentTheme() = ThemeSetting.valueOf(
        DataStoreRepository(settingsDataStore).getString(Constants.THEME)
            ?: ThemeSetting.System.name
    )

    private fun createDb() = Room.databaseBuilder(this, AppDatabase::class.java, "db")
        //.fallbackToDestructiveMigration()
        .build()

    private fun handleOutsideInputIntent(): String? {
        return if (intent.type == "text/plain") {
            when (intent.action) {
                Intent.ACTION_TRANSLATE, Intent.ACTION_DEFINE, Intent.ACTION_SEND -> {
                    intent.getStringExtra(Intent.EXTRA_TEXT)
                }

                Intent.ACTION_PROCESS_TEXT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                    else null
                }

                else -> null
            }
        } else null
    }

    @Composable
    private fun MainContent(
        currentTheme: ThemeSetting,
        content: @Composable () -> Unit
    ) {
        OwlTheme(
            isDarkTheme = isDarkTheme(currentTheme, isSystemInDarkTheme()),
            isOledTheme = currentTheme == ThemeSetting.Darker,
            isDynamicColor = currentTheme == ThemeSetting.System,
            content = content
        )
    }

    private fun isDarkTheme(
        themeSetting: ThemeSetting,
        isSystemInDarkTheme: Boolean
    ) = when (themeSetting) {
        ThemeSetting.Light -> false
        ThemeSetting.System -> isSystemInDarkTheme
        ThemeSetting.Dark, ThemeSetting.Darker -> true
    }

    @Composable
    private fun MainNav(
        onThemeChanged: (ThemeSetting) -> Unit
    ) {
        val context = LocalContext.current
        val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory {
            initializer {
                SettingsViewModel(DataStoreRepository(context.settingsDataStore))
            }
        })

        val historyVM: HistoryViewModel = viewModel(factory = viewModelFactory {
            initializer {
                HistoryViewModel(context.historyDataStore)
            }
        })

        val favouritesVM: FavouritesViewModel = viewModel(factory = viewModelFactory {
            initializer {
                FavouritesViewModel(context.favouritesDataStore)
            }
        })

        val start = "${Nav.Routes.Home}/{${Nav.Arguments.Search}}"
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = start,
            builder = {
                composable(start) {
                    var searchTerm = it.arguments?.getString(Nav.Arguments.Search.toString())
                    if (searchTerm == null && outsideInput != null)
                        searchTerm = outsideInput.toString()
                    HomeContent(
                        searchTerm = searchTerm,
                        onTopBarClick = { item -> navController.navigate(item.route()) },
                        ttsLang = settingsViewModel.ttsLang.collectAsState().value,
                        isVibrating = settingsViewModel.isVibrating.collectAsState().value,
                        isStartingBlank = settingsViewModel.isStartingBlank.collectAsState().value,
                        onAddToHistory = historyVM::add,
                        onAddToFavourite = favouritesVM::add
                    )
                }

                val onBackClick: () -> Unit = { navController.popBackStack() }

                composable(Nav.Routes.About.toString()) {
                    AboutContent(onBackClick)
                }

                composable(Nav.Routes.Favourites.toString()) {
                    FavouritesContent(
                        onFavouritesItemClick = { favourite -> navController.navigate("${Nav.Routes.Home}/${favourite}") },
                        onBackClick = onBackClick,
                        favourites = favouritesVM.favourites.collectAsState().value.toList(),
                        onRemoveAll = favouritesVM::removeAll,
                        onRemove = favouritesVM::remove
                    )
                }

                composable(Nav.Routes.History.toString()) {
                    HistoryContent(
                        onHistoryItemClick = { history -> navController.navigate("${Nav.Routes.Home}/${history}") },
                        onBackClick = onBackClick,
                        history = historyVM.history.collectAsState().value.toList(),
                        onRemoveAll = historyVM::removeAll,
                        onRemove = historyVM::remove
                    )
                }

                composable(Nav.Routes.Settings.toString()) {
                    SettingsContent(
                        isVibrating = settingsViewModel.isVibrating.collectAsState().value,
                        onVibratingChange = settingsViewModel::updateVibrationSetting,
                        isStartingBlank = settingsViewModel.isStartingBlank.collectAsState().value,
                        onStartingBlankChange = settingsViewModel::updateStartingBlank,
                        themeSetting = settingsViewModel.themeSetting.collectAsState().value,
                        onSystemThemeChange = onThemeChanged,
                        onThemeSettingChange = settingsViewModel::updateThemeSetting,
                        ttsTag = settingsViewModel.ttsLang.collectAsState().value,
                        onTtsTagChange = settingsViewModel::updateTtsLang,
                        onBackClick = onBackClick
                    )
                }
            }
        )
    }
}
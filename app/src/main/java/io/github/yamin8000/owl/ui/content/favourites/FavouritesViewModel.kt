/*
 *     freeDictionaryApp/freeDictionaryApp.app.main
 *     FavouritesViewModel.kt Copyrighted by Yamin Siahmargooei at 2024/5/9
 *     FavouritesViewModel.kt Last modified at 2024/3/23
 *     This file is part of freeDictionaryApp/freeDictionaryApp.app.main.
 *     Copyright (C) 2024  Yamin Siahmargooei
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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class FavouritesViewModel(
    private val favouritesDataStore: DataStore<Preferences>
) : ViewModel() {
    private val scope = viewModelScope

    private var _favourites = MutableStateFlow(setOf<String>())
    val favourites = _favourites.asStateFlow()

    init {
        scope.launch {
            favouritesDataStore.data.collect { preferences ->
                _favourites.value = buildSet {
                    preferences.asMap().forEach { (key) ->
                        add(key.toString())
                    }
                }
            }
        }
    }

    fun remove(
        favourite: String
    ) = scope.launch {
        favouritesDataStore.edit { it.remove(stringPreferencesKey(favourite)) }
        val data = _favourites.value.toMutableSet()
        data.remove(favourite)
        _favourites.value = data
    }

    fun removeAll() = scope.launch {
        favouritesDataStore.edit { it.clear() }
        _favourites.value = emptySet()
    }

    fun add(
        favourite: String
    ) = scope.launch {
        favouritesDataStore.edit {
            it[stringPreferencesKey(favourite)] = favourite
        }
    }
}
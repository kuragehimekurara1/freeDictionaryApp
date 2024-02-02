/*
 *     freeDictionaryApp/freeDictionaryApp.app.main
 *     TopBarItem.kt Copyrighted by Yamin Siahmargooei at 2024/2/2
 *     TopBarItem.kt Last modified at 2024/2/2
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

package io.github.yamin8000.owl.ui.content

internal sealed class TopBarItem {
    data object Info : TopBarItem()
    data object Settings : TopBarItem()
    data object Favourites : TopBarItem()
    data object History : TopBarItem()
    data object Random : TopBarItem()
}
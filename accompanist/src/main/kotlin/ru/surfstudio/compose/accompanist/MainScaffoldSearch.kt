/*
 * Copyright 2021 Surf LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.surfstudio.compose.accompanist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * The main block of the application with topBar which has a built-in search and loader
 *
 * @param modifier Modifier to apply to this layout node.
 * @param contentTitle Content rendered in the topBar body
 * @param contentLoad Content loader (default it CircularProgressIndicator) rendered in the topBar body
 * @param contentLoadState Loader state, true is enable false is disable
 * @param navigationIcon Navigation icon nullable
 * @param navigationBodyIcon If you are not satisfied with the navigationIcon ImageVector
 * @param navigationIconDescription Navigation icon description
 * @param navigationIconOnClick Navigation icon callback click
 * @param searchIcon Search icon, default - Icons.Default.Search
 * @param searchIconDescription Search icon description
 * @param searchListener Callback text for search
 * @param closeSearchListener Callback close search enable
 * @param searchTextColor Color text
 * @param topBarIconColor Color icons
 * @param topBarBackgroundColor Color bg TobBar
 * @param searchDescription Hint show after click search icon
 * @param topBarElevation the elevation of this TopAppBar.
 * @param actions for add custom IconButton-s
 * @param content main body content
 *
 * @since 0.0.3
 * @author Vitaliy Zarubin
 *
 * @see <a href="https://github.com/keygenqt/android-DemoCompose/blob/master/app/src/main/kotlin/com/keygenqt/demo_contacts/modules/catalog/ui/screens/catalogScreen/CatalogBody.kt#L60">CatalogBody.kt#L60</a>
 */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScaffoldSearch(
    modifier: Modifier = Modifier,
    contentTitle: @Composable (() -> Unit)? = null,
    contentLoad: @Composable (() -> Unit)? = null,
    contentLoadState: Boolean = false,
    navigationIcon: ImageVector? = null,
    navigationBodyIcon: @Composable (() -> Unit)? = null,
    navigationIconDescription: String = "Navigate up",
    navigationIconOnClick: () -> Unit = {},
    searchIcon: ImageVector = Icons.Default.Search,
    searchIconDescription: String = "Search",
    searchListener: ((String?) -> Unit)? = null,
    closeSearchListener: (() -> Unit)? = null,
    searchTextColor: Color = MaterialTheme.colors.onPrimary,
    topBarIconColor: Color = MaterialTheme.colors.onPrimary,
    topBarBackgroundColor: Color = MaterialTheme.colors.primary,
    searchDescription: String = "Search...",
    topBarElevation: Dp = AppBarDefaults.TopAppBarElevation,
    actions: @Composable ((RowScope) -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    var isShowSearch by remember { mutableStateOf(false) }
    val state = remember { FieldState() }
    val requester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current



    Scaffold(
        modifier = modifier,
        topBar =
        if (contentTitle == null) {
            {}
        } else {
            {
                TopAppBar(
                    backgroundColor = topBarBackgroundColor,
                    elevation = topBarElevation,
                    title = {
                        Box {
                            if (searchListener != null) {
                                if (isShowSearch) {
                                    if (state.getValue().isEmpty()) {
                                        Text(
                                            fontSize = TextUnit.Unspecified,
                                            text = searchDescription,
                                            color = searchTextColor
                                        )
                                    }
                                    BasicTextField(
                                        singleLine = true,
                                        value = state.text,
                                        onValueChange = { state.text = it },
                                        modifier = Modifier
                                            .focusRequester(requester)
                                            .fillMaxWidth()
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    state.positionToEnd()
                                                }
                                            },
                                        textStyle = MaterialTheme.typography.h5.merge(
                                            TextStyle(
                                                color = searchTextColor
                                            )
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            capitalization = KeyboardCapitalization.Sentences,
                                            imeAction = ImeAction.Search
                                        ),
                                        keyboardActions = KeyboardActions(onSearch = {
                                            focusManager.clearFocus()
                                            searchListener(state.getValue())
                                            softwareKeyboardController?.hide()
                                        }),
                                        cursorBrush = SolidColor(searchTextColor)
                                    )
                                    LaunchedEffect(isShowSearch) {
                                        requester.requestFocus()
                                    }
                                } else {
                                    contentTitle.invoke()
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .padding(end = if (navigationIcon == null) 12.dp else 0.dp)
                                        .fillMaxWidth(),
                                ) {
                                    contentTitle.invoke()
                                }
                            }
                        }
                    },
                    navigationIcon = navigationBodyIcon ?: if (navigationIcon != null) {
                        {
                            IconButton(onClick = navigationIconOnClick) {
                                Icon(
                                    imageVector = navigationIcon,
                                    contentDescription = navigationIconDescription,
                                    tint = topBarIconColor
                                )
                            }
                        }
                    } else null,
                    actions = {
                        if (searchListener != null) {
                            IconButton(onClick = {
                                state.clear()
                                isShowSearch = !isShowSearch
                                if (!isShowSearch) {
                                    searchListener(null)
                                    softwareKeyboardController?.hide()
                                    requester.freeFocus()
                                    closeSearchListener?.invoke()
                                }
                            }) {
                                if (isShowSearch) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = searchIconDescription,
                                        tint = topBarIconColor
                                    )
                                } else {
                                    Icon(
                                        imageVector = searchIcon,
                                        contentDescription = searchIconDescription,
                                        tint = topBarIconColor
                                    )
                                }
                            }
                        }

                        actions?.invoke(this)

                        if (contentLoadState) {
                            contentLoad?.invoke() ?: run {
                                Box(
                                    modifier = Modifier.size(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        color = topBarIconColor,
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },
        content = {
            content.invoke(it)
        },
    )
}
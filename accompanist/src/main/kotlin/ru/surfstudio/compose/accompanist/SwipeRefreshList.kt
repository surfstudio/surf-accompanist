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

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val TAG = "SwipeRefreshList"

/**
 * LazyColumn with embedded SwipeRefresh and set of states for content: Error, Empty, Loading
 *
 * @param modifier Modifier to apply to this layout node.
 * @param items List items.
 * @param listState rememberLazyListState.
 * @param refreshState rememberSwipeRefreshState.
 * @param indicator the indicator that represents the current state. By default this will use a [SwipeRefreshIndicator].
 * @param contentPadding a padding around the whole content.
 * @param contentLoadState loadState  LoadState.Loading / LoadState.Error.
 * @param contentLoading Content screen LoadState.Loading.
 * @param contentEmpty Content screen empty data.
 * @param content Content item model.
 **
 * @since 0.0.3
 * @author Vitaliy Zarubin
 *
 * @see <a href="https://github.com/keygenqt/android-DemoCompose/blob/master/app/src/main/kotlin/com/keygenqt/demo_contacts/modules/favorite/ui/screens/listFavorite/FavoriteBody.kt#L57">FavoriteBody.kt#L57</a>
 */
@Composable
fun <T : Any> SwipeRefreshList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<T>,
    listState: LazyListState = rememberLazyListState(),
    refreshState: SwipeRefreshState = rememberSwipeRefreshState(items.loadState.refresh is LoadState.Loading),
    indicator: @Composable (state: SwipeRefreshState, refreshTrigger: Dp) -> Unit = { s, trigger ->
        SwipeRefreshIndicator(s, trigger)
    },
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 16.dp,
        end = 16.dp,
        bottom = 0.dp
    ),
    contentLoadState: @Composable ((LoadState) -> Unit)? = null,
    contentLoading: @Composable (() -> Unit)? = null,
    contentEmpty: @Composable (() -> Unit)? = null,
    contentError: @Composable (() -> Unit)? = null,
    content: @Composable (Int, T) -> Unit,
) {

    // check show loading
    val isLoading: () -> Boolean = {
        items.loadState.source.append is LoadState.NotLoading
                && items.loadState.source.refresh is LoadState.NotLoading
                && (items.loadState.refresh is LoadState.Loading
                || (items.loadState.append is LoadState.Loading && items.itemCount == 0))
    }

    // check show empty
    val isEmpty: () -> Boolean = {
        items.loadState.source.append is LoadState.NotLoading
                && items.loadState.source.refresh is LoadState.NotLoading
                && items.loadState.refresh is LoadState.NotLoading
                && items.loadState.append is LoadState.NotLoading
                && items.itemCount == 0
    }

    // check show error
    val isError: () -> LoadState.Error? = {
        val error = listOfNotNull(
            items.loadState.source.append as? LoadState.Error,
            items.loadState.source.refresh as? LoadState.Error,
            items.loadState.refresh as? LoadState.Error,
            items.loadState.append as? LoadState.Error,
        ).firstOrNull()
        // exclude cancel
        if (error != null && error.error::class.java.simpleName == "CancelIsolatedRunnerException") {
            null
        } else {
            error
        }
    }

    // val check error
    val isErrorPage = isError.invoke()

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            items.refresh()
        },
        indicator = indicator,
        modifier = modifier
            .fillMaxSize()
    ) {
        if (items.itemCount != 0 && isErrorPage == null) {
            LazyColumn(
                state = listState,
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (items.loadState.refresh !is LoadState.Loading) 1f else 0f)
            ) {
                itemsIndexed(items) { index, item ->
                    if (item != null) {
                        content.invoke(index, item)
                    }
                }
                items.apply {
                    if (loadState.append is LoadState.Loading) {
                        item {
                            if (contentLoadState == null) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            } else {
                                contentLoadState.invoke(LoadState.Loading)
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) { }
        }
    }

    isErrorPage?.apply {
        error.let { Log.e(TAG, it::class.java.simpleName) }
        error.localizedMessage?.let { Log.e(TAG, it) }
        contentError?.invoke()
    } ?: run {
        when {
            isLoading.invoke() -> contentLoading?.invoke()
            isEmpty.invoke() -> contentEmpty?.invoke()
            else -> Unit
        }
    }
}
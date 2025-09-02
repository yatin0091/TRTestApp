package com.webguru.trtest.ui.photolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.webguru.trtest.data.model.Photo
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PhotosRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PhotosViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    PhotosMainScreen(
        modifier = modifier,
        photos = ui.items,
        isRefreshing = ui.isRefreshing,
        isAppending = ui.isAppending,
        error = ui.error,
    ) { viewModel.loadMore() }
}

@Composable
fun PhotosMainScreen(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    isRefreshing: Boolean = false,
    isAppending: Boolean = false,
    error: String? = null,
    loadNextPage: () -> Unit = {}
) {
    val listGridState = rememberLazyGridState()
    LaunchedEffect(listGridState, isAppending, isRefreshing) {
        snapshotFlow {
            val last = listGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = listGridState.layoutInfo.totalItemsCount
            last to total
        }
            .distinctUntilChanged()
            .collect { (last, total) ->
                val nearEnd = total > 0 && last >= total - 6
                if (nearEnd && !isAppending && !isRefreshing) {
                    loadNextPage()
                }
            }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text("Photos")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
            state = listGridState
        ) {
            items(items = photos, key = { it.id }) { photo ->
                PhotoItem(photo)
            }
            item(key = "append-footer") {
                when {
                    error != null -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Couldn't load more: ${error}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = loadNextPage) { Text("Retry") }
                        }
                    }

                    isAppending -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

fun LazyGridState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun PhotoItem(photo: Photo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = CardDefaults.cardColors().disabledContentColor,
            disabledContainerColor = CardDefaults.cardColors().disabledContainerColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = photo.smallUrl,
                contentDescription = photo.id
            )
        }
    }
}
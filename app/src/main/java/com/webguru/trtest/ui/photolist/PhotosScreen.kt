package com.webguru.trtest.ui.photolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.webguru.trtest.data.model.Photo

@Composable
fun PhotosMainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PhotosViewModel = hiltViewModel()
) {
    val photoUiState by viewModel.photoUiState.collectAsStateWithLifecycle()
    when (photoUiState) {
        is PhotoUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoUiState.Error -> Text("Error: ${(photoUiState as PhotoUiState.Error).errorMessage}")
        is PhotoUiState.Success -> PhotosMainScreen(
            modifier,
            (photoUiState as PhotoUiState.Success).photos
        )
    }
}

@Composable
fun PhotosMainScreen(modifier: Modifier = Modifier, photos: List<Photo>) {
    Column(modifier = modifier.fillMaxSize()) {
        Text("Photos")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = photos, key = { it.id }) { photo ->
                PhotoItem(photo)
            }
        }
    }
}

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
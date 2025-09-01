package com.webguru.trtest.ui.photolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.imageLoader
import com.webguru.trtest.data.model.Photo

@Composable
fun PhotosMainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PhotosViewModel = hiltViewModel()
) {
    val photoUiState by viewModel.photoUiState.collectAsStateWithLifecycle()
    when (photoUiState) {
        is PhotoUiState.Loading -> Text("Loading")
        is PhotoUiState.Error -> Text("Error: ${(photoUiState as PhotoUiState.Error).errorMessage}")
        is PhotoUiState.Success ->  PhotosMainScreen(modifier, (photoUiState as PhotoUiState.Success).photos)
    }
}

@Composable
fun PhotosMainScreen(modifier: Modifier = Modifier, photos: List<Photo>) {
    Column(modifier) {
        Text("Photos")
        LazyColumn {
            items(items = photos, key = { it.id }) { photo ->
                PhotoItem(photo)
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo){
//    val imageLoader = LocalContext.current.imageLoader
    AsyncImage(
        model = photo.smallUrl,
        contentDescription = photo.id
    )
}
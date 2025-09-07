package com.webguru.trtest.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchScreen(vm: SearchVm = hiltViewModel()) {
    val s by vm.ui.collectAsStateWithLifecycle()

    Column {
        OutlinedTextField(
            value = s.query,
            onValueChange = vm::onQueryChange,
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )
        if (s.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (s.error != null) Text("Error: ${s.error}", color = Color.Red)
        LazyColumn {
            items(s.items, key = { it.id }) { item ->
                Text(item.name, Modifier.padding(12.dp))
            }
        }
    }
}

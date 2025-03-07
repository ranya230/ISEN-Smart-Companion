package fr.isen.amara.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "History", fontSize = 24.sp)
    }
}

package fr.isen.amara.isensmartcompanion

import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventDetailScreen()
        }
    }
}

@Composable
fun EventDetailScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Event Details", fontSize = 24.sp)
        Text(text = "More information about the selected event.", fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
    }
}


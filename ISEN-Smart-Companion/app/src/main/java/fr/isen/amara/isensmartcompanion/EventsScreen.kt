package fr.isen.amara.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EventsScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Events", fontSize = 24.sp)

        Button(
            onClick = { navController.navigate("eventDetail") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View Event Details")
        }
    }
}

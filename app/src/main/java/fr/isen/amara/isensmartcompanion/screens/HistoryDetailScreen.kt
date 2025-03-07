package fr.isen.amara.isensmartcompanion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import fr.isen.amara.isensmartcompanion.data.Interaction
import fr.isen.amara.isensmartcompanion.data.InteractionViewModel

@Composable
fun HistoryDetailScreen(navController: NavController, backStackEntry: NavBackStackEntry, viewModel: InteractionViewModel = viewModel()) {
    val interactionId = backStackEntry.arguments?.getString("interactionId")?.toIntOrNull()
    val interactions by viewModel.allInteractions.collectAsState(initial = emptyList())
    val interaction = interactions.find { it.id == interactionId }

    Scaffold(
        topBar = { HistoryDetailTopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (interaction == null) {
                Text(
                    text = "Chargement...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            } else {
                HistoryDetailContent(interaction)
            }
        }
    }
}

@Composable
fun HistoryDetailContent(interaction: Interaction) {
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = interaction.question,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFB71C1C)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Date",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(interaction.date),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = interaction.answer,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailTopBar(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
        },
        title = { Text("Détail de l'interaction", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
    )
}

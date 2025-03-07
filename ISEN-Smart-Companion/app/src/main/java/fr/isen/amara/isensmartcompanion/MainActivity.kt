package fr.isen.amara.isensmartcompanion

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import fr.isen.amara.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { MainScreen() }
            composable("events") { EventsScreen(navController) }
            composable("history") { HistoryScreen() }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        val items = listOf("home", "events", "history")
        val icons = listOf("🏠", "🎉", "📜")

        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Text(text = icons[index]) },
                label = { Text(text = screen.replaceFirstChar { it.uppercase() }) },
                selected = false,
                onClick = { navController.navigate(screen) }
            )
        }
    }
}

@Composable
fun MainScreen() {
    val userInput = remember { mutableStateOf("") }
    val aiResponse = remember { mutableStateOf("How can I help you today?") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val logo: Painter = painterResource(id = R.drawable.logo)
        Image(painter = logo, contentDescription = "logo", modifier = Modifier.size(150.dp))

        Text(
            text = "Smart Companion",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        TextField(
            value = userInput.value,
            onValueChange = { userInput.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Ask anything") }
        )

        Button(
            onClick = {
                aiResponse.value = "This is a fake AI response to: ${userInput.value}"
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Send")
        }

        Text(
            text = aiResponse.value,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

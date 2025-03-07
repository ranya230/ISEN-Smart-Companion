package fr.isen.amara.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import fr.isen.amara.isensmartcompanion.data.InteractionViewModel
import fr.isen.amara.isensmartcompanion.navigation.BottomNavItem
import fr.isen.amara.isensmartcompanion.screens.AssistantScreen
import fr.isen.amara.isensmartcompanion.screens.EventsScreen
import fr.isen.amara.isensmartcompanion.screens.HistoryScreen
import fr.isen.amara.isensmartcompanion.screens.AgendaScreen
import fr.isen.amara.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.amara.isensmartcompanion.screens.EventDetailScreen
import fr.isen.amara.isensmartcompanion.screens.HistoryDetailScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.amara.isensmartcompanion.screens.EventsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Events,
        BottomNavItem.Agenda,
        BottomNavItem.History
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val eventsViewModel: EventsViewModel = viewModel()

    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { AssistantScreen() }
        composable(BottomNavItem.Events.route) {
            EventsScreen(navController, eventsViewModel)
        }
        composable(BottomNavItem.History.route) {
            val interactionViewModel: InteractionViewModel = viewModel()
            HistoryScreen(interactionViewModel, navController)
        }
        composable("eventDetail/{eventId}") { backStackEntry ->
            EventDetailScreen(navController, backStackEntry, eventsViewModel)
        }

        composable(BottomNavItem.Agenda.route) { AgendaScreen(navController) }

        composable("historyDetail/{interactionId}") { backStackEntry ->
            HistoryDetailScreen(navController, backStackEntry, viewModel())
        }
    }
}


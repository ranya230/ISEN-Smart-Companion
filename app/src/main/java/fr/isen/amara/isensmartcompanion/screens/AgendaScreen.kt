@file:OptIn(ExperimentalMaterial3Api::class) // Pour utiliser les API expérimentales de Material 3

package fr.isen.amara.isensmartcompanion.screens

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Nouvelle palette de couleurs rouges
val PrimaryColor = Color(0xFFA85959) // Rouge foncé
val SecondaryColor = Color(0xFFB97D8A) // Rouge vif
val BackgroundColor = Color(0xFFF5F5F5) // Blanc cassé
val TextColor = Color(0xFF000000) // Noir
val White = Color(0xFFFFFFFF) // Blanc

// Modèle de données pour un événement
data class Event(
    val title: String,
    val date: String,
    val time: String?,
    val description: String?
)

// Fonction pour sauvegarder les événements dans les préférences partagées
fun saveEventsToPreferences(context: Context, events: List<Event>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(events)
    editor.putString("events_list", json)
    editor.apply()
}

fun loadEventsFromPreferences(context: Context): MutableList<Event> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("events_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("events_list", null)
    val type = object : TypeToken<MutableList<Event>>() {}.type
    return gson.fromJson(json, type) ?: mutableListOf()
}

@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE).format(Date())) }
    val eventList = remember { mutableStateListOf<Event>().apply { addAll(loadEventsFromPreferences(context)) } }
    var showDialog by remember { mutableStateOf(false) }
    var newEventTitle by remember { mutableStateOf("") }
    var newEventTime by remember { mutableStateOf("") }
    var newEventDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AgendaTopBar() },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calendrier
            AndroidView(
                factory = { ctx ->
                    CalendarView(ctx).apply {
                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            val calendar = Calendar.getInstance()
                            calendar.set(year, month, dayOfMonth)
                            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr", "FR"))
                            selectedDate.value = dateFormat.format(calendar.time)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour ajouter un événement avec dégradé et emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryColor, SecondaryColor)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(2.dp, PrimaryColor, RoundedCornerShape(12.dp))
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text("📅 Ajouter un événement", color = White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des événements pour la date sélectionnée
            LazyColumn {
                items(eventList.filter { it.date == selectedDate.value }) { event ->
                    EventItem(event, eventList, coroutineScope, context)
                }
            }
        }
    }

    // Dialog pour ajouter un événement
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = White,
            title = {
                Text(
                    "Ajouter un événement",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = newEventTitle,
                        onValueChange = { newEventTitle = it },
                        label = { Text("Titre de l'événement", color = TextColor) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = White,
                            focusedIndicatorColor = PrimaryColor,
                            unfocusedIndicatorColor = TextColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sélection de l'heure
                    var showTimePicker by remember { mutableStateOf(false) }
                    val context = LocalContext.current

                    Button(
                        onClick = { showTimePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text(
                            text = if (newEventTime.isEmpty()) "⏰ Sélectionner l'heure" else "⏰ Heure : $newEventTime",
                            color = White
                        )
                    }

                    if (showTimePicker) {
                        val calendar = Calendar.getInstance()
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                newEventTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                showTimePicker = false
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newEventDescription,
                        onValueChange = { newEventDescription = it },
                        label = { Text("📝 Description de l'événement", color = TextColor) },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = White,
                            focusedIndicatorColor = PrimaryColor,
                            unfocusedIndicatorColor = TextColor
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEventTitle.isNotEmpty()) {
                            eventList.add(Event(newEventTitle, selectedDate.value, newEventTime, newEventDescription))
                            saveEventsToPreferences(context, eventList)
                            showDialog = false
                            Toast.makeText(context, "Événement ajouté ! 🎉", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Ajouter", color = White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) {
                    Text("Annuler", color = White)
                }
            }
        )
    }
}

@Composable
fun EventItem(event: Event, eventList: MutableList<Event>, coroutineScope: CoroutineScope, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Bouton de suppression avec emoji
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        eventList.remove(event)
                        saveEventsToPreferences(context, eventList)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Supprimer",
                    tint = PrimaryColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "📌 ${event.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextColor)
                event.time?.let { Text(text = "⏰ Heure: $it", color = TextColor) }
                event.description?.let { Text(text = "📝 Description: $it", color = TextColor) }
            }
        }
    }
}

@Composable
fun AgendaTopBar() {
    TopAppBar(
        title = {
            Text("📅 Mon Agenda", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryColor,
            titleContentColor = White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AgendaScreen(navController = NavController(LocalContext.current))
}

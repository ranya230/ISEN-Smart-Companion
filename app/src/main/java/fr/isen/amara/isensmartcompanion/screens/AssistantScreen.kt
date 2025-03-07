package fr.isen.amara.isensmartcompanion.screens

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.amara.isensmartcompanion.data.InteractionViewModel

@Composable
fun AssistantScreen(viewModel: InteractionViewModel = viewModel()) {
    var question by remember { mutableStateOf("") }
    var lastQuestion by remember { mutableStateOf<String?>(null) }
    var aiResponse by remember { mutableStateOf<String?>(null) }
    var isListening by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val generativeModel = GenerativeModel("gemini-1.5-flash", "AIzaSyD14bguW-Xa4EAGqZl8wsidEwU2K_huT3s")

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ISEN", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                    Text("Smart Companion", fontSize = 22.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Text("Voici quelques suggestions :", fontSize = 18.sp, color = Color.Gray)

            val suggestions = listOf(
                "Quels sont les événements à venir ?",
                "Comment m'inscrire à un événement ?",
                "Quels sont mes cours aujourd'hui ?"
            )

            LazyRow {
                items(suggestions) { suggestion ->
                    SuggestionChip(text = suggestion) { question = suggestion }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (lastQuestion != null && aiResponse != null) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
                ) {
                    ResponseCard(lastQuestion!!, aiResponse!!)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question...") },
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = true,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            VoiceSearchButton { voiceInput ->
                question = voiceInput
                isListening = false
            }

            IconButton(
                onClick = {
                    if (question.isNotEmpty()) {
                        isLoading = true
                        val currentQuestion = question
                        question = ""

                        coroutineScope.launch(Dispatchers.IO) {
                            val aiResponseText = getAIResponse(generativeModel, currentQuestion)

                            withContext(Dispatchers.Main) {
                                lastQuestion = currentQuestion
                                aiResponse = aiResponseText
                                isLoading = false
                            }

                            viewModel.insertInteraction(currentQuestion, aiResponseText)
                        }
                    } else {
                        Toast.makeText(context, "Veuillez entrer une question", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFB71C1C), shape = CircleShape)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Envoyer", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFF44336),
            labelColor = Color.White
        ),
        elevation = AssistChipDefaults.assistChipElevation(4.dp)
    )
}

@Composable
fun ResponseCard(question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Vous : $question", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text("IA : $answer", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

private suspend fun getAIResponse(generativeModel: GenerativeModel, input: String): String {
    return try {
        val response = generativeModel.generateContent(input)
        response.text ?: "Aucune réponse obtenue"
    } catch (e: Exception) {
        "Erreur: ${e.message}"
    }
}

@Composable
fun VoiceSearchButton(onVoiceInput: (String) -> Unit) {
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var isListening by remember { mutableStateOf(false) }

    val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            isListening = true
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            isListening = false
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                onVoiceInput(matches[0])
            }
            isListening = false
        }

        override fun onError(error: Int) {
            isListening = false
            Toast.makeText(context, "Erreur de reconnaissance vocale ($error)", Toast.LENGTH_SHORT).show()
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    IconButton(
        onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer.startListening(intent)
        },
        modifier = Modifier
            .size(50.dp)
            .background(if (isListening) Color.Green else Color(0xFFB71C1C), shape = CircleShape)
    ) {
        Icon(imageVector = Icons.Default.Mic, contentDescription = "Recherche vocale", tint = Color.White)
    }
}

@Composable
fun RequestAudioPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Permission audio refusée", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
}

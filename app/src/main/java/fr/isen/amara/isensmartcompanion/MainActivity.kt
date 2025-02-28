
package fr.isen.amara.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import fr.isen.amara.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), content = { MainScreen() })
            }
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
            label = {
                if (userInput.value.isEmpty()) {
                    Text("Ask anything")
                }
            }
        )

        Button(
            onClick = {
                aiResponse.value = "This is a fake AI response to: ${userInput.value}"
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Send")
        }

        // Utilisation de LaunchedEffect pour afficher le Toast
        LaunchedEffect(userInput.value) {
            if (userInput.value.isNotEmpty()) {
                Toast.makeText(context, "Question Submitted", Toast.LENGTH_SHORT).show()
            }
        }

        Text(
            text = aiResponse.value,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
} 

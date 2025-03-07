package fr.isen.amara.isensmartcompanion.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InteractionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HistoryDatabase.getDatabase(application)
    private val interactionDao = db.interactionDao()

    private val _allInteractions = MutableStateFlow<List<Interaction>>(emptyList())
    val allInteractions = _allInteractions.asStateFlow()

    init {
        loadInteractions()
    }

    private fun loadInteractions() {
        viewModelScope.launch(Dispatchers.IO) {
            _allInteractions.value = interactionDao.getAllInteractions()
        }
    }

    fun toggleFavorite(interaction: Interaction) {
        viewModelScope.launch {
            val newFavoriteState = !interaction.isFavorite
            interactionDao.updateFavorite(interaction.id, newFavoriteState) // ✅ Maintenant id est bien un Int
            _allInteractions.value = interactionDao.getAllInteractions() // 🔄 Recharger les données
        }
    }

    fun insertInteraction(question: String, answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val interaction = Interaction(
                question = question,
                answer = answer,
                date = System.currentTimeMillis()
            )
            interactionDao.insertInteraction(interaction)
            loadInteractions() // ✅ Recharge les données après l'insertion
        }
    }

    fun deleteInteraction(interaction: Interaction) {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.deleteInteraction(interaction)
            loadInteractions() // ✅ Recharge les données après la suppression
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.deleteAllInteractions() // ✅ Utilisation de la bonne fonction
            _allInteractions.value = emptyList() // ✅ Met à jour la liste
        }
    }
}

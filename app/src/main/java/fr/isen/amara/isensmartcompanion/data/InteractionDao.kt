package fr.isen.amara.isensmartcompanion.data

import androidx.room.*

@Dao
interface InteractionDao {
    @Insert
    suspend fun insertInteraction(interaction: Interaction): Long

    @Query("SELECT * FROM interactions ORDER BY date DESC")
    suspend fun getAllInteractions(): List<Interaction>
    @Delete
    suspend fun deleteInteraction(interaction: Interaction): Int

    @Query("DELETE FROM interactions")
    suspend fun deleteAllInteractions(): Int

    @Query("SELECT * FROM interactions WHERE question = :question ORDER BY date DESC LIMIT 1")
    suspend fun getLastInteraction(question: String): Interaction?

    @Query("UPDATE interactions SET is_favorite = :isFavorite WHERE id = :interactionId")
    suspend fun updateFavorite(interactionId: Int, isFavorite: Boolean)
}

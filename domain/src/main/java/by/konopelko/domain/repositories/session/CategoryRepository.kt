package by.konopelko.domain.repositories.session

import android.content.Context

interface CategoryRepository {
    suspend fun createDefaultCategories(ownerId: String, titles: ArrayList<String>, context: Context): Boolean
}
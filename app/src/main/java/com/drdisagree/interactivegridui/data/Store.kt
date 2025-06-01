package com.drdisagree.interactivegridui.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.drdisagree.interactivegridui.MyApplication.Companion.appContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.itemDataStore: DataStore<Preferences> by preferencesDataStore(name = "item_store")

val ITEM_LIST_KEY = stringPreferencesKey("item_list_key")

suspend fun saveItems(items: List<Item>) {
    val jsonString = Json.encodeToString(items)

    appContext.itemDataStore.edit { preferences ->
        preferences[ITEM_LIST_KEY] = jsonString
    }
}

suspend fun loadItems(): List<Item> {
    val jsonString = appContext.itemDataStore.data.first()[ITEM_LIST_KEY]

    return if (jsonString != null) {
        Json.decodeFromString(jsonString)
    } else {
        items
    }
}

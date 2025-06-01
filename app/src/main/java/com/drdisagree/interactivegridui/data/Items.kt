package com.drdisagree.interactivegridui.data

import androidx.annotation.DrawableRes
import com.drdisagree.interactivegridui.R
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Item(
    val id: Int = UUID.randomUUID().hashCode(),
    val name: String? = null,
    @DrawableRes val iconResId: Int? = null,
    val packageName: String? = null
)

val EMPTY_ITEM: Item get() = Item()

fun Item.isEmpty(): Boolean = this.name == null

val items: List<Item> = listOf(
    Item(
        name = "Google",
        iconResId = R.drawable.google,
        packageName = "com.google.android.googlequicksearchbox"
    ),
    Item(
        name = "Calendar",
        iconResId = R.drawable.google_calendar,
        packageName = "com.google.android.calendar"
    ),
    EMPTY_ITEM,
    EMPTY_ITEM,
    EMPTY_ITEM,
    EMPTY_ITEM,
    EMPTY_ITEM,
    EMPTY_ITEM,
    Item(
        name = "Docs",
        iconResId = R.drawable.google_docs_editors,
        packageName = "com.google.android.apps.docs.editors.docs"
    ),
    Item(
        name = "Drive",
        iconResId = R.drawable.google_drive,
        packageName = "com.google.android.apps.docs"
    ),
    Item(
        name = "Maps",
        iconResId = R.drawable.google_maps,
        packageName = "com.google.android.apps.maps"
    ),
    Item(
        name = "Meet",
        iconResId = R.drawable.google_meet,
        packageName = "com.google.android.apps.meetings"
    ),
    Item(
        name = "Playstore",
        iconResId = R.drawable.google_play,
        packageName = "com.android.vending"
    ),
    Item(
        name = "Podcasts",
        iconResId = R.drawable.google_podcasts,
        packageName = "com.google.android.apps.podcasts"
    ),
    Item(
        name = "Scholar",
        iconResId = R.drawable.google_scholar,
        packageName = "com.google.android.apps.search.googleapp"
    ),
    Item(
        name = "Youtube",
        iconResId = R.drawable.youtube,
        packageName = "com.google.android.youtube"
    )
)
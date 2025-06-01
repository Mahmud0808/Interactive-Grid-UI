package com.drdisagree.interactivegridui.utils

import android.widget.Toast
import com.drdisagree.interactivegridui.MyApplication.Companion.appContext
import com.drdisagree.interactivegridui.data.EMPTY_ITEM
import com.drdisagree.interactivegridui.data.Item
import com.drdisagree.interactivegridui.data.isEmpty

fun launchApp(packageName: String) {
    val launchIntent = appContext.packageManager.getLaunchIntentForPackage(packageName)
    if (launchIntent != null) {
        appContext.startActivity(launchIntent)
    } else {
        Toast.makeText(appContext, "App not installed", Toast.LENGTH_SHORT).show()
    }
}

fun List<Item>.handleItemReorder(
    fromIndex: Int,
    toIndex: Int,
    topItemSize: Int
): List<Item> {
    val mutableList = toMutableList()

    val isFromTop = fromIndex < topItemSize
    val isToTop = toIndex < topItemSize

    // Remove the item from its original location
    val item = mutableList.removeAt(fromIndex)

    if (isFromTop) {
        // Insert EMPTY_ITEM in original spot if removing from top
        mutableList.add(fromIndex, EMPTY_ITEM)
    }

    if (isToTop) {
        // Add to top section logic
        if (mutableList[toIndex].isEmpty()) {
            // Replace empty directly
            mutableList[toIndex] = item
        } else {
            // Try shifting right to make room
            val rightEmpty = (toIndex + 1 until topItemSize).firstOrNull {
                mutableList[it].isEmpty()
            }
            if (rightEmpty != null) {
                for (i in rightEmpty downTo toIndex + 1) {
                    mutableList[i] = mutableList[i - 1]
                }
                mutableList[toIndex] = item
            } else {
                // Try shifting left
                val leftEmpty = (0 until toIndex).lastOrNull { mutableList[it].isEmpty() }

                if (leftEmpty != null) {
                    for (i in leftEmpty until toIndex) {
                        mutableList[i] = mutableList[i + 1]
                    }
                    mutableList[toIndex] = item
                } else {
                    // No empty space, shift right
                    mutableList.add(toIndex, item)
                }
            }
        }
    } else {
        // Add to bottom section
        mutableList.add(toIndex, item)
    }

    // Clean bottom: remove all EMPTY_ITEMs from bottom
    val cleaned = mutableList.filterIndexed { index, it ->
        !(index >= topItemSize && it.isEmpty())
    }.toMutableList()

    return cleaned
}

fun List<Item>.addItemToTop(index: Int, topItemSize: Int): List<Item> {
    val mutableList = toMutableList()

    // Try to find an empty slot in top section
    val emptyIndex = (0 until topItemSize).firstOrNull { mutableList[it].isEmpty() }

    if (emptyIndex != null) {
        mutableList[emptyIndex] = mutableList.removeAt(index)
    }

    return mutableList
}

fun List<Item>.removeItemFromTop(index: Int): List<Item> {
    val mutableList = toMutableList()

    val removedItem = mutableList[index]
    mutableList[index] = EMPTY_ITEM
    mutableList.add(mutableList.size, removedItem)

    return mutableList
}

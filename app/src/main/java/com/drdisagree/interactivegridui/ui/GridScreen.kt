package com.drdisagree.interactivegridui.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.interactivegridui.data.Item
import com.drdisagree.interactivegridui.data.isEmpty
import com.drdisagree.interactivegridui.data.loadItems
import com.drdisagree.interactivegridui.data.saveItems
import com.drdisagree.interactivegridui.utils.ReorderHapticFeedbackType
import com.drdisagree.interactivegridui.utils.addItemToTop
import com.drdisagree.interactivegridui.utils.handleItemReorder
import com.drdisagree.interactivegridui.utils.launchApp
import com.drdisagree.interactivegridui.utils.rememberReorderHapticFeedback
import com.drdisagree.interactivegridui.utils.removeItemFromTop
import dev.omkartenkale.explodable.Explodable
import dev.omkartenkale.explodable.ExplosionController
import dev.omkartenkale.explodable.rememberExplosionController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Preview
@Composable
fun GridScreen(modifier: Modifier = Modifier) {
    val topItemSize = 8 // Number of items in the top section
    val itemsPerRow = 4
    val haptic = rememberReorderHapticFeedback()
    val coroutineScope = rememberCoroutineScope()

    var list by remember { mutableStateOf<List<Item>>(emptyList()) }
    val lazyGridState = rememberLazyGridState()
    val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        if (list[from.index].isEmpty()) return@rememberReorderableLazyGridState

        list = list.handleItemReorder(
            fromIndex = from.index,
            toIndex = to.index,
            topItemSize = topItemSize
        )

        coroutineScope.launch { saveItems(list) }
        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LaunchedEffect(Unit) {
        list = loadItems()
    }

    fun ReorderableCollectionItemScope.reorderableItemModifier(
        index: Int,
        item: Item
    ): Modifier {
        return Modifier
            .heightIn(min = 96.dp)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Move Before",
                        action = {
                            if (index > 0) {
                                val fromIndex = index
                                val toIndex = index - 1

                                // Disallow moving empty items
                                if (list[fromIndex].isEmpty()) {
                                    return@CustomAccessibilityAction false
                                }
                                if (fromIndex >= topItemSize && toIndex == list.lastIndex) {
                                    return@CustomAccessibilityAction false
                                }

                                list = list.handleItemReorder(
                                    fromIndex = fromIndex,
                                    toIndex = toIndex,
                                    topItemSize = topItemSize
                                )

                                coroutineScope.launch { saveItems(list) }

                                true
                            } else {
                                false
                            }
                        }
                    ),
                    CustomAccessibilityAction(
                        label = "Move After",
                        action = {
                            if (index < list.size - 1) {
                                val fromIndex = index
                                val toIndex = index + 1

                                // Disallow moving empty items
                                if (list[fromIndex].isEmpty()) {
                                    return@CustomAccessibilityAction false
                                }
                                if (fromIndex >= topItemSize && toIndex == list.lastIndex) {
                                    return@CustomAccessibilityAction false
                                }

                                list = list.handleItemReorder(
                                    fromIndex = fromIndex,
                                    toIndex = toIndex,
                                    topItemSize = topItemSize
                                )

                                coroutineScope.launch { saveItems(list) }

                                true
                            } else {
                                false
                            }
                        }
                    )
                )
            }
            .longPressDraggableHandle(
                onDragStarted = {
                    if (!item.isEmpty()) {
                        haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                    }
                },
                onDragStopped = {
                    if (!item.isEmpty()) {
                        haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                    }
                }
            )
            .clearAndSetSemantics { }
    }

    val shouldAnimate: SnapshotStateMap<Int, Boolean> = remember {
        mutableStateMapOf()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(itemsPerRow),
        modifier = modifier
            .padding(top = 24.dp)
            .fillMaxSize(),
        state = lazyGridState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
            val explosionController = rememberExplosionController()
            val itemModifier = if (shouldAnimate[item.id] != false) {
                Modifier.animateItem()
            } else {
                Modifier
            }
            var explodingIndex by remember { mutableIntStateOf(-1) }

            PlaceHolderIndicator(index, topItemSize)
            ReorderableItem(
                state = reorderableLazyGridState,
                key = item.id,
                animateItemModifier = itemModifier
            ) {
                LauncherItem(
                    item = item,
                    isTopItem = index < topItemSize,
                    modifier = reorderableItemModifier(index, item),
                    onActionClick = {
                        if (index < topItemSize) {
                            explodingIndex = index
                            shouldAnimate[item.id] = false
                            explosionController.explode()
                        } else {
                            shouldAnimate[item.id] = true
                            list = list.addItemToTop(index, topItemSize)
                            coroutineScope.launch { saveItems(list) }
                        }
                    },
                    explosionController = explosionController,
                    onExplode = {
                        list = list.removeItemFromTop(explodingIndex)
                        coroutineScope.launch {
                            saveItems(list)

                            delay(100)
                            explosionController.reset()
                            shouldAnimate[item.id] = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceHolderIndicator(index: Int, topItemSize: Int) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .alpha(if (index < topItemSize) 1f else 0f),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(44.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

@Composable
fun LauncherItem(
    item: Item,
    isTopItem: Boolean,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit,
    explosionController: ExplosionController,
    onExplode: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Explodable(
        controller = explosionController,
        onExplode = onExplode
    ) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = {
                        if (isTopItem && !item.isEmpty()) {
                            launchApp(item.packageName!!)
                        }
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (item.iconResId != null && item.name != null) {
                Box {
                    Image(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.name,
                        modifier = Modifier.size(52.dp)
                    )
                    Image(
                        imageVector = if (isTopItem) Icons.Rounded.Remove
                        else Icons.Rounded.Add,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        contentDescription = item.name,
                        modifier = Modifier
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .clickable { onActionClick() }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

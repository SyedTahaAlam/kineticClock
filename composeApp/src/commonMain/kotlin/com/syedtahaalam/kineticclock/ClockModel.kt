package com.syedtahaalam.kineticclock

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

internal data class HandTarget(
    val leftAngle: Float,
    val rightAngle: Float,
    val visible: Boolean = true,
)

internal data class GridTargets(
    val rows: Int,
    val cols: Int,
    val hands: List<HandTarget>,
)

internal val BackgroundColor = Color(0xFF05060A)
internal val HandColor = Color(0xFFE8F6FF)
internal val AccentColor = Color(0xFF5ED8FF)

private val Off = HandTarget(0f, 0f, visible = false)
private val Horizontal = HandTarget(90f, 270f)
private val Vertical = HandTarget(0f, 180f)
private val Slash = HandTarget(45f, 225f)
private val BackSlash = HandTarget(135f, 315f)

private val glyphs: Map<Char, List<List<HandTarget>>> = mapOf(
    '0' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Vertical),
        listOf(Vertical, Off, Vertical),
        listOf(Vertical, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '1' to listOf(
        listOf(Off, Slash, Off),
        listOf(Off, Vertical, Off),
        listOf(Off, Vertical, Off),
        listOf(Off, Vertical, Off),
        listOf(Off, BackSlash, Horizontal),
    ),
    '2' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Off, Off, Vertical),
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Off),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '3' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Off, Off, Vertical),
        listOf(Off, Horizontal, Vertical),
        listOf(Off, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '4' to listOf(
        listOf(Vertical, Off, Vertical),
        listOf(Vertical, Off, Vertical),
        listOf(BackSlash, Horizontal, Vertical),
        listOf(Off, Off, Vertical),
        listOf(Off, Off, Vertical),
    ),
    '5' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Off),
        listOf(BackSlash, Horizontal, Slash),
        listOf(Off, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '6' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Off),
        listOf(Vertical, Horizontal, BackSlash),
        listOf(Vertical, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '7' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Off, Off, Vertical),
        listOf(Off, Slash, Off),
        listOf(Off, Vertical, Off),
        listOf(Off, Vertical, Off),
    ),
    '8' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Vertical),
        listOf(Vertical, Horizontal, Vertical),
        listOf(Vertical, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    '9' to listOf(
        listOf(Slash, Horizontal, BackSlash),
        listOf(Vertical, Off, Vertical),
        listOf(BackSlash, Horizontal, Vertical),
        listOf(Off, Off, Vertical),
        listOf(BackSlash, Horizontal, Slash),
    ),
    ':' to listOf(
        listOf(Off),
        listOf(Vertical),
        listOf(Off),
        listOf(Vertical),
        listOf(Off),
    ),
)

internal fun buildGridTargets(display: String): GridTargets {
    val rows = 5
    val gap = listOf(Off)
    val mergedRows = List(rows) { mutableListOf<HandTarget>() }

    display.forEachIndexed { index, char ->
        val glyph = glyphs[char] ?: glyphs.getValue('0')
        repeat(rows) { row -> mergedRows[row].addAll(glyph[row]) }
        if (index < display.lastIndex) {
            repeat(rows) { row -> mergedRows[row].addAll(gap) }
        }
    }

    val cols = mergedRows.firstOrNull()?.size ?: 0
    val flattened = buildList(rows * cols) {
        repeat(rows) { row -> addAll(mergedRows[row]) }
    }

    return GridTargets(rows = rows, cols = cols, hands = flattened)
}

internal fun normalizedAngle(angle: Float): Float {
    var value = angle % 360f
    if (value < 0) value += 360f
    return value
}

internal fun shortestAngleDelta(current: Float, target: Float): Float {
    val a = normalizedAngle(current)
    val b = normalizedAngle(target)
    val delta = (b - a + 540f) % 360f - 180f
    return if (abs(delta) == 180f) 180f else delta
}

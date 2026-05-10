package com.syedtahaalam.kineticclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun KineticClockApp(modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    var use24Hour by rememberSaveable { mutableStateOf(true) }
    var speed by rememberSaveable { mutableFloatStateOf(0.18f) }

    var tick by remember { mutableLongStateOf(0L) }
    var display by remember { mutableStateOf("00:00:00") }
    var targets by remember { mutableStateOf(buildGridTargets(display)) }

    val leftAngles = remember { mutableListOf<Float>() }
    val rightAngles = remember { mutableListOf<Float>() }
    val opacities = remember { mutableListOf<Float>() }

    fun ensureBuffers(size: Int) {
        while (leftAngles.size < size) {
            leftAngles += 0f
            rightAngles += 0f
            opacities += 0f
        }
        while (leftAngles.size > size) {
            leftAngles.removeAt(leftAngles.lastIndex)
            rightAngles.removeAt(rightAngles.lastIndex)
            opacities.removeAt(opacities.lastIndex)
        }
    }

    LaunchedEffect(use24Hour) {
        while (isActive) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
            display = if (use24Hour) {
                "${now.hour.twoDigits()}:${now.minute.twoDigits()}:${now.second.twoDigits()}"
            } else {
                val hour = when (val h = now.hour % 12) {
                    0 -> 12
                    else -> h
                }
                "${hour.twoDigits()}:${now.minute.twoDigits()}:${now.second.twoDigits()}"
            }
            targets = buildGridTargets(display)
            kotlinx.coroutines.delay(100L)
        }
    }

    LaunchedEffect(targets, speed) {
        ensureBuffers(targets.hands.size)
        while (isActive) {
            withFrameNanos {
                targets.hands.forEachIndexed { index, target ->
                    leftAngles[index] = normalizedAngle(leftAngles[index] + shortestAngleDelta(leftAngles[index], target.leftAngle) * speed)
                    rightAngles[index] = normalizedAngle(rightAngles[index] + shortestAngleDelta(rightAngles[index], target.rightAngle) * speed)
                    val targetOpacity = if (target.visible) 1f else 0.12f
                    opacities[index] += (targetOpacity - opacities[index]) * 0.2f
                }
                tick = it
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(12.dp)
            .focusRequester(focusRequester)
            .focusable()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { use24Hour = !use24Hour })
            }
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                        use24Hour = !use24Hour
                        true
                    }

                    Key.DirectionLeft, Key.DirectionDown -> {
                        speed = (speed - 0.02f).coerceAtLeast(0.08f)
                        true
                    }

                    Key.DirectionRight, Key.DirectionUp -> {
                        speed = (speed + 0.02f).coerceAtMost(0.35f)
                        true
                    }

                    else -> false
                }
            },
    ) {
        val _ = tick
        val cellSize = min(size.width / targets.cols.coerceAtLeast(1), size.height / targets.rows.coerceAtLeast(1))
        val gridWidth = cellSize * targets.cols
        val gridHeight = cellSize * targets.rows
        val originX = (size.width - gridWidth) / 2f
        val originY = (size.height - gridHeight) / 2f
        val radius = cellSize * 0.4f
        val handLength = radius * 0.85f

        targets.hands.forEachIndexed { index, _ ->
            val row = index / targets.cols
            val col = index % targets.cols
            val cx = originX + (col + 0.5f) * cellSize
            val cy = originY + (row + 0.5f) * cellSize
            val center = Offset(cx, cy)
            val alpha = opacities.getOrElse(index) { 0f }
            val stroke = cellSize * 0.08f

            val left = angleToPoint(center, leftAngles.getOrElse(index) { 0f }, handLength)
            val right = angleToPoint(center, rightAngles.getOrElse(index) { 180f }, handLength)

            drawLine(
                color = HandColor.copy(alpha = alpha),
                start = center,
                end = left,
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = AccentColor.copy(alpha = alpha * 0.92f),
                start = center,
                end = right,
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun Int.twoDigits(): String = if (this < 10) "0$this" else "$this"

private fun angleToPoint(center: Offset, angle: Float, length: Float): Offset {
    val radians = Math.toRadians((angle - 90f).toDouble())
    return Offset(
        x = center.x + cos(radians).toFloat() * length,
        y = center.y + sin(radians).toFloat() * length,
    )
}

package com.syedtahaalam.kineticclock

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    var fullScreen by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Kinetic Clock",
        undecorated = fullScreen,
        state = WindowState(placement = if (fullScreen) WindowPlacement.Fullscreen else WindowPlacement.Floating),
        resizable = true,
        onKeyEvent = { event ->
            if (event.type != KeyEventType.KeyDown) return@Window false
            when (event.key) {
                Key.Escape -> {
                    if (fullScreen) {
                        fullScreen = false
                    } else {
                        exitApplication()
                    }
                    true
                }

                Key.F, Key.Enter, Key.NumPadEnter -> {
                    fullScreen = !fullScreen
                    true
                }

                else -> false
            }
        },
    ) {
        DisposableEffect(Unit) {
            val wakeLock = DesktopWakeLock.acquire()
            onDispose { wakeLock.close() }
        }
        KineticClockApp()
    }
}

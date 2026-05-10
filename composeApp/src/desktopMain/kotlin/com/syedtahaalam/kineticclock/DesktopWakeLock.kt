package com.syedtahaalam.kineticclock

import java.io.Closeable
import java.util.Locale

internal object DesktopWakeLock {
    fun acquire(): Closeable {
        val os = System.getProperty("os.name").lowercase(Locale.US)
        val process = when {
            os.contains("mac") -> startProcess(listOf("caffeinate", "-dimsu"))
            os.contains("win") -> startProcess(
                listOf(
                    "powershell",
                    "-NoProfile",
                    "-Command",
                    "Add-Type -Name Native -Namespace KeepAwake -MemberDefinition '[DllImport(\"kernel32.dll\")]public static extern uint SetThreadExecutionState(uint esFlags);'; while(\$true){ [KeepAwake.Native]::SetThreadExecutionState(0x80000002) | Out-Null; Start-Sleep -Seconds 30 }",
                ),
            )

            else -> startProcess(listOf("systemd-inhibit", "--what=idle", "--why=KineticClock", "sleep", "infinity"))
        }

        return Closeable {
            process?.destroy()
        }
    }

    private fun startProcess(command: List<String>): Process? = runCatching {
        ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()
    }.getOrNull()
}

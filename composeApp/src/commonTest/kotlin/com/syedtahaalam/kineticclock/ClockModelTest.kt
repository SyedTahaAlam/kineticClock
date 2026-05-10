package com.syedtahaalam.kineticclock

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClockModelTest {
    @Test
    fun `buildGridTargets creates fixed five rows`() {
        val targets = buildGridTargets("12:34")

        assertEquals(5, targets.rows)
        assertTrue(targets.cols > 0)
        assertEquals(targets.rows * targets.cols, targets.hands.size)
    }

    @Test
    fun `shortestAngleDelta picks shortest path around zero`() {
        assertEquals(20f, shortestAngleDelta(350f, 10f))
        assertEquals(-20f, shortestAngleDelta(10f, 350f))
    }
}

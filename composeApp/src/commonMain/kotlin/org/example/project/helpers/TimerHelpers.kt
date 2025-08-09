package org.example.project.helpers

fun formatTimer(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return if (minutes == 0L) {
        "${seconds.padWith0()}:${((time % 1000)).padWith0().take(2)}"
    } else {
        "${minutes.padWith0()}:${seconds.padWith0()}"
    }
}

fun formatTimeToMinute(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "${minutes.padWith0()}:${seconds.padWith0()}"
}

fun Long.padWith0(length: Int = 2): String {
    return this.toString().padWith0(length)
}

fun String.padWith0(length: Int = 2): String {
    return this.padStart(length, '0')
}
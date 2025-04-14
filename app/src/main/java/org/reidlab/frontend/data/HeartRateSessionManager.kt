package org.reidlab.frontend.data

import android.content.Context
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

class HeartRateSessionManager(private val context: Context) {
    private val recordedValues = mutableListOf<Double>()
    private var startTime: Instant? = null
    private val sessionId: String = UUID.randomUUID().toString()

    fun startSession() {
        recordedValues.clear()
        startTime = Instant.now()
    }

    fun record(value: Double) {
        recordedValues.add(value)
    }

    fun stopAndExportCsv(userId: Int = 1): File? {
        val start = startTime ?: return null
        if (recordedValues.isEmpty()) return null

        val avg = recordedValues.average()
        val max = recordedValues.maxOrNull() ?: avg
        val min = recordedValues.minOrNull() ?: avg
        val formatter = DateTimeFormatter.ISO_INSTANT

        val csvContent = buildString {
            appendLine("session_id,time_started,user_id,avg_rate,max_rate,min_rate")
            appendLine("$sessionId,${formatter.format(start)},$userId,$avg,$max,$min")
        }

        val file = File(context.cacheDir, "heart_session_$sessionId.csv")
        file.writeText(csvContent)
        return file
    }
}

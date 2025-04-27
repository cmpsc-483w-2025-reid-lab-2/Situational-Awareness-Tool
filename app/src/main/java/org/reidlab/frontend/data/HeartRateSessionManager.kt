// SPDX-License-Identifier: MIT
// Copyright (c) 2025 REID Lab 2

package org.reidlab.frontend.data

import android.content.Context
import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

class HeartRateSessionManager(private val context: Context) {
  private val recordedValues = mutableListOf<Int>()
  private var startTime: Instant? = null
  private val sessionId: String = UUID.randomUUID().toString()

  fun startSession() {
    recordedValues.clear()
    startTime = Instant.now()
  }

  fun record(value: Int) {
    Log.d("SessionManager", "Recording HR value: $value")
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
      appendLine("time_started,user_id,avg_rate,max_rate,min_rate")
      appendLine("${formatter.format(start)},$userId,$avg,$max,$min")
    }

    val file = File(context.cacheDir, "heart_session_$sessionId.csv")
    file.writeText(csvContent)

    android.util.Log.d("CSV_EXPORT", "CSV written to: ${file.absolutePath}")
    android.util.Log.d("CSV_EXPORT", "File exists: ${file.exists()}")

    return file
  }

  fun uploadCsvToServer(file: File, endpointUrl: String, onComplete: (Boolean, String) -> Unit) {
    Thread {
        try {
          val boundary = "Boundary-${System.currentTimeMillis()}"
          val connection = URL(endpointUrl).openConnection() as HttpURLConnection

          connection.apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
          }

          val outputStream = DataOutputStream(connection.outputStream)
          val fileName = file.name

          // Write form data headers
          outputStream.writeBytes("--$boundary\r\n")
          outputStream.writeBytes(
            "Content-Disposition: form-data; name=\"heartRateFile\"; filename=\"$fileName\"\r\n"
          )
          outputStream.writeBytes("Content-Type: text/csv\r\n\r\n")

          // Write file content
          outputStream.write(file.readBytes())
          outputStream.writeBytes("\r\n--$boundary--\r\n")
          outputStream.flush()
          outputStream.close()

          val responseCode = connection.responseCode
          if (responseCode == HttpURLConnection.HTTP_OK) {
            onComplete(true, "Upload successful")
          } else {
            onComplete(false, "Upload failed: HTTP $responseCode")
          }
        } catch (e: Exception) {
          onComplete(false, "Upload error: ${e.message}")
        }
      }
      .start()
  }
}

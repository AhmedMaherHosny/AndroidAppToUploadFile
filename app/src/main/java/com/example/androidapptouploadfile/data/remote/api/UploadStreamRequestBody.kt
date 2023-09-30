package com.example.androidapptouploadfile.data.remote.api

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.InputStream

class UploadStreamRequestBody(
    private val mediaType: String,
    private val inputStream: InputStream,
    private val onUploadProgress: (Int, Long, Long) -> Unit,
) : RequestBody() {
    private var startTime: Long = 0

    override fun contentLength(): Long = inputStream.available().toLong()

    override fun contentType(): MediaType? = mediaType.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded = 0L
        startTime = System.currentTimeMillis()
        inputStream.use { inputStream ->
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read)
                uploaded += read
                val progress = (100 * uploaded / contentLength()).toInt()
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime
                val uploadSpeed = if (elapsedTime > 0) (uploaded / elapsedTime) * 1000 else 0L
                val estimatedTimeRemaining =
                    if (uploadSpeed > 0) (contentLength() - uploaded) / uploadSpeed else 0L
                onUploadProgress(progress, uploadSpeed, estimatedTimeRemaining)
            }
        }
    }
}

package com.example.androidapptouploadfile.presentation.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.androidapptouploadfile.R
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadUseCase
import com.example.androidapptouploadfile.presentation.ui.MainActivity
import com.example.androidapptouploadfile.utils.Constants.CHANNEL_ID
import com.example.androidapptouploadfile.utils.Constants.NOTIFICATION_ID
import com.example.androidapptouploadfile.utils.formatSpeed
import com.example.androidapptouploadfile.utils.formatTime
import com.example.androidapptouploadfile.utils.getFileName
import com.example.androidapptouploadfile.utils.getFileSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject


@AndroidEntryPoint
class UploadFileService : LifecycleService() {

    @Inject
    lateinit var uploadUseCase: UploadUseCase

    companion object {
        val progress = MutableLiveData<Int>()
        val uploadSpeed = MutableLiveData<Double>()
        val estimatedTimeRemaining = MutableLiveData<Long>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            UploadFileServiceActions.START.toString() -> startService(uri = intent.data!!)
            UploadFileServiceActions.PAUSE.toString() -> pauseService()
            UploadFileServiceActions.CANCEL.toString() -> cancelService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService(uri: Uri) {
        startForeground(NOTIFICATION_ID, buildNotification(0, 0, -1))
        val contentLength = uri.getFileSize(contentResolver)
        val fileName = uri.getFileName(contentResolver)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE * 1024)
        var currentPosition = 0
        var startByte: Long
        var endByte: Long
        var bytesRead: Int
        var contentRange: String
        var requestBody: RequestBody
        var filePart: MultipartBody.Part
        val startTime = System.currentTimeMillis()
        val bytesUploaded = AtomicLong(0)
        lifecycleScope.launch {
            val inputStream = contentResolver.openInputStream(uri) ?: return@launch
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                startByte = currentPosition.toLong()
                endByte = (currentPosition + bytesRead - 1).toLong()
                contentRange = "bytes $startByte-$endByte/$contentLength"
                requestBody = buffer.copyOfRange(0, bytesRead).toRequestBody()
                filePart = MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    requestBody
                )
                val response = uploadUseCase.uploadFileToServer(
                    contentRange = contentRange,
                    file = filePart
                )
                currentPosition += bytesRead
                bytesUploaded.addAndGet(bytesRead.toLong())
                val progressValue =
                    ((bytesUploaded.get().toDouble() / contentLength.toDouble()) * 100).toInt()
                progress.postValue(progressValue)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = (currentTime - startTime) / 1000.0
                if (elapsedTime > 0) {
                    val currentSpeed = bytesUploaded.get() / elapsedTime
                    uploadSpeed.postValue(currentSpeed)
                    val remainingBytes = contentLength - bytesUploaded.get()
                    val estimatedTimeSec =
                        if (currentSpeed > 0) (remainingBytes / currentSpeed).toLong() else -1
                    estimatedTimeRemaining.postValue(estimatedTimeSec)
                }
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(
                        progress.value ?: 0,
                        uploadSpeed.value?.toLong() ?: 0,
                        estimatedTimeRemaining.value ?: -1
                    )
                )
            }
            inputStream.close()
            cancelService()
        }
    }

    private fun pauseService() {
        // Implement your pause logic here
    }

    private fun cancelService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun getPendingIntent(action: UploadFileServiceActions): PendingIntent {
        val intent = Intent(this, UploadFileService::class.java)
        intent.action = action.toString()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildNotification(
        progress: Int,
        uploadSpeed: Long,
        estimatedTimeRemaining: Long
    ): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("File Upload")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.upload_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, "Start", getPendingIntent(UploadFileServiceActions.START))
            .addAction(0, "Pause", getPendingIntent(UploadFileServiceActions.PAUSE))
            .addAction(0, "Cancel", getPendingIntent(UploadFileServiceActions.CANCEL))
        if (progress < 100) builder.setProgress(100, progress, false)
        else builder.setProgress(0, 0, false)

        if (progress < 100) {
            builder.setSubText(uploadSpeed.formatSpeed())
            if (estimatedTimeRemaining > 0) builder.setContentText(estimatedTimeRemaining.formatTime())
            else builder.setSubText("Time remaining: 1 year")
        }
        return builder.build()
    }

    enum class UploadFileServiceActions {
        START, PAUSE, CANCEL
    }
}

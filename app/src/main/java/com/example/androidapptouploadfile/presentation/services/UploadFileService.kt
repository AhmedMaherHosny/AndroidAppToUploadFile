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
import com.example.androidapptouploadfile.utils.getFileName
import com.example.androidapptouploadfile.utils.getFileSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


@AndroidEntryPoint
class UploadFileService : LifecycleService() {

    @Inject
    lateinit var uploadUseCase: UploadUseCase

    companion object {
        val progress = MutableLiveData<Int>()
        val uploadSpeed = MutableLiveData<Long>()
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
        startForeground(NOTIFICATION_ID, buildNotification())
        val contentLength = uri.getFileSize(contentResolver)
        val fileName = uri.getFileName(contentResolver)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var currentPosition = 0
        var startByte: Long
        var endByte: Long
        var bytesRead: Int
        var contentRange: String
        var requestBody: RequestBody
        var filePart: MultipartBody.Part
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

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("File Upload")
            .setContentText("Uploading file...")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.upload_icon)
            .setProgress(100, 0, false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, "Start", getPendingIntent(UploadFileServiceActions.START))
            .addAction(0, "Pause", getPendingIntent(UploadFileServiceActions.PAUSE))
            .addAction(0, "Cancel", getPendingIntent(UploadFileServiceActions.CANCEL))
            .build()
    }

    enum class UploadFileServiceActions {
        START, PAUSE, CANCEL
    }
}

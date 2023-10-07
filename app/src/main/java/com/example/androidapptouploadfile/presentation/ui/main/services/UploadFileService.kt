package com.example.androidapptouploadfile.presentation.ui.main.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.androidapptouploadfile.R
import com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases.LocalDatastoreUseCases
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadUseCases
import com.example.androidapptouploadfile.presentation.mappers.toUriDetailsDomainModel
import com.example.androidapptouploadfile.presentation.mappers.toUriDetailsUiModel
import com.example.androidapptouploadfile.presentation.models.UriDetailsUiModel
import com.example.androidapptouploadfile.presentation.ui.MainActivity
import com.example.androidapptouploadfile.utils.Constants.CHANNEL_ID
import com.example.androidapptouploadfile.utils.Constants.NOTIFICATION_ID
import com.example.androidapptouploadfile.utils.RetrofitErrorType
import com.example.androidapptouploadfile.utils.formatSpeed
import com.example.androidapptouploadfile.utils.formatTime
import com.example.androidapptouploadfile.utils.generateUUIDv4
import com.example.androidapptouploadfile.utils.getFileName
import com.example.androidapptouploadfile.utils.getFileSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import javax.inject.Inject


@AndroidEntryPoint
class UploadFileService : LifecycleService() {

    @Inject
    lateinit var uploadUseCases: UploadUseCases

    @Inject
    lateinit var localDatastoreUseCases: LocalDatastoreUseCases

    private var isPaused = false
    private var isCanceled = false
    private var uriOfTheFile: Uri? = null


    companion object {
        val progress = MutableStateFlow(0)
        val uploadSpeed = MutableStateFlow(0.0)
        val estimatedTimeRemaining = MutableStateFlow(0L)
        val eventError = MutableSharedFlow<RetrofitErrorType>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            UploadFileServiceActions.START.toString() -> startService(uri = intent.data!!)
            UploadFileServiceActions.PAUSE_RESUME.toString() -> if (!isPaused) pauseService() else resumeService()

            UploadFileServiceActions.CANCEL.toString() -> cancelService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService(uri: Uri) {
        uriOfTheFile = uri
        val contentLength = uri.getFileSize(contentResolver)
        val fileName = uri.getFileName(contentResolver)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uriDetails: UriDetailsUiModel
        var currentPosition: Long
        var startByte: Long
        var endByte: Long
        var bytesRead: Int
        var contentRange: String
        var requestBody: RequestBody
        var filePart: MultipartBody.Part
        val startTime = System.currentTimeMillis()
        lifecycleScope.launch(Dispatchers.IO + retrofitExceptionHandler) {
            uriDetails = initializeUriDetails(uri)
            startByte = uriDetails.startByte ?: 0
            currentPosition = uriDetails.startByte ?: 0
            val inputStream = contentResolver.openInputStream(uri) ?: return@launch
            withContext(Dispatchers.IO) {
                inputStream.skip(startByte)
            }
            while (withContext(Dispatchers.IO) {
                    inputStream.read(buffer)
                }.also { bytesRead = it } != -1) {
                if (isPaused || isCanceled) break
                startByte = currentPosition
                endByte = (currentPosition + bytesRead - 1)
                contentRange = "bytes $startByte-$endByte/$contentLength"
                requestBody = buffer.copyOfRange(0, bytesRead).toRequestBody()
                filePart = MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    requestBody
                )
                uploadUseCases.uploadFileToServerUseCase(
                    contentRange = contentRange,
                    fileIdentifier = uriDetails.fileIdentifier!!,
                    file = filePart
                )
                currentPosition += bytesRead
                progress.value = calculateProgressValue(endByte, contentLength)
                uploadSpeed.value = calculateUploadSpeed(endByte, startTime)
                estimatedTimeRemaining.value = calculateEstimatedTimeRemaining(
                    endByte,
                    contentLength,
                    uploadSpeed.value
                )
                updateUriDetailsModel(
                    uri,
                    uriDetails.fileIdentifier!!,
                    endByte,
                    progress.value
                )
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(
                        progress.value,
                        uploadSpeed.value.toLong(),
                        estimatedTimeRemaining.value,
                        isPaused
                    )
                )
            }
            withContext(Dispatchers.IO) {
                inputStream.close()
            }
            if (!isPaused) {
                cancelService()
            }
        }
    }

    private suspend fun initializeUriDetails(uri: Uri): UriDetailsUiModel {
        var uriDetails = localDatastoreUseCases.readUriModelDetailsForUploadUseCase(uri.toString())
            ?.toUriDetailsUiModel()
        if (uriDetails == null) {
            localDatastoreUseCases.writeUriModelDetailsForUploadUseCase(
                uri.toString(), UriDetailsUiModel(
                    fileIdentifier = generateUUIDv4().toString(),
                    startByte = 0,
                ).toUriDetailsDomainModel()
            )
            uriDetails = localDatastoreUseCases.readUriModelDetailsForUploadUseCase(uri.toString())
                ?.toUriDetailsUiModel()
        } else {
            uriDetails.startByte = uriDetails.startByte ?: 0
        }
        return uriDetails!!
    }

    private fun calculateProgressValue(endByte: Long, contentLength: Long) =
        (((endByte + 1).toDouble() / contentLength.toDouble()) * 100).toInt()

    private fun calculateEstimatedTimeRemaining(
        endByte: Long,
        contentLength: Long,
        currentSpeed: Double
    ): Long {
        val remainingBytes = contentLength - (endByte + 1)
        return if (currentSpeed > 0) {
            (remainingBytes / currentSpeed).toLong()
        } else {
            -1
        }
    }

    private fun calculateUploadSpeed(endByte: Long, startTime: Long): Double {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = (currentTime - startTime) / 1000.0
        return if (elapsedTime > 0) {
            (endByte + 1) / elapsedTime
        } else {
            0.0
        }
    }

    private suspend fun updateUriDetailsModel(
        uri: Uri,
        fileIdentifier: String,
        endByte: Long,
        progress: Int
    ) {
        localDatastoreUseCases.writeUriModelDetailsForUploadUseCase(
            uri.toString(), UriDetailsUiModel(
                fileIdentifier = fileIdentifier,
                startByte = endByte + 1,
                progress = progress
            ).toUriDetailsDomainModel()
        )
    }

    private fun pauseService() {
        isPaused = true
        startForeground(
            NOTIFICATION_ID + 1,
            buildNotification(
                progress.value,
                0,
                -1,
                isPaused
            )
        )
    }

    private fun resumeService() {
        isPaused = false
        startService(uriOfTheFile!!)
    }

    private fun cancelService() {
        isCanceled = true
        lifecycleScope.launch {
            localDatastoreUseCases.deleteUriDetailsAboutUploadUseCase(uriOfTheFile.toString())
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun getPendingIntent(action: UploadFileServiceActions): PendingIntent {
        val intent = Intent(this, UploadFileService::class.java)
        intent.action = action.toString()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildNotification(
        progress: Int,
        uploadSpeed: Long,
        estimatedTimeRemaining: Long,
        isPaused: Boolean
    ): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val pauseResumeLabel = if (isPaused) "Resume" else "Pause"
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("File Upload")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.upload_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, pauseResumeLabel, getPendingIntent(UploadFileServiceActions.PAUSE_RESUME))
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
        START, PAUSE_RESUME, CANCEL
    }

    private val retrofitExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is IOException) {
            setEventError(RetrofitErrorType.NETWORK_ERROR)
        }
    }

    private fun setEventError(event: RetrofitErrorType) {
        lifecycleScope.launch {
            eventError.emit(event)
        }
    }
}

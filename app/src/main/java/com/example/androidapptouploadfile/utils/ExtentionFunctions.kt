package com.example.androidapptouploadfile.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import com.example.androidapptouploadfile.presentation.services.UploadFileService
import java.io.ByteArrayOutputStream


fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", this.packageName, null)
    ).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(it)
    }
}

fun Long.formatTime(): String {
    val minutes = this / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = weeks / 4
    val years = months / 12

    return when {
        years > 0 -> "$years years left"
        months > 0 -> "$months months left"
        weeks > 0 -> "$weeks weeks left"
        days > 0 -> "$days days left"
        hours > 0 -> "$hours hours left"
        minutes > 0 -> "$minutes minutes left"
        else -> "$this seconds left"
    }
}

fun Long.formatSpeed(): String {
    val bytesPerSecond = this.toDouble()

    val kilobytesPerSecond = bytesPerSecond / 1024.0
    val megabytesPerSecond = kilobytesPerSecond / 1024.0
    val gigabytesPerSecond = megabytesPerSecond / 1024.0

    return when {
        gigabytesPerSecond >= 1.0 -> String.format("%.2f GB/s", gigabytesPerSecond)
        megabytesPerSecond >= 1.0 -> String.format("%.2f MB/s", megabytesPerSecond)
        kilobytesPerSecond >= 1.0 -> String.format("%.2f KB/s", kilobytesPerSecond)
        else -> String.format("%.2f B/s", bytesPerSecond)
    }
}

fun Uri.getFileName(contentResolver: ContentResolver): String? {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor: Cursor? = contentResolver.query(this, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

fun Uri.getFileSize(contentResolver: ContentResolver): Long {
    var result: Long = -1
    if (this.scheme == "content") {
        val cursor: Cursor? = contentResolver.query(this, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
            }
        } finally {
            cursor?.close()
        }
    }
    return result
}

fun Uri.readBytes(context: Context): ByteArray? {
    val contentResolver: ContentResolver = context.contentResolver
    try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            val buffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val data = ByteArray(bufferSize)
            var bytesRead: Int
            while (inputStream.read(data, 0, bufferSize).also { bytesRead = it } != -1) {
                buffer.write(data, 0, bytesRead)
            }
            return buffer.toByteArray()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Context.sendCommandToUploadService(data: Uri, action: String) =
    Intent(this, UploadFileService::class.java).also {
        it.action = action
        it.data = data
        this.startService(it)
    }


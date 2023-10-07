package com.example.androidapptouploadfile.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.androidapptouploadfile.presentation.ui.main.services.UploadFileService
import com.example.androidapptouploadfile.presentation.ui.components.ForegroundServicesPermissionTextProvider
import com.example.androidapptouploadfile.presentation.ui.components.PermissionDialog
import com.example.androidapptouploadfile.presentation.ui.components.PostNotificationPermissionTextProvider
import com.example.androidapptouploadfile.presentation.ui.components.ReadExternalStoragePermissionTextProvider
import com.example.androidapptouploadfile.presentation.ui.main.viewmodel.MainViewModel
import com.example.androidapptouploadfile.presentation.ui.main.viewmodel.states.MainScreenState
import com.example.androidapptouploadfile.presentation.ui.main.viewmodel.states.UploadStatus
import com.example.androidapptouploadfile.utils.findActivity
import com.example.androidapptouploadfile.utils.openAppSettings
import com.example.androidapptouploadfile.utils.sendCommandToUploadService


@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val state = viewModel.mainScreenState
    val permissionsToRequest = mutableListOf<String>()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionsToRequest.addAll(
            listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
        )
    }
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )
    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> ReadExternalStoragePermissionTextProvider()
                    Manifest.permission.POST_NOTIFICATIONS -> PostNotificationPermissionTextProvider()
                    Manifest.permission.FOREGROUND_SERVICE -> ForegroundServicesPermissionTextProvider()
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    activity, permission
                ),
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onDismiss = viewModel::dismissDialog,
                onGoToAppSettingsClick = { context.openAppSettings() }
            )
        }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val fileUri = data?.data
            if (fileUri != null) {
                context.sendCommandToUploadService(
                    data = fileUri,
                    action = UploadFileService.UploadFileServiceActions.START.toString()
                )
            } else {
                // handle uri is null
            }
        }
    }
    MainScreenContent(state = state, pickFileLauncher) {
        multiplePermissionResultLauncher.launch(
            permissionsToRequest.toTypedArray()
        )
    }
}


@Composable
fun MainScreenContent(
    state: MainScreenState,
    pickFileLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    launchPermissions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if ((state.uploadStatus == UploadStatus.CANCELED || state.uploadStatus == UploadStatus.COMPLETED) && state.fileName != null) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "upload file",
                        tint = Color.White,
                        modifier = Modifier
                            .rotate(-25f)
                    )
                }
            }
            if (state.uploadStatus == UploadStatus.UPLOADING || state.uploadStatus == UploadStatus.PAUSED) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "cancel the upload",
                        tint = Color.White,
                    )
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (state.uploadStatus == UploadStatus.UPLOADING) {
                Text(
                    text = "Upload speed : ${state.speed}  Mega/Sec",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "time remaining : ${state.timeRemaining}  Min",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "progress : ${state.progress}%",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Button(onClick = {
                launchPermissions()
                // i want to check if storage permission granted or no to open the gallery
                pickFileLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "*/*" })
            }) {
                Text(text = "Choose a file")
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (state.fileName != null) {
                Text(
                    text = "File name : ${state.fileName}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "File Size : ${state.fileSize}  Mega",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}
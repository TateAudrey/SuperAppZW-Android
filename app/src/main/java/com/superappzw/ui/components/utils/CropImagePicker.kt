package com.superappzw.ui.components.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

// ── Gallery picker ────────────────────────────────────────────────────────────

@Composable
fun rememberImagePickerLauncher(
    onImagePicked: (Bitmap) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        uri.toBitmap(context)
            ?.croppedToAspectRatio(4f, 3f)
            ?.downsized(1024, 768)
            ?.let(onImagePicked)
    }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

// ── Camera launcher ───────────────────────────────────────────────────────────
// Mirrors Swift's CropImagePicker(sourceType: .camera)
// Uses TakePicture contract which writes to a temp FileProvider URI,
// then applies the same 4:3 crop and 1024×768 downsize as the gallery picker.

@Composable
fun rememberCameraLauncher(
    onImageCaptured: (Bitmap) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    // Create a stable temp file URI — recreated only when context changes
    val tempUri = remember(context) { context.createTempImageUri() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success: Boolean ->
        if (!success) return@rememberLauncherForActivityResult
        tempUri.toBitmap(context)
            ?.croppedToAspectRatio(4f, 3f)
            ?.downsized(1024, 768)
            ?.let(onImageCaptured)
    }

    return remember(launcher, tempUri) {
        { launcher.launch(tempUri) }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

// Creates a FileProvider URI for the camera to write to.
// Requires a file_paths.xml entry and provider declared in AndroidManifest.xml.
private fun Context.createTempImageUri(): Uri {
    val tempFile = File(cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        tempFile,
    )
}

private fun Uri.toBitmap(context: Context): Bitmap? =
    context.contentResolver
        .openInputStream(this)
        ?.use { BitmapFactory.decodeStream(it) }

// ── Bitmap helpers ────────────────────────────────────────────────────────────

fun Bitmap.croppedToAspectRatio(targetWidth: Float, targetHeight: Float): Bitmap {
    val targetRatio = targetWidth / targetHeight
    val imageRatio = width.toFloat() / height.toFloat()

    val cropX: Int
    val cropY: Int
    val cropWidth: Int
    val cropHeight: Int

    if (imageRatio > targetRatio) {
        cropWidth = (height * targetRatio).toInt()
        cropHeight = height
        cropX = (width - cropWidth) / 2
        cropY = 0
    } else {
        cropWidth = width
        cropHeight = (width / targetRatio).toInt()
        cropX = 0
        cropY = (height - cropHeight) / 2
    }

    return Bitmap.createBitmap(this, cropX, cropY, cropWidth, cropHeight)
}

fun Bitmap.downsized(maxWidth: Int, maxHeight: Int): Bitmap {
    val scale = minOf(
        maxWidth.toFloat() / width.toFloat(),
        maxHeight.toFloat() / height.toFloat(),
        1f,
    )
    if (scale >= 1f) return this
    return Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
}
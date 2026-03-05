package com.superappzw.ui.components.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// ── Image picker launcher ─────────────────────────────────────────────────────
// Mirrors Swift's CropImagePicker — launches the system photo picker,
// then applies a 4:3 center crop and downsize to 1024×768.

@Composable
fun rememberImagePickerLauncher(
    onImagePicked: (Bitmap) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        val bitmap = context.contentResolver
            .openInputStream(uri)
            ?.use { BitmapFactory.decodeStream(it) }
            ?: return@rememberLauncherForActivityResult

        // Step 1: Enforce 4:3 center crop — mirrors croppedToAspectRatio(4, height: 3)
        val cropped = bitmap.croppedToAspectRatio(targetWidth = 4f, targetHeight = 3f)

        // Step 2: Downsize to cap memory — mirrors downsized(to: CGSize(1024, 768))
        val downsized = cropped.downsized(maxWidth = 1024, maxHeight = 768)

        onImagePicked(downsized)
    }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

// ── Bitmap helpers ────────────────────────────────────────────────────────────

// Crops the bitmap to a given aspect ratio from the center.
// Mirrors UIImage.croppedToAspectRatio(_:height:)
fun Bitmap.croppedToAspectRatio(targetWidth: Float, targetHeight: Float): Bitmap {
    val targetRatio = targetWidth / targetHeight
    val imageRatio = width.toFloat() / height.toFloat()

    val cropX: Int
    val cropY: Int
    val cropWidth: Int
    val cropHeight: Int

    if (imageRatio > targetRatio) {
        // Image is wider than target — trim the sides
        cropWidth = (height * targetRatio).toInt()
        cropHeight = height
        cropX = (width - cropWidth) / 2
        cropY = 0
    } else {
        // Image is taller than target — trim top and bottom
        cropWidth = width
        cropHeight = (width / targetRatio).toInt()
        cropX = 0
        cropY = (height - cropHeight) / 2
    }

    return Bitmap.createBitmap(this, cropX, cropY, cropWidth, cropHeight)
}

// Scales the bitmap down to fit within maxWidth/maxHeight while preserving
// aspect ratio. Never upscales. Mirrors UIImage.downsized(to:)
fun Bitmap.downsized(maxWidth: Int, maxHeight: Int): Bitmap {
    val scale = minOf(
        maxWidth.toFloat() / width.toFloat(),
        maxHeight.toFloat() / height.toFloat(),
        1f, // never upscale
    )
    if (scale >= 1f) return this
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}
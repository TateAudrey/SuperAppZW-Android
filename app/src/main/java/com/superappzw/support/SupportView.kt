package com.superappzw.support

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportView(
    currentUserName: String? = null,
    currentUserEmail: String? = null,
    viewModel: SupportViewModel = viewModel(),
) {
    val context = LocalContext.current

    val name        by viewModel.name.collectAsState()
    val email       by viewModel.email.collectAsState()
    val category    by viewModel.category.collectAsState()
    val description by viewModel.description.collectAsState()
    val attachedImages by viewModel.attachedImages.collectAsState()

    val showMailUnavailable by viewModel.showMailUnavailableAlert.collectAsState()
    val showSuccess         by viewModel.showSuccessAlert.collectAsState()
    val errorMessage        by viewModel.errorMessage.collectAsState()

    // Prefill name/email from session on first composition
    remember(currentUserName, currentUserEmail) {
        viewModel.prefill(currentUserName, currentUserEmail)
    }

    // Image picker — up to 3 images
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val bitmaps = uris.mapNotNull { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) { null }
        }
        viewModel.addImages(bitmaps)
    }

    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Support",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp),
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Contact Support",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Text(
                    text = "Having an issue or want to share feedback? Fill in the form below and we'll get back to you as soon as possible.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            }

            // ── Your details ──────────────────────────────────────────────────
            FormSection(title = "YOUR DETAILS") {
                FormTextField(
                    value = name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "Full Name",
                )
                SectionDivider()
                FormTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "Email Address",
                    keyboardType = KeyboardType.Email,
                )
            }

            // ── Issue type ────────────────────────────────────────────────────
            FormSection(title = "ISSUE TYPE") {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                ) {
                    OutlinedTextField(
                        value = category.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        SupportCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    viewModel.onCategoryChange(cat)
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            // ── Description ───────────────────────────────────────────────────
            FormSection(title = "DESCRIPTION") {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (description.isEmpty()) {
                        Text(
                            text = "Describe your issue or feedback in as much detail as possible...",
                            fontSize = 16.sp,
                            color = Color(0xFFBDBDBD),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        )
                    }
                    androidx.compose.material3.TextField(
                        value = description,
                        onValueChange = viewModel::onDescriptionChange,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // ── Attachments ───────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ATTACHMENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp)),
                ) {
                    // Attach button
                    TextButton(
                        onClick = {
                            if (attachedImages.size < 3) imagePicker.launch("image/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Attach Screenshots (max 3)",
                            color = PrimaryColor,
                            fontSize = 15.sp,
                        )
                    }

                    // Thumbnail previews
                    if (attachedImages.isNotEmpty()) {
                        SectionDivider()
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            itemsIndexed(attachedImages) { index, bitmap ->
                                AttachmentThumbnail(
                                    bitmap = bitmap,
                                    onRemove = { viewModel.removeImage(index) },
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Screenshots help us resolve your issue faster.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            // ── Send button ───────────────────────────────────────────────────
            val canSend = viewModel.canSend
            Button(
                onClick = { viewModel.sendEmail(context) },
                enabled = canSend,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = Color.Gray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send to Support",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }

    // ── Mail unavailable alert ────────────────────────────────────────────────
    // Mirrors Swift's "Email Not Available" alert — offers clipboard copy
    if (showMailUnavailable) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMailUnavailable() },
            title = { Text("Email Not Available", style = MaterialTheme.typography.headlineSmall) },
            text  = {
                Text(
                    "No email app is set up on this device. You can reach us directly at ${SupportViewModel.SUPPORT_EMAIL}",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.copyEmailToClipboard(context)
                    viewModel.dismissMailUnavailable()
                }) {
                    Text("Copy Email Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissMailUnavailable() }) {
                    Text("OK")
                }
            },
        )
    }

    // ── Success alert ─────────────────────────────────────────────────────────
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSuccess() },
            title = { Text("Message Sent", style = MaterialTheme.typography.headlineSmall) },
            text  = {
                Text(
                    "Thanks for reaching out. We'll get back to you within 24–48 hours.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissSuccess() }) { Text("Done") }
            },
        )
    }

    // ── Error alert ───────────────────────────────────────────────────────────
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error", style = MaterialTheme.typography.headlineSmall) },
            text  = { Text(errorMessage ?: "", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) { Text("OK") }
            },
        )
    }
}

// ── Attachment thumbnail ──────────────────────────────────────────────────────

@Composable
private fun AttachmentThumbnail(
    bitmap: Bitmap,
    onRemove: () -> Unit,
) {
    Box {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

// ── Form section ──────────────────────────────────────────────────────────────

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp)),
        ) {
            content()
        }
    }
}

// ── Form text field ───────────────────────────────────────────────────────────

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    androidx.compose.material3.TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFBDBDBD)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = androidx.compose.material3.TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Section divider ───────────────────────────────────────────────────────────

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF0F0F0),
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SupportViewPreview() {
    SuperAppZWTheme {
        SupportView(
            currentUserName = "Tatenda Moyo",
            currentUserEmail = "tatenda@example.com",
        )
    }
}

package com.superappzw.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.components.utils.rememberCameraLauncher
import com.superappzw.ui.components.utils.rememberImagePickerLauncher
import com.superappzw.ui.home.province.ProvinceViewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailView(
    onDismiss: () -> Unit = {},
    viewModel: ProfileDetailViewModel = viewModel(),
    provinceViewModel: ProvinceViewModel = viewModel(),
) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val suburb by viewModel.suburb.collectAsState()
    val location by viewModel.location.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val virtualShopName by viewModel.virtualShopName.collectAsState()
    val profileImageURL by viewModel.profileImageURL.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val isFormValid by lazy { viewModel.isFormValid }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val didSaveSuccessfully by viewModel.didSaveSuccessfully.collectAsState()
    val showImageSourceSheet by viewModel.showImageSourceSheet.collectAsState()

    // Load profile + provinces in parallel on first composition
    // Mirrors: async let profile = viewModel.loadProfile()
    //          async let provinces = provinceViewModel.load()
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        provinceViewModel.load()
    }

    // Navigate back on successful save — mirrors .alert dismiss + dismiss()
    LaunchedEffect(didSaveSuccessfully) {
        if (didSaveSuccessfully) onDismiss()
    }

    // Image picker — triggers upload immediately on selection
    // Mirrors .onChange(of: viewModel.selectedProfileImage) { viewModel.uploadProfileImage($0) }
    val pickImage = rememberImagePickerLauncher { bitmap ->
        viewModel.uploadProfileImage(bitmap)
        viewModel.setShowImageSourceSheet(false)
    }

    val takePhoto = rememberCameraLauncher { bitmap ->
        viewModel.uploadProfileImage(bitmap)
        viewModel.setShowImageSourceSheet(false)
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                },
                actions = {
                    // Save button — disabled when form is invalid or saving
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 16.dp),
                        )
                    } else {
                        TextButton(
                            onClick = { viewModel.saveProfile() },
                            enabled = viewModel.isFormValid,
                        ) {
                            Text(
                                text = "Save",
                                color = if (viewModel.isFormValid) PrimaryColor else Color.Gray,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
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
                .padding(vertical = 20.dp),
        ) {

            // ── Avatar header ─────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                AvatarView(
                    imageURL = profileImageURL,
                    initials = viewModel.initials,
                    isUploading = isUploadingImage,
                    onTap = { viewModel.setShowImageSourceSheet(true) },
                )
            }

            // ── Personal info ─────────────────────────────────────────────────
            FormSection(title = "PERSONAL INFO") {
                ProfileTextField(
                    value = firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    placeholder = "First Name",
                )
                SectionDivider()
                ProfileTextField(
                    value = lastName,
                    onValueChange = viewModel::onLastNameChange,
                    placeholder = "Last Name",
                )
                SectionDivider()
                ProfileTextField(
                    value = suburb,
                    onValueChange = viewModel::onSuburbChange,
                    placeholder = "Suburb",
                )
                SectionDivider()
                ProfileTextField(
                    value = phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    placeholder = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                )
            }

            // ── Location ──────────────────────────────────────────────────────
            val provinces by provinceViewModel.provinces.collectAsState()
            val isLoadingProvinces by provinceViewModel.isLoading.collectAsState()
            var showProvinceDropdown by remember { mutableStateOf(false) }

            FormSection(title = "LOCATION") {
                ExposedDropdownMenuBox(
                    expanded = showProvinceDropdown,
                    onExpandedChange = { if (!isLoadingProvinces) showProvinceDropdown = it },
                ) {
                    OutlinedTextField(
                        value = location.ifBlank { if (isLoadingProvinces) "Loading..." else "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Province") },
                        trailingIcon = {
                            if (isLoadingProvinces) {
                                CircularProgressIndicator(
                                    color = PrimaryColor,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp),
                                )
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProvinceDropdown)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = showProvinceDropdown,
                        onDismissRequest = { showProvinceDropdown = false },
                    ) {
                        provinces.forEach { province ->
                            DropdownMenuItem(
                                text = { Text(province, fontSize = 14.sp) },
                                onClick = {
                                    viewModel.onLocationChange(province)
                                    provinceViewModel.selectProvince(province)
                                    showProvinceDropdown = false
                                },
                                trailingIcon = if (province == location) {
                                    { Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp)) }
                                } else null,
                            )
                        }
                    }
                }
            }

            // ── Virtual shop ──────────────────────────────────────────────────
            FormSection(title = "VIRTUAL SHOP") {
                ProfileTextField(
                    value = virtualShopName,
                    onValueChange = viewModel::onVirtualShopNameChange,
                    placeholder = "Virtual Shop Name",
                )
            }
        }
    }

    // ── Image source sheet ────────────────────────────────────────────────────
    // Mirrors .confirmationDialog("Update Profile Photo", ...)
    if (showImageSourceSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowImageSourceSheet(false) },
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Update Profile Photo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Button(
                    onClick = {
                        viewModel.setShowImageSourceSheet(false)
                        takePhoto()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text("Take Photo", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        viewModel.setShowImageSourceSheet(false)
                        pickImage()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E5EA),
                        contentColor = Color.Black,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text("Choose from Library", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { viewModel.setShowImageSourceSheet(false) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }

    // ── Error alert ───────────────────────────────────────────────────────────
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error") },
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("OK")
                }
            },
        )
    }

    // ── Save success alert ────────────────────────────────────────────────────
    if (didSaveSuccessfully) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Profile Saved") },
            text = { Text("Your profile has been updated successfully.") },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("OK") }
            },
        )
    }
}

// ── Form section ──────────────────────────────────────────────────────────────

@Composable
private fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
        ) {
            content()
        }
    }
}

// ── Profile text field ────────────────────────────────────────────────────────

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = Color(0xFFBDBDBD))
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
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

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ProfileDetailViewPreview() {
    SuperAppZWTheme {
        ProfileDetailView()
    }
}
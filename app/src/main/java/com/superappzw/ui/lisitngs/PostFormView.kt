package com.superappzw.ui.lisitngs

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.superappzw.ui.categories.CategoryItem
import com.superappzw.ui.components.utils.rememberCameraLauncher
import com.superappzw.ui.components.utils.rememberImagePickerLauncher
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFormView(
    selectedCategory: CategoryItem,
    onCategorySelected: (CategoryItem) -> Unit,
    selectedCurrency: ListingCurrency,
    onCurrencySelected: (ListingCurrency) -> Unit,
    productName: String,
    onProductNameChange: (String) -> Unit,
    priceText: String,
    onPriceTextChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedImage: Bitmap?,
    onImageSelected: (Bitmap?) -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFormValid = productName.isNotBlank() &&
            description.isNotBlank() &&
            priceText.isNotBlank() &&
            selectedImage != null

    var showImageSourceSheet by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    // Gallery picker — same as before
    val pickImage = rememberImagePickerLauncher { bitmap ->
        onImageSelected(bitmap)
        showImageSourceSheet = false
    }

    // Camera launcher — new
    val takePhoto = rememberCameraLauncher { bitmap ->
        onImageSelected(bitmap)
        showImageSourceSheet = false
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 120.dp),
    ) {

        // ── Image picker ──────────────────────────────────────────────────────
        FormSection(title = "PHOTO") {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE5E5EA))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showImageSourceSheet = true },
                    ),
            ) {
                if (selectedImage != null) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = "Change",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddPhotoAlternate,
                            contentDescription = "Add photo",
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Tap to add image",
                            fontSize = 14.sp,
                            color = Color(0xFFBDBDBD),
                        )
                    }
                }
            }
        }

        // ── Listing details ───────────────────────────────────────────────────
        FormSection(title = "LISTING DETAILS") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "Listing Type",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                )
                Text(text = "Product", fontSize = 15.sp, color = Color.Gray)
            }

            SectionDivider()

            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = it },
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false },
                ) {
                    CategoryItem.all.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name, fontSize = 14.sp) },
                            onClick = {
                                onCategorySelected(category)
                                showCategoryDropdown = false
                            },
                        )
                    }
                }
            }

            SectionDivider()

            FormTextField(
                placeholder = "Product Name",
                value = productName,
                onValueChange = onProductNameChange,
            )
        }

        // ── Pricing ───────────────────────────────────────────────────────────
        FormSection(title = "PRICING") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
            ) {
                ExposedDropdownMenuBox(
                    expanded = showCurrencyDropdown,
                    onExpandedChange = { showCurrencyDropdown = it },
                    modifier = Modifier.width(130.dp),
                ) {
                    OutlinedTextField(
                        value = selectedCurrency.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        modifier = Modifier.menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = showCurrencyDropdown,
                        onDismissRequest = { showCurrencyDropdown = false },
                    ) {
                        ListingCurrency.entries.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency.label) },
                                onClick = {
                                    onCurrencySelected(currency)
                                    showCurrencyDropdown = false
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = onPriceTextChange,
                    placeholder = { Text("0.00", color = Color(0xFFBDBDBD)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Description ───────────────────────────────────────────────────────
        FormSection(title = "DESCRIPTION") {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { Text("Describe your listing...", color = Color(0xFFBDBDBD)) },
                minLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // ── Notice ────────────────────────────────────────────────────────────
        Text(
            text = "Please ensure your listing complies with our community guidelines and terms of service.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        // ── Publish button ────────────────────────────────────────────────────
        Button(
            onClick = onPublish,
            enabled = isFormValid,
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = Color.White,
                disabledContainerColor = PrimaryColor.copy(alpha = 0.4f),
                disabledContentColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Publish Listing", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    // ── Image source sheet ────────────────────────────────────────────────────
    if (showImageSourceSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showImageSourceSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
            ) {
                Text(
                    text = "Add Photo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                // Take photo — camera
                Button(
                    onClick = {
                        showImageSourceSheet = false
                        takePhoto()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                ) {
                    Text("Take Photo", fontWeight = FontWeight.SemiBold)
                }

                // Choose from library
                Button(
                    onClick = {
                        showImageSourceSheet = false
                        pickImage()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E5EA),
                        contentColor = Color.Black,
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                ) {
                    Text("Choose from Library", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { showImageSourceSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
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
        modifier = modifier.fillMaxWidth(),
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
                .background(Color.White, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
        ) {
            content()
        }
    }
}

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
private fun PostFormViewPreview() {
    SuperAppZWTheme {
        PostFormView(
            selectedCategory = CategoryItem.all.first(),
            onCategorySelected = {},
            selectedCurrency = ListingCurrency.USD,
            onCurrencySelected = {},
            productName = "",
            onProductNameChange = {},
            priceText = "",
            onPriceTextChange = {},
            description = "",
            onDescriptionChange = {},
            selectedImage = null,
            onImageSelected = {},
            onPublish = {},
            modifier = Modifier.background(Color(0xFFF2F2F7)),
        )
    }
}
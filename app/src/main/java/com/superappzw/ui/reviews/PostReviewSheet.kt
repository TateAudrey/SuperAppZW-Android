package com.superappzw.ui.reviews

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostReviewSheet(
    storeOwnerUID: String,
    hasReviewed: Boolean = false,
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    submitSuccess: Boolean = false,
    onSubmit: (rating: Int, reviewText: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    val canSubmit = selectedRating > 0 && reviewText.trim().isNotEmpty()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    // Auto-dismiss on success
    if (submitSuccess) {
        scope.launch { sheetState.hide() }.also { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {

            // ── Drag handle ───────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFBDBDBD)),
                )
            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text = "Write a Review",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
            )

            // ── Star rating ───────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Your Rating",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (star in 1..5) {
                        val isSelected = star <= selectedRating

                        val scale by animateFloatAsState(
                            targetValue = if (star == selectedRating) 1.2f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                            label = "starScale_$star",
                        )

                        IconButton(
                            onClick = { selectedRating = star },
                            modifier = Modifier
                                .size(48.dp)
                                .scale(scale),
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Star
                                else Icons.Outlined.StarOutline,
                                contentDescription = "$star stars",
                                tint = if (isSelected) Color(0xFFFFCC00) else Color(0xFFBDBDBD),
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                }
            }

            // ── Review text ───────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Your Review",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                )

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = {
                        Text(
                            text = "Share your experience...",
                            color = Color(0xFFBDBDBD),
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color(0xFFF2F2F7),
                        unfocusedContainerColor = Color(0xFFF2F2F7),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6,
                )
            }

            // ── Error message ─────────────────────────────────────────────────
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    color = Color.Red,
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // ── Submit button ─────────────────────────────────────────────────
            Button(
                onClick = { onSubmit(selectedRating, reviewText) },
                enabled = canSubmit && !isSubmitting,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canSubmit) PrimaryColor else Color(0xFFBDBDBD),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBDBDBD),
                    disabledContentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(
                        text = if (hasReviewed) "Update Review" else "Submit Review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PostReviewSheetPreview() {
    SuperAppZWTheme {
        // Render the sheet content directly for preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFBDBDBD)),
                )
            }

            Text(
                text = "Write a Review",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Your Rating", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (star in 1..5) {
                        Icon(
                            imageVector = if (star <= 3) Icons.Filled.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (star <= 3) Color(0xFFFFCC00) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Your Review", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.Gray)
                OutlinedTextField(
                    value = "Great service and fast delivery!",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedContainerColor = Color(0xFFF2F2F7),
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
            }

            Button(
                onClick = {},
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) {
                Text(text = "Submit Review", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
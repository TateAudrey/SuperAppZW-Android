package com.superappzw.ui.lisitngs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun FormPickerRow(
    label: String,
    value: String,
    isDisabled: Boolean = false,
    trailingIcon: ImageVector = Icons.Filled.UnfoldMore, // chevron.up.chevron.down equivalent
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isDisabled) 0.4f else 1f)
            .shadow(
                elevation = if (isDisabled) 0.dp else 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isDisabled) Color(0xFFF2F2F7).copy(alpha = 0.5f) else Color.White
            )
            .clickable(
                enabled = !isDisabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDisabled) Color.Gray else Color.Black,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.Gray,
        )

        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(14.dp),
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun FormPickerRowPreview() {
    SuperAppZWTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            FormPickerRow(
                label = "Package",
                value = "Standard",
                onClick = {},
            )
            FormPickerRow(
                label = "Category",
                value = "Electronics",
                isDisabled = true,
                onClick = {},
            )
        }
    }
}
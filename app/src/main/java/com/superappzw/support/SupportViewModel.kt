package com.superappzw.support

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ── Support categories ────────────────────────────────────────────────────────

enum class SupportCategory(val label: String) {
    GENERAL("General Enquiry"),
    LISTING_ISSUE("Listing Issue"),
    ACCOUNT("Account Issue"),
    BUG_REPORT("Bug Report"),
    FEEDBACK("Feedback"),
    OTHER("Other"),
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class SupportViewModel : ViewModel() {

    companion object {
        const val SUPPORT_EMAIL = "superappzw@outlook.com"
    }

    // ── Form fields ───────────────────────────────────────────────────────────

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _category = MutableStateFlow(SupportCategory.GENERAL)
    val category: StateFlow<SupportCategory> = _category.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    // ── Attachments ───────────────────────────────────────────────────────────

    private val _attachedImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val attachedImages: StateFlow<List<Bitmap>> = _attachedImages.asStateFlow()

    // ── UI state ──────────────────────────────────────────────────────────────

    private val _showMailUnavailableAlert = MutableStateFlow(false)
    val showMailUnavailableAlert: StateFlow<Boolean> = _showMailUnavailableAlert.asStateFlow()

    private val _showSuccessAlert = MutableStateFlow(false)
    val showSuccessAlert: StateFlow<Boolean> = _showSuccessAlert.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ── Field updaters ────────────────────────────────────────────────────────

    fun onNameChange(value: String)        { _name.value = value }
    fun onEmailChange(value: String)       { _email.value = value }
    fun onCategoryChange(value: SupportCategory) { _category.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }

    // ── Prefill from current user ─────────────────────────────────────────────

    fun prefill(userName: String?, userEmail: String?) {
        if (!userName.isNullOrBlank())  _name.value  = userName
        if (!userEmail.isNullOrBlank()) _email.value = userEmail
    }

    // ── Attachments ───────────────────────────────────────────────────────────

    fun addImages(bitmaps: List<Bitmap>) {
        val current = _attachedImages.value.toMutableList()
        val remaining = 3 - current.size
        current.addAll(bitmaps.take(remaining))
        _attachedImages.value = current
    }

    fun removeImage(index: Int) {
        val current = _attachedImages.value.toMutableList()
        if (index < current.size) {
            current.removeAt(index)
            _attachedImages.value = current
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    val canSend: Boolean
        get() = _name.value.isNotBlank() &&
                _email.value.isNotBlank() &&
                _description.value.isNotBlank()

    // ── Email subject / body ──────────────────────────────────────────────────

    val emailSubject: String
        get() = "[${_category.value.label}] Support Request from ${_name.value}"

    val emailBody: String
        get() = """
            Name: ${_name.value}
            Email: ${_email.value}
            Category: ${_category.value.label}

            ---

            ${_description.value}

            ---
            Sent from Super App ZW
        """.trimIndent()

    // ── Send via Intent ───────────────────────────────────────────────────────
    // On Android we use an email Intent instead of MFMailComposeViewController.
    // If no email app is installed, we show the "mail unavailable" alert.

    fun sendEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL,   arrayOf(SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT,    emailBody)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Send Support Email"))
            _showSuccessAlert.value = true
        } else {
            _showMailUnavailableAlert.value = true
        }
    }

    fun copyEmailToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", SUPPORT_EMAIL))
    }

    // ── Alert dismissals ──────────────────────────────────────────────────────

    fun dismissMailUnavailable() { _showMailUnavailableAlert.value = false }
    fun dismissSuccess()         { _showSuccessAlert.value = false }
    fun dismissError()           { _errorMessage.value = null }
}
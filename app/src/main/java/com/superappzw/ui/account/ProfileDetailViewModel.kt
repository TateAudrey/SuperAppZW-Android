package com.superappzw.ui.account

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel : ViewModel() {

    // ── Profile fields ────────────────────────────────────────────────────────

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _suburb = MutableStateFlow("")
    val suburb: StateFlow<String> = _suburb.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _virtualShopName = MutableStateFlow("")
    val virtualShopName: StateFlow<String> = _virtualShopName.asStateFlow()

    private val _profileImageURL = MutableStateFlow<String?>(null)
    val profileImageURL: StateFlow<String?> = _profileImageURL.asStateFlow()

    // ── UI state ──────────────────────────────────────────────────────────────

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _didSaveSuccessfully = MutableStateFlow(false)
    val didSaveSuccessfully: StateFlow<Boolean> = _didSaveSuccessfully.asStateFlow()

    // ── Image picker state ────────────────────────────────────────────────────

    private val _showImageSourceSheet = MutableStateFlow(false)
    val showImageSourceSheet: StateFlow<Boolean> = _showImageSourceSheet.asStateFlow()

    // ── Internals ─────────────────────────────────────────────────────────────

    private val userService = UserService.getInstance()
    private var hasLoaded = false

    // ── Computed properties ───────────────────────────────────────────────────

    val isFormValid: Boolean
        get() = _firstName.value.isNotBlank() &&
                _lastName.value.isNotBlank() &&
                _phoneNumber.value.isNotBlank() &&
                _location.value.isNotBlank() &&
                _suburb.value.isNotBlank() &&
                _virtualShopName.value.isNotBlank()

    val fullName: String
        get() = "${_firstName.value} ${_lastName.value}".trim()

    val initials: String
        get() {
            val f = _firstName.value.take(1).uppercase()
            val l = _lastName.value.take(1).uppercase()
            return if (f.isEmpty()) "?" else "$f$l"
        }

    // ── Field updaters (called from UI) ───────────────────────────────────────

    fun onFirstNameChange(value: String) { _firstName.value = value }
    fun onLastNameChange(value: String) { _lastName.value = value }
    fun onSuburbChange(value: String) { _suburb.value = value }
    fun onLocationChange(value: String) { _location.value = value }
    fun onPhoneNumberChange(value: String) { _phoneNumber.value = value }
    fun onVirtualShopNameChange(value: String) { _virtualShopName.value = value }
    fun setShowImageSourceSheet(show: Boolean) { _showImageSourceSheet.value = show }

    // ── Load profile ──────────────────────────────────────────────────────────

    fun loadProfile() {
        if (hasLoaded) return           // guard against re-fetch on recomposition
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = userService.fetchProfile(uid = uid)
                _firstName.value = profile.firstName
                _lastName.value = profile.lastName
                _suburb.value = profile.suburb
                _location.value = profile.location
                _phoneNumber.value = profile.phoneNumber
                _virtualShopName.value = profile.virtualShopName
                _profileImageURL.value = profile.profileImageURL
                hasLoaded = true        // mark loaded so returning from picker doesn't re-fetch
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Save profile ──────────────────────────────────────────────────────────

    fun saveProfile() {
        if (!isFormValid) {
            _errorMessage.value = "Please fill in all fields to complete your profile."
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _errorMessage.value = "No authenticated user found."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                userService.updateProfile(
                    uid = uid,
                    firstName = _firstName.value,
                    lastName = _lastName.value,
                    suburb = _suburb.value,
                    location = _location.value,
                    phoneNumber = _phoneNumber.value,
                    virtualShopName = _virtualShopName.value,
                )
                _didSaveSuccessfully.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    // ── Upload profile image ──────────────────────────────────────────────────

    fun uploadProfileImage(bitmap: Bitmap) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                // Crop, resize, upload, and write URL to Firestore — all in UserService
                val downloadURL = userService.uploadProfileImage(uid = uid, bitmap = bitmap)

                // Update local URL so the avatar refreshes immediately
                _profileImageURL.value = downloadURL
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    // ── Dismiss error ─────────────────────────────────────────────────────────

    fun dismissError() { _errorMessage.value = null }
}
package com.superappzw.ui.lisitngs

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.StoreReviewModel
import com.superappzw.services.ListingService
import com.superappzw.services.PostingEligibilityResult
import com.superappzw.services.PostingEligibilityService
import com.superappzw.services.ReviewService
import com.superappzw.services.UserService
import com.superappzw.ui.categories.CategoryItem
import com.superappzw.ui.components.utils.AppAlertType
import com.superappzw.ui.store.StoreListing
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyListingsViewModel : ViewModel() {

    // ── Tab & form state ──────────────────────────────────────────────────────

    private val _selectedTab = MutableStateFlow(ListingTab.PRODUCTS)
    val selectedTab: StateFlow<ListingTab> = _selectedTab.asStateFlow()

    private val _selectedListingType = MutableStateFlow(ListingType.PRODUCT)
    val selectedListingType: StateFlow<ListingType> = _selectedListingType.asStateFlow()

    private val _selectedCategory = MutableStateFlow(CategoryItem.all[0])
    val selectedCategory: StateFlow<CategoryItem> = _selectedCategory.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(ListingCurrency.USD)
    val selectedCurrency: StateFlow<ListingCurrency> = _selectedCurrency.asStateFlow()

    private val _productName = MutableStateFlow("")
    val productName: StateFlow<String> = _productName.asStateFlow()

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _selectedImage = MutableStateFlow<Bitmap?>(null)
    val selectedImage: StateFlow<Bitmap?> = _selectedImage.asStateFlow()

    private val _showCategoryPicker = MutableStateFlow(false)
    val showCategoryPicker: StateFlow<Boolean> = _showCategoryPicker.asStateFlow()

    private val _showListingTypePicker = MutableStateFlow(false)
    val showListingTypePicker: StateFlow<Boolean> = _showListingTypePicker.asStateFlow()

    private val _showCurrencyPicker = MutableStateFlow(false)
    val showCurrencyPicker: StateFlow<Boolean> = _showCurrencyPicker.asStateFlow()

    // ── User location (fetched from profile, used when publishing) ────────────
    private val _userLocation = MutableStateFlow("")
    val userLocation: StateFlow<String> = _userLocation.asStateFlow()

    // ── Form: location & negotiable ───────────────────────────────────────────────
    private val _selectedLocation = MutableStateFlow("")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _isNegotiable = MutableStateFlow(false)
    val isNegotiable: StateFlow<Boolean> = _isNegotiable.asStateFlow()

    // ── Listings ──────────────────────────────────────────────────────────────

    private val _myProducts = MutableStateFlow<List<StoreListing>>(emptyList())
    val myProducts: StateFlow<List<StoreListing>> = _myProducts.asStateFlow()

    private val _myServices = MutableStateFlow<List<String>>(emptyList())
    val myServices: StateFlow<List<String>> = _myServices.asStateFlow()

    private val _selectedListing = MutableStateFlow<StoreListing?>(null)
    val selectedListing: StateFlow<StoreListing?> = _selectedListing.asStateFlow()

    // ── Reviews ───────────────────────────────────────────────────────────────

    private val _receivedReviews = MutableStateFlow<List<StoreReviewModel>>(emptyList())
    val receivedReviews: StateFlow<List<StoreReviewModel>> = _receivedReviews.asStateFlow()

    private val _isLoadingReviews = MutableStateFlow(false)
    val isLoadingReviews: StateFlow<Boolean> = _isLoadingReviews.asStateFlow()

    // ── UI state ──────────────────────────────────────────────────────────────

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _alertType = MutableStateFlow<AppAlertType?>(null)
    val alertType: StateFlow<AppAlertType?> = _alertType.asStateFlow()

    private val _navigateToProfile = MutableStateFlow(false)
    val navigateToProfile: StateFlow<Boolean> = _navigateToProfile.asStateFlow()

    // ── Services ──────────────────────────────────────────────────────────────

    private val listingService = ListingService()
    private val reviewService = ReviewService()
    private val eligibilityService = PostingEligibilityService.shared
    private val userService = UserService.getInstance()

    // ── Computed ──────────────────────────────────────────────────────────────

    val isService: Boolean
        get() = _selectedListingType.value == ListingType.SERVICE

    // ── Field updaters ────────────────────────────────────────────────────────

    fun onTabSelected(tab: ListingTab)                { _selectedTab.value = tab }
    fun onListingTypeSelected(type: ListingType)      { _selectedListingType.value = type }
    fun onCategorySelected(category: CategoryItem)    { _selectedCategory.value = category }
    fun onCurrencySelected(currency: ListingCurrency) { _selectedCurrency.value = currency }
    fun onProductNameChange(value: String)            { _productName.value = value }
    fun onPriceTextChange(value: String)              { _priceText.value = value }
    fun onDescriptionChange(value: String)            { _description.value = value }
    fun onImageSelected(bitmap: Bitmap?)              { _selectedImage.value = bitmap }
    fun setShowCategoryPicker(show: Boolean)          { _showCategoryPicker.value = show }
    fun setShowListingTypePicker(show: Boolean)       { _showListingTypePicker.value = show }
    fun setShowCurrencyPicker(show: Boolean)          { _showCurrencyPicker.value = show }
    fun setSelectedListing(listing: StoreListing?)    { _selectedListing.value = listing }
    fun setNavigateToProfile(value: Boolean)          { _navigateToProfile.value = value }
    fun dismissAlert()                                { _alertType.value = null }
    fun setAlertType(alert: AppAlertType)             { _alertType.value = alert }
    fun onLocationSelected(location: String)   { _selectedLocation.value = location }
    fun onNegotiableChange(negotiable: Boolean) {
        _isNegotiable.value = negotiable
        if (negotiable) _priceText.value = ""
    }

    private var hasLoaded = false

    // ── Present alert ─────────────────────────────────────────────────────────

    private fun presentAlert(alert: AppAlertType) {
        _isLoading.value = false
        _showCategoryPicker.value = false
        _showListingTypePicker.value = false
        _showCurrencyPicker.value = false
        viewModelScope.launch {
            delay(80)
            _alertType.value = alert
        }
    }

    // ── Load user data ────────────────────────────────────────────────────────

    fun loadUserData(forceRefresh: Boolean = false) {
        if (hasLoaded && !forceRefresh) return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val productsDeferred = async { listingService.fetchUserListings(uid) }
                val servicesDeferred = async { listingService.fetchUserServices(uid) }
                val reviewsDeferred  = async { loadReceivedReviewsInternal(uid) }
                // ── Fetch profile to get the user's province for new listings ──
                val profileDeferred  = async { userService.fetchProfile(uid) }

                _myProducts.value   = productsDeferred.await()
                _myServices.value   = servicesDeferred.await()
                reviewsDeferred.await()

                // Store location so publishListing can attach it automatically
                val profile = profileDeferred.await()
                _userLocation.value = profile?.location ?: ""

                hasLoaded = true
            } catch (e: Exception) {
                println("MyListingsViewModel: loadUserData error — ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Load received reviews ─────────────────────────────────────────────────

    fun loadReceivedReviews() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _receivedReviews.value = emptyList()
            return
        }
        viewModelScope.launch { loadReceivedReviewsInternal(uid) }
    }

    private suspend fun loadReceivedReviewsInternal(uid: String) {
        _isLoadingReviews.value = true
        try {
            val reviews = reviewService.fetchReviews(storeID = uid)
            _receivedReviews.value = reviews.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            println("MyListingsViewModel: loadReceivedReviews error — ${e.message}")
            _receivedReviews.value = emptyList()
        } finally {
            _isLoadingReviews.value = false
        }
    }

    // ── Add service ───────────────────────────────────────────────────────────

    fun addService(service: String) {
        _isLoading.value = true

        viewModelScope.launch {
            when (val eligibility = eligibilityService.check(isService = true)) {
                is PostingEligibilityResult.ProfileIncomplete -> {
                    presentAlert(AppAlertType.Confirm(
                        title = "Profile Incomplete",
                        message = "You need to complete your profile before adding a service. Would you like to go there now?",
                        cancelAction = null,
                        proceedAction = { _navigateToProfile.value = true },
                    ))
                    return@launch
                }
                is PostingEligibilityResult.LimitReached -> {
                    presentAlert(AppAlertType.Info(title = "Limit Reached", message = eligibility.message))
                    return@launch
                }
                is PostingEligibilityResult.Error -> {
                    presentAlert(AppAlertType.Info(title = "Error", message = eligibility.message))
                    return@launch
                }
                is PostingEligibilityResult.Allowed -> Unit
            }

            try {
                listingService.addServiceListing(service = service)

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    userService.incrementPostCount(uid = uid, isService = true)
                }

                _myServices.value = _myServices.value + service
                _isLoading.value = false

            } catch (e: Exception) {
                presentAlert(AppAlertType.Info(title = "Add Failed", message = e.message ?: "Unknown error."))
            }
        }
    }

    // ── Publish listing ───────────────────────────────────────────────────────

    fun publishListing() {
        viewModelScope.launch { performPublish() }
    }

    private suspend fun performPublish() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            presentAlert(AppAlertType.Info(title = "Error", message = "No authenticated user found."))
            return
        }

        _isLoading.value = true
        val capturedIsService = isService

        // ── Eligibility check ─────────────────────────────────────────────────
        when (val eligibility = eligibilityService.check(isService = capturedIsService)) {
            is PostingEligibilityResult.ProfileIncomplete -> {
                presentAlert(AppAlertType.Confirm(
                    title = "Profile Incomplete",
                    message = "You need to complete your profile before posting a listing. Would you like to go there now?",
                    cancelAction = null,
                    proceedAction = { _navigateToProfile.value = true },
                ))
                return
            }
            is PostingEligibilityResult.LimitReached -> {
                presentAlert(AppAlertType.Info(title = "Limit Reached", message = eligibility.message))
                return
            }
            is PostingEligibilityResult.Error -> {
                presentAlert(AppAlertType.Info(title = "Error", message = eligibility.message))
                return
            }
            is PostingEligibilityResult.Allowed -> Unit
        }

        // ── If location is blank, re-fetch profile before publishing ──────────
        // Guards against the case where loadUserData hasn't run yet or the
        // user just updated their profile.
        if (_userLocation.value.isBlank()) {
            try {
                val profile = userService.fetchProfile(uid)
                _userLocation.value = profile?.location ?: ""
            } catch (e: Exception) {
                println("MyListingsViewModel: location re-fetch failed — ${e.message}")
            }
        }

        // ── Upload listing ────────────────────────────────────────────────────
        try {
            val imageData = _selectedImage.value?.let { bitmap ->
                val stream = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                stream.toByteArray()
            }

            listingService.publishListing(
                type        = com.superappzw.services.ListingType.valueOf(_selectedListingType.value.name),
                category    = _selectedCategory.value.name,
                currency    = _selectedCurrency.value.label,
                title       = _productName.value,
                priceText   = when {
                    capturedIsService      -> null
                    _isNegotiable.value    -> null   // negotiable = no price sent
                    else                   -> _priceText.value
                },
                description = _description.value,
                imageData   = imageData,
                userId      = uid,
                location    = _selectedLocation.value.ifBlank { _userLocation.value }, // form value, fallback to profile
            )

            try { userService.incrementPostCount(uid = uid, isService = capturedIsService) }
            catch (_: Exception) {}

            delay(500)
            loadUserData(forceRefresh = true)

            _selectedTab.value = if (capturedIsService) ListingTab.SERVICES else ListingTab.PRODUCTS

            presentAlert(AppAlertType.Info(
                title = "Listing Published!",
                message = "Your listing is now live.",
                dismissAction = { resetForm() },
            ))

        } catch (e: Exception) {
            presentAlert(AppAlertType.Info(title = "Publish Failed", message = e.message ?: "Unknown error."))
        }
    }

    // ── Delete listing ────────────────────────────────────────────────────────

    fun deleteListing(listing: StoreListing) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                listingService.deleteListing(itemCode = listing.itemCode)
                _myProducts.value = _myProducts.value.filter { it.itemCode != listing.itemCode }
            } catch (e: Exception) {
                presentAlert(AppAlertType.Info(title = "Delete Failed", message = e.message ?: "Unknown error."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Delete service ────────────────────────────────────────────────────────

    fun deleteService(service: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                listingService.deleteServiceListing(service = service)
                _myServices.value = _myServices.value.filter { it != service }
            } catch (e: Exception) {
                presentAlert(AppAlertType.Info(title = "Delete Failed", message = e.message ?: "Unknown error."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Reset form ────────────────────────────────────────────────────────────

    fun resetForm() {
        _productName.value         = ""
        _priceText.value           = ""
        _description.value         = ""
        _selectedImage.value       = null
        _selectedListingType.value = ListingType.PRODUCT
        _selectedCategory.value    = CategoryItem.all[0]
        _selectedCurrency.value    = ListingCurrency.USD
        _selectedLocation.value    = ""
        _isNegotiable.value        = false
        // Note: _userLocation is intentionally NOT reset — it stays populated
        // from the user's profile for the next listing they post
    }
}

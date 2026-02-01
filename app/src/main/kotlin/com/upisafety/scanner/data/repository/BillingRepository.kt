package com.upisafety.scanner.data.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BillingRepository(private val context: Context) {
    
    companion object {
        const val PRODUCT_ID = "upi_pro_yearly"
        private const val PREFS_NAME = "billing_prefs"
        private const val KEY_IS_PRO = "is_pro"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isProUser = MutableStateFlow(prefs.getBoolean(KEY_IS_PRO, false))
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()
    
    private val _billingError = MutableStateFlow<String?>(null)
    val billingError: StateFlow<String?> = _billingError.asStateFlow()
    
    private lateinit var billingClient: BillingClient
    private var isInitialized = false
    
    init {
        initializeBillingClient()
    }
    
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchases(purchases)
                }
            }
            .enablePendingPurchases()
            .build()
        
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isInitialized = true
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                isInitialized = false
            }
        })
    }
    
    suspend fun queryProductDetails(): ProductDetails? = suspendCancellableCoroutine { continuation ->
        if (!isInitialized) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                continuation.resume(productDetailsList.firstOrNull())
            } else {
                continuation.resume(null)
            }
        }
    }
    
    suspend fun launchPurchaseFlow(activity: Activity): Boolean = suspendCancellableCoroutine { continuation ->
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val productDetails = queryProductDetails()
            
            if (productDetails == null) {
                _billingError.value = "Product not available"
                continuation.resume(false)
                return@launch
            }
            
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            
            if (offerToken == null) {
                _billingError.value = "Subscription offer not available"
                continuation.resume(false)
                return@launch
            }
            
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
            
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            
            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
            continuation.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
        }
    }
    
    fun queryPurchases() {
        if (!isInitialized) return
        
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            }
        }
    }
    
    private fun handlePurchases(purchases: List<Purchase>) {
        val hasPro = purchases.any { purchase ->
            purchase.products.contains(PRODUCT_ID) && 
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        
        updateProStatus(hasPro)
        
        // Acknowledge purchases if needed
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Purchase acknowledged
            }
        }
    }
    
    private fun updateProStatus(isPro: Boolean) {
        _isProUser.value = isPro
        prefs.edit().putBoolean(KEY_IS_PRO, isPro).apply()
    }
    
    fun restorePurchases() {
        queryPurchases()
    }
    
    fun disconnect() {
        billingClient.endConnection()
    }
}

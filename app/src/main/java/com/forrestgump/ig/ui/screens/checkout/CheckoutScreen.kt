package com.forrestgump.ig.ui.screens.checkout

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.forrestgump.ig.BuildConfig
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forrestgump.ig.R
import com.forrestgump.ig.ui.screens.profile.ProfileViewModel

@Composable
fun findActivity(): Activity {
    var context = LocalContext.current
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found in context chain.")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onCheckoutComplete: () -> Unit,
) {
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }

    var error by remember { mutableStateOf<String?>(null) }

    val activity = findActivity()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isPremium = uiState.curUser.isPremium
    val remainingDays = uiState.curUser.getRemainingPremiumDays()

    val paymentSheet = rememberPaymentSheet { paymentResult ->
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                showToast("Payment successful! You are now a premium user!", activity, context)
                // Update premium status
                viewModel.updatePremiumStatus(
                    isPremium = true,
                    onSuccess = {
                        onCheckoutComplete()
                    },
                    onFailure = { e ->
                        error =
                            "Payment was successful but we couldn't update your account. Please contact support."
                        Log.e("CheckoutScreen", "Error updating premium status", e)
                    }
                )
            }

            is PaymentSheetResult.Canceled -> showToast("Payment canceled!", activity, context)
            is PaymentSheetResult.Failed -> {
                error = paymentResult.error.localizedMessage ?: paymentResult.error.message
            }
        }
    }

    error?.let { errorMessage ->
        ErrorAlert(
            errorMessage = errorMessage,
            onDismiss = {
                error = null
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!isPremium) {
            fetchPaymentIntent().onSuccess { clientSecret ->
                paymentIntentClientSecret = clientSecret
            }.onFailure { paymentIntentError ->
                error = paymentIntentError.localizedMessage ?: paymentIntentError.message
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isPremium) {
                PremiumInfoScreen(
                    remainingDays = remainingDays,
                    onCancelPremium = {
                        viewModel.updatePremiumStatus(
                            isPremium = false,
                            onSuccess = {
                                showToast("Premium subscription canceled", activity, context)
                                onCheckoutComplete()
                            },
                            onFailure = { e ->
                                error = "Could not cancel subscription. Please try again."
                                Log.e("CheckoutScreen", "Error canceling premium", e)
                            }
                        )
                    }
                )
            } else {
                PayButton(
                    enabled = paymentIntentClientSecret != null,
                    onClick = {
                        paymentIntentClientSecret?.let {
                            onPayClicked(
                                paymentSheet = paymentSheet,
                                paymentIntentClientSecret = it,
                            )
                        }
                    }

                )
            }
        }
    }
}

@Composable
private fun PremiumInfoScreen(
    remainingDays: Int,
    onCancelPremium: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Premium Active",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Premium Đang Hoạt Động",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Bạn đang là người dùng Premium!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (remainingDays > 0) {
                Text(
                    text = "Còn lại: $remainingDays ngày",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Text(
                    text = "Bạn vừa đăng ký thành công gói Premium!",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            Text(
                text = "Với tài khoản Premium, bạn có thể:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            PremiumFeatureItem(text = "Đưa bài quảng cáo của bạn lên hàng đầu")
            PremiumFeatureItem(text = "Nâng cao hiệu quả chiến dịch marketing của bạn")
            PremiumFeatureItem(text = "Nhìn rõ doanh số mà của bạn tăng lên từng ngày")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCancelPremium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Premium",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Hủy Gói Premium",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PremiumFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp)
        )

        Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun PayButton(
    enabled: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 2.dp,
                color = Color(0xFFFF4081),
                shape = RoundedCornerShape(24.dp)
            )
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.my_logo),
                    contentDescription = stringResource(id = R.string.app_logo),
                    modifier = Modifier
                        .height(96.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))


                Text(
                    text = "Mystagram Premium",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Text(
                text = "Trải nghiệm Mystagram premium giúp nâng tầm chiến dịch marketing của chính bạn",
                textAlign = TextAlign.Center,
                color = Color.DarkGray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B7DE9),
                    disabledContainerColor = Color(0xFF9BBAE8)
                )
            ) {
                Text(
                    text = "Thanh toán thôi",
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Text(
                text = "Chỉ 50$, sự khác biệt đến bất ngờ!!",
                fontSize = 12.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )

            Text(
                text = "Thanh toán một lần mỗi 1 tháng • không bao gồm VAT",
                fontSize = 12.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )

        }
    }
}

@Composable
private fun ErrorAlert(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Error occurred during checkout")
        },
        text = {
            Text(text = errorMessage)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onDismiss) {
                Text(text = "Ok")
            }
        }
    )
}

private suspend fun fetchPaymentIntent(): Result<String> = suspendCoroutine { continuation ->
    val url = "${BuildConfig.STRIPE_URL}api/create-payment-intent"

    val shoppingCartContent = """
            {
                "items": [
                    {"id":"premium-subscription",
                    "amount":"5000"}
                ]
            }
        """

    val mediaType = "application/json; charset=utf-8".toMediaType()

    val body = shoppingCartContent.toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    OkHttpClient()
        .newCall(request)
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resume(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    continuation.resume(Result.failure(Exception(response.message)))
                } else {
                    val clientSecret = extractClientSecretFromResponse(response)

                    clientSecret?.let { secret ->
                        continuation.resume(Result.success(secret))
                    } ?: run {
                        val error =
                            Exception("Could not find payment intent client secret in response!")

                        continuation.resume(Result.failure(error))
                    }
                }
            }
        })
}

private fun extractClientSecretFromResponse(response: Response): String? {
    return try {
        val responseData = response.body?.string()
        val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()

        responseJson.getString("clientSecret")
    } catch (exception: JSONException) {
        null
    }
}

private fun showToast(message: String, activity: Activity, context: Context) {
    activity.runOnUiThread {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

private fun onPayClicked(
    paymentSheet: PaymentSheet,
    paymentIntentClientSecret: String,
) {
    val configuration = PaymentSheet.Configuration.Builder(merchantDisplayName = "Example, Inc.")
        .build()

    // Present Payment Sheet
    paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
}
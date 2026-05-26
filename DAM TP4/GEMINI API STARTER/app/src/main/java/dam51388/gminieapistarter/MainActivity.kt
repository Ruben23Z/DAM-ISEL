package dam51388.gminieapistarter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import okhttp3.*
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import okio.ByteString
import org.json.JSONObject
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var cardCookies: MaterialCardView
    private lateinit var cardCakeSlice: MaterialCardView
    private lateinit var cardCakeFull: MaterialCardView
    private lateinit var cardCustomPick: MaterialCardView
    private lateinit var cardCustomPreview: MaterialCardView
    private lateinit var ivCustomPreview: ImageView
    private lateinit var tvCustomLabel: TextView
    private lateinit var ivCustomIcon: ImageView
    
    private lateinit var etPrompt: EditText
    private lateinit var chipGroupPresets: ChipGroup
    private lateinit var btnSubmit: MaterialButton
    
    private lateinit var cardResponse: MaterialCardView
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutContent: LinearLayout
    private lateinit var tvResponse: TextView
    private lateinit var btnCopy: ImageButton
    private lateinit var btnShare: ImageButton
    
    private lateinit var tvApiStatus: TextView
    private lateinit var layoutHistoryList: LinearLayout
    private lateinit var tvHistoryEmpty: TextView
    private lateinit var btnClearHistory: MaterialButton

    private var selectedImageName = "cookies" // cookies, cake_slice, cake_full, custom
    private var customImageUri: Uri? = null
    
    private lateinit var historyHelper: HistoryHelper

    // Registers the Photo Picker launcher (No special permission needed!)
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customImageUri = uri
            selectedImageName = "custom"
            updateImageSelectionUI()
            
            // Show custom preview card
            cardCustomPreview.visibility = View.VISIBLE
            ivCustomPreview.setImageURI(uri)
            
            tvCustomLabel.text = "Change Custom"
            tvCustomLabel.setTextColor(Color.parseColor("#2563EB"))
            ivCustomIcon.setColorFilter(Color.parseColor("#2563EB"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        historyHelper = HistoryHelper(this)
        
        initViews()
        setupListeners()
        updateImageSelectionUI()
        loadHistoryList()
        checkApiKeyStatus()
    }

    private fun initViews() {
        cardCookies = findViewById(R.id.card_cookies)
        cardCakeSlice = findViewById(R.id.card_cake_slice)
        cardCakeFull = findViewById(R.id.card_cake_full)
        cardCustomPick = findViewById(R.id.card_custom_pick)
        cardCustomPreview = findViewById(R.id.card_custom_preview)
        ivCustomPreview = findViewById(R.id.iv_custom_preview)
        tvCustomLabel = findViewById(R.id.tv_custom_label)
        ivCustomIcon = findViewById(R.id.iv_custom_icon)
        
        etPrompt = findViewById(R.id.et_prompt)
        chipGroupPresets = findViewById(R.id.chip_group_presets)
        btnSubmit = findViewById(R.id.btn_submit)
        
        cardResponse = findViewById(R.id.card_response)
        layoutLoading = findViewById(R.id.layout_loading)
        layoutContent = findViewById(R.id.layout_content)
        tvResponse = findViewById(R.id.tv_response)
        btnCopy = findViewById(R.id.btn_copy)
        btnShare = findViewById(R.id.btn_share)
        
        tvApiStatus = findViewById(R.id.tv_api_status)
        layoutHistoryList = findViewById(R.id.layout_history_list)
        tvHistoryEmpty = findViewById(R.id.tv_history_empty)
        btnClearHistory = findViewById(R.id.btn_clear_history)
    }

    private fun checkApiKeyStatus() {
        val token = BuildConfig.NVIDIA_TOKEN
        if (token.isBlank()) {
            tvApiStatus.text = "NVIDIA Token: MISSING (Set nvidiaToken in local.properties)"
            tvApiStatus.setTextColor(Color.parseColor("#EF4444"))
        } else {
            tvApiStatus.text = "NVIDIA Token: Configured"
            tvApiStatus.setTextColor(Color.parseColor("#10B981"))
        }
    }

    private fun setupListeners() {
        // Selection cards
        cardCookies.setOnClickListener {
            selectedImageName = "cookies"
            customImageUri = null
            cardCustomPreview.visibility = View.GONE
            updateImageSelectionUI()
        }

        cardCakeSlice.setOnClickListener {
            selectedImageName = "cake_slice"
            customImageUri = null
            cardCustomPreview.visibility = View.GONE
            updateImageSelectionUI()
        }

        cardCakeFull.setOnClickListener {
            selectedImageName = "cake_full"
            customImageUri = null
            cardCustomPreview.visibility = View.GONE
            updateImageSelectionUI()
        }

        cardCustomPick.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Suggestion Chip clicks
        findViewById<Chip>(R.id.chip_recipe).setOnClickListener {
            etPrompt.setText(getString(R.string.prompt_recipe))
        }
        findViewById<Chip>(R.id.chip_name).setOnClickListener {
            etPrompt.setText(getString(R.string.prompt_name))
        }
        findViewById<Chip>(R.id.chip_calories).setOnClickListener {
            etPrompt.setText(getString(R.string.prompt_calories))
        }
        findViewById<Chip>(R.id.chip_ingredients).setOnClickListener {
            etPrompt.setText(getString(R.string.prompt_ingredients))
        }

        // Actions
        btnSubmit.setOnClickListener {
            performGeminiAnalysis()
        }

        btnCopy.setOnClickListener {
            val responseText = tvResponse.text.toString()
            if (responseText.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Gemini Response", responseText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied recipe details to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }

        btnShare.setOnClickListener {
            val responseText = tvResponse.text.toString()
            if (responseText.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Gemini AI Sweet Analysis")
                    putExtra(Intent.EXTRA_TEXT, responseText)
                }
                startActivity(Intent.createChooser(intent, "Share response with friends"))
            }
        }

        btnClearHistory.setOnClickListener {
            historyHelper.clearHistory()
            loadHistoryList()
            Toast.makeText(this, "History cleared!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateImageSelectionUI() {
        // Reset stroke configurations
        resetCardStroke(cardCookies)
        resetCardStroke(cardCakeSlice)
        resetCardStroke(cardCakeFull)
        resetCardStroke(cardCustomPick)

        // Set selected border
        when (selectedImageName) {
            "cookies" -> setCardSelected(cardCookies)
            "cake_slice" -> setCardSelected(cardCakeSlice)
            "cake_full" -> setCardSelected(cardCakeFull)
            "custom" -> setCardSelected(cardCustomPick)
        }
    }

    private fun resetCardStroke(card: MaterialCardView) {
        card.strokeColor = Color.TRANSPARENT
        card.strokeWidth = 0
    }

    private fun setCardSelected(card: MaterialCardView) {
        card.strokeColor = Color.parseColor("#2563EB")
        card.strokeWidth = 8
    }

    private fun performGeminiAnalysis() {
        val token = BuildConfig.NVIDIA_TOKEN // token loaded from local.properties
        if (token.isBlank()) {
            Toast.makeText(this, "NVIDIA token not configured", Toast.LENGTH_LONG).show()
            return
        }
        val promptText = etPrompt.text.toString().trim()
        if (promptText.isEmpty()) {
            etPrompt.error = "Please enter a prompt or select a quick option chip above!"
            return
        }
        // Show loader
        cardResponse.visibility = View.VISIBLE
        layoutLoading.visibility = View.VISIBLE
        layoutContent.visibility = View.GONE
        btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                // Load bitmap
                val bitmap = withContext(Dispatchers.IO) { loadSelectedBitmap() }
                if (bitmap == null) throw Exception("Could not load/decode selected image.")
                // Convert to base64
                val base64Image = withContext(Dispatchers.IO) {
                    val stream = java.io.ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
                }
                // Build JSON payload
                val json = JSONObject()
                json.put("model", "google/gemma-4-31b-it")
                val messages = org.json.JSONArray()
                val userMsg = JSONObject()
                userMsg.put("role", "user")
                val contentArray = org.json.JSONArray()
                // Image part
                val imageObj = JSONObject()
                imageObj.put("type", "image_url")
                val imageUrlObj = JSONObject()
                imageUrlObj.put("url", "data:image/png;base64,$base64Image")
                imageObj.put("image_url", imageUrlObj)
                contentArray.put(imageObj)
                // Text part
                val textObj = JSONObject()
                textObj.put("type", "text")
                textObj.put("text", promptText)
                contentArray.put(textObj)
                userMsg.put("content", contentArray)
                messages.put(userMsg)
                json.put("messages", messages)
                json.put("max_tokens", 1024)
                json.put("temperature", 0.7)
                // OkHttp request
                val client = OkHttpClient()
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("https://integrate.api.nvidia.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $token")
                    .post(body)
                    .build()
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                if (!response.isSuccessful) throw Exception("API error: ${response.code}")
                val respBody = response.body?.string() ?: throw Exception("Empty response body")
                val respJson = JSONObject(respBody)
                val answer = respJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                // Update UI
                tvResponse.text = answer
                layoutLoading.visibility = View.GONE
                layoutContent.visibility = View.VISIBLE
                // Save history
                val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val historyItem = HistoryItem(
                    id = UUID.randomUUID().toString(),
                    timestamp = timestamp,
                    imageName = selectedImageName,
                    customImageUriString = customImageUri?.toString(),
                    prompt = promptText,
                    response = answer
                )
                historyHelper.saveHistoryItem(historyItem)
                loadHistoryList()
            } catch (e: Exception) {
                e.printStackTrace()
                tvResponse.text = "Error calling NVIDIA API:\n${e.localizedMessage ?: "Unknown error"}"
                layoutLoading.visibility = View.GONE
                layoutContent.visibility = View.VISIBLE
            } finally {
                btnSubmit.isEnabled = true
            }
        }

        // Legacy Gemini API block removed
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY") {
            Toast.makeText(this, "Please configure your API key in local.properties first!", Toast.LENGTH_LONG).show()
            tvResponse.text = "ERROR:\nMissing API Key!\nPlease edit the local.properties file and add:\n\napiKey=YOUR_REAL_API_KEY\n\nthen sync Gradle and run the app."
            cardResponse.visibility = View.VISIBLE
            layoutLoading.visibility = View.GONE
            layoutContent.visibility = View.VISIBLE
            return
        }

        val promptText = etPrompt.text.toString().trim()
        if (promptText.isEmpty()) {
            etPrompt.error = "Please enter a prompt or select a quick option chip above!"
            return
        }

        // Show card loader
        cardResponse.visibility = View.VISIBLE
        layoutLoading.visibility = View.VISIBLE
        layoutContent.visibility = View.GONE
        btnSubmit.isEnabled = false

        // Run API call in coroutines context safely
        lifecycleScope.launch {
            try {
                // 1. Decode image bitmap inside background thread
                val bitmap = withContext(Dispatchers.IO) {
                    loadSelectedBitmap()
                }

                if (bitmap == null) {
                    throw Exception("Could not load/decode selected image.")
                }

                // 2. Instantiate generative model (Gemini 1.5 Flash for vision tasks)
                val model = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )

                // 3. Query the API
                val inputContent = content {
                    image(bitmap)
                    text(promptText)
                }

                val response = withContext(Dispatchers.IO) {
                    model.generateContent(inputContent)
                }

                // 4. Handle response in UI
                val answer = response.text ?: "Could not extract a valid response."
                
                tvResponse.text = answer
                layoutLoading.visibility = View.GONE
                layoutContent.visibility = View.VISIBLE

                // 5. Save in local offline query history
                val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val historyItem = HistoryItem(
                    id = UUID.randomUUID().toString(),
                    timestamp = timestamp,
                    imageName = selectedImageName,
                    customImageUriString = customImageUri?.toString(),
                    prompt = promptText,
                    response = answer
                )
                historyHelper.saveHistoryItem(historyItem)
                
                // Refresh history pane
                loadHistoryList()

            } catch (e: Exception) {
                e.printStackTrace()
                tvResponse.text = "Error calling Gemini API:\n${e.localizedMessage ?: "Unknown error. Check internet connection."}"
                layoutLoading.visibility = View.GONE
                layoutContent.visibility = View.VISIBLE
            } finally {
                btnSubmit.isEnabled = true
            }
        }
    }

    private fun loadSelectedBitmap(): Bitmap? {
        return try {
            when (selectedImageName) {
                "cookies" -> BitmapFactory.decodeResource(resources, R.drawable.cookies)
                "cake_slice" -> BitmapFactory.decodeResource(resources, R.drawable.cake_slice)
                "cake_full" -> BitmapFactory.decodeResource(resources, R.drawable.cake_full)
                "custom" -> {
                    val uri = customImageUri ?: return null
                    var inputStream: InputStream? = null
                    try {
                        inputStream = contentResolver.openInputStream(uri)
                        BitmapFactory.decodeStream(inputStream)
                    } finally {
                        inputStream?.close()
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadHistoryList() {
        val historyList = historyHelper.getHistory()
        layoutHistoryList.removeAllViews()

        if (historyList.isEmpty()) {
            tvHistoryEmpty.visibility = View.VISIBLE
            btnClearHistory.visibility = View.GONE
            return
        }

        tvHistoryEmpty.visibility = View.GONE
        btnClearHistory.visibility = View.VISIBLE

        for (item in historyList) {
            val itemView = layoutInflater.inflate(android.R.layout.simple_list_item_2, layoutHistoryList, false)
            
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)

            // Setup styling for list items
            text1.text = "${item.timestamp} - Image: ${item.imageName.uppercase()}"
            text1.setTextColor(Color.parseColor("#1E293B"))
            text1.textSize = 13f
            text1.paint?.isFakeBoldText = true

            text2.text = "Q: ${item.prompt}\n\nA: ${item.response.take(160)}..."
            text2.setTextColor(Color.parseColor("#475569"))
            text2.textSize = 12f
            text2.setPadding(0, 4, 0, 16)

            // Make list item selectable to reload the prompt and results
            itemView.isClickable = true
            itemView.setOnClickListener {
                etPrompt.setText(item.prompt)
                tvResponse.text = item.response
                cardResponse.visibility = View.VISIBLE
                layoutLoading.visibility = View.GONE
                layoutContent.visibility = View.VISIBLE
                
                selectedImageName = item.imageName
                if (selectedImageName == "custom" && item.customImageUriString != null) {
                    customImageUri = Uri.parse(item.customImageUriString)
                    cardCustomPreview.visibility = View.VISIBLE
                    ivCustomPreview.setImageURI(customImageUri)
                } else {
                    customImageUri = null
                    cardCustomPreview.visibility = View.GONE
                }
                updateImageSelectionUI()
                
                Toast.makeText(this, "Reloaded history item into inputs!", Toast.LENGTH_SHORT).show()
            }

            layoutHistoryList.addView(itemView)
            
            // Add a clean divider line
            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2).apply {
                    setMargins(0, 4, 0, 12)
                }
                setBackgroundColor(Color.parseColor("#E2E8F0"))
            }
            layoutHistoryList.addView(divider)
        }
    }
}
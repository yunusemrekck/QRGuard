package com.yunusek.qrguard.ui.screens.create

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunusek.qrguard.QrGuardApp
import com.yunusek.qrguard.data.local.CreatedQrEntity
import com.yunusek.qrguard.data.repository.CreatedQrRepository
import com.yunusek.qrguard.generator.QrGenerator
import com.yunusek.qrguard.generator.QrType
import com.yunusek.qrguard.generator.WifiSecurityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CreateViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: CreatedQrRepository
    
    init {
        val database = (application as QrGuardApp).database
        repository = CreatedQrRepository(database.createdQrDao())
    }
    
    private val _selectedType = MutableStateFlow(QrType.TEXT)
    val selectedType: StateFlow<QrType> = _selectedType.asStateFlow()
    
    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()
    
    private val _qrContent = MutableStateFlow("")
    val qrContent: StateFlow<String> = _qrContent.asStateFlow()
    
    private val _foregroundColor = MutableStateFlow(Color.Black)
    val foregroundColor: StateFlow<Color> = _foregroundColor.asStateFlow()
    
    private val _backgroundColor = MutableStateFlow(Color.White)
    val backgroundColor: StateFlow<Color> = _backgroundColor.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()
    
    // Form fields
    private val _textInput = MutableStateFlow("")
    val textInput: StateFlow<String> = _textInput.asStateFlow()
    
    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()
    
    private val _wifiSsid = MutableStateFlow("")
    val wifiSsid: StateFlow<String> = _wifiSsid.asStateFlow()
    
    private val _wifiPassword = MutableStateFlow("")
    val wifiPassword: StateFlow<String> = _wifiPassword.asStateFlow()
    
    private val _wifiSecurity = MutableStateFlow(WifiSecurityType.WPA)
    val wifiSecurity: StateFlow<WifiSecurityType> = _wifiSecurity.asStateFlow()
    
    private val _emailAddress = MutableStateFlow("")
    val emailAddress: StateFlow<String> = _emailAddress.asStateFlow()
    
    private val _emailSubject = MutableStateFlow("")
    val emailSubject: StateFlow<String> = _emailSubject.asStateFlow()
    
    private val _emailBody = MutableStateFlow("")
    val emailBody: StateFlow<String> = _emailBody.asStateFlow()
    
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()
    
    private val _smsNumber = MutableStateFlow("")
    val smsNumber: StateFlow<String> = _smsNumber.asStateFlow()
    
    private val _smsMessage = MutableStateFlow("")
    val smsMessage: StateFlow<String> = _smsMessage.asStateFlow()
    
    private val _contactName = MutableStateFlow("")
    val contactName: StateFlow<String> = _contactName.asStateFlow()
    
    private val _contactPhone = MutableStateFlow("")
    val contactPhone: StateFlow<String> = _contactPhone.asStateFlow()
    
    private val _contactEmail = MutableStateFlow("")
    val contactEmail: StateFlow<String> = _contactEmail.asStateFlow()
    
    private val _contactCompany = MutableStateFlow("")
    val contactCompany: StateFlow<String> = _contactCompany.asStateFlow()
    
    private val _geoLatitude = MutableStateFlow("")
    val geoLatitude: StateFlow<String> = _geoLatitude.asStateFlow()
    
    private val _geoLongitude = MutableStateFlow("")
    val geoLongitude: StateFlow<String> = _geoLongitude.asStateFlow()
    
    private val _geoLabel = MutableStateFlow("")
    val geoLabel: StateFlow<String> = _geoLabel.asStateFlow()
    
    fun selectType(type: QrType) {
        _selectedType.value = type
        _qrBitmap.value = null
        _qrContent.value = ""
    }
    
    fun updateTextInput(value: String) { _textInput.value = value }
    fun updateUrlInput(value: String) { _urlInput.value = value }
    fun updateWifiSsid(value: String) { _wifiSsid.value = value }
    fun updateWifiPassword(value: String) { _wifiPassword.value = value }
    fun updateWifiSecurity(value: WifiSecurityType) { _wifiSecurity.value = value }
    fun updateEmailAddress(value: String) { _emailAddress.value = value }
    fun updateEmailSubject(value: String) { _emailSubject.value = value }
    fun updateEmailBody(value: String) { _emailBody.value = value }
    fun updatePhoneNumber(value: String) { _phoneNumber.value = value }
    fun updateSmsNumber(value: String) { _smsNumber.value = value }
    fun updateSmsMessage(value: String) { _smsMessage.value = value }
    fun updateContactName(value: String) { _contactName.value = value }
    fun updateContactPhone(value: String) { _contactPhone.value = value }
    fun updateContactEmail(value: String) { _contactEmail.value = value }
    fun updateContactCompany(value: String) { _contactCompany.value = value }
    fun updateGeoLatitude(value: String) { _geoLatitude.value = value }
    fun updateGeoLongitude(value: String) { _geoLongitude.value = value }
    fun updateGeoLabel(value: String) { _geoLabel.value = value }
    
    fun updateForegroundColor(color: Color) {
        _foregroundColor.value = color
        regenerateQrIfNeeded()
    }
    
    fun updateBackgroundColor(color: Color) {
        _backgroundColor.value = color
        regenerateQrIfNeeded()
    }
    
    private fun regenerateQrIfNeeded() {
        if (_qrContent.value.isNotEmpty()) {
            generateQr()
        }
    }
    
    fun generateQr() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val content = when (_selectedType.value) {
                QrType.TEXT -> QrGenerator.generateTextQrContent(_textInput.value)
                QrType.URL -> QrGenerator.generateUrlQrContent(_urlInput.value)
                QrType.WIFI -> QrGenerator.generateWifiQrContent(
                    ssid = _wifiSsid.value,
                    password = _wifiPassword.value,
                    securityType = _wifiSecurity.value
                )
                QrType.EMAIL -> QrGenerator.generateEmailQrContent(
                    email = _emailAddress.value,
                    subject = _emailSubject.value.ifEmpty { null },
                    body = _emailBody.value.ifEmpty { null }
                )
                QrType.PHONE -> QrGenerator.generatePhoneQrContent(_phoneNumber.value)
                QrType.SMS -> QrGenerator.generateSmsQrContent(
                    phoneNumber = _smsNumber.value,
                    message = _smsMessage.value.ifEmpty { null }
                )
                QrType.CONTACT -> QrGenerator.generateVCardQrContent(
                    name = _contactName.value,
                    phone = _contactPhone.value.ifEmpty { null },
                    email = _contactEmail.value.ifEmpty { null },
                    company = _contactCompany.value.ifEmpty { null }
                )
                QrType.LOCATION -> QrGenerator.generateGeoQrContent(
                    latitude = _geoLatitude.value.toDoubleOrNull() ?: 0.0,
                    longitude = _geoLongitude.value.toDoubleOrNull() ?: 0.0,
                    label = _geoLabel.value.ifEmpty { null }
                )
                QrType.EVENT -> ""
            }
            
            _qrContent.value = content
            
            if (content.isNotEmpty()) {
                val bitmap = QrGenerator.generateQrCode(
                    content = content,
                    size = 512,
                    foregroundColor = _foregroundColor.value.toArgb(),
                    backgroundColor = _backgroundColor.value.toArgb()
                )
                _qrBitmap.value = bitmap
            }
            
            _isLoading.value = false
        }
    }
    
    fun saveToGallery(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val bitmap = _qrBitmap.value
            
            if (bitmap != null) {
                val success = saveBitmapToGallery(context, bitmap)
                _saveSuccess.value = success
                
                if (success) {
                    val label = getQrLabel()
                    repository.saveCreatedQr(
                        CreatedQrEntity(
                            content = _qrContent.value,
                            type = _selectedType.value,
                            label = label,
                            foregroundColor = _foregroundColor.value.toArgb(),
                            backgroundColor = _backgroundColor.value.toArgb()
                        )
                    )
                }
            } else {
                _saveSuccess.value = false
            }
            
            _isLoading.value = false
        }
    }
    
    private fun getQrLabel(): String {
        return when (_selectedType.value) {
            QrType.TEXT -> _textInput.value.take(30)
            QrType.URL -> _urlInput.value.take(30)
            QrType.WIFI -> _wifiSsid.value
            QrType.EMAIL -> _emailAddress.value
            QrType.PHONE -> _phoneNumber.value
            QrType.SMS -> _smsNumber.value
            QrType.CONTACT -> _contactName.value
            QrType.LOCATION -> _geoLabel.value.ifEmpty { "Konum" }
            QrType.EVENT -> "Etkinlik"
        }
    }
    
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val filename = "QRGuard_${System.currentTimeMillis()}.png"
            
            val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRGuard")
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val qrGuardDir = File(imagesDir, "QRGuard")
                if (!qrGuardDir.exists()) qrGuardDir.mkdirs()
                val imageFile = File(qrGuardDir, filename)
                FileOutputStream(imageFile)
            }
            
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun shareQr(context: Context) {
        viewModelScope.launch {
            val bitmap = _qrBitmap.value ?: return@launch
            
            try {
                val cachePath = File(context.cacheDir, "qr_codes")
                cachePath.mkdirs()
                val file = File(cachePath, "shared_qr.png")
                FileOutputStream(file).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(Intent.createChooser(shareIntent, "QR Kodu Paylaş"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun clearSaveStatus() {
        _saveSuccess.value = null
    }
    
    fun isFormValid(): Boolean {
        return when (_selectedType.value) {
            QrType.TEXT -> _textInput.value.isNotBlank()
            QrType.URL -> _urlInput.value.isNotBlank()
            QrType.WIFI -> _wifiSsid.value.isNotBlank()
            QrType.EMAIL -> _emailAddress.value.isNotBlank()
            QrType.PHONE -> _phoneNumber.value.isNotBlank()
            QrType.SMS -> _smsNumber.value.isNotBlank()
            QrType.CONTACT -> _contactName.value.isNotBlank()
            QrType.LOCATION -> _geoLatitude.value.isNotBlank() && _geoLongitude.value.isNotBlank()
            QrType.EVENT -> false
        }
    }
}

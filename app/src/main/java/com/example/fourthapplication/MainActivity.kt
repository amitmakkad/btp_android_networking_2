package com.example.fourthapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import okhttp3.MediaType
import okhttp3.RequestBody

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private val PICK_CSV_REQUEST = 2

    private lateinit var selectImageButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImageButton = findViewById(R.id.selectImageButton)
        resultTextView = findViewById(R.id.resultTextView)
        val uploadButton: Button = findViewById(R.id.uploadButton)

        ApiClient.fetchGitHubBaseUrl { gitHubBaseUrl ->
            // Update the Retrofit instance with the fetched GITHUB_BASE_URL
            ApiClient.updateBaseUrl(gitHubBaseUrl)
            // Now that the GitHub Pages URL is fetched, set the click listener
            selectImageButton.setOnClickListener {
                openGallery()
            }
        }

        uploadButton.setOnClickListener {
            openFilePicker()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/csv"
        startActivityForResult(intent, PICK_CSV_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri = data.data ?: return
            val imageStream = contentResolver.openInputStream(imageUri) ?: return
            val byteArray = readBytes(imageStream)
            uploadImage(byteArray)
        }
        if (requestCode == PICK_CSV_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val csvUri: Uri = data.data ?: return
            uploadCsvFile(csvUri)
        }
    }

    private fun readBytes(inputStream: java.io.InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = inputStream.read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }

        return byteBuffer.toByteArray()
    }

    private fun uploadImage(imageByteArray: ByteArray) {
        val requestBody = imageByteArray.toRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
        val apiService = ApiClient.serverRetrofit.create(ApiService::class.java)
        val call = apiService.uploadImage(imagePart);

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val averagePixelValue = response.body()?.averagePixelValue ?: 0.0
                    resultTextView.text = "Average Pixel Value: $averagePixelValue"
                } else {
                    resultTextView.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                resultTextView.text = "Error: ${t.message}"
            }
        })
    }

    private fun uploadCsvFile(csvUri: Uri) {
        val apiService = ApiClient.localRetrofit.create(ApiService::class.java)
        val inputStream = contentResolver.openInputStream(csvUri) ?: return
        val csvRequestBody = RequestBody.create(null, inputStream.readBytes())
        val csvPart = MultipartBody.Part.createFormData("file", "data.csv", csvRequestBody)

        val call = apiService.uploadCsv(csvPart)
        call.enqueue(object : Callback<CsvResponse> {
            override fun onResponse(call: Call<CsvResponse>, response: Response<CsvResponse>) {
                if (response.isSuccessful) {
                    val csvResponse = response.body()
                    val dataShape = csvResponse?.dataShape ?: "Shape information not available."
                    val responseTextView: TextView = findViewById(R.id.responseTextView)
                    responseTextView.text = "CSV Shape: $dataShape"
                } else {
                    val responseTextView: TextView = findViewById(R.id.responseTextView)
                    responseTextView.text = "Failed to upload CSV."
                }
            }

            override fun onFailure(call: Call<CsvResponse>, t: Throwable) {
                val responseTextView: TextView = findViewById(R.id.responseTextView)
                responseTextView.text = "Network error: ${t.message}"
            }
        })
    }
}
data class CsvResponse(val dataShape: String)
data class ApiResponse(val averagePixelValue: Double)
data class GitHubBaseUrlResponse(val serverUrl: String)
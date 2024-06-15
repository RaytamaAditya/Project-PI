package com.example.myappplant.home

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappplant.database.AnalysisResult
import com.example.myappplant.database.AnalysisResultDao
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class HomeViewModel(private val dao: AnalysisResultDao) : ViewModel() {
    var currentImageUri: Uri? = null

    fun saveAnalysisResult(context: Context, label: String, score: Float) {
        currentImageUri?.let { imageUri ->
            Log.d("HomeViewModel", "Saving image URI: $imageUri")

            // Generate a unique filename for the image
            val uniqueFileName = UUID.randomUUID().toString() + ".jpg"
            val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), uniqueFileName)

            // Copy the image to the destination file
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                saveImageToFile(inputStream, destinationFile)
            }

            val analysisResult = AnalysisResult(label = label, score = score, imageUri = destinationFile.absolutePath)

            viewModelScope.launch {
                // Check if the imageUri already exists
                val existingItem = dao.getAnalysisResultByImageUri(destinationFile.absolutePath)
                if (existingItem == null) {
                    dao.insert(analysisResult)
                    Log.d("HomeViewModel", "Data saved successfully: $analysisResult")
                } else {
                    Log.d("HomeViewModel", "Image already exists in the database: ${destinationFile.absolutePath}")
                }
            }
        } ?: run {
            Log.e("HomeViewModel", "Image URI is null")
        }
    }

    private fun saveImageToFile(inputStream: InputStream, destinationFile: File) {
        FileOutputStream(destinationFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    }
}

package com.example.myappplant.view

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(context: Context) {
    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(FileUtil.loadMappedFile(context, "Plant.tflite"))
    }

    fun classifyImage(bitmap: Bitmap): FloatArray {
        val image = TensorImage(DataType.UINT8)
        image.load(bitmap)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
        val byteBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(224 * 224)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        // Convert ARGB pixel values to RGB and pack into ByteBuffer
        for (pixelValue in pixels) {
            // Extract RGB components from pixel value
            val r = (pixelValue shr 16) and 0xFF
            val g = (pixelValue shr 8) and 0xFF
            val b = pixelValue and 0xFF
            // Pack RGB values into ByteBuffer
            byteBuffer.put((r.toFloat() / 255.0f * 127.5f + 127.5f).toInt().toByte())
            byteBuffer.put((g.toFloat() / 255.0f * 127.5f + 127.5f).toInt().toByte())
            byteBuffer.put((b.toFloat() / 255.0f * 127.5f + 127.5f).toInt().toByte())
        }

        inputFeature0.loadBuffer(byteBuffer)

        val outputs = Array(1) { FloatArray(16) }
        interpreter.run(inputFeature0.buffer, outputs)

        return outputs[0]
    }

    fun close() {
        interpreter.close()
    }
}



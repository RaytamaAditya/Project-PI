package com.example.myappplant.home

import HomeViewModelFactory
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myappplant.R
import com.example.myappplant.ml.Plant
import com.example.myappplant.view.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var btnAnalyze: ImageButton
    private lateinit var btnLoadImage: Button
    private lateinit var btnCaptureImage: ImageButton
    private lateinit var txtOutput: TextView

    // Inisialisasi viewModel menggunakan delegasi by viewModels
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity().application)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        imageView = view.findViewById(R.id.imageView)
        btnAnalyze = view.findViewById(R.id.btn_analyze)
        btnLoadImage = view.findViewById(R.id.btn_load_image)
        btnCaptureImage = view.findViewById(R.id.btn_capture_image)
        txtOutput = view.findViewById(R.id.tvOutput)

        setupListeners()
        restoreImageState()

        return view
    }


    private fun restoreImageState() {
        viewModel.currentImageUri?.let {
            val bitmap = getBitmapFromUri(it)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun setupListeners() {
        btnLoadImage.setOnClickListener {
            startGallery.launch("image/*")
        }

        btnCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        btnAnalyze.setOnClickListener {
            val drawable = imageView.drawable as? BitmapDrawable
            val bitmap = drawable?.bitmap

            if (bitmap != null) {
                analyzeImage(bitmap)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please load or capture an image first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        // Resize the image to 300x300
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false)
        // Normalize the pixels
        val normalizedBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        for (i in 0 until 300) {
            for (j in 0 until 300) {
                val p = resizedBitmap.getPixel(i, j)
                val r = ((p shr 16 and 0xff) / 255.0).toFloat()
                val g = ((p shr 8 and 0xff) / 255.0).toFloat()
                val b = ((p and 0xff) / 255.0).toFloat()
                normalizedBitmap.setPixel(
                    i,
                    j,
                    Color.rgb((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
                )
            }
        }
        return normalizedBitmap
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
                viewModel.currentImageUri = getImageUri(requireContext(), imageBitmap)
            }
        }


    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun setupOutputClickListener(diseaseLabelIndonesian: String) {
        txtOutput.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=$diseaseLabelIndonesian")
            )
            startActivity(intent)
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            }
        }

    private val startGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.currentImageUri = it
                if (isVectorDrawable(requireContext(), it)) {
                    imageView.setImageResource(R.drawable.ic_place_holder)
                    Toast.makeText(
                        requireContext(),
                        "VectorDrawable cannot be uploaded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startUCropActivity(it)
                }
            }
        }

    private fun startUCropActivity(uri: Uri) {
        val file = File(requireContext().filesDir, "cropped_image.jpg")
        val outputUri = Uri.fromFile(file)
        val uCrop = UCrop.of(uri, outputUri)
            .withAspectRatio(16f, 9f)
        cropLauncher.launch(uCrop.getIntent(requireContext()))
    }


    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    val bitmap = getBitmapFromUri(it)
                    imageView.setImageBitmap(bitmap)
                    viewModel.currentImageUri = it
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                Toast.makeText(requireContext(), "Crop error: $cropError", Toast.LENGTH_LONG).show()
            }
        }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        requireContext().contentResolver.openInputStream(uri).use { inputStream ->
            return BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun isVectorDrawable(context: Context, uri: Uri): Boolean {
        return context.contentResolver.getType(uri) == "image/svg+xml"
    }

    private fun analyzeImage(bitmap: Bitmap) {
        val preprocessedBitmap = preprocessImage(bitmap)
        val model = Plant.newInstance(requireContext())

        val tfImage = TensorImage.fromBitmap(preprocessedBitmap)
        val outputs = model.process(tfImage)
        val probability = outputs.probabilityAsCategoryList

        val maxProbability = probability.maxByOrNull { it.score }
        val index = probability.indexOf(maxProbability)
        val diseaseLabel = arrayOf(
            "Tomato Healthy",
            "Tomato Septoria Leaf Spot",
            "Tomato Bacterial Spot",
            "Tomato Blight",
            "Cabbage Healthy",
            "Tomato Spider Mite",
            "Tomato Leaf Mold",
            "Tomato Yellow Leaf Curl Virus",
            "Soy Frogeye Leaf Spot",
            "Soy Downy Mildew",
            "Maize Ravi Corn Rust",
            "Maize Healthy",
            "Maize Grey Leaf Spot",
            "Maize Lethal Necrosis",
            "Soy Healthy",
            "Cabbage Black Rot"
        )[index]
        val diseaseProbability = Math.round((maxProbability?.score ?: 0f) * 100)

        val indonesianLabels = mapOf(
            "Tomato Healthy" to "Tomat Sehat",
            "Tomato Septoria Leaf Spot" to "Tomat Bercak Daun Septoria",
            "Tomato Bacterial Spot" to "Tomat Bercak Bakteri",
            "Tomato Blight" to "Tomat Busuk",
            "Tomato Spider Mite" to "Tungau Laba-laba Daun Tomat",
            "Tomato Leaf Mold" to "Jamur Daun pada Tomat",
            "Tomato Yellow Leaf Curl Virus" to "Virus Kuning Keriting Daun Tomat",
            "Cabbage Healthy" to "Kubis Sehat",
            "Cabbage Black Rot" to "Kubis Busuk Hitam",
            "Soy Frogeye Leaf Spot" to "Kedelai Bercak Daun Frogeye",
            "Soy Downy Mildew" to "Embun Bulu Kedelai",
            "Soy Healthy" to "Kedelai Sehat",
            "Maize Ravi Corn Rust" to "Jagung Karat",
            "Maize Healthy" to "Jagung Sehat",
            "Maize Grey Leaf Spot" to "Jagung Bercak Daun Abu-abu",
            "Maize Lethal Necrosis" to "Nekrosis Mematikan pada Jagung"
        )

        val diseaseLabelIndonesian = indonesianLabels[diseaseLabel] ?: diseaseLabel

        // Set the output text to the Indonesian label without percentage
        txtOutput.text = diseaseLabelIndonesian

        // Setup click listener to search the disease on Google using the Indonesian label
        setupOutputClickListener(diseaseLabelIndonesian)

        // Save the analysis result to the database
        viewModel.saveAnalysisResult(requireContext(), diseaseLabel, maxProbability?.score ?: 0f)

        // Create an Intent to start ResultActivity with English label and rounded percentage
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(
                "IMAGE",
                viewModel.currentImageUri.toString()
            )  // Use viewModel to access currentImageUri
            putExtra("LABEL", diseaseLabel) // English label
            putExtra("SCORE", "$diseaseProbability%") // Rounded percentage
        }
        startActivity(intent)

        model.close()
    }
}

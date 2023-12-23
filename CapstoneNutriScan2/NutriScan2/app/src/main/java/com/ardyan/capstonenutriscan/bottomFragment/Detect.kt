package com.ardyan.capstonenutriscan.bottomFragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ardyan.capstonenutriscan.databinding.FragmentDetectBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.ardyan.capstonenutriscan.activities.CameraXActivity
import com.ardyan.capstonenutriscan.activities.CameraXActivity.Companion.CAMERAX_RESULT
import com.ardyan.capstonenutriscan.ml.ConvertedModel


class Detect : Fragment() {
    private var _binding: FragmentDetectBinding? = null
    private val binding get() = _binding!!
    lateinit var bitmap: Bitmap
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireActivity(), "Permission request denied", Toast.LENGTH_LONG).show()

            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectBinding.inflate(inflater, container, false)
        val view = binding.root

        // Camera X
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val labels = requireActivity().application.assets.open("Label_Food.text").bufferedReader().readLines()

        // image processor
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(32, 32, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        binding.buttonGalery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.buttonCamera.setOnClickListener {
            startCameraX()
        }

        binding.upload.setOnClickListener {
            if (::bitmap.isInitialized) {
                // Check if bitmap is initialized and not null
                var tensorImage = TensorImage(DataType.FLOAT32)
                tensorImage.load(bitmap)

                tensorImage = imageProcessor.process(tensorImage)

                val model = ConvertedModel.newInstance(requireContext())

                // Membuat input untuk referensi.
                val inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(tensorImage.buffer)

                // Menjalankan inferensi model dan mendapatkan hasil.
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                var maxIdx = 0
                outputFeature0.forEachIndexed { index, fl ->
                    if (outputFeature0[maxIdx] < fl) {
                        maxIdx = index
                    }
                }

                binding.result.text = labels[maxIdx]

                model.close()
            } else {
                // Handle case when bitmap is not initialized
                Toast.makeText(requireActivity(), "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun startCameraX() {
        Log.d("DetectFragment", "Starting CameraX")
        val intent = Intent(requireContext(), CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraXActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("DetectFragment", "Image URI: $it")
            bitmap = uriToBitmap(requireContext().contentResolver, it)!!
            binding.imageView.setImageURI(it)
        }
    }

    fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            Log.d("DetectFragment", "Input Stream: $inputStream")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Log.d("DetectFragment", "Bitmap Decoded Successfully")
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("DetectFragment", "Error decoding bitmap: ${e.message}")
            null
        }
    }


    // Fungsi ini akan dipanggil ketika gambar dipilih dari galeri atau diambil dari kamera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                val convertedBitmap = uriToBitmap(requireContext().contentResolver, it)
                if (convertedBitmap != null) {
                    bitmap = convertedBitmap
                    binding.imageView.setImageBitmap(bitmap)

                    // Pastikan bahwa bitmap tidak null setelah diinisialisasi
                    Log.d("DetectFragment", "Bitmap Size: ${bitmap.width} x ${bitmap.height}")
                } else {
                    // Handle case when conversion failed
                    Toast.makeText(requireActivity(), "Failed to convert URI to Bitmap", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Handle case when uri is null (optional, depends on your logic)
                Toast.makeText(requireActivity(), "Failed to get image from gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
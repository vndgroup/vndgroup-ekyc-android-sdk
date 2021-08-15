package vn.vndgroup.ekycsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import vn.vndgroup.ekycsample.databinding.ActivityViewImageBinding
import java.io.File

class ViewImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        val filePath = intent.getStringExtra("data")
        binding.imageView.load(File(filePath!!)) {
            placeholder(R.drawable.test_drawable)
        }
    }

    override fun onBackPressed() {
        if (binding.imageView.isZoomed) {
            binding.imageView.resetZoom()
        } else {
            super.onBackPressed()
        }
    }
}
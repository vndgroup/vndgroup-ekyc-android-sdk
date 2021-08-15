package vn.vndgroup.ekycsample

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import vn.vndgroup.ekyc.global.Constants
import vn.vndgroup.ekyc.objectdetection.IdCardType
import vn.vndgroup.ekyc.sample.VerifyProfileStep
import vn.vndgroup.ekycsample.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val takePhotoContract =
        registerForActivityResult(TakePhotoCameraActivity.Contract()) { result ->
            handleActivityResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.imageViewSelfie.setOnClickListener {
            takePhotoContract.launch(VerifyProfileStep.SELFIE)
        }
        binding.imageViewFrontIdCard.setOnClickListener {
            takePhotoContract.launch(VerifyProfileStep.FRONT_ID_CARD)
        }
        binding.imageViewBackIdCard.setOnClickListener {
            takePhotoContract.launch(VerifyProfileStep.BACK_ID_CARD)
        }
    }

    private fun handleActivityResult(result: Intent?) {
        when {
            result == null -> {
            }
            result.getStringExtra(Constants.KEY_BITMAP).isNullOrEmpty() -> {
                Toast.makeText(this, "Không phân tích được dữ liệu!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val captureStep: VerifyProfileStep =
                    result.getSerializableExtra(Constants.KEY_DATA) as VerifyProfileStep
                val imageFilePath = result.getStringExtra(Constants.KEY_BITMAP)!!
                val cardType = result.getStringExtra(Constants.KEY_ID_CARD_TYPE)

                when (captureStep) {
                    VerifyProfileStep.SELFIE -> {
                        binding.imageViewSelfie.loadWithRoundedCorners(imageFilePath)
                        binding.imageViewSelfie.setOnClickListener { showImage(imageFilePath) }
                    }
                    VerifyProfileStep.FRONT_ID_CARD -> {
                        binding.imageViewFrontIdCard.loadWithRoundedCorners(imageFilePath)
                        binding.textViewTypeFront.text = cardType.cardTypeToString()
                        binding.imageViewFrontIdCard.setOnClickListener { showImage(imageFilePath) }
                    }
                    VerifyProfileStep.BACK_ID_CARD -> {
                        binding.imageViewBackIdCard.loadWithRoundedCorners(imageFilePath)
                        binding.textViewTypeBack.text = cardType.cardTypeToString()
                        binding.imageViewBackIdCard.setOnClickListener { showImage(imageFilePath) }
                    }
                }
            }
        }
    }

    private fun showImage(url: String?) {
        if (!url.isNullOrEmpty()) {
            startActivity(
                Intent(this, ViewImageActivity::class.java).putExtra(
                    "data",
                    url
                )
            )
        }
    }

    private fun ImageView.loadWithRoundedCorners(imageFilePath: String) {
        load(File(imageFilePath)) {
            transformations(RoundedCornersTransformation(20f))
        }
    }

    private fun String?.cardTypeToString(): String {
        return when (this) {
            IdCardType.FRONT_IDENTITY_CARD -> "Mặt trước CMTND"
            IdCardType.BACK_IDENTITY_CARD -> "Mặt sau CMTND"
            IdCardType.FRONT_CITIZEN_IDENTIFICATION -> "Mặt trước CCCD"
            IdCardType.BACK_CITIZEN_IDENTIFICATION -> "Mặt sau CCCD"
            IdCardType.FRONT_CHIP_BASED_CITIZEN_IDENTIFICATION -> "Mặt trước CCCD gắn chip"
            IdCardType.BACK_CHIP_BASED_CITIZEN_IDENTIFICATION -> "Mặt sau CCCD gắn chip"
            else -> ""
        }
    }
}
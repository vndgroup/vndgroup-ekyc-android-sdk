## I. INTEGRATION:
### 1. Import EKYC AAR file into project. You can follow the instruction described here:
```
https://developer.android.com/studio/projects/android-library#psd-add-aar-jar-dependency.
```
### 2. Add required dependencies below into your app-level build.grade file:
```
- ...
- def camerax_version = "1.1.0-alpha07"
- implementation "androidx.camera:camera-camera2:$camerax_version"
- implementation "androidx.camera:camera-core:$camerax_version"
- implementation "androidx.camera:camera-lifecycle:$camerax_version"
- implementation 'androidx.camera:camera-view:1.0.0-alpha27'
- implementation 'com.google.android.gms:play-services-mlkit-face-detection:16.2.0'
- implementation 'org.tensorflow:tensorflow-lite-support:0.1.0'
- implementation 'org.tensorflow:tensorflow-lite-task-vision:0.1.0'
- implementation 'org.tensorflow:tensorflow-lite-metadata:0.1.0-rc2'
- implementation 'com.quickbirdstudios:opencv:4.3.0'
- implementation 'com.squareup.retrofit2:adapter-rxjava2:2.9.0'
- implementation "com.jakewharton.rxbinding2:rxbinding-kotlin:2.0.0"
- implementation 'com.jakewharton.rxbinding4:rxbinding:4.0.0'
- ...
```
### 3. You'll also need to add jcenter into your project-level build.gradle file if you haven't declared it yet:
    allprojects {
        repositories {
            google()
            mavenCentral()
            jcenter()
	    ...
        }
    }

## II. USAGE:
### 1. Initialize CameraX:
```
val cameraManager = CameraManager.Builder()
  .setActivity(this) // Set context for camera manager.
  .setCameraPreview(binding.previewView) // Bind camera's PreviewView.
  .setCameraMode(CameraMode.MODE_PHOTO) // There are 2 modes: MODE_PHOTO, MODE_VIDEO.
  .setCameraExecutor(ContextCompat.getMainExecutor(this)) // Set camera executor.
  .setCameraSelectorOption(
      if (mode == MODE_TAKE_SELFIE)
          CameraOption.LENS_FACING_FRONT else CameraOption.LENS_FACING_BACK
  )
  .setAspectRatio(
      if (mode == MODE_TAKE_SELFIE)
          Constants.RATIO_3_4 else Constants.RATIO_4_3
  )
  .setCallback(object : CameraManager.CameraCallback {
      override fun onSuccess(filePath: String) {
      }

      override fun onError(errorMessage: String?) {
          setActivityResult(false)
      }
  }) // Set callback which will be called when image has been captured or video has been recorded (Or an error has occured).
  .build()
```
### 2. Start the camera:
```
cameraManager.startCamera() // Start the camera.
```
### 3. Capture images & record videos:
```
	cameraManager.doAction()
```
+ Depends on camera mode and recording status. Calling this method will trigger one of following actions: take picture, start recording, stop recording. 

### 4. Initialize & use face detector:
+ Initialize:
```
val faceDetectionProcessor =
  FaceContourDetectionProcessor(
      object : FaceContourDetectionProcessor.Callback {
          override fun onAnalyzed(bitmap: Bitmap, analyzeResult: AnalyzeResult) {
              onDetectedFaceSuccessfully(bitmap, analyzeResult == AnalyzeResult.OK)
          }
      },
      borderMarginHorizontal.toFloat(), // In case you want to restrict user's face inside a box.
      borderMarginVertical.toFloat() // In case you want to restrict user's face inside a box.
  )
```
+ Usage:
```
	faceDetectionProcessor.analyze(binding.previewView)
```
or
```
	faceDetectionProcessor.analyze(bitmap)
```
+ The analyzed results will be returned on ```onAnalyzed()``` method.
+ AnalyzeResult:
```
- OK(0), // Detected valid face
- FAILED(1), // No faces found
- INVALID_POSITION(2), // Invalid face position (Outside of restricted box).
- INVALID_FACIAL_POSTURE(3), // Invalid facial posture (Face down or up, etc).
- LOW_EYES_OPEN_PROBABILITY(4), // Maybe users are closing their eyes. 
- MULTIPLE_FACES(5) // More than one face appear in the frame.
```
+ Stop the processor when you no longer need it:
   faceDetectionProcessor.stop()

### 5. Initialize & use id card detector:
+ Initialize:
```
val cornersDetectionProcessor = CardCornersDetectionProcessor(
  this,
  object : CardCornersDetectionProcessor.Callback {
      override fun onDetectedIdCardBitmap(result: AlignCardResult) { // Will be called when id card has been detected.
          onDetectedIdCardSuccessfully(result)
      }
  },
  borderMarginHorizontal.toFloat(), // In case you want to restrict user's id card inside a box.
  borderMarginVertical.toFloat(), // In case you want to restrict user's id card inside a box.
  isCapturingFrontIdCard // True if you want to capture front id card, false otherwise.
)
```
+ Usage:
```
cornersDetectionProcessor.process(bitmap)
```
+ The analyzed results will be returned on ```onDetectedIdCardBitmap()``` method.
+ AlignCardResult:
+ bitmap
+ IdCardType:
```
- FRONT_IDENTITY_CARD = "cmnd_mt"
- BACK_IDENTITY_CARD = "cmnd_ms"
- FRONT_CITIZEN_IDENTIFICATION = "cccd_mt"
- BACK_CITIZEN_IDENTIFICATION = "cccd_ms"
- FRONT_CHIP_BASED_CITIZEN_IDENTIFICATION = "cccd_chip_mt"
- BACK_CHIP_BASED_CITIZEN_IDENTIFICATION = "cccd_chip_ms"
```
- Stop the processor when you no longer need it:
```
cornersDetectionProcessor.stop()
```
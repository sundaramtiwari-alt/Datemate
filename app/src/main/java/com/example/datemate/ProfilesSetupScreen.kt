package com.example.datemate

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File

@Composable
fun ProfileSetupScreen(
    userName: String,
    userCity: String,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {

    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }

    var profileImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            profileImageUri = uri
            errorMessage = ""
        }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Complete your profile",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us a little about yourself",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // PROFILE PICTURE

        if (profileImageUri != null) {

            Image(
                painter = rememberAsyncImagePainter(
                    model = profileImageUri
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = "👤",
                fontSize = 70.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25252D)
            ),
            enabled = !isSaving
        ) {

            Text(
                text = if (profileImageUri == null) {
                    "Add Profile Photo"
                } else {
                    "Change Photo"
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {
                dateOfBirth = it
                errorMessage = ""
            },
            label = {
                Text("Date of Birth")
            },
            placeholder = {
                Text("DD/MM/YYYY")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = gender,
            onValueChange = {
                gender = it
                errorMessage = ""
            },
            label = {
                Text("Gender")
            },
            placeholder = {
                Text("Male / Female / Other")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = {
                bio = it
                errorMessage = ""
            },
            label = {
                Text("About you")
            },
            placeholder = {
                Text("Write something about yourself")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = interests,
            onValueChange = {
                interests = it
                errorMessage = ""
            },
            label = {
                Text("Interests")
            },
            placeholder = {
                Text("Music, Travel, Movies...")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                val currentUser = auth.currentUser

                if (currentUser == null) {

                    errorMessage =
                        "User account not found. Please login again."

                } else if (userName.isBlank()) {

                    errorMessage =
                        "Please enter your name."

                } else if (userCity.isBlank()) {

                    errorMessage =
                        "Please enter your city."

                } else if (dateOfBirth.isBlank()) {

                    errorMessage =
                        "Please enter your date of birth."

                } else if (gender.isBlank()) {

                    errorMessage =
                        "Please enter your gender."

                } else {

                    isSaving = true
                    errorMessage = ""

                    val selectedUri = profileImageUri

                    if (selectedUri == null) {

                        saveProfileToFirestore(
                            firestore = firestore,
                            currentUser = currentUser,
                            userName = userName,
                            userCity = userCity,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            bio = bio,
                            interests = interests,
                            profilePhotoUrl = "",
                            onSuccess = {
                                isSaving = false
                                onContinue()
                            },
                            onError = { message ->
                                isSaving = false
                                errorMessage = message
                            }
                        )

                    } else {

                        uploadImageToCloudinary(
                            uri = selectedUri,
                            onSuccess = { cloudinaryUrl ->

                                saveProfileToFirestore(
                                    firestore = firestore,
                                    currentUser = currentUser,
                                    userName = userName,
                                    userCity = userCity,
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    bio = bio,
                                    interests = interests,
                                    profilePhotoUrl = cloudinaryUrl,
                                    onSuccess = {
                                        isSaving = false
                                        onContinue()
                                    },
                                    onError = { message ->
                                        isSaving = false
                                        errorMessage = message
                                    }
                                )
                            },
                            onError = { message ->

                                isSaving = false
                                errorMessage = message
                            }
                        )
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4F81)
            ),
            enabled = !isSaving
        ) {

            Text(
                text = if (isSaving) {
                    "Saving..."
                } else {
                    "Continue"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {

            Text("Back")
        }
    }
}


// ----------------------------------------------------
// CLOUDINARY IMAGE UPLOAD
// ----------------------------------------------------

private fun uploadImageToCloudinary(
    uri: Uri,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {

    try {

        val context = DateMateApplication.instance

        val inputStream =
            context.contentResolver.openInputStream(uri)

        if (inputStream == null) {

            onError("Could not read selected image.")
            return
        }

        val tempFile = File(
            context.cacheDir,
            "profile_${System.currentTimeMillis()}.jpg"
        )

        inputStream.use { input ->

            tempFile.outputStream().use { output ->

                input.copyTo(output)
            }
        }

        val requestBody =
            tempFile
                .asRequestBody("image/*".toMediaType())

        val multipartBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    requestBody
                )
                .addFormDataPart(
                    "upload_preset",
                    "datemate_profiles"
                )
                .build()

        val request =
            Request.Builder()
                .url(
                    "https://api.cloudinary.com/v1_1/psfqosdc/image/upload"
                )
                .post(multipartBody)
                .build()

        Thread {

            try {

                val client = OkHttpClient()

                val response =
                    client.newCall(request).execute()

                val responseBody =
                    response.body?.string()

                if (!response.isSuccessful || responseBody == null) {

                    android.os.Handler(
                        android.os.Looper.getMainLooper()
                    ).post {

                        onError(
                            "Image upload failed."
                        )
                    }

                    return@Thread
                }

                val json =
                    JSONObject(responseBody)

                val secureUrl =
                    json.getString("secure_url")

                android.os.Handler(
                    android.os.Looper.getMainLooper()
                ).post {

                    onSuccess(secureUrl)
                }

            } catch (e: Exception) {

                android.os.Handler(
                    android.os.Looper.getMainLooper()
                ).post {

                    onError(
                        e.message
                            ?: "Image upload failed."
                    )
                }

            } finally {

                tempFile.delete()
            }

        }.start()

    } catch (e: Exception) {

        onError(
            e.message
                ?: "Could not prepare image."
        )
    }
}


// ----------------------------------------------------
// SAVE PROFILE TO FIRESTORE
// ----------------------------------------------------

private fun saveProfileToFirestore(
    firestore: FirebaseFirestore,
    currentUser: com.google.firebase.auth.FirebaseUser,
    userName: String,
    userCity: String,
    dateOfBirth: String,
    gender: String,
    bio: String,
    interests: String,
    profilePhotoUrl: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    val userProfile =
        hashMapOf(
            "uid" to currentUser.uid,
            "email" to (currentUser.email ?: ""),
            "name" to userName,
            "city" to userCity,
            "dateOfBirth" to dateOfBirth,
            "gender" to gender,
            "bio" to bio,
            "interests" to interests,
            "profilePhoto" to profilePhotoUrl,
            "createdAt" to System.currentTimeMillis()
        )

    firestore
        .collection("users")
        .document(currentUser.uid)
        .set(userProfile)
        .addOnSuccessListener {

            onSuccess()
        }
        .addOnFailureListener { exception ->

            onError(
                exception.message
                    ?: "Profile could not be saved."
            )
        }
}
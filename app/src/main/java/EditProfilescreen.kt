package com.example.datemate

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }

    var existingPhotoUrl by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedPhotoUri = uri
                errorMessage = ""
            }
        }

    // =========================
    // LOAD PROFILE
    // =========================

    LaunchedEffect(currentUser?.uid) {

        if (currentUser == null) {
            isLoading = false
            errorMessage = "User is not logged in."
            return@LaunchedEffect
        }

        firestore
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                name = document.getString("name") ?: ""
                city = document.getString("city") ?: ""
                dateOfBirth = document.getString("dateOfBirth") ?: ""
                gender = document.getString("gender") ?: ""
                bio = document.getString("bio") ?: ""
                interests = document.getString("interests") ?: ""
                existingPhotoUrl =
                    document.getString("profilePhoto") ?: ""

                isLoading = false
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: "Could not load profile."
            }
    }

    // =========================
    // LOADING SCREEN
    // =========================

    if (isLoading) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F14)),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                color = Color(0xFFFF4F81)
            )
        }

        return
    }

    // =========================
    // MAIN SCREEN
    // =========================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Edit Profile",
            fontSize = 30.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(25.dp))

        // =========================
        // PROFILE PHOTO
        // =========================

        when {

            selectedPhotoUri != null -> {

                Image(
                    painter = rememberAsyncImagePainter(
                        model = selectedPhotoUri
                    ),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            existingPhotoUrl.isNotEmpty() -> {

                Image(
                    painter = rememberAsyncImagePainter(
                        model = existingPhotoUrl
                    ),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF292933)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "👤",
                        fontSize = 60.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
            },
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25252D)
            )
        ) {

            Text("Change Photo")
        }

        Spacer(modifier = Modifier.height(25.dp))

        // =========================
        // NAME
        // =========================

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = ""
            },
            label = {
                Text("Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        // =========================
        // CITY
        // =========================

        OutlinedTextField(
            value = city,
            onValueChange = {
                city = it
                errorMessage = ""
            },
            label = {
                Text("City")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        // =========================
        // DATE OF BIRTH
        // =========================

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {
                dateOfBirth = it
                errorMessage = ""
            },
            label = {
                Text("Date of Birth")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        // =========================
        // GENDER
        // =========================

        OutlinedTextField(
            value = gender,
            onValueChange = {
                gender = it
                errorMessage = ""
            },
            label = {
                Text("Gender")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        // =========================
        // BIO
        // =========================

        OutlinedTextField(
            value = bio,
            onValueChange = {
                bio = it
                errorMessage = ""
            },
            label = {
                Text("About you")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(14.dp))

        // =========================
        // INTERESTS
        // =========================

        OutlinedTextField(
            value = interests,
            onValueChange = {
                interests = it
                errorMessage = ""
            },
            label = {
                Text("Interests")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(18.dp))

        // =========================
        // ERROR
        // =========================

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // =========================
        // UPDATE PROFILE
        // =========================

        Button(
            onClick = {

                if (currentUser == null) {

                    errorMessage = "User is not logged in."
                    return@Button
                }

                if (name.isBlank()) {

                    errorMessage = "Name cannot be empty."
                    return@Button
                }

                if (city.isBlank()) {

                    errorMessage = "City cannot be empty."
                    return@Button
                }

                isSaving = true
                errorMessage = ""

                scope.launch {

                    try {

                        var finalPhotoUrl = existingPhotoUrl

                        // Upload selected photo to Cloudinary
                        if (selectedPhotoUri != null) {

                            finalPhotoUrl = uploadImageToCloudinary(
                                selectedPhotoUri!!
                            )
                        }

                        // =========================
                        // FIRESTORE UPDATE
                        // =========================

                        val updatedProfile =
                            hashMapOf(
                                "uid" to currentUser.uid,
                                "email" to (currentUser.email ?: ""),
                                "name" to name.trim(),
                                "city" to city.trim(),
                                "dateOfBirth" to dateOfBirth.trim(),
                                "gender" to gender.trim(),
                                "bio" to bio.trim(),
                                "interests" to interests.trim(),
                                "profilePhoto" to finalPhotoUrl
                            )

                        firestore
                            .collection("users")
                            .document(currentUser.uid)
                            .set(
                                updatedProfile,
                                com.google.firebase.firestore.SetOptions.merge()
                            )
                            .addOnSuccessListener {

                                isSaving = false

                                onSaved()
                            }
                            .addOnFailureListener { exception ->

                                isSaving = false

                                errorMessage =
                                    exception.message
                                        ?: "Profile update failed."
                            }

                    } catch (exception: Exception) {

                        isSaving = false

                        errorMessage =
                            exception.message
                                ?: "Photo upload failed."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4F81)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {

            if (isSaving) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text("Updating...")

            } else {

                Text(
                    text = "Update Profile",
                    fontSize = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // =========================
        // CANCEL
        // =========================

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled = !isSaving,
            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Cancel",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}


// =====================================================
// CLOUDINARY UPLOAD FUNCTION
// =====================================================

suspend fun uploadImageToCloudinary(
    imageUri: Uri
): String {

    return withContext(Dispatchers.IO) {

        val context =
            DateMateApplication.instance

        val inputStream =
            context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Could not open selected image.")

        val url =
            URL(
                "https://api.cloudinary.com/v1_1/psfqosdc/image/upload"
            )

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.doInput = true

        val boundary =
            "----DateMateBoundary${System.currentTimeMillis()}"

        connection.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )

        val outputStream =
            connection.outputStream

        fun writeText(
            name: String,
            value: String
        ) {

            outputStream.write(
                "--$boundary\r\n".toByteArray()
            )

            outputStream.write(
                "Content-Disposition: form-data; name=\"$name\"\r\n\r\n"
                    .toByteArray()
            )

            outputStream.write(
                "$value\r\n".toByteArray()
            )
        }

        writeText(
            "upload_preset",
            "datemate_profiles"
        )

        outputStream.write(
            "--$boundary\r\n".toByteArray()
        )

        outputStream.write(
            "Content-Disposition: form-data; name=\"file\"; filename=\"profile.jpg\"\r\n"
                .toByteArray()
        )

        outputStream.write(
            "Content-Type: image/jpeg\r\n\r\n"
                .toByteArray()
        )

        inputStream.copyTo(outputStream)

        outputStream.write(
            "\r\n--$boundary--\r\n".toByteArray()
        )

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        val responseCode =
            connection.responseCode

        val responseStream =
            if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

        val response =
            BufferedReader(
                InputStreamReader(responseStream)
            ).use {
                it.readText()
            }

        connection.disconnect()

        if (responseCode !in 200..299) {

            throw Exception(
                "Cloudinary upload failed: $response"
            )
        }

        val json =
            JSONObject(response)

        json.getString("secure_url")
    }
}
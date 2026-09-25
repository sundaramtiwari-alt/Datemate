package com.example.datemate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class DatingProfile(
    val uid: String = "",
    val name: String = "",
    val city: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val bio: String = "",
    val interests: String = "",
    val profilePhoto: String = ""
)

@Composable
fun DiscoverScreen() {

    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var profiles by remember {
        mutableStateOf<List<DatingProfile>>(emptyList())
    }

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var matchMessage by remember {
        mutableStateOf("")
    }

    val scrollState = rememberScrollState()

    // ======================================
    // LOAD PROFILES
    // ======================================

    LaunchedEffect(currentUser?.uid) {

        if (currentUser == null) {

            isLoading = false
            errorMessage = "You are not logged in."

            return@LaunchedEffect
        }

        firestore
            .collection("users")
            .get()
            .addOnSuccessListener { result ->

                val loadedProfiles =
                    result.documents.mapNotNull { document ->

                        val uid =
                            document.getString("uid")
                                ?: document.id

                        if (uid == currentUser.uid) {
                            return@mapNotNull null
                        }

                        DatingProfile(
                            uid = uid,
                            name = document.getString("name") ?: "",
                            city = document.getString("city") ?: "",
                            dateOfBirth =
                                document.getString("dateOfBirth") ?: "",
                            gender =
                                document.getString("gender") ?: "",
                            bio =
                                document.getString("bio") ?: "",
                            interests =
                                document.getString("interests") ?: "",
                            profilePhoto =
                                document.getString("profilePhoto") ?: ""
                        )
                    }

                profiles = loadedProfiles
                currentIndex = 0
                isLoading = false
            }
            .addOnFailureListener { exception ->

                isLoading = false

                errorMessage =
                    exception.message
                        ?: "Could not load profiles."
            }
    }

    // ======================================
    // NEXT PROFILE
    // ======================================

    fun goToNextProfile() {

        if (currentIndex < profiles.lastIndex) {

            currentIndex++

        } else {

            currentIndex = profiles.size
        }

        isProcessing = false
    }

    // ======================================
    // PASS
    // ======================================

    fun passProfile(profile: DatingProfile) {

        if (isProcessing) {
            return
        }

        isProcessing = true
        matchMessage = ""

        goToNextProfile()
    }

    // ======================================
    // LIKE
    // ======================================

    fun likeProfile(profile: DatingProfile) {

        if (currentUser == null) {

            errorMessage = "You are not logged in."
            return
        }

        if (isProcessing) {
            return
        }

        isProcessing = true
        errorMessage = ""
        matchMessage = ""

        val currentUid = currentUser.uid
        val targetUid = profile.uid

        // Our Like document
        val likeId = "${currentUid}_${targetUid}"

        val likeData = hashMapOf(
            "likerId" to currentUid,
            "targetId" to targetUid,
            "createdAt" to System.currentTimeMillis()
        )

        // ======================================
        // SAVE OUR LIKE
        // ======================================

        firestore
            .collection("likes")
            .document(likeId)
            .set(likeData)
            .addOnSuccessListener {

                // ======================================
                // CHECK REVERSE LIKE
                // ======================================

                val reverseLikeId =
                    "${targetUid}_${currentUid}"

                firestore
                    .collection("likes")
                    .document(reverseLikeId)
                    .get()
                    .addOnSuccessListener { reverseLike ->

                        // ======================================
                        // MUTUAL LIKE
                        // ======================================

                        if (reverseLike.exists()) {

                            val matchId =
                                if (currentUid < targetUid) {
                                    "${currentUid}_${targetUid}"
                                } else {
                                    "${targetUid}_${currentUid}"
                                }

                            val matchData =
                                hashMapOf(
                                    "userIds" to listOf(
                                        currentUid,
                                        targetUid
                                    ),
                                    "createdAt" to
                                            System.currentTimeMillis()
                                )

                            firestore
                                .collection("matches")
                                .document(matchId)
                                .set(matchData)
                                .addOnSuccessListener {

                                    matchMessage =
                                        "💕 It's a Match with ${profile.name}!"

                                    goToNextProfile()
                                }
                                .addOnFailureListener { exception ->

                                    errorMessage =
                                        exception.message
                                            ?: "Could not create match."

                                    isProcessing = false
                                }

                        } else {

                            // ======================================
                            // NORMAL LIKE
                            // ======================================

                            goToNextProfile()
                        }
                    }
                    .addOnFailureListener { exception ->

                        errorMessage =
                            exception.message
                                ?: "Could not check match."

                        isProcessing = false
                    }
            }
            .addOnFailureListener { exception ->

                errorMessage =
                    exception.message
                        ?: "Could not send like."

                isProcessing = false
            }
    }

    // ======================================
    // MAIN UI
    // ======================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
    ) {

        when {

            // ======================================
            // LOADING
            // ======================================

            isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Color(0xFFFF4F81)
                    )
                }
            }

            // ======================================
            // ERROR
            // ======================================

            errorMessage.isNotEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Something went wrong",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF6B6B),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ======================================
            // NO PROFILES
            // ======================================

            profiles.isEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "💕",
                            fontSize = 60.sp
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Text(
                            text = "No profiles yet",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Create another account to test DateMate.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ======================================
            // EVERYONE SEEN
            // ======================================

            currentIndex >= profiles.size -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🎉",
                            fontSize = 65.sp
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Text(
                            text = "You've seen everyone!",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Come back later for more profiles.",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ======================================
            // PROFILE
            // ======================================

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(
                            horizontal = 20.dp,
                            vertical = 18.dp
                        )
                ) {

                    Text(
                        text = "Discover",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Find someone who matches your vibe.",
                        fontSize = 15.sp,
                        color = Color(0xFF9E9EA8)
                    )

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    if (matchMessage.isNotEmpty()) {

                        Text(
                            text = matchMessage,
                            color = Color(0xFFFF4F81),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )
                    }

                    ProfileCard(
                        profile = profiles[currentIndex]
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = {

                                passProfile(
                                    profiles[currentIndex]
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            enabled = !isProcessing,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF292933)
                                ),
                            shape =
                                RoundedCornerShape(16.dp)
                        ) {

                            Text(
                                text = "❌ Pass",
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = {

                                likeProfile(
                                    profiles[currentIndex]
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            enabled = !isProcessing,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFFFF4F81)
                                ),
                            shape =
                                RoundedCornerShape(16.dp)
                        ) {

                            if (isProcessing) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Text(
                                    text = "❤️ Like",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}


// ======================================
// PROFILE CARD
// ======================================

@Composable
fun ProfileCard(
    profile: DatingProfile
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(24.dp)
            )
            .background(
                Color(0xFF1B1B23)
            )
            .padding(18.dp)
    ) {

        if (profile.profilePhoto.isNotEmpty()) {

            Image(
                painter =
                    rememberAsyncImagePainter(
                        model = profile.profilePhoto
                    ),
                contentDescription =
                    "Profile Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ),
                contentScale =
                    ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(
                        Color(0xFF292933)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "👤",
                    fontSize = 90.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            text =
                if (profile.name.isNotEmpty())
                    profile.name
                else
                    "Unknown",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                "${profile.gender} • ${profile.city}",
            fontSize = 15.sp,
            color = Color(0xFFB5B5BE)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (profile.bio.isNotEmpty()) {

            Text(
                text = profile.bio,
                fontSize = 16.sp,
                color = Color(0xFFE1E1E6),
                lineHeight = 23.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        if (profile.interests.isNotEmpty()) {

            Text(
                text = "Interests",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = profile.interests,
                fontSize = 15.sp,
                color = Color(0xFFB5B5BE)
            )
        }
    }
}
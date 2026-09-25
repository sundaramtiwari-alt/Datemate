package com.example.datemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.datemate.ui.theme.DatemateTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// ======================================================
// MAIN ACTIVITY
// ======================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            DatemateTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F0F14)
                ) {

                    DateMateApp()
                }
            }
        }
    }
}


// ======================================================
// MAIN APP NAVIGATION
// ======================================================

@Composable
fun DateMateApp() {

    // ==================================================
    // CHECK IF USER IS ALREADY LOGGED IN
    // ==================================================

    val firebaseUser =
        FirebaseAuth
            .getInstance()
            .currentUser


    var currentScreen by remember {

        mutableStateOf(

            if (firebaseUser != null)
                "home"
            else
                "welcome"
        )
    }


    var userName by remember {
        mutableStateOf("")
    }

    var userCity by remember {
        mutableStateOf("")
    }


    when (currentScreen) {

        // ----------------------------------------------
        // WELCOME
        // ----------------------------------------------

        "welcome" -> {

            WelcomeScreen(

                onCreateAccount = {

                    currentScreen =
                        "createAccount"
                },

                onLogin = {

                    currentScreen =
                        "login"
                }
            )
        }


        // ----------------------------------------------
        // CREATE ACCOUNT
        // ----------------------------------------------

        "createAccount" -> {

            CreateAccountScreen(

                onBack = {

                    currentScreen =
                        "welcome"
                },

                onContinue = { name, city ->

                    userName = name

                    userCity = city

                    currentScreen =
                        "accountDetails"
                }
            )
        }


        // ----------------------------------------------
        // ACCOUNT DETAILS
        // ----------------------------------------------

        "accountDetails" -> {

            AccountDetailsScreen(

                onBack = {

                    currentScreen =
                        "createAccount"
                },

                onContinue = {

                    currentScreen =
                        "profileSetup"
                }
            )
        }


        // ----------------------------------------------
        // PROFILE SETUP
        // ----------------------------------------------

        "profileSetup" -> {

            ProfileSetupScreen(

                userName =
                    userName,

                userCity =
                    userCity,

                onBack = {

                    currentScreen =
                        "accountDetails"
                },

                onContinue = {

                    currentScreen =
                        "home"
                }
            )
        }


        // ----------------------------------------------
        // LOGIN
        // ----------------------------------------------

        "login" -> {

            LoginScreen(

                onBack = {

                    currentScreen =
                        "welcome"
                },

                onLoginSuccess = {

                    currentScreen =
                        "home"
                }
            )
        }


        // ----------------------------------------------
        // HOME
        // ----------------------------------------------

        "home" -> {

            MainAppScreen(

                onEditProfile = {

                    currentScreen =
                        "editProfile"
                },

                onSettings = {

                    currentScreen =
                        "settings"
                },

                onPrivacySafety = {

                    currentScreen =
                        "privacySafety"
                },

                onHelpContact = {

                    currentScreen =
                        "helpContact"
                },

                onLogout = {

                    FirebaseAuth
                        .getInstance()
                        .signOut()

                    currentScreen =
                        "welcome"
                }
            )
        }


        // ----------------------------------------------
        // EDIT PROFILE
        // ----------------------------------------------

        "editProfile" -> {

            EditProfileScreen(

                onBack = {

                    currentScreen =
                        "home"
                },

                onSaved = {

                    currentScreen =
                        "home"
                }
            )
        }


        // ----------------------------------------------
        // SETTINGS
        // ----------------------------------------------

        "settings" -> {

            SettingsScreen(

                onBack = {

                    currentScreen =
                        "home"
                }
            )
        }


        // ----------------------------------------------
        // PRIVACY & SAFETY
        // ----------------------------------------------

        "privacySafety" -> {

            PrivacySafetyScreen(

                onBack = {

                    currentScreen =
                        "home"
                }
            )
        }


        // ----------------------------------------------
        // HELP & CONTACT
        // ----------------------------------------------

        "helpContact" -> {

            HelpContactScreen(

                onBack = {

                    currentScreen =
                        "home"
                }
            )
        }
    }
}


// ======================================================
// WELCOME SCREEN
// ======================================================

@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "DateMate",

            fontSize = 42.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color(0xFFFF4F81)
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "Meet. Match. Connect.",

            fontSize =
                18.sp,

            color =
                Color.White
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text =
                "Find people and build meaningful connections.",

            fontSize =
                14.sp,

            color =
                Color(0xFFAAAAAF)
        )

        Spacer(
            modifier =
                Modifier.height(45.dp)
        )

        Button(
            onClick =
                onCreateAccount,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Color(0xFFFF4F81)
                ),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Text(
                text =
                    "Create Account",

                fontSize =
                    16.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(15.dp)
        )

        OutlinedButton(
            onClick =
                onLogin,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Text(
                text =
                    "Login",

                fontSize =
                    16.sp,

                color =
                    Color.White
            )
        }
    }
}


// ======================================================
// MAIN APP SCREEN
// ======================================================

@Composable
fun MainAppScreen(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onPrivacySafety: () -> Unit,
    onHelpContact: () -> Unit,
    onLogout: () -> Unit
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }


    // User currently selected for chat
    var selectedChatUserId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedChatUserName by remember {
        mutableStateOf("")
    }


    Scaffold(

        containerColor =
            Color(0xFF0F0F14),

        bottomBar = {

            // Hide bottom navigation while chat is open
            if (selectedChatUserId == null) {

                NavigationBar(
                    containerColor =
                        Color(0xFF18181F)
                ) {

                    // DISCOVER

                    NavigationBarItem(

                        selected =
                            selectedTab == 0,

                        onClick = {

                            selectedTab =
                                0
                        },

                        icon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Search,

                                contentDescription =
                                    "Discover"
                            )
                        },

                        label = {

                            Text(
                                "Discover"
                            )
                        },

                        colors =
                            navigationColors()
                    )


                    // MATCHES

                    NavigationBarItem(

                        selected =
                            selectedTab == 1,

                        onClick = {

                            selectedTab =
                                1
                        },

                        icon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Favorite,

                                contentDescription =
                                    "Matches"
                            )
                        },

                        label = {

                            Text(
                                "Matches"
                            )
                        },

                        colors =
                            navigationColors()
                    )


                    // CHAT

                    NavigationBarItem(

                        selected =
                            selectedTab == 2,

                        onClick = {

                            selectedTab =
                                2
                        },

                        icon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Chat,

                                contentDescription =
                                    "Chat"
                            )
                        },

                        label = {

                            Text(
                                "Chat"
                            )
                        },

                        colors =
                            navigationColors()
                    )


                    // PROFILE

                    NavigationBarItem(

                        selected =
                            selectedTab == 3,

                        onClick = {

                            selectedTab =
                                3
                        },

                        icon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,

                                contentDescription =
                                    "Profile"
                            )
                        },

                        label = {

                            Text(
                                "Profile"
                            )
                        },

                        colors =
                            navigationColors()
                    )
                }
            }
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(

                    if (
                        selectedChatUserId == null
                    )
                        innerPadding
                    else
                        PaddingValues(0.dp)
                )
        ) {

            // ==================================================
            // CHAT SCREEN
            // ==================================================

            if (
                selectedChatUserId != null
            ) {

                ChatScreen(

                    otherUserId =
                        selectedChatUserId!!,

                    otherUserName =
                        selectedChatUserName,

                    onBack = {

                        selectedChatUserId =
                            null

                        selectedChatUserName =
                            ""

                        selectedTab =
                            1
                    }
                )

            } else {

                // ==================================================
                // NORMAL TABS
                // ==================================================

                when (selectedTab) {

                    // DISCOVER
                    0 -> {

                        DiscoverScreen()
                    }


                    // MATCHES
                    1 -> {

                        MatchesScreen(

                            onMatchClick = {
                                    userId,
                                    userName ->

                                selectedChatUserId =
                                    userId

                                selectedChatUserName =
                                    userName
                            }
                        )
                    }


                    // CHAT
                    2 -> {

                        ChatListScreen(

                            onChatClick = {
                                    userId,
                                    userName ->

                                selectedChatUserId =
                                    userId

                                selectedChatUserName =
                                    userName
                            }
                        )
                    }


                    // PROFILE
                    3 -> {

                        ProfileScreen(

                            onEditProfile =
                                onEditProfile,

                            onSettings =
                                onSettings,

                            onPrivacySafety =
                                onPrivacySafety,

                            onHelpContact =
                                onHelpContact,

                            onLogout =
                                onLogout
                        )
                    }
                }
            }
        }
    }
}


// ======================================================
// NAVIGATION COLORS
// ======================================================

@Composable
fun navigationColors():
        NavigationBarItemColors {

    return NavigationBarItemDefaults.colors(

        selectedIconColor =
            Color(0xFFFF4F81),

        selectedTextColor =
            Color(0xFFFF4F81),

        unselectedIconColor =
            Color.Gray,

        unselectedTextColor =
            Color.Gray,

        indicatorColor =
            Color(0xFF30202A)
    )
}


// ======================================================
// MATCHES SCREEN
// ======================================================

@Composable
fun MatchesScreen(
    onMatchClick: (String, String) -> Unit
) {

    val firestore =
        FirebaseFirestore.getInstance()

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    var matches by remember {
        mutableStateOf<List<String>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scrollState =
        rememberScrollState()


    // ==============================================
    // REAL-TIME MATCH LISTENER
    // ==============================================

    DisposableEffect(currentUser?.uid) {

        if (currentUser == null) {

            isLoading =
                false

            onDispose { }

        } else {

            val listener =
                firestore
                    .collection("matches")
                    .whereArrayContains(
                        "userIds",
                        currentUser.uid
                    )
                    .addSnapshotListener {
                            result,
                            error ->

                        if (error != null) {

                            isLoading =
                                false

                            errorMessage =
                                error.message
                                    ?: "Could not load matches."

                            return@addSnapshotListener
                        }

                        if (result == null) {

                            isLoading =
                                false

                            errorMessage =
                                "Could not load matches."

                            return@addSnapshotListener
                        }

                        val otherUserIds =
                            result.documents.mapNotNull {
                                    document ->

                                val userIds =
                                    document.get(
                                        "userIds"
                                    ) as? List<*>

                                val validUserIds =
                                    userIds
                                        ?.filterIsInstance<String>()
                                        ?: emptyList()

                                validUserIds.firstOrNull {
                                        userId ->

                                    userId !=
                                            currentUser.uid
                                }
                            }

                        matches =
                            otherUserIds.distinct()

                        errorMessage =
                            ""

                        isLoading =
                            false
                    }

            onDispose {
                listener.remove()
            }
        }
    }


    // ==============================================
    // UI
    // ==============================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .verticalScroll(
                scrollState
            )
            .padding(24.dp)
    ) {

        Text(
            text =
                "Matches",

            fontSize =
                32.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "People who liked you back.",

            fontSize =
                16.sp,

            color =
                Color(0xFF9E9EA8)
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        Color(0xFFFF4F81)
                )
            }
        }

        else if (
            errorMessage.isNotEmpty()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Something went wrong",

                        fontSize =
                            20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            errorMessage,

                        fontSize =
                            14.sp,

                        color =
                            Color(0xFFFF6B6B)
                    )
                }
            }
        }

        else if (
            matches.isEmpty()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "❤️",

                        fontSize =
                            60.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )

                    Text(
                        text =
                            "No matches yet",

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Keep discovering people!",

                        fontSize =
                            15.sp,

                        color =
                            Color.Gray
                    )
                }
            }
        }

        else {

            Column(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                matches.forEach { userId ->

                    MatchUserCard(

                        userId =
                            userId,

                        onClick = { name ->

                            onMatchClick(
                                userId,
                                name
                            )
                        }
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


// ======================================================
// MATCH USER CARD
// ======================================================

@Composable
fun MatchUserCard(
    userId: String,
    onClick: (String) -> Unit
) {

    var name by remember(userId) {
        mutableStateOf("Loading...")
    }

    var city by remember(userId) {
        mutableStateOf("")
    }

    var photoUrl by remember(userId) {
        mutableStateOf("")
    }

    var profileError by remember(userId) {
        mutableStateOf("")
    }


    LaunchedEffect(userId) {

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    name =
                        document.getString(
                            "name"
                        ) ?: "Unknown"

                    city =
                        document.getString(
                            "city"
                        ) ?: ""

                    photoUrl =
                        document.getString(
                            "profilePhoto"
                        ) ?: ""

                } else {

                    name =
                        "Unknown User"
                }
            }
            .addOnFailureListener {
                    exception ->

                name =
                    "Could not load profile"

                profileError =
                    exception.message ?: ""
            }
    }


    Surface(
        onClick = {

            if (
                name.isNotEmpty() &&
                name != "Loading..." &&
                name != "Unknown User"
            ) {

                onClick(name)
            }
        },

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            if (photoUrl.isNotEmpty()) {

                Image(
                    painter =
                        rememberAsyncImagePainter(
                            model =
                                photoUrl
                        ),

                    contentDescription =
                        "Match Photo",

                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),

                    contentScale =
                        ContentScale.Crop
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFF292933)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "👤",

                        fontSize =
                            35.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(15.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        name,

                    fontSize =
                        19.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )

                if (city.isNotEmpty()) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            city,

                        fontSize =
                            14.sp,

                        color =
                            Color.Gray
                    )
                }

                if (
                    profileError.isNotEmpty()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            profileError,

                        fontSize =
                            11.sp,

                        color =
                            Color(0xFFFF6B6B)
                    )
                }
            }


            Text(
                text =
                    "💬",

                fontSize =
                    24.sp
            )
        }
    }
}


// ======================================================
// CHAT LIST SCREEN
// ======================================================

@Composable
fun ChatListScreen(
    onChatClick: (String, String) -> Unit
) {

    val firestore =
        FirebaseFirestore.getInstance()

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    var chats by remember {

        mutableStateOf<
                List<com.google.firebase.firestore.DocumentSnapshot>
                >(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    DisposableEffect(currentUser?.uid) {

        if (currentUser == null) {

            isLoading =
                false

            onDispose { }

        } else {

            val listener =
                firestore
                    .collection("chats")
                    .whereArrayContains(
                        "userIds",
                        currentUser.uid
                    )
                    .addSnapshotListener {
                            result,
                            error ->

                        if (error != null) {

                            isLoading =
                                false

                            errorMessage =
                                error.message
                                    ?: "Could not load chats."

                            return@addSnapshotListener
                        }

                        if (result == null) {

                            isLoading =
                                false

                            errorMessage =
                                "Could not load chats."

                            return@addSnapshotListener
                        }


                        chats =
                            result.documents
                                .sortedByDescending {

                                    it.getLong(
                                        "lastMessageAt"
                                    ) ?: 0L
                                }

                        errorMessage =
                            ""

                        isLoading =
                            false
                    }


            onDispose {
                listener.remove()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .padding(24.dp)
    ) {

        Text(
            text =
                "Chats",

            fontSize =
                32.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "Your conversations",

            fontSize =
                16.sp,

            color =
                Color(0xFF9E9EA8)
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        Color(0xFFFF4F81)
                )
            }
        }

        else if (
            errorMessage.isNotEmpty()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Chat,

                        contentDescription =
                            null,

                        tint =
                            Color(0xFFFF6B6B),

                        modifier =
                            Modifier.size(50.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Something went wrong",

                        fontSize =
                            20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            errorMessage,

                        fontSize =
                            14.sp,

                        color =
                            Color(0xFFFF6B6B)
                    )
                }
            }
        }

        else if (
            chats.isEmpty()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Chat,

                        contentDescription =
                            null,

                        tint =
                            Color(0xFF666670),

                        modifier =
                            Modifier.size(60.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )

                    Text(
                        text =
                            "No conversations",

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Match with someone to start chatting.",

                        fontSize =
                            15.sp,

                        color =
                            Color.Gray
                    )
                }
            }
        }

        else {

            Column(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                chats.forEach { chatDocument ->

                    val userIds =
                        chatDocument.get(
                            "userIds"
                        ) as? List<*>
                            ?: emptyList<Any>()


                    val otherUserId =
                        userIds
                            .filterIsInstance<String>()
                            .firstOrNull {
                                    userId ->

                                userId !=
                                        currentUser?.uid
                            }


                    if (
                        otherUserId != null
                    ) {

                        ChatListItem(

                            chatDocument =
                                chatDocument,

                            otherUserId =
                                otherUserId,

                            onClick = {
                                    userName ->

                                onChatClick(
                                    otherUserId,
                                    userName
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


// ======================================================
// CHAT LIST ITEM
// ======================================================

@Composable
fun ChatListItem(
    chatDocument:
    com.google.firebase.firestore.DocumentSnapshot,

    otherUserId:
    String,

    onClick:
        (String) -> Unit
) {

    val firestore =
        FirebaseFirestore.getInstance()

    var userName by remember(
        otherUserId
    ) {
        mutableStateOf("Loading...")
    }

    var profilePhoto by remember(
        otherUserId
    ) {
        mutableStateOf("")
    }


    LaunchedEffect(otherUserId) {

        firestore
            .collection("users")
            .document(otherUserId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    userName =
                        document.getString(
                            "name"
                        ) ?: "User"

                    profilePhoto =
                        document.getString(
                            "profilePhoto"
                        ) ?: ""

                } else {

                    userName =
                        "Unknown User"
                }
            }
            .addOnFailureListener {

                userName =
                    "Unknown User"
            }
    }


    val lastMessage =
        chatDocument.getString(
            "lastMessage"
        ) ?: "Start a conversation"


    Surface(

        onClick = {

            if (
                userName.isNotEmpty() &&
                userName != "Loading..." &&
                userName != "Unknown User"
            ) {

                onClick(userName)
            }
        },

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            if (
                profilePhoto.isNotEmpty()
            ) {

                Image(

                    painter =
                        rememberAsyncImagePainter(
                            model =
                                profilePhoto
                        ),

                    contentDescription =
                        userName,

                    modifier = Modifier
                        .size(62.dp)
                        .clip(
                            CircleShape
                        ),

                    contentScale =
                        ContentScale.Crop
                )

            } else {

                Box(

                    modifier = Modifier
                        .size(62.dp)
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color(0xFF292933)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "👤",

                        fontSize =
                            32.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(15.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        userName,

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White,

                    maxLines =
                        1
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        lastMessage,

                    fontSize =
                        14.sp,

                    color =
                        Color(0xFF9E9EA8),

                    maxLines =
                        1
                )
            }


            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )


            Icon(
                imageVector =
                    Icons.Default.Chat,

                contentDescription =
                    "Open Chat",

                tint =
                    Color(0xFFFF4F81),

                modifier =
                    Modifier.size(24.dp)
            )
        }
    }
}


// ======================================================
// PROFILE SCREEN
// ======================================================

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onPrivacySafety: () -> Unit,
    onHelpContact: () -> Unit,
    onLogout: () -> Unit
) {

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    var name by remember {
        mutableStateOf("")
    }

    var city by remember {
        mutableStateOf("")
    }

    var profilePhoto by remember {
        mutableStateOf("")
    }

    val scrollState =
        rememberScrollState()


    // LOAD PROFILE

    LaunchedEffect(
        currentUser?.uid
    ) {

        if (currentUser != null) {

            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(
                    currentUser.uid
                )
                .get()
                .addOnSuccessListener {
                        document ->

                    name =
                        document.getString(
                            "name"
                        ) ?: ""

                    city =
                        document.getString(
                            "city"
                        ) ?: ""

                    profilePhoto =
                        document.getString(
                            "profilePhoto"
                        ) ?: ""
                }
        }
    }


    // PROFILE UI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .verticalScroll(
                scrollState
            )
            .padding(24.dp)
    ) {

        Text(
            text =
                "Profile",

            fontSize =
                32.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // PROFILE PHOTO

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Color(0xFF1B1B23),
                    RoundedCornerShape(20.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            if (
                profilePhoto.isNotEmpty()
            ) {

                Image(
                    painter =
                        rememberAsyncImagePainter(
                            model =
                                profilePhoto
                        ),

                    contentDescription =
                        "Profile Photo",

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        ),

                    contentScale =
                        ContentScale.Crop
                )

            } else {

                Text(
                    text =
                        "👤",

                    fontSize =
                        70.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // NAME

        Text(
            text =
                if (
                    name.isNotEmpty()
                )
                    name
                else
                    "Your Name",

            fontSize =
                25.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )


        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        // CITY

        Text(
            text =
                if (
                    city.isNotEmpty()
                )
                    city
                else
                    "Your City",

            fontSize =
                15.sp,

            color =
                Color(0xFFAAAAAF)
        )


        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        // EMAIL

        Text(
            text =
                currentUser?.email
                    ?: "No email",

            fontSize =
                15.sp,

            color =
                Color.Gray
        )


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // EDIT PROFILE

        Button(
            onClick =
                onEditProfile,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Color(0xFFFF4F81)
                ),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Text(
                text =
                    "Edit Profile",

                fontSize =
                    16.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        // SETTINGS

        ProfileOption(
            title =
                "Settings",

            subtitle =
                "Manage your account",

            onClick =
                onSettings
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // PRIVACY

        ProfileOption(
            title =
                "Privacy & Safety",

            subtitle =
                "Control your privacy and safety",

            onClick =
                onPrivacySafety
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // HELP

        ProfileOption(
            title =
                "Help & Contact",

            subtitle =
                "Get help or contact us",

            onClick =
                onHelpContact
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // LOGOUT

        OutlinedButton(
            onClick =
                onLogout,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            shape =
                RoundedCornerShape(16.dp)
        ) {

            Text(
                text =
                    "Logout",

                color =
                    Color(0xFFFF6B6B),

                fontSize =
                    16.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(35.dp)
        )
    }
}


// ======================================================
// PROFILE OPTION
// ======================================================

@Composable
fun ProfileOption(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Surface(

        onClick =
            onClick,

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        title,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        subtitle,

                    fontSize =
                        14.sp,

                    color =
                        Color(0xFF9E9EA8)
                )
            }

            Text(
                text =
                    "›",

                fontSize =
                    28.sp,

                color =
                    Color(0xFF777780)
            )
        }
    }
}


// ======================================================
// COMMON INNER SCREEN HEADER
// ======================================================

@Composable
fun InnerScreenHeader(
    title: String,
    onBack: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick =
                onBack
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription =
                    "Back",

                tint =
                    Color.White
            )
        }

        Text(
            text =
                title,

            fontSize =
                27.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )
    }
}


// ======================================================
// SETTINGS SCREEN
// ======================================================

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        InnerScreenHeader(
            title =
                "Settings",

            onBack =
                onBack
        )

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // ACCOUNT

        SettingsSectionTitle(
            text =
                "Account"
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        SettingsInfoCard(
            title =
                "Email",

            value =
                currentUser?.email
                    ?: "No email"
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        SettingsInfoCard(
            title =
                "Account Status",

            value =
                "Active"
        )


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // APP PREFERENCES

        SettingsSectionTitle(
            text =
                "App Preferences"
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        SettingsInfoCard(
            title =
                "Notifications",

            value =
                "Notification preferences"
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        SettingsInfoCard(
            title =
                "Appearance",

            value =
                "Dark theme"
        )


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // ABOUT

        SettingsSectionTitle(
            text =
                "About"
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        SettingsInfoCard(
            title =
                "DateMate",

            value =
                "Meet. Match. Connect."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        SettingsInfoCard(
            title =
                "Version",

            value =
                "1.0"
        )


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


// ======================================================
// SETTINGS SECTION TITLE
// ======================================================

@Composable
fun SettingsSectionTitle(
    text: String
) {

    Text(
        text =
            text,

        fontSize =
            15.sp,

        fontWeight =
            FontWeight.Bold,

        color =
            Color(0xFFFF4F81)
    )
}


// ======================================================
// SETTINGS INFO CARD
// ======================================================

@Composable
fun SettingsInfoCard(
    title: String,
    value: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Column(
            modifier =
                Modifier.padding(17.dp)
        ) {

            Text(
                text =
                    title,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    value,

                fontSize =
                    14.sp,

                color =
                    Color(0xFF9E9EA8)
            )
        }
    }
}


// ======================================================
// PRIVACY & SAFETY SCREEN
// ======================================================

@Composable
fun PrivacySafetyScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        InnerScreenHeader(
            title =
                "Privacy & Safety",

            onBack =
                onBack
        )

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        PrivacySafetyCard(
            title =
                "Your Privacy",

            text =
                "Your DateMate account uses Firebase Authentication for login. Your profile and chat information is stored in the app's Firebase database."
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        PrivacySafetyCard(
            title =
                "Keep Personal Information Safe",

            text =
                "Avoid sharing sensitive information such as passwords, financial details, OTPs or private documents with people you meet through the app."
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        PrivacySafetyCard(
            title =
                "Stay Respectful",

            text =
                "Treat other users respectfully. Do not harass, threaten, impersonate or repeatedly contact someone who does not want to continue a conversation."
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        PrivacySafetyCard(
            title =
                "Report & Block",

            text =
                "If someone makes you uncomfortable or behaves inappropriately, stop interacting with them. Reporting and blocking tools can be connected to this section as the app grows."
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        PrivacySafetyCard(
            title =
                "Account Security",

            text =
                "Never share your DateMate password or authentication information with another person. Use your own account and keep your login credentials private."
        )

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


// ======================================================
// PRIVACY SAFETY CARD
// ======================================================

@Composable
fun PrivacySafetyCard(
    title: String,
    text: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text =
                    title,

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    text,

                fontSize =
                    14.sp,

                lineHeight =
                    21.sp,

                color =
                    Color(0xFF9E9EA8)
            )
        }
    }
}


// ======================================================
// HELP & CONTACT SCREEN
// ======================================================

@Composable
fun HelpContactScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0F0F14)
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        InnerScreenHeader(
            title =
                "Help & Contact",

            onBack =
                onBack
        )

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        Text(
            text =
                "Frequently Asked Questions",

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.White
        )

        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        HelpQuestionCard(
            question =
                "How do I edit my profile?",

            answer =
                "Open the Profile section from the bottom navigation and tap Edit Profile."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        HelpQuestionCard(
            question =
                "How do I log out?",

            answer =
                "Open Profile and scroll down to the Logout button."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        HelpQuestionCard(
            question =
                "How do I start a chat?",

            answer =
                "When you have a match, open the Matches section and tap the matched person to open the conversation."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        HelpQuestionCard(
            question =
                "Why is my chat not appearing?",

            answer =
                "The Chat section displays conversations that have been created in the app. Sending the first message creates the conversation record."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        HelpQuestionCard(
            question =
                "How can I stay safe?",

            answer =
                "Do not share passwords, OTPs, financial information or sensitive personal documents. If a conversation makes you uncomfortable, stop interacting with that person."
        )

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )
    }
}


// ======================================================
// HELP QUESTION CARD
// ======================================================

@Composable
fun HelpQuestionCard(
    question: String,
    answer: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFF1B1B23)
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text =
                    question,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    answer,

                fontSize =
                    14.sp,

                lineHeight =
                    21.sp,

                color =
                    Color(0xFF9E9EA8)
            )
        }
    }
}
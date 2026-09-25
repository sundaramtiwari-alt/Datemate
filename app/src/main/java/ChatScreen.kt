package com.example.datemate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

@Composable
fun ChatScreen(
    otherUserId: String,
    otherUserName: String,
    onBack: () -> Unit
) {

    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var messageText by remember {
        mutableStateOf("")
    }

    var messages by remember {
        mutableStateOf(listOf<ChatMessage>())
    }

    var sending by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()


    // ==========================================
    // LOGIN CHECK
    // ==========================================

    if (currentUser == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "User is not logged in.",
                color = Color.White
            )
        }

        return
    }


    val currentUserId = currentUser.uid


    // ==========================================
    // CHAT ID
    // ==========================================

    val chatId =
        if (currentUserId < otherUserId) {
            "${currentUserId}_${otherUserId}"
        } else {
            "${otherUserId}_${currentUserId}"
        }


    // ==========================================
    // LOAD MESSAGES
    // ==========================================

    DisposableEffect(chatId) {

        val listener =
            firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                    "timestamp",
                    Query.Direction.ASCENDING
                )
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {

                        messages =
                            snapshot.documents.mapNotNull { document ->

                                val message =
                                    document.getString("message")

                                val senderId =
                                    document.getString("senderId")

                                val timestamp =
                                    document.getLong("timestamp")
                                        ?: 0L

                                if (
                                    message != null &&
                                    senderId != null
                                ) {

                                    ChatMessage(
                                        id = document.id,
                                        senderId = senderId,
                                        message = message,
                                        timestamp = timestamp
                                    )

                                } else {
                                    null
                                }
                            }
                    }
                }

        onDispose {
            listener.remove()
        }
    }


    // ==========================================
    // AUTO SCROLL
    // ==========================================

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }


    // ==========================================
    // SCREEN
    // ==========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
    ) {


        // ======================================
        // TOP BAR
        // ======================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF191922))
                .padding(
                    horizontal = 8.dp,
                    vertical = 10.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription = "Back",

                    tint = Color.White
                )
            }

            Text(
                text = otherUserName,
                color = Color.White,
                fontSize = 20.sp
            )
        }


        // ======================================
        // MESSAGES
        // ======================================

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),

            state = listState,

            verticalArrangement =
                Arrangement.spacedBy(8.dp),

            contentPadding =
                PaddingValues(vertical = 12.dp)
        ) {

            items(messages) { chatMessage ->

                val isMyMessage =
                    chatMessage.senderId == currentUserId

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        if (isMyMessage)
                            Arrangement.End
                        else
                            Arrangement.Start
                ) {

                    Surface(
                        shape =
                            RoundedCornerShape(18.dp),

                        color =
                            if (isMyMessage)
                                Color(0xFFFF4F81)
                            else
                                Color(0xFF292933),

                        modifier =
                            Modifier.widthIn(
                                max = 280.dp
                            )
                    ) {

                        Text(
                            text =
                                chatMessage.message,

                            color = Color.White,

                            fontSize = 16.sp,

                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 10.dp
                                )
                        )
                    }
                }
            }
        }


        // ======================================
        // INPUT AREA
        // ======================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF191922))
                .padding(10.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = messageText,

                onValueChange = {
                    messageText = it
                },

                modifier =
                    Modifier.weight(1f),

                placeholder = {
                    Text(
                        text = "Type a message...",
                        color = Color(0xFFAAAAAA)
                    )
                },

                singleLine = true,

                // ==================================
                // TEXT VISIBILITY FIX
                // ==================================

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color(0xFFAAAAAA),

                    focusedBorderColor = Color(0xFFFF4F81),
                    unfocusedBorderColor = Color(0xFF66666F),

                    cursorColor = Color(0xFFFF4F81)
                )
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            // ==================================
            // SEND BUTTON
            // ==================================

            Button(

                onClick = {

                    val text =
                        messageText.trim()

                    if (
                        text.isEmpty() ||
                        sending
                    ) {
                        return@Button
                    }


                    sending = true


                    val chatData =
                        hashMapOf(
                            "userIds" to listOf(
                                currentUserId,
                                otherUserId
                            ),

                            "createdAt" to
                                    System.currentTimeMillis(),

                            "lastMessage" to text,

                            "lastSenderId" to
                                    currentUserId,

                            "lastMessageAt" to
                                    System.currentTimeMillis()
                        )


                    val messageData =
                        hashMapOf(

                            "senderId" to
                                    currentUserId,

                            "receiverId" to
                                    otherUserId,

                            "message" to text,

                            "timestamp" to
                                    System.currentTimeMillis()
                        )


                    // First create/update chat
                    firestore
                        .collection("chats")
                        .document(chatId)
                        .set(chatData)
                        .addOnSuccessListener {

                            // Then add message
                            firestore
                                .collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .add(messageData)
                                .addOnSuccessListener {

                                    messageText = ""

                                    sending = false
                                }
                                .addOnFailureListener {

                                    sending = false
                                }
                        }
                        .addOnFailureListener {

                            sending = false
                        }
                },

                enabled =
                    messageText.trim().isNotEmpty() &&
                            !sending,

                modifier =
                    Modifier.height(55.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFFFF4F81),

                        disabledContainerColor =
                            Color(0xFF55555F)
                    ),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                if (sending) {

                    Text(
                        text = "...",
                        color = Color.White,
                        fontSize = 18.sp
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Send,

                        contentDescription =
                            "Send",

                        tint = Color.White
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )
    }
}
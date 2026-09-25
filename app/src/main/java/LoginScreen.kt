package com.example.datemate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome back",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Login to your DateMate account",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        // ==============================
        // EMAIL
        // ==============================

        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
                errorMessage = ""
            },

            label = {
                Text(
                    text = "Email",
                    color = Color.White
                )
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            enabled = !isLoading,

            colors = OutlinedTextFieldDefaults.colors(

                // Typed text
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,

                // Label
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color(0xFFAAAAAA),

                // Border
                focusedBorderColor = Color(0xFFFF4F81),
                unfocusedBorderColor = Color(0xFF777780),

                // Cursor
                cursorColor = Color(0xFFFF4F81)
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ==============================
        // PASSWORD
        // ==============================

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
                errorMessage = ""
            },

            label = {
                Text(
                    text = "Password",
                    color = Color.White
                )
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            enabled = !isLoading,

            colors = OutlinedTextFieldDefaults.colors(

                // Typed text
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,

                // Label
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color(0xFFAAAAAA),

                // Border
                focusedBorderColor = Color(0xFFFF4F81),
                unfocusedBorderColor = Color(0xFF777780),

                // Cursor
                cursorColor = Color(0xFFFF4F81)
            )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // ==============================
        // LOGIN BUTTON
        // ==============================

        Button(
            onClick = {

                if (email.isBlank()) {

                    errorMessage = "Please enter your email."

                } else if (password.isBlank()) {

                    errorMessage = "Please enter your password."

                } else {

                    isLoading = true
                    errorMessage = ""

                    auth.signInWithEmailAndPassword(
                        email.trim(),
                        password
                    )
                        .addOnCompleteListener { task ->

                            isLoading = false

                            if (task.isSuccessful) {

                                onLoginSuccess()

                            } else {

                                errorMessage =
                                    task.exception?.message
                                        ?: "Login failed."
                            }
                        }
                }

            },

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4F81)
            ),

            enabled = !isLoading
        ) {

            Text(
                text = if (isLoading) {
                    "Logging in..."
                } else {
                    "Login"
                },

                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ==============================
        // BACK BUTTON
        // ==============================

        Button(
            onClick = onBack,

            modifier = Modifier.fillMaxWidth(),

            enabled = !isLoading
        ) {

            Text("Back")
        }
    }
}
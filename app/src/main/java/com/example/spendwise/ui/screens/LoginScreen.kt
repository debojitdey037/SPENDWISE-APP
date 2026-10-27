package com.example.spendwise.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var isEmailLogin by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (otpSent) "Verify OTP" else "Welcome to SpendWise",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )
        
        Text(
            text = if (otpSent) "Enter the code sent to your ${if (isEmailLogin) "email" else "phone"}" 
                   else "Login to track your expenses",
            fontSize = 14.sp,
            color = Text2Color,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        if (!otpSent) {
            // Toggle between Phone and Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = !isEmailLogin,
                    onClick = { isEmailLogin = false },
                    label = { Text("Phone") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentColor,
                        selectedLabelColor = Color.White,
                        labelColor = Text2Color
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = isEmailLogin,
                    onClick = { isEmailLogin = true },
                    label = { Text("Email") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentColor,
                        selectedLabelColor = Color.White,
                        labelColor = Text2Color
                    )
                )
            }

            AnimatedVisibility(visible = isEmailLogin) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = Bg4Color,
                        focusedLabelColor = AccentColor,
                        cursorColor = AccentColor,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            AnimatedVisibility(visible = !isEmailLogin) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = Bg4Color,
                        focusedLabelColor = AccentColor,
                        cursorColor = AccentColor,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isEmailLogin && email.isEmpty()) {
                        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!isEmailLogin && phoneNumber.isEmpty()) {
                        Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isLoading = true
                    scope.launch {
                        delay(1500) // Simulate network call
                        isLoading = false
                        otpSent = true
                        Toast.makeText(context, "OTP sent: 123456 (Demo)", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // OTP Verification
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("Enter 6-digit OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = Bg4Color,
                    focusedLabelColor = AccentColor,
                    cursorColor = AccentColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (otpCode == "123456") {
                        isLoading = true
                        scope.launch {
                            delay(1000)
                            isLoading = false
                            onLoginSuccess()
                        }
                    } else {
                        Toast.makeText(context, "Invalid OTP. Use 123456", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp),
                enabled = otpCode.length == 6 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Verify & Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = { otpSent = false },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Change ${if (isEmailLogin) "email" else "phone number"}", color = AccentColor)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Google Sign In Button (Placeholder)
        if (!otpSent) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Bg4Color)
                Text(" OR ", color = Text3Color, modifier = Modifier.padding(horizontal = 8.dp), fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Bg4Color)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { 
                    isLoading = true
                    scope.launch {
                        delay(2000)
                        isLoading = false
                        onLoginSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Bg4Color)),
                enabled = !isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // In a real app, use Google icon
                    Text("Continue with Google", color = TextColor, fontSize = 16.sp)
                }
            }
        }
    }
}

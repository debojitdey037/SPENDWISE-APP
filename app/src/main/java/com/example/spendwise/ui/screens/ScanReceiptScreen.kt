package com.example.spendwise.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.spendwise.data.FirestoreManager
import com.example.spendwise.model.Expense
import com.example.spendwise.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanReceiptScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Camera", "Upload Image", "Manual Entry")
    val icons = listOf(Icons.Default.CameraAlt, Icons.Default.CloudUpload, Icons.Default.Edit)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val firestoreManager = remember { FirestoreManager() }

    // Launcher for Gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch { snackbarHostState.showSnackbar("Image selected successfully") }
        }
    }

    // Launcher for Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            scope.launch { snackbarHostState.showSnackbar("Photo captured successfully") }
        }
    }

    // Launcher for Camera Permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Camera permission is required to scan receipts") }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Scan Receipt", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
            Text(text = "March 2025 · Smart Expense Tracker", fontSize = 13.sp, color = Text2Color)
            
            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Bg2Color)
                    .padding(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == index) Bg3Color else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                icons[index],
                                contentDescription = null,
                                tint = if (selectedTab == index) Color.White else Text2Color,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == index) Color.White else Text2Color
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                0 -> CameraView(onAction = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                            cameraLauncher.launch(null)
                        }
                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                })
                1 -> UploadView(onAction = { imagePickerLauncher.launch("image/*") })
                2 -> ManualEntryView(
                    onSave = { expense ->
                        scope.launch {
                            val result = firestoreManager.addExpense(expense)
                            if (result.isSuccess) {
                                snackbarHostState.showSnackbar("Expense saved to Firebase!")
                            } else {
                                snackbarHostState.showSnackbar("Error: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    },
                    onShowMessage = { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
            }
        }
    }
}

@Composable
fun CameraView(onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Bg2Color)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Text2Color, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Scan Receipt with Camera", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColor)
            Text(
                text = "Point your camera at the receipt. ML Kit OCR will extract expense details automatically.",
                fontSize = 13.sp,
                color = Text2Color,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Camera", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UploadView(onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Bg2Color)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Text2Color, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Upload Receipt Image", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColor)
            Text(
                text = "Drag & drop a photo of your receipt, or click to browse files.",
                fontSize = 13.sp,
                color = Text2Color,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Choose File", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Supports JPEG, PNG, HEIC", fontSize = 11.sp, color = Text3Color)
        }
    }
}

@Composable
fun ManualEntryView(onSave: (Expense) -> Unit, onShowMessage: (String) -> Unit) {
    var merchant by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Bg2Color)
            .padding(24.dp)
    ) {
        Text(text = "Manual Entry", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("MERCHANT / STORE NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            placeholder = { Text("e.g. Swiggy") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.2f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("TOTAL AMOUNT (₹)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.2f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
        Spacer(modifier = Modifier.height(8.dp))
        
        val categories = listOf(
            "Food" to "🍔", "Grocery" to "🛒", "Transport" to "🚗", "Health" to "💊",
            "Shopping" to "🛍️", "Utilities" to "💡", "Fun" to "🎮", "Other" to "📁"
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (name, icon) ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategory == name) AccentColor.copy(alpha = 0.2f) else Bg3Color)
                        .border(
                            width = 1.dp,
                            color = if (selectedCategory == name) AccentColor else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = name }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = icon, fontSize = 20.sp)
                    Text(text = name, fontSize = 10.sp, color = if (selectedCategory == name) Color.White else Text2Color)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (merchant.isNotBlank() && amountDouble != null) {
                    val expense = Expense(
                        merchant = merchant,
                        amount = amountDouble,
                        date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()),
                        category = selectedCategory.lowercase()
                    )
                    onSave(expense)
                    merchant = ""
                    amount = ""
                } else {
                    onShowMessage("Please fill in all fields with valid data")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Expense", fontWeight = FontWeight.Bold)
        }
    }
}

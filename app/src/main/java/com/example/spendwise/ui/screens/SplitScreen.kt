package com.example.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.data.FirestoreManager
import com.example.spendwise.model.Friend
import com.example.spendwise.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SplitScreen() {
    val firestoreManager = remember { FirestoreManager() }
    val friendsList by firestoreManager.getFriends().collectAsState(initial = emptyList())
    
    var amount by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }
    var friendPhone by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Split Bill", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
                    Text("Dutch split with friends & notify", fontSize = 13.sp, color = Text2Color)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Total Amount Input
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("TOTAL BILL AMOUNT (₹)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentColor,
                            unfocusedBorderColor = Text3Color
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Add Friends", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = friendName,
                            onValueChange = { friendName = it },
                            placeholder = { Text("Name", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = friendPhone,
                            onValueChange = { friendPhone = it },
                            placeholder = { Text("Phone", fontSize = 12.sp) },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        IconButton(
                            onClick = {
                                if (friendName.isNotBlank() && friendPhone.isNotBlank()) {
                                    scope.launch {
                                        val newFriend = Friend(
                                            id = "",
                                            name = friendName,
                                            phoneNumber = friendPhone
                                        )
                                        firestoreManager.addFriend(newFriend)
                                        friendName = ""
                                        friendPhone = ""
                                    }
                                }
                            },
                            modifier = Modifier.clip(CircleShape).background(AccentColor)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }

                items(friendsList) { friend ->
                    FriendItem(friend, onRemove = { 
                        // Note: Delete logic could be added to FirestoreManager if needed
                    })
                }

                item {
                    if (friendsList.isNotEmpty() && amount.isNotBlank()) {
                        val totalAmount = amount.toDoubleOrNull() ?: 0.0
                        val splitAmount = totalAmount / (friendsList.size + 1)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Bg2Color),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DUTCH SPLIT RESULT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("₹${String.format(Locale.getDefault(), "%.2f", splitAmount)}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AccentColor)
                                Text("per person (including you)", fontSize = 11.sp, color = Text2Color)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Fixed Send Button at Bottom
            if (friendsList.isNotEmpty() && amount.isNotBlank()) {
                val splitAmount = (amount.toDoubleOrNull() ?: 0.0) / (friendsList.size + 1)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Message sent to ${friendsList.size} friends: Please pay ₹${String.format(Locale.getDefault(), "%.2f", splitAmount)}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Send Split Request", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: Friend, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Bg2Color)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(AccentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(friend.name.take(1).uppercase(), color = AccentColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(friend.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
            Text(friend.phoneNumber, fontSize = 12.sp, color = Text2Color)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, null, tint = RedColor.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        }
    }
}

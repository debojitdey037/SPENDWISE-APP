package com.example.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Add
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
import com.example.spendwise.model.Expense
import com.example.spendwise.ui.components.ExpenseItem
import com.example.spendwise.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(onScanClick: () -> Unit = {}) {
    val firestoreManager = remember { FirestoreManager() }
    val expenses by firestoreManager.getExpenses().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    var showAllExpenses by remember { mutableStateOf(false) }
    
    val displayExpenses = if (showAllExpenses || expenses.size <= 4) expenses else expenses.take(4)

    // Calculate totals from real data
    val totalSpent = expenses.sumOf { it.amount }
    val budget = 25000.0
    val left = (budget - totalSpent).coerceAtLeast(0.0)
    val dailyAvg = if (expenses.isNotEmpty()) totalSpent / 30 else 0.0 // Simplified monthly average

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val dummy = Expense(
                            id = "",
                            merchant = "Test Store",
                            amount = (100..1000).random().toDouble(),
                            date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()),
                            category = "grocery",
                            notes = "Added via Firebase"
                        )
                        firestoreManager.addExpense(dummy)
                    }
                },
                containerColor = AccentColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        containerColor = Color.Transparent // Scaffold inside Box in MainActivity
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HeaderSection(onScanClick = onScanClick)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Dashboard Stats
                DashboardStatsCard(
                    total = totalSpent,
                    txns = expenses.size,
                    left = left,
                    daily = dailyAvg
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Monthly Budget Progress
                BudgetSummaryCard(totalSpent, budget)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recent Expenses Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recent Expenses", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextColor)
                    if (expenses.size > 4) {
                        TextButton(onClick = { showAllExpenses = !showAllExpenses }) {
                            Text(
                                text = if (showAllExpenses) "Show Less" else "View All", 
                                fontSize = 12.sp, 
                                color = AccentColor
                            )
                        }
                    }
                }
            }

            if (expenses.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No expenses found. Add one!", color = Text2Color)
                    }
                }
            } else {
                items(displayExpenses) { expense ->
                    ExpenseItem(expense)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Nav spacing
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(onScanClick: () -> Unit) {
    var currentMonth by remember { mutableStateOf("March 2025") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Date(it)
                        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        currentMonth = formatter.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = AccentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Text2Color)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Bg2Color)
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextColor
            )
            Text(
                text = "$currentMonth · Smart Expense Tracker",
                fontSize = 13.sp,
                color = Text2Color
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Bg2Color)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Text2Color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currentMonth,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DashboardStatsCard(total: Double, txns: Int, left: Double, daily: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItemPoint("Total Spent", "₹${String.format("%.0f", total)}", modifier = Modifier.weight(1f))
            VerticalDivider(modifier = Modifier.height(30.dp).padding(horizontal = 4.dp), color = Text3Color.copy(alpha = 0.2f))
            StatItemPoint("Txns", txns.toString(), modifier = Modifier.weight(0.7f))
            VerticalDivider(modifier = Modifier.height(30.dp).padding(horizontal = 4.dp), color = Text3Color.copy(alpha = 0.2f))
            StatItemPoint("Left", "₹${String.format("%.0f", left)}", modifier = Modifier.weight(1f))
            VerticalDivider(modifier = Modifier.height(30.dp).padding(horizontal = 4.dp), color = Text3Color.copy(alpha = 0.2f))
            StatItemPoint("Daily", "₹${String.format("%.0f", daily)}", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItemPoint(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Text2Color, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
    }
}

@Composable
fun BudgetSummaryCard(totalSpent: Double, budget: Double) {
    val progress = (totalSpent / budget).toFloat().coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MONTHLY BUDGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
                Text("₹${String.format("%.0f", totalSpent)} / ₹${String.format("%.0f", budget)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextColor)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress > 0.9f) RedColor else GreenColor,
                trackColor = Bg3Color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$percentage% of budget used", fontSize = 10.sp, color = if (progress > 0.9f) RedColor else GreenColor)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (progress > 0.9f) "Warning" else "Good standing", fontSize = 10.sp, color = Text2Color)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (progress > 0.9f) RedColor else GreenColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

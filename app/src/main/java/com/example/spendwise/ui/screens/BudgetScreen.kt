package com.example.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
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
import com.example.spendwise.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun BudgetScreen(onScanClick: () -> Unit = {}) {
    val firestoreManager = remember { FirestoreManager() }
    val currentBudget by firestoreManager.getBudget().collectAsState(initial = 25000.0)
    val expenses by firestoreManager.getExpenses().collectAsState(initial = emptyList())
    
    val totalSpent = expenses.sumOf { it.amount }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BudgetHeader(onScanClick = onScanClick)
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SetBudgetCard(
                        currentBudget = currentBudget,
                        onBudgetSet = { newBudget ->
                            scope.launch {
                                firestoreManager.updateBudget(newBudget.toDouble())
                                snackbarHostState.showSnackbar("Monthly budget updated to ₹$newBudget")
                            }
                        },
                        modifier = Modifier.weight(1.2f)
                    )
                    BudgetUtilizationCard(
                        currentBudget = currentBudget, 
                        spent = totalSpent,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                CategoryBudgetsSection(expenses)
                Spacer(modifier = Modifier.height(24.dp))
                AlertsSection(totalSpent, currentBudget)
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun BudgetHeader(onScanClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = "Budget", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
            Text(text = "March 2025 · Smart Expense Tracker", fontSize = 13.sp, color = Text2Color)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Bg2Color)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Text2Color, modifier = Modifier.size(20.dp))
                Text(text = "March 2025", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextColor, modifier = Modifier.padding(horizontal = 4.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Text2Color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Camera, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SetBudgetCard(currentBudget: Double, onBudgetSet: (Int) -> Unit, modifier: Modifier = Modifier) {
    var budgetValue by remember { mutableStateOf("") }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SET MONTHLY BUDGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = budgetValue,
                    onValueChange = { budgetValue = it },
                    placeholder = { Text("e.g. 30000", fontSize = 12.sp, color = Text3Color) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Bg3Color,
                        unfocusedContainerColor = Bg3Color,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = AccentColor.copy(alpha = 0.5f),
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { 
                        val newBudget = budgetValue.toIntOrNull()
                        if (newBudget != null) {
                            onBudgetSet(newBudget)
                            budgetValue = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Set", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current: ₹${String.format(Locale.getDefault(), "%.0f", currentBudget)}", fontSize = 11.sp, color = Text2Color)
        }
    }
}

@Composable
fun BudgetUtilizationCard(currentBudget: Double, spent: Double, modifier: Modifier = Modifier) {
    val progress = if (currentBudget > 0) spent.toFloat() / currentBudget.toFloat() else 0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("BUDGET UTILIZATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Spent", fontSize = 12.sp, color = Text2Color)
                Text("₹${String.format(Locale.getDefault(), "%.0f", spent)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (progress > 0.9f) RedColor else GreenColor,
                trackColor = Bg3Color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$percentage%", fontSize = 11.sp, color = if (progress > 0.9f) RedColor else GreenColor)
                Text("of ₹${String.format(Locale.getDefault(), "%.0f", currentBudget)}", fontSize = 11.sp, color = Text2Color)
            }
        }
    }
}

@Composable
fun CategoryBudgetsSection(expenses: List<com.example.spendwise.model.Expense>) {
    val foodSpent = expenses.filter { it.category == "food" }.sumOf { it.amount }
    val grocerySpent = expenses.filter { it.category == "grocery" }.sumOf { it.amount }
    val transportSpent = expenses.filter { it.category == "transport" }.sumOf { it.amount }
    val entertainmentSpent = expenses.filter { it.category == "entertainment" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Bg2Color)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category Budgets", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextColor)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Bg3Color)
                    .clickable { /* Add Category Budget */ }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, null, tint = Text2Color, modifier = Modifier.size(14.dp))
                Text("Add", fontSize = 11.sp, color = Text2Color, modifier = Modifier.padding(start = 2.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val categoryBudgets = listOf(
            CategoryBudgetItem("Food", "₹${foodSpent.toInt()} spent", (foodSpent/3000).toFloat().coerceIn(0f, 1f), PinkColor, "₹3,000", "🍔"),
            CategoryBudgetItem("Grocery", "₹${grocerySpent.toInt()} spent", (grocerySpent/6000).toFloat().coerceIn(0f, 1f), GreenColor, "₹6,000", "🛒"),
            CategoryBudgetItem("Transport", "₹${transportSpent.toInt()} spent", (transportSpent/2000).toFloat().coerceIn(0f, 1f), BlueColor, "₹2,000", "🚗"),
            CategoryBudgetItem("Fun", "₹${entertainmentSpent.toInt()} spent", (entertainmentSpent/1500).toFloat().coerceIn(0f, 1f), AmberColor, "₹1,500", "🎬")
        )
        
        categoryBudgets.forEach { budget ->
            CategoryBudgetRow(budget)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class CategoryBudgetItem(val name: String, val spent: String, val progress: Float, val color: Color, val limit: String, val icon: String)

@Composable
fun CategoryBudgetRow(budget: CategoryBudgetItem) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(budget.icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(budget.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Text(budget.spent, fontSize = 12.sp, color = Text2Color)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(budget.limit, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Close, null, tint = RedColor.copy(alpha = 0.5f), modifier = Modifier.size(16.dp).clickable { /* Remove */ })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { budget.progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = budget.color,
            trackColor = Bg3Color
        )
    }
}

@Composable
fun AlertsSection(totalSpent: Double, currentBudget: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Bg2Color)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔔", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Alerts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextColor)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        if (totalSpent > currentBudget) {
            Text("You have exceeded your monthly budget!", color = RedColor, fontSize = 13.sp)
        } else if (totalSpent > currentBudget * 0.9) {
            Text("Warning: You have used 90% of your budget.", color = AmberColor, fontSize = 13.sp)
        } else {
            Text("Budget is within healthy limits.", color = GreenColor, fontSize = 13.sp)
        }
    }
}

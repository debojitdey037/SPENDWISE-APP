package com.example.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Camera
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
import com.example.spendwise.ui.theme.*
import java.util.Locale

@Composable
fun AnalyticsScreen(onScanClick: () -> Unit = {}) {
    val firestoreManager = remember { FirestoreManager() }
    val expenses by firestoreManager.getExpenses().collectAsState(initial = emptyList())
    
    // Calculate real analytics data
    val totalSpent = expenses.sumOf { it.amount }
    val avgPerTxn = if (expenses.isNotEmpty()) totalSpent / expenses.size else 0.0
    val dailyAvg = totalSpent / 30.0 // Simplified monthly average
    
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    val merchantTotals = expenses.groupBy { it.merchant }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            AnalyticsHeader(onScanClick = onScanClick)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CategoryBreakdown(categoryTotals, totalSpent, modifier = Modifier.weight(1f))
                TopMerchants(merchantTotals, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCardAnalytics("DAILY AVERAGE", "₹${String.format(Locale.getDefault(), "%.0f", dailyAvg)}", "per day this month", modifier = Modifier.weight(1f))
                StatCardAnalytics("AVG PER TRANSACTION", "₹${String.format(Locale.getDefault(), "%.0f", avgPerTxn)}", "per receipt", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TrendChart()
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun AnalyticsHeader(onScanClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = "Analytics", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
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
fun CategoryBreakdown(categoryTotals: List<Pair<String, Double>>, totalSpent: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TOP CATEGORIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (categoryTotals.isEmpty()) {
                Text("No data", fontSize = 12.sp, color = Text3Color)
            }

            categoryTotals.forEach { (category, amount) ->
                val progress = if (totalSpent > 0) (amount / totalSpent).toFloat() else 0f
                val color = when(category.lowercase()) {
                    "food" -> PinkColor
                    "grocery" -> GreenColor
                    "transport" -> BlueColor
                    "shopping" -> PurpleColor
                    "utilities" -> AmberColor
                    else -> AccentColor
                }
                CategoryRow(category.replaceFirstChar { it.uppercase() }, "₹${amount.toInt()}", progress, color)
            }
        }
    }
}

@Composable
fun CategoryRow(label: String, amount: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, color = TextColor)
            Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = Bg3Color,
        )
    }
}

@Composable
fun TopMerchants(merchantTotals: List<Pair<String, Double>>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TOP MERCHANTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (merchantTotals.isEmpty()) {
                Text("No data", fontSize = 12.sp, color = Text3Color)
            }

            merchantTotals.forEachIndexed { index, (name, amount) ->
                MerchantRow((index + 1).toString(), name, "₹${amount.toInt()}")
            }
        }
    }
}

@Composable
fun MerchantRow(rank: String, name: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(rank, fontSize = 11.sp, color = Text2Color, modifier = Modifier.width(16.dp))
            Text(name, fontSize = 12.sp, color = TextColor, maxLines = 1)
        }
        Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
    }
}

@Composable
fun StatCardAnalytics(title: String, value: String, sub: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextColor)
            Text(sub, fontSize = 11.sp, color = Text2Color)
        }
    }
}

@Composable
fun TrendChart() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Bg2Color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("6-MONTH TREND", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Text2Color)
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                // Placeholder for Chart
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
                    Box(modifier = Modifier.width(60.dp).fillMaxHeight(0.6f).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(AccentColor))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Oct", "Nov", "Dec", "Jan", "Feb", "Mar").forEach { 
                    Text(it, fontSize = 10.sp, color = Text2Color)
                }
            }
        }
    }
}

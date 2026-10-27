package com.example.spendwise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.model.CATEGORIES
import com.example.spendwise.model.Expense
import com.example.spendwise.ui.theme.*

@Composable
fun ExpenseItem(expense: Expense) {
    val category = CATEGORIES.find { it.id == expense.category } ?: CATEGORIES.last()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(category.color).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.label,
                tint = Color(category.color),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Info Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.merchant,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColor
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(category.color).copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(category.color)
                    )
                }
                if (expense.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = expense.notes, fontSize = 11.sp, color = Text3Color)
                }
            }
        }
        
        // Amount Column
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₹${expense.amount.toInt()}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextColor
            )
            Text(
                text = expense.date,
                fontSize = 11.sp,
                color = Text3Color
            )
        }
    }
}

package com.example.spendwise.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Expense(
    val id: String = "",
    val merchant: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val category: String = "other",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Category(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Long
)

val CATEGORIES = listOf(
    Category("food", "Food", Icons.Default.Restaurant, 0xFFFF6EB4),
    Category("grocery", "Grocery", Icons.Default.ShoppingCart, 0xFF22D3A0),
    Category("transport", "Transport", Icons.Default.DirectionsCar, 0xFF4FA8FF),
    Category("health", "Health", Icons.Default.MedicalServices, 0xFFFF5E6E),
    Category("shopping", "Shopping", Icons.Default.ShoppingBag, 0xFF7C6AFF),
    Category("utilities", "Utilities", Icons.Default.Lightbulb, 0xFFFFB347),
    Category("entertainment", "Fun", Icons.Default.Movie, 0xFFFF9940),
    Category("other", "Other", Icons.Default.Category, 0xFF9090A8)
)

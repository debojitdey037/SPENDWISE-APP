package com.example.spendwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.screens.*
import com.example.spendwise.ui.theme.AccentColor
import com.example.spendwise.ui.theme.Bg2Color
import com.example.spendwise.ui.theme.BgColor
import com.example.spendwise.ui.theme.SpendwiseTheme
import com.example.spendwise.ui.theme.Text2Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendwiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgColor
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    var isLoggedIn by remember { mutableStateOf(false) }
                    
                    if (showSplash) {
                        WelcomeScreen(onTimeout = { showSplash = false })
                    } else if (!isLoggedIn) {
                        LoginScreen(onLoginSuccess = { isLoggedIn = true })
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Dashboard", "Scan", "Split", "Budget", "Analytics", "Offers")
    val icons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.PhotoCamera,
        Icons.Default.Group,
        Icons.Default.AccountBalanceWallet,
        Icons.Default.BarChart,
        Icons.Default.LocalOffer
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Bg2Color,
                contentColor = Text2Color,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = icons[index], 
                                contentDescription = item,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { Text(item, fontSize = 9.sp, maxLines = 1) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentColor,
                            selectedTextColor = AccentColor,
                            unselectedIconColor = Text2Color,
                            unselectedTextColor = Text2Color,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        containerColor = BgColor
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> DashboardScreen(onScanClick = { selectedItem = 1 })
                1 -> ScanReceiptScreen()
                2 -> SplitScreen()
                3 -> BudgetScreen(onScanClick = { selectedItem = 1 })
                4 -> AnalyticsScreen(onScanClick = { selectedItem = 1 })
                5 -> OffersScreen()
            }
        }
    }
}

package com.example.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.components.OfferCard
import com.example.spendwise.ui.theme.*

data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val code: String,
    val color: Color,
    val category: String
)

@Composable
fun OffersScreen() {
    val categories = listOf("All", "Food", "Travel", "Grocery", "Health", "Shopping", "Utilities", "Entertainment")
    var selectedCategory by remember { mutableStateOf("All") }

    val allOffers = listOf(
        // Food
        Offer("1", "Swiggy Delights", "Get 50% OFF on your next 3 orders. Max discount ₹100.", "SWIGGY50", Color(0xFFFF7622), "Food"),
        Offer("3", "Zomato Gold", "Exclusive 15% discount for SpendWise users on Zomato.", "GOLDWISE", Color(0xFFCB202D), "Food"),
        Offer("5", "Dominos Party", "Flat ₹100 off on orders above ₹500. Every Friday.", "PIZZALOVE", Color(0xFF006491), "Food"),
        
        // Travel
        Offer("4", "Uber Savings", "Book an Uber Premier and get ₹50 off. Valid on weekends.", "UBERPREMIER", Color(0xFF000000), "Travel"),
        Offer("6", "MakeMyTrip", "Flat 12% off on domestic flight bookings.", "MMTSAVE", Color(0xFF22D3EE), "Travel"),
        Offer("7", "RedBus Deal", "Save ₹150 on your first bus booking.", "FIRSTBUS", Color(0xFFD84E55), "Travel"),
        
        // Grocery
        Offer("8", "Blinkit", "Flash sale! ₹75 cashback on grocery orders above ₹499.", "BLINK75", Color(0xFFFFD100), "Grocery"),
        Offer("9", "BigBasket", "Extra 10% off on fresh fruits and vegetables.", "BBFRESH", Color(0xFF84C225), "Grocery"),
        
        // Health
        Offer("10", "Apollo Pharmacy", "20% OFF on all prescription medicines.", "APOLLO20", Color(0xFF008EAA), "Health"),
        Offer("11", "Pharmeasy", "Flat 25% OFF + 10% Cashback on first order.", "PHARM35", Color(0xFF15BE97), "Health"),
        
        // Shopping
        Offer("12", "Myntra Insider", "Flat ₹400 off on styles from top brands.", "MYNTRA400", Color(0xFFFF3F6C), "Shopping"),
        Offer("13", "Ajio Special", "Up to 80% off + Extra 10% off on Reliance Trends.", "AJIOWISE", Color(0xFF2C4152), "Shopping"),
        
        // Utilities
        Offer("2", "Amazon Pay", "Flat ₹50 cashback on mobile recharges above ₹199.", "AMZPAY50", Color(0xFF232F3E), "Utilities"),
        Offer("14", "Airtel Thanks", "Get 5GB data free on your next recharge.", "AIRTEL5GB", Color(0xFFE40000), "Utilities"),
        
        // Entertainment
        Offer("15", "BookMyShow", "Buy 1 Get 1 Free on movie tickets (Weekdays).", "BMSBOGO", Color(0xFFF84464), "Entertainment"),
        Offer("16", "Netflix Gift", "1 month extra on annual subscription plans.", "NETFLIXPLUS", Color(0xFFE50914), "Entertainment")
    )

    val filteredOffers = if (selectedCategory == "All") {
        allOffers
    } else {
        allOffers.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Special Offers",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextColor
            )
            Text(
                text = "Handpicked deals across categories",
                fontSize = 14.sp,
                color = Text2Color
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category Selector
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { 
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Bg2Color,
                        labelColor = Text2Color,
                        selectedContainerColor = AccentColor,
                        selectedLabelColor = Color.White
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredOffers) { offer ->
                OfferCard(
                    title = offer.title,
                    description = offer.description,
                    code = offer.code,
                    color = offer.color
                )
            }
            
            if (filteredOffers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(text = "No offers found in this category", color = Text3Color)
                    }
                }
            }
        }
    }
}

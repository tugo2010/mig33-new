package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar

@Composable
fun MigWorldScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val regions = listOf(
        Triple("🌏 Asia-Pacific", "45,210 online", listOf("Indonesia (31.2k)", "Singapore (4.5k)", "Malaysia (3.1k)", "Philippines (6.4k)")),
        Triple("🌍 South Asia & Middle East", "29,480 online", listOf("India (15.2k)", "Nepal (6.8k)", "Maldives (2.4k)", "UAE (5.0k)")),
        Triple("🌎 Americas & Europe", "14,850 online", listOf("United States (7.1k)", "United Kingdom (3.4k)", "Brazil (4.3k)"))
    )

    val countryRooms = listOf(
        Triple("danger_1", "Danger 1 (Global Hub)", "48 / 50 • International"),
        Triple("game_warteg", "Game-warteg (Indonesia)", "24 / 60 • Jakarta & Java"),
        Triple("butwal_guz", "Butwal.guz (Nepal)", "18 / 25 • Kathmandu & Butwal"),
        Triple("dhivehin_queens", "dhivehin queens (Maldives)", "12 / 25 • Male City"),
        Triple("singapore_chill", "Singapore Cafe (Singapore)", "15 / 30 • Orchard & Marina")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Back",
                    leftAvatarRes = null,
                    centerTitle = "migWorld",
                    rightAvatarRes = null,
                    onLeftClick = onBack
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFECEFF1))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Global Live Telemetry Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF80D8FF), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("mig33 Global Live Telemetry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Connecting 89.5k+ members across 180+ countries.", color = Color(0xFFB3E5FC), fontSize = 12.sp)
                    }
                }
            }

            // Regions
            items(regions) { region ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(region.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0288D1))
                            Text(region.second, fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Active Hotspots: " + region.third.joinToString(" • "), fontSize = 11.sp, color = Color(0xFF546E7A))
                    }
                }
            }

            // Country Rooms Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("🌍 INTERNATIONAL FEATURED ROOMS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }

            items(countryRooms) { room ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onJoinRoom(room.first) },
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(room.second, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF263238))
                            Text(room.third, fontSize = 11.sp, color = Color(0xFF78909C))
                        }
                        Button(
                            onClick = { onJoinRoom(room.first) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Enter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

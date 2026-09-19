package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MigRepository
import com.example.model.DiceOutcome
import com.example.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MigRoomGamesDialog(
    roomId: String,
    userProfile: UserProfile,
    repository: MigRepository,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dice, 1: Trivia, 2: Coin Flip, 3: Lucky 7, 4: Commands
    val tabs = listOf("🎲 Dice", "🧠 Trivia", "🪙 Flip", "🎰 Lucky 7", "🤖 Commands")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(16.dp))
                .testTag("room_games_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0288D1), Color(0xFF01579B), Color(0xFF0D47A1))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎮", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "mig33 Room Games Bot",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Wallet: ${userProfile.credits} cr • Level ${userProfile.migLevel}",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF38BDF8),
                    edgePadding = 8.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                // Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (selectedTab) {
                        0 -> DiceGameTab(roomId = roomId, userProfile = userProfile, repository = repository)
                        1 -> TriviaGameTab(roomId = roomId, userProfile = userProfile, repository = repository)
                        2 -> CoinFlipGameTab(roomId = roomId, userProfile = userProfile, repository = repository)
                        3 -> Lucky7GameTab(roomId = roomId, userProfile = userProfile, repository = repository)
                        4 -> CommandsHelpTab(roomId = roomId, repository = repository, onDismiss = onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceGameTab(
    roomId: String,
    userProfile: UserProfile,
    repository: MigRepository
) {
    var betAmount by remember { mutableIntStateOf(0) }
    var isRolling by remember { mutableStateOf(false) }
    var lastOutcome by remember { mutableStateOf<String?>(null) }
    var diceP1 by remember { mutableIntStateOf(5) }
    var diceP2 by remember { mutableIntStateOf(6) }
    var diceB1 by remember { mutableIntStateOf(3) }
    var diceB2 by remember { mutableIntStateOf(4) }
    val coroutineScope = rememberCoroutineScope()

    val betOptions = listOf(0, 10, 25, 50, 100, 250, 500)
    val diceIcons = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎲 Dice Duel vs migBot",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Roll 2 dice against migBot. Highest sum wins! Tie refunds bet.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )

        // Dice Arena Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Side
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "👤 You (@${userProfile.username})", fontSize = 12.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0288D1),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = diceIcons[diceP1 - 1], fontSize = 28.sp, color = Color.White)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0288D1),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = diceIcons[diceP2 - 1], fontSize = 28.sp, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total: ${diceP1 + diceP2}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Black)
                }

                Text(text = "VS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))

                // Bot Side
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🤖 migBot", fontSize = 12.sp, color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFBE123C),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = diceIcons[diceB1 - 1], fontSize = 28.sp, color = Color.White)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFBE123C),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = diceIcons[diceB2 - 1], fontSize = 28.sp, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total: ${diceB1 + diceB2}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }

        // Bet Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Select Bet Amount (رهان الكريدت):", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(betOptions) { amount ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (betAmount == amount) Color(0xFFF59E0B) else Color(0xFF334155),
                        modifier = Modifier.clickable { betAmount = amount }
                    ) {
                        Text(
                            text = if (amount == 0) "Free (0 cr)" else "$amount cr",
                            color = if (betAmount == amount) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Outcome Banner
        if (lastOutcome != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0288D1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lastOutcome!!,
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Roll Button
        Button(
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    coroutineScope.launch {
                        // Rolling animation
                        for (i in 0..6) {
                            diceP1 = (1..6).random()
                            diceP2 = (1..6).random()
                            diceB1 = (1..6).random()
                            diceB2 = (1..6).random()
                            delay(80)
                        }
                        val res = repository.playDiceGame(roomId, betAmount)
                        diceP1 = res.playerDie1
                        diceP2 = res.playerDie2
                        diceB1 = res.botDie1
                        diceB2 = res.botDie2
                        lastOutcome = when (res.outcome) {
                            DiceOutcome.WIN -> "🎉 WINNER! You won +${res.wonAmount} cr & XP!"
                            DiceOutcome.LOSE -> "💥 migBot won this round! (-${betAmount} cr)"
                            DiceOutcome.TIE -> "🤝 Tie round! Bet refunded."
                        }
                        isRolling = false
                    }
                }
            },
            enabled = !isRolling && (betAmount == 0 || userProfile.credits >= betAmount),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
        ) {
            Text(
                text = if (isRolling) "🎲 Rolling Dice..." else if (betAmount == 0) "🎲 Roll Free Dice" else "🎲 Roll & Bet ($betAmount cr)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TriviaGameTab(
    roomId: String,
    userProfile: UserProfile,
    repository: MigRepository
) {
    val activeRounds by repository.activeTriviaRounds.collectAsState()
    val activeRound = activeRounds[roomId]
    val isRoundActive = activeRound != null && !activeRound.isAnswered && System.currentTimeMillis() < activeRound.endMillis
    var lastAnswerResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🧠 mig33 Live Room Trivia",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Answer fast to win the credit jackpot! First correct answer wins.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )

        if (isRoundActive && activeRound != null) {
            // Active Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00ACC1))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF00838F)
                        ) {
                            Text(
                                text = "🏆 Prize: ${activeRound.question.prizeCredits} cr",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(text = "Category: ${activeRound.question.category}", fontSize = 11.sp, color = Color(0xFF80DEEA))
                    }

                    Text(
                        text = activeRound.question.question,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Select your answer:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)

                    activeRound.question.options.forEachIndexed { index, option ->
                        Button(
                            onClick = {
                                val correct = repository.submitTriviaAnswer(roomId, index)
                                lastAnswerResult = if (correct) "🎉 Correct! You won ${activeRound.question.prizeCredits} credits!" else "❌ Wrong answer! Try another option!"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Text(
                                text = "[${index + 1}] $option",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        } else {
            // No round active - Launch button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🎯 No Active Round", color = Color(0xFFE2E8F0), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Be the Trivia Master! Launch a question into ${roomId} for all room participants.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { repository.startTriviaGame(roomId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                    ) {
                        Text("🚀 Start Trivia Round (مجاني)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        if (lastAnswerResult != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00ACC1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lastAnswerResult!!,
                    color = Color(0xFF80DEEA),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun CoinFlipGameTab(
    roomId: String,
    userProfile: UserProfile,
    repository: MigRepository
) {
    var choice by remember { mutableStateOf("HEADS") }
    var betAmount by remember { mutableIntStateOf(20) }
    var isFlipping by remember { mutableStateOf(false) }
    var flipResultText by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val betOptions = listOf(10, 20, 50, 100, 250, 500)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🪙 Coin Flip Duel",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Choose Heads or Tails. Guess right and double your credits!",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )

        // Side Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clickable { choice = "HEADS" },
                shape = RoundedCornerShape(12.dp),
                color = if (choice == "HEADS") Color(0xFFF59E0B) else Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (choice == "HEADS") Color(0xFFFDE047) else Color(0xFF334155)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑", fontSize = 28.sp)
                    Text(
                        text = "HEADS (ملك)",
                        color = if (choice == "HEADS") Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clickable { choice = "TAILS" },
                shape = RoundedCornerShape(12.dp),
                color = if (choice == "TAILS") Color(0xFFF59E0B) else Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (choice == "TAILS") Color(0xFFFDE047) else Color(0xFF334155)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🦅", fontSize = 28.sp)
                    Text(
                        text = "TAILS (كتابة)",
                        color = if (choice == "TAILS") Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bet Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Select Bet Amount:", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(betOptions) { amount ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (betAmount == amount) Color(0xFFF59E0B) else Color(0xFF334155),
                        modifier = Modifier.clickable { betAmount = amount }
                    ) {
                        Text(
                            text = "$amount cr",
                            color = if (betAmount == amount) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (flipResultText != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = flipResultText!!,
                    color = Color(0xFFFDE047),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Button(
            onClick = {
                if (!isFlipping) {
                    isFlipping = true
                    coroutineScope.launch {
                        delay(600)
                        val res = repository.playCoinFlip(roomId, choice, betAmount)
                        flipResultText = if (res.isWin) "🎉 Coin landed on ${res.outcome}! WON +$betAmount cr!" else "💥 Coin landed on ${res.outcome}! Lost -$betAmount cr."
                        isFlipping = false
                    }
                }
            },
            enabled = !isFlipping && userProfile.credits >= betAmount,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
        ) {
            Text(
                text = if (isFlipping) "🪙 Flipping Coin..." else "🪙 Flip Coin ($betAmount cr)",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun Lucky7GameTab(
    roomId: String,
    userProfile: UserProfile,
    repository: MigRepository
) {
    var choice by remember { mutableStateOf("7") }
    var betAmount by remember { mutableIntStateOf(20) }
    var isRolling by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val betOptions = listOf(10, 20, 50, 100, 250)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎰 Lucky 7 (النرد المحظوظ)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Predict 2 dice sum: Low (<7) [2x], Lucky 7 (=7) [4x Payout! 🔥], High (>7) [2x]",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )

        // 3 Choices
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("LOW", "Low (<7)", "2x"),
                Triple("7", "Lucky 7 (=7)", "4x 🔥"),
                Triple("HIGH", "High (>7)", "2x")
            ).forEach { (key, title, mult) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                        .clickable { choice = key },
                    shape = RoundedCornerShape(10.dp),
                    color = if (choice == key) Color(0xFF8B5CF6) else Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (choice == key) Color(0xFFC084FC) else Color(0xFF334155)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = mult, color = Color(0xFFFDE047), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Bet Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Select Bet Amount:", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(betOptions) { amount ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (betAmount == amount) Color(0xFF8B5CF6) else Color(0xFF334155),
                        modifier = Modifier.clickable { betAmount = amount }
                    ) {
                        Text(
                            text = "$amount cr",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (resultText != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = resultText!!,
                    color = Color(0xFFC084FC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Button(
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    coroutineScope.launch {
                        delay(600)
                        val res = repository.playLucky7(roomId, choice, betAmount)
                        resultText = if (res.isWin) "🎉 Dice sum: ${res.total}! WON +${res.wonAmount} cr!" else "💥 Dice sum: ${res.total}! Lost -$betAmount cr."
                        isRolling = false
                    }
                }
            },
            enabled = !isRolling && userProfile.credits >= betAmount,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
        ) {
            Text(
                text = if (isRolling) "🎰 Rolling Dice..." else "🎰 Roll & Bet ($betAmount cr)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CommandsHelpTab(
    roomId: String,
    repository: MigRepository,
    onDismiss: () -> Unit
) {
    val commands = listOf(
        Pair("!dice 50", "Roll 2 dice vs migBot with 50 cr bet"),
        Pair("!trivia", "Start a live trivia quiz round in this room"),
        Pair("!ans 1", "Submit option 1 to active trivia question"),
        Pair("!flip heads 50", "Bet 50 cr on Heads in coin flip"),
        Pair("!lucky7 7 50", "Bet 50 cr on Lucky 7 (4x Payout)"),
        Pair("!credits", "Check your current credit wallet balance"),
        Pair("!rules", "Display game payout rates and XP bonuses"),
        Pair("!help", "Display migBot room games help menu")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🤖 Quick Command Cheatsheet",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "You can type any command directly in the chat bar, or tap below to execute instantly:",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        commands.forEach { (cmd, desc) ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        repository.sendRoomMessage(roomId, cmd)
                        onDismiss()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cmd, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = desc, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Text(text = "Send ➡️", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

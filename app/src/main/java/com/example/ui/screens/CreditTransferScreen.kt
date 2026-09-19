package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.model.CreditTransfer
import com.example.model.TransferResult
import com.example.ui.components.MigHeaderBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditTransferScreen(
    repository: MigRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialRecipient: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState()
    val transferHistory by repository.creditTransfers.collectAsState()
    val friendsList by repository.friends.collectAsState()

    var recipient by remember { mutableStateOf(initialRecipient ?: "") }
    var amountText by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }

    var isProcessing by remember { mutableStateOf(false) }
    var showFriendPicker by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf<CreditTransfer?>(null) }

    val quickAmounts = listOf(10, 50, 100, 250, 500, 1000)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onBack)
                // Ribbon Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B)))
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Transfer Credits (تحويل الكريدت)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFF8F00),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "🪙", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${userProfile.credits} cr",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF4F6F9)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: Balance & PIN Security Status
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF0288D1))
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "رصيد الكريدت المتاح:",
                                    color = Color(0xFFBBDEFB),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${userProfile.credits}",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "credits",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "PIN Protected",
                                        tint = Color(0xFF81C784),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (userProfile.securityPin.isNotEmpty()) "محمي برمز الأمان PIN" else "لم يتم تعيين PIN",
                                        color = Color(0xFFC8E6C9),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { showChangePinDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Key, contentDescription = "PIN", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "تعديل PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Card 2: Transfer Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Transfer",
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إرسال كريدت لمستخدم mig33",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Recipient Input
                        Text(
                            text = "اسم المستخدم المستلم (Username):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF455A64)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = recipient,
                            onValueChange = { recipient = it },
                            placeholder = { Text("مثال: f1yingkit3", fontSize = 12.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { showFriendPicker = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("الأصدقاء 👥", fontSize = 11.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transfer_recipient_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0288D1),
                                unfocusedBorderColor = Color(0xFFCFD8DC)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Amount Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مبلغ الكريدت (Amount):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF455A64)
                            )
                            Text(
                                text = "الحد الأقصى: ${userProfile.credits} cr",
                                fontSize = 10.sp,
                                color = Color(0xFF78909C)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.all { it.isDigit() }) {
                                    amountText = input
                                }
                            },
                            placeholder = { Text("0", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Text(text = "🪙", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { amountText = userProfile.credits.toString() },
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Text("الكل MAX", fontSize = 10.sp, color = Color(0xFFFF6D00), fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transfer_amount_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0288D1),
                                unfocusedBorderColor = Color(0xFFCFD8DC)
                            )
                        )

                        // Quick Amount Chips
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickAmounts.forEach { amt ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (amountText == amt.toString()) Color(0xFF0288D1) else Color(0xFFECEFF1),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { amountText = amt.toString() }
                                ) {
                                    Text(
                                        text = "+$amt",
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (amountText == amt.toString()) Color.White else Color(0xFF455A64),
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Security PIN
                        Text(
                            text = "رمز الأمان السري (Security PIN):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF455A64)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = securityPin,
                            onValueChange = {
                                if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                    securityPin = it
                                }
                            },
                            placeholder = { Text("أدخل رمز PIN المكون من 4 أرقام (الافتراضي: 1234)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                    Icon(
                                        imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle PIN",
                                        tint = Color(0xFF78909C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transfer_pin_input"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0288D1),
                                unfocusedBorderColor = Color(0xFFCFD8DC)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Optional Note
                        Text(
                            text = "رسالة التحويل (اختياري):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF455A64)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { if (it.length <= 60) note = it },
                            placeholder = { Text("مثال: شكراً على لعبة النرد! 🎲", fontSize = 11.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Message, contentDescription = null, tint = Color(0xFF78909C), modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0288D1),
                                unfocusedBorderColor = Color(0xFFCFD8DC)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val amount = amountText.toIntOrNull() ?: 0
                                if (recipient.isBlank()) {
                                    Toast.makeText(context, "يرجى تحديد اسم المستلم!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (amount <= 0) {
                                    Toast.makeText(context, "يرجى إدخال مبلغ كريدت صحيح!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (securityPin.isBlank()) {
                                    Toast.makeText(context, "يرجى إدخال رمز الأمان PIN!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isProcessing = true
                                coroutineScope.launch {
                                    val result = repository.transferCredits(
                                        recipientUsername = recipient,
                                        amount = amount,
                                        pin = securityPin,
                                        note = note
                                    )
                                    isProcessing = false
                                    when (result) {
                                        is TransferResult.Success -> {
                                            showSuccessDialog = result.transfer
                                            amountText = ""
                                            securityPin = ""
                                            note = ""
                                            Toast.makeText(context, "تم تحويل $amount كريدت بنجاح!", Toast.LENGTH_LONG).show()
                                        }
                                        is TransferResult.Error -> {
                                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            enabled = !isProcessing && recipient.isNotBlank() && (amountText.toIntOrNull() ?: 0) > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("transfer_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تأكيد إرسال الكريدت 💸",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Info / Rules notice
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "رسوم التحويل 0% مجاناً • التحويل فوري وموثق في سجل المعاملات.",
                                fontSize = 10.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }
            }

            // Card 3: Transfer History Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سجل التحويلات السابقة (${transferHistory.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                }
            }

            if (transferHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "لا توجد معاملات تحويل سابقة حتى الآن.",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                    }
                }
            } else {
                items(transferHistory) { transfer ->
                    val isSent = transfer.senderUsername.equals(userProfile.username, ignoreCase = true)
                    val dateFormatted = remember(transfer.timestamp) {
                        SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()).format(Date(transfer.timestamp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSent) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSent) Icons.AutoMirrored.Filled.Send else Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = if (isSent) Color(0xFFC62828) else Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isSent) "إرسال إلى @${transfer.receiverUsername}" else "استلام من @${transfer.senderUsername}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF263238)
                                    )
                                    if (transfer.note.isNotEmpty()) {
                                        Text(
                                            text = "\"${transfer.note}\"",
                                            fontSize = 10.sp,
                                            color = Color(0xFF546E7A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = dateFormatted,
                                        fontSize = 9.sp,
                                        color = Color(0xFF90A4AE)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isSent) "-${transfer.amount} cr" else "+${transfer.amount} cr",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSent) Color(0xFFC62828) else Color(0xFF2E7D32)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE0F2FE)
                                ) {
                                    Text(
                                        text = "مكتمل ✓",
                                        fontSize = 9.sp,
                                        color = Color(0xFF0288D1),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Friend Picker Dialog
    if (showFriendPicker) {
        AlertDialog(
            onDismissRequest = { showFriendPicker = false },
            title = {
                Text(
                    text = "اختر من قائمة الأصدقاء",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (friendsList.isEmpty()) {
                        item {
                            Text(
                                text = "لا يوجد أصدقاء مسجلين حالياً.",
                                fontSize = 12.sp,
                                color = Color(0xFF78909C),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(friendsList) { friendUsername ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        recipient = friendUsername
                                        showFriendPicker = false
                                    },
                                color = Color(0xFFF1F5F9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "👤", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "@$friendUsername", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0D47A1))
                                        Text(text = "mig33 Friend", fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFriendPicker = false }) {
                    Text("إلغاء", color = Color(0xFF546E7A))
                }
            }
        )
    }

    // Change Security PIN Dialog
    if (showChangePinDialog) {
        var currentPinInput by remember { mutableStateOf("") }
        var newPinInput by remember { mutableStateOf("") }
        var confirmPinInput by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF8F00))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تعديل رمز الأمان (Security PIN)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "رمز الأمان يحمي رصيدك من التحويلات غير المصرح بها.",
                        fontSize = 11.sp,
                        color = Color(0xFF546E7A)
                    )

                    OutlinedTextField(
                        value = currentPinInput,
                        onValueChange = { if (it.length <= 6) currentPinInput = it },
                        label = { Text("رمز PIN الحالي (الافتراضي: 1234)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 6) newPinInput = it },
                        label = { Text("رمز PIN الجديد (4-6 أرقام)", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 6) confirmPinInput = it },
                        label = { Text("تأكيد رمز PIN الجديد", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    pinError?.let { err ->
                        Text(text = err, color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput != confirmPinInput) {
                            pinError = "رمز PIN الجديد غير متطابق!"
                            return@Button
                        }
                        if (newPinInput.length < 4) {
                            pinError = "يجب أن يتكون رمز PIN من 4 أرقام على الأقل!"
                            return@Button
                        }

                        coroutineScope.launch {
                            val success = repository.updateSecurityPin(currentPinInput, newPinInput)
                            if (success) {
                                Toast.makeText(context, "تم تحديث رمز الأمان بنجاح!", Toast.LENGTH_SHORT).show()
                                showChangePinDialog = false
                            } else {
                                pinError = "رمز PIN الحالي غير صحيح!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text("حفظ PIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("إلغاء", color = Color(0xFF546E7A))
                }
            }
        )
    }

    // Success Confirmation Dialog
    showSuccessDialog?.let { transfer ->
        AlertDialog(
            onDismissRequest = { showSuccessDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎉", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نجحت عملية التحويل!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F8E9))
                        .padding(12.dp)
                ) {
                    Text(text = "المبلغ المحول: ${transfer.amount} cr", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B5E20))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "المستلم: @${transfer.receiverUsername}", fontSize = 12.sp, color = Color(0xFF33691E))
                    if (transfer.note.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "الملاحظة: ${transfer.note}", fontSize = 11.sp, color = Color(0xFF558B2F))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "رصيدك المتبقي: ${userProfile.credits} cr", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("تم", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

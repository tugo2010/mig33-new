package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.FeedPost
import com.example.model.PostReply
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar
import com.example.ui.theme.*

@Composable
fun FeedsScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val posts by repository.feedPosts.collectAsState()
    val userProfile by repository.userProfile.collectAsState()
    var commentInput by remember { mutableStateOf("") }
    var selectedPostId by remember { mutableStateOf<String?>(posts.firstOrNull()?.id) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }
    var newPostTag by remember { mutableStateOf("#General") }
    var includePhoto by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(
                    onMenuClick = onOpenDrawer,
                    unreadNotifications = 3
                )
                MigSubHeaderBar(
                    leftText = "",
                    leftAvatarRes = R.drawable.avatar_f1yingkit,
                    centerTitle = "migCute",
                    rightAvatarRes = R.drawable.avatar_hipst4r,
                    onLeftClick = { onNavigateToChat("f1yingkit3") },
                    onRightClick = { onNavigateToChat("hipst4r") }
                )
            }
        },
        bottomBar = {
            // Bottom Reply / Status Bar matching Screenshot 2
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feed_bottom_input_bar"),
                color = Color(0xFFEEEEEE),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Yellow / Camera Icon Button - Opens Create Post
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF222222))
                            .border(1.dp, Color(0xFF444444), RoundedCornerShape(6.dp))
                            .clickable { showCreatePostDialog = true }
                            .testTag("feed_create_post_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color(0xFFFFD54F), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // White Text Input Field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (commentInput.isEmpty()) {
                            val targetPost = posts.find { it.id == selectedPostId }
                            val targetName = targetPost?.authorDisplayName ?: "migCute"
                            Text(text = "Reply to $targetName...", color = Color(0xFF9E9E9E), fontSize = 13.sp)
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.Black,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feed_reply_input")
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                                )
                            )
                            .clickable {
                                selectedPostId?.let { pid ->
                                    if (commentInput.isNotBlank()) {
                                        repository.addPostReply(pid, commentInput)
                                        commentInput = ""
                                    }
                                }
                            }
                            .padding(horizontal = 10.dp)
                            .testTag("feed_reply_submit_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Reply", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .testTag("feeds_list")
        ) {
            items(posts) { post ->
                PostItemView(
                    post = post,
                    currentUser = userProfile,
                    onStarClick = { repository.toggleStarPost(post.id) },
                    onShareClick = { repository.sharePost(post.id) },
                    onReplyClick = { selectedPostId = post.id },
                    onAuthorClick = { onOpenProfile(post.authorUsername) },
                    onReplyAuthorClick = { onOpenProfile(it) }
                )
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            }
        }
    }

    // Create New Post Modal Dialog
    if (showCreatePostDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = {
                Text(text = "📸 Create mig33 Post", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("What's on your mind? #Cute #Games...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )

                    // Tag Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("#General", "#Cute", "#Trends", "#Gaming").forEach { tag ->
                            val isSelected = newPostTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) Color(0xFFFF9800) else Color(0xFFECEFF1))
                                    .clickable { newPostTag = tag }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = if (isSelected) Color.White else Color(0xFF455A64),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includePhoto = !includePhoto },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = includePhoto, onCheckedChange = { includePhoto = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Attach photo illustration", fontSize = 13.sp, color = Color(0xFF37474F))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPostText.isNotBlank()) {
                            val photoRes = if (includePhoto) R.drawable.cat_glasses_meme else null
                            repository.addFeedPost(newPostText.trim(), newPostTag, photoRes)
                            newPostText = ""
                            showCreatePostDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Publish", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PostItemView(
    post: FeedPost,
    currentUser: com.example.model.UserProfile? = null,
    onStarClick: () -> Unit,
    onShareClick: () -> Unit,
    onReplyClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onReplyAuthorClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("post_item_${post.id}")
    ) {
        // Author Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAuthorClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isMe = currentUser != null && post.authorUsername.equals(currentUser.username, ignoreCase = true)
            if (isMe) {
                com.example.ui.components.AvatarView(
                    config = currentUser.avatarConfig,
                    modifier = Modifier.size(48.dp),
                    cornerRadius = 4.dp,
                    borderColor = Color(0xFF0288D1),
                    borderWidth = 1.dp
                )
            } else {
                Image(
                    painter = painterResource(id = post.authorAvatarRes),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF0288D1), RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorDisplayName,
                        color = Color(0xFF0288D1),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (post.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF43A047)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✔", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Content / Hashtag
                Text(
                    text = post.content,
                    color = Color(0xFF212121),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Post Image (e.g. Cat Spectacles Meme)
        if (post.imageRes != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(4.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = "Post photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // TimeAgo and Meta stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.timeAgo,
                color = Color(0xFF757575),
                fontSize = 12.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Reply, contentDescription = "Replies", tint = Color(0xFF757575), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${post.repliesCount}", color = Color(0xFF757575), fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Shares", tint = Color(0xFF757575), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${post.shareCount}", color = Color(0xFF757575), fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = if (post.isStarred) Color(0xFFFFB300) else Color(0xFF757575), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${post.starCount}", color = Color(0xFF757575), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3 Large Glossy Action Pills (Matching Screenshot 2 & 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Reply Button Pill (Glossy Blue Gradient)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5), Color(0xFF1565C0))
                        )
                    )
                    .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(6.dp))
                    .clickable { onReplyClick() }
                    .testTag("post_reply_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Share Button Pill (Glossy Silver Gradient)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFEEEEEE), Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                        )
                    )
                    .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(6.dp))
                    .clickable { onShareClick() }
                    .testTag("post_share_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF616161),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Star / Favorite Button Pill (Glossy Silver Gradient)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFEEEEEE), Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                        )
                    )
                    .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(6.dp))
                    .clickable { onStarClick() }
                    .testTag("post_star_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = if (post.isStarred) Color(0xFFFFB300) else Color(0xFF757575),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Nested Comments List
        if (post.replies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                post.replies.forEach { reply ->
                    PostReplyItemView(
                        reply = reply,
                        onAuthorClick = { onReplyAuthorClick(reply.authorUsername) }
                    )
                }
            }
        }
    }
}

@Composable
fun PostReplyItemView(
    reply: PostReply,
    onAuthorClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAuthorClick() }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = reply.authorAvatarRes),
            contentDescription = "Reply Avatar",
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(3.dp))
                .clickable { onAuthorClick() },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reply.authorUsername,
                    color = Color(0xFF0288D1),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (reply.authorDisplayName.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reply.authorDisplayName,
                        color = Color(0xFF757575),
                        fontSize = 12.sp
                    )
                }
                if (reply.badges.isNotEmpty()) {
                    reply.badges.forEach { b ->
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (b == "A") Color(0xFFFFB300) else Color(0xFF7E57C2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = b, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (reply.replyToUser != null) {
                Text(
                    text = "Reply: @${reply.replyToUser}",
                    color = Color(0xFF0288D1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment Text
            Text(
                text = reply.content,
                color = Color(0xFF263238),
                fontSize = 13.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Bottom reply meta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = reply.timeAgo, color = Color(0xFF9E9E9E), fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(12.dp))
                        Text(text = "${reply.repliesCount}", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(12.dp))
                        Text(text = "${reply.shareCount}", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(12.dp))
                        Text(text = "${reply.starCount}", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

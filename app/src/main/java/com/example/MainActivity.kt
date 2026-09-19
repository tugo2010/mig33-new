package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.data.MigRepository
import com.example.ui.components.MigDrawerContent
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MigApp()
            }
        }
    }
}

@Composable
fun MigApp() {
    val context = LocalContext.current
    val repository = remember { MigRepository(context.applicationContext) }
    val userProfile by repository.userProfile.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val onOpenPublicProfile: (String) -> Unit = { u -> 
        if (u == userProfile.username) { 
            navController.navigate("profile") 
        } else { 
            navController.navigate("public_profile/$u") 
        } 
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            MigDrawerContent(
                userProfile = userProfile,
                onNavigateTo = { destination ->
                    coroutineScope.launch { drawerState.close() }
                    when (destination) {
                        "profile" -> navController.navigate("profile")
                        "avatar_studio" -> navController.navigate("avatar_studio")
                        "chat_rooms" -> navController.navigate("chat_rooms")
                        "gifts_shop" -> navController.navigate("gifts_shop")
                        "feeds" -> navController.navigate("feeds")
                        "home_chats" -> navController.navigate("home_chats")
                        else -> navController.navigate(destination)
                    }
                },
                onLogout = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSearch = { query ->
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("chat_rooms")
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "home_chats",
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Login Screen
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { username, invisible ->
                        repository.setUserProfile(username, invisible)
                        navController.navigate("home_chats") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // 2. Home Chats Screen (Screenshot 7)
            composable("home_chats") {
                HomeChatsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenChatRoom = { roomId -> navController.navigate("chat_room/$roomId") },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenProfile = onOpenPublicProfile,
                    onOpenFriends = { navController.navigate("friends") },
                    onOpenStore = { navController.navigate("gifts_shop") }
                )
            }

            // 3. Chat Rooms Directory (Screenshot 1)
            composable("chat_rooms") {
                ChatRoomsDirectoryScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onJoinRoom = { roomId -> navController.navigate("chat_room/$roomId") }
                )
            }

            // 4. Live Chat Room (Screenshot 5)
            composable(
                route = "chat_room/{roomId}",
                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: "danger_1"
                ChatRoomScreen(
                    roomId = roomId,
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBackHome = { navController.navigate("home_chats") },
                    onOpenProfile = onOpenPublicProfile,
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") }
                )
            }

            // 5. Direct Chat with Retro Keyboard (Screenshot 6)
            composable(
                route = "direct_chat/{username}",
                arguments = listOf(navArgument("username") { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: "f1yingkit3"
                DirectChatScreen(
                    recipientUsername = username,
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBackHome = { navController.navigate("home_chats") },
                    onOpenProfile = onOpenPublicProfile
                )
            }

            // 6. Feeds / Mini-blog (Screenshots 2 & 4)
            composable("feeds") {
                FeedsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onNavigateToChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenProfile = onOpenPublicProfile
                )
            }

            // 7. Profile Screen
            composable("profile") {
                ProfileScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenAvatarStudio = { navController.navigate("avatar_studio") },
                    onOpenGiftsShop = { navController.navigate("gifts_shop") },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenBadges = { navController.navigate("badges") },
                    onOpenFriends = { navController.navigate("friends") },
                    onOpenEditProfile = { navController.navigate("edit_profile") },
                    onOpenAdminDashboard = { navController.navigate("admin_dashboard") },
                    onOpen2DAvatarBuilder = { navController.navigate("avatar_builder") }
                )
            }
            composable(
                route = "public_profile/{username}",
                arguments = listOf(navArgument("username") { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                PublicProfileScreen(
                    username = username,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onOpenDirectChat = { u -> navController.navigate("direct_chat/$u") },
                    onTransferCredits = { u -> navController.navigate("credit_transfer/$u") }
                )
            }

            // 8. Admin Control Panel Screen
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() }
                )
            }

            // 8. Badges Showcase Screen
            composable("badges") {
                BadgesScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() }
                )
            }

            // 9. Friends List Screen
            composable("friends") {
                FriendsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenProfile = onOpenPublicProfile
                )
            }

            // 10. Edit Full Profile Screen
            composable("edit_profile") {
                EditProfileScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenAvatarStudio = { navController.navigate("avatar_studio") }
                )
            }

            // 8. Avatar Studio Customizer
            composable("avatar_studio") {
                AvatarStudioScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onOpen2DBuilder = { navController.navigate("avatar_builder") }
                )
            }

            // 8b. 2D Layered Avatar Builder (MigNew Foundation)
            composable("avatar_builder") {
                val avatarVm = remember { AvatarViewModel(repository) }
                AvatarBuilderScreen(
                    viewModel = avatarVm,
                    onBack = { navController.popBackStack() }
                )
            }

            // 9. Virtual Gifts & Credits Shop
            composable("gifts_shop") {
                GiftsShopScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            // 9b. Credit Transfer Screen with PIN Security
            composable("credit_transfer") {
                CreditTransferScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "credit_transfer/{username}",
                arguments = listOf(navArgument("username") { type = NavType.StringType })
            ) { backStackEntry ->
                val targetUser = backStackEntry.arguments?.getString("username") ?: ""
                CreditTransferScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    initialRecipient = targetUser
                )
            }

            // 10. Invite a Friend
            composable("invite") {
                InviteScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() }
                )
            }

            // 11. Recommendations
            composable("recommendations") {
                RecommendationsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onJoinRoom = { roomId -> navController.navigate("chat_room/$roomId") }
                )
            }

            // 12. Groups
            composable("groups") {
                GroupsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenGroupChat = { groupId -> navController.navigate("chat_room/danger_1") }
                )
            }

            // 13. migWorld
            composable("mig_world") {
                MigWorldScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onJoinRoom = { roomId -> navController.navigate("chat_room/$roomId") }
                )
            }

            // 14. Mentions
            composable("mentions") {
                MentionsScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenProfile = onOpenPublicProfile
                )
            }

            // 15. Watchlist
            composable("watchlist") {
                WatchlistScreen(
                    repository = repository,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onBack = { navController.popBackStack() },
                    onOpenDirectChat = { username -> navController.navigate("direct_chat/$username") },
                    onOpenChatRoom = { roomId -> navController.navigate("chat_room/$roomId") },
                    onOpenProfile = onOpenPublicProfile
                )
            }
        }
    }
}

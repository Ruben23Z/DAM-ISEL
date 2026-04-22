package A51388.spinnet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

private data class FeedPost(
    val title: String,
    val author: String,
    val timeAgo: String,
    val description: String,
    val likes: Int,
    val comments: Int,
    val category: String,
    val accentColor: Color
)

private val feedPosts = listOf(
    FeedPost(
        title       = "Aggressive Forehand Loops",
        author      = "Chen Wei",
        timeAgo     = "2 hours ago",
        description = "A 45-minute multi-ball session focused on weight transfer and generating maximum topspin from the mid-distance.",
        likes       = 142,
        comments    = 28,
        category    = "FOREHAND",
        accentColor = NeonGreen
    ),
    FeedPost(
        title       = "Service Mastery",
        author      = "Elena R.",
        timeAgo     = "5 hours ago",
        description = "Detailed breakdown of the pendulum serve, focusing on wrist snap and disguised spin variations.",
        likes       = 98,
        comments    = 15,
        category    = "SERVICE",
        accentColor = VibrantPurple
    ),
    FeedPost(
        title       = "Dynamic Footwork Foundations",
        author      = "Marcus T.",
        timeAgo     = "1 day ago",
        description = "A grueling shadow play routine to improve side-to-side lateral movement and pivot speed.",
        likes       = 205,
        comments    = 43,
        category    = "FOOTWORK",
        accentColor = Tertiary
    ),
    FeedPost(
        title       = "Backspin Loop Conversion",
        author      = "Yuki S.",
        timeAgo     = "2 days ago",
        description = "Mastering the transition from passive block to aggressive loop against heavy backspin serves.",
        likes       = 76,
        comments    = 11,
        category    = "TECHNIQUE",
        accentColor = NeonGreen
    ),
)

private val categories = listOf("ALL", "FOREHAND", "BACKHAND", "SERVICE", "FOOTWORK", "TECHNIQUE")

@Composable
fun CommunityFeedScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
) {
    var selectedTab      by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    val scrollState = rememberScrollState()
    val tabs = listOf("Feed", "Network", "Alerts")

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            SpinNetBottomBar(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF0A1A3E), Surface),
                        radius = 900f
                    )
                )
                .padding(padding)
        ) {
            // ── Top header ───────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SPINNET",
                            color = NeonGreen,
                            style = MaterialTheme.typography.labelLarge,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "Explore",
                            color = OnSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                            .border(1.dp, GlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Discover high-performance training routines shared\nby the SpinNet community.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Tabs ─────────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = SurfaceContainer,
                contentColor     = NeonGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color    = NeonGreen
                    )
                }
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick  = { selectedTab = idx },
                        text = {
                            Text(
                                text  = title.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedTab == idx) NeonGreen else OnSurfaceVariant
                            )
                        }
                    )
                }
            }

            // ── Content (only Feed tab has full content) ─────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // ── Category chips ───────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick  = { selectedCategory = cat },
                                label = {
                                    Text(
                                        cat,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor    = NeonGreen.copy(alpha = 0.2f),
                                    selectedLabelColor        = NeonGreen,
                                    containerColor            = SurfaceContainerHigh,
                                    labelColor                = OnSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled          = true,
                                    selected         = isSelected,
                                    selectedBorderColor = NeonGreen.copy(alpha = 0.6f),
                                    borderColor      = OutlineVariant
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Feed cards ───────────────────────────────
                    val filtered = if (selectedCategory == "ALL") feedPosts
                                   else feedPosts.filter { it.category == selectedCategory }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No posts for this category yet.",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        filtered.forEach { post ->
                            FeedCard(post = post)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                } else {
                    // Placeholder for Network / Alerts tabs
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                if (selectedTab == 1) Icons.Outlined.People else Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint   = OnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text  = if (selectedTab == 1) "Connect with other players" else "You're all caught up!",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeedCard(post: FeedPost) {
    var liked by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            // Category chip + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(post.accentColor.copy(alpha = 0.15f))
                        .border(1.dp, post.accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(post.category, color = post.accentColor, style = MaterialTheme.typography.labelSmall)
                }
                Text(post.timeAgo, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(10.dp))

            // Title
            Text(post.title, color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Author
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(VibrantPurple.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.author.first().toString(),
                        color = Tertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text("By ${post.author}", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))

            // Description
            Text(post.description, color = OnSurface.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f))

            Spacer(Modifier.height(10.dp))

            // Actions row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent)
                ) {
                    IconButton(
                        onClick = { liked = !liked },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (liked) NeonGreen else OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        "${if (liked) post.likes + 1 else post.likes}",
                        color = if (liked) NeonGreen else OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comments", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${post.comments}", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF051424)
@Composable
fun CommunityFeedPreview() {
    SpinNetTheme {
        CommunityFeedScreen(
            currentDestination = SpinNetDestination.Community,
            onNavigate = {}
        )
    }
}

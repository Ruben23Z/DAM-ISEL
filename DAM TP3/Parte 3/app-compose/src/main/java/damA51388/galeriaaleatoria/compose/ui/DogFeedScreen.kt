package damA51388.galeriaaleatoria.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import damA51388.core.model.ImageItem
import damA51388.galeriaaleatoria.compose.viewmodel.ImageViewModelCompose
import kotlinx.coroutines.delay

@Composable
fun DogFeedScreen(
    modifier: Modifier = Modifier,
    viewModel: ImageViewModelCompose = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.images.size })

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (uiState.isLoading && uiState.images.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { if (it < uiState.images.size) uiState.images[it].id else it }
            ) { page ->
                if (page < uiState.images.size) {
                    val item = uiState.images[page]
                    DogCard(
                        item = item,
                        onDoubleTap = { viewModel.toggleLike(item.id) }
                    )
                    
                    // Infinite scroll logic
                    if (page >= uiState.images.size - 5) {
                        LaunchedEffect(Unit) {
                            viewModel.loadMore()
                        }
                    }
                }
            }

            // --- TOP OVERLAY (Tabs + Favorites) ---
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
            ) {
                // Tabs: For You
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "For You",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Favorites Thumbnails
                if (uiState.favorites.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favorites) { fav ->
                            AsyncImage(
                                model = fav.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Color.DarkGray)
                            )
                        }
                    }
                }
            }

            // --- BOTTOM OVERLAY (Info + Actions) ---
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Breed Info
                val currentPage = pagerState.currentPage
                if (currentPage < uiState.images.size) {
                    val currentItem = uiState.images[currentPage]
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "@dog_lover",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Beautiful ${currentItem.displayBreed} found!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "#dogs #breed #${currentItem.displayBreed.lowercase().replace(" ", "")}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    // Actions Bar (Using current item state)
                    BottomActionsBar(
                        isLiked = currentItem.isLiked,
                        isFavorite = viewModel.isFavorite(currentItem.id),
                        onLikeToggle = { viewModel.toggleLike(currentItem.id) },
                        onFavoriteToggle = { viewModel.toggleFavorite(currentItem) },
                        onDownloadClick = { viewModel.downloadImage(currentItem.id) }
                    )
                }
            }
        }

        // Offline / Error Banner
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 90.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Download Success Message
        AnimatedVisibility(
            visible = uiState.downloadMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = uiState.downloadMessage ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DogCard(
    item: ImageItem,
    onDoubleTap: () -> Unit
) {
    var showHeartAnimation by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showHeartAnimation = true
                        onDoubleTap()
                    }
                )
            }
    ) {
        AsyncImage(
            model = item.url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient at bottom for text legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )

        // Double-tap heart animation
        if (showHeartAnimation) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)
            )
            LaunchedEffect(Unit) {
                delay(500)
                showHeartAnimation = false
            }
        }
    }
}

@Composable
fun BottomActionsBar(
    isLiked: Boolean,
    isFavorite: Boolean,
    onLikeToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionItem(
                icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                label = if (isLiked) "Curtido" else "Curtir",
                tint = if (isLiked) Color.Red else Color.White,
                modifier = Modifier.weight(1f),
                onClick = onLikeToggle
            )
            
            VerticalDivider(modifier = Modifier.height(32.dp).width(0.5.dp), color = Color.White.copy(alpha = 0.2f))
            
            ActionItem(
                icon = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                label = if (isFavorite) "Salvo" else "Salvar",
                tint = if (isFavorite) Color.Yellow else Color.White,
                modifier = Modifier.weight(1f),
                onClick = onFavoriteToggle
            )
            
            VerticalDivider(modifier = Modifier.height(32.dp).width(0.5.dp), color = Color.White.copy(alpha = 0.2f))
            
            ActionItem(
                icon = Icons.Default.FileDownload,
                label = "Baixar",
                tint = Color.White,
                modifier = Modifier.weight(1f),
                onClick = onDownloadClick
            )
        }
    }
}

@Composable
fun RowScope.ActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

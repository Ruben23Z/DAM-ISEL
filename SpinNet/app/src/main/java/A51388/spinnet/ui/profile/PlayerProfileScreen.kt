package A51388.spinnet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*

private data class ProfileStrings(
    val playerProfile: String,
    val personalInfo: String,
    val fullName: String,
    val email: String,
    val club: String,
    val country: String,
    val memberSince: String,
    val equipment: String,
    val racket: String,
    val forehandRubber: String,
    val backhandRubber: String,
    val appSettings: String,
    val language: String,
    val notifications: String,
    val hapticFeedback: String,
    val account: String,
    val editProfile: String,
    val changePassword: String,
    val privacySettings: String,
    val helpSupport: String,
    val logout: String,
    val logoutConfirmTitle: String,
    val logoutConfirmMsg: String,
    val cancel: String,
    val confirm: String,
    val appVersion: String,
    val level: String
)

private val stringsEN = ProfileStrings(
    playerProfile = "Player Profile",
    personalInfo = "Personal Information",
    fullName = "Full Name",
    email = "Email",
    club = "Club",
    country = "Country",
    memberSince = "Member Since",
    equipment = "Equipment",
    racket = "Racket",
    forehandRubber = "FH Rubber",
    backhandRubber = "BH Rubber",
    appSettings = "App Settings",
    language = "Language",
    notifications = "Notifications",
    hapticFeedback = "Haptic Feedback",
    account = "Account",
    editProfile = "Edit Profile",
    changePassword = "Change Password",
    privacySettings = "Privacy Settings",
    helpSupport = "Help & Support",
    logout = "Log Out",
    logoutConfirmTitle = "Log Out?",
    logoutConfirmMsg = "You will be returned to the login screen.",
    cancel = "Cancel",
    confirm = "Log Out",
    appVersion = "SpinNet v1.0.0",
    level = "Advanced · Penhold"
)

private val stringsPT = ProfileStrings(
    playerProfile = "Perfil do Jogador",
    personalInfo = "Informações Pessoais",
    fullName = "Nome Completo",
    email = "Email",
    club = "Clube",
    country = "País",
    memberSince = "Membro Desde",
    equipment = "Equipamento",
    racket = "Raquete",
    forehandRubber = "Borracha DT",
    backhandRubber = "Borracha RE",
    appSettings = "Definições",
    language = "Idioma",
    notifications = "Notificações",
    hapticFeedback = "Vibração",
    account = "Conta",
    editProfile = "Editar Perfil",
    changePassword = "Alterar Password",
    privacySettings = "Privacidade",
    helpSupport = "Ajuda e Suporte",
    logout = "Terminar Sessão",
    logoutConfirmTitle = "Terminar Sessão?",
    logoutConfirmMsg = "Será redirecionado para o ecrã de login.",
    cancel = "Cancelar",
    confirm = "Terminar",
    appVersion = "SpinNet v1.0.0",
    level = "Avançado · Caneta"
)
@Composable
fun PlayerProfileScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    onLogout: () -> Unit
) {
    var isPortuguese by remember { mutableStateOf(false) }
    var notificationsOn by remember { mutableStateOf(true) }
    var hapticOn by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val s = if (isPortuguese) stringsPT else stringsEN
    val scrollState = rememberScrollState()

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            title = s.logoutConfirmTitle,
            message = s.logoutConfirmMsg,
            cancel = s.cancel,
            confirm = s.confirm,
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            }
        )
    }

    Scaffold(
        containerColor = Surface,
        bottomBar = { SpinNetBottomBar(currentDestination, onNavigate) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1A0A2E), Surface), radius = 900f))
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Secondary.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
                    .padding(top = 36.dp, bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Secondary, Tertiary))
                            )
                            .border(3.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "AC",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Alex Chen",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        s.level,
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Secondary.copy(alpha = 0.15f))
                            .border(1.dp, Secondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Bolt,
                                null,
                                tint = Tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "PRO TIER",
                                color = Tertiary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Box(Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Tertiary))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                ProfileSection(title = s.personalInfo) {
                    InfoRow(Icons.Outlined.Person, s.fullName, "Alex \"The Spinner\" Chen")
                    InfoRow(Icons.Outlined.Email, s.email, "alex.chen@spinnet.com")
                    InfoRow(Icons.Outlined.Groups, s.club, "SP Table Tennis Club")
                    InfoRow(Icons.Outlined.Flag, s.country, "Portugal 🇵🇹")
                    InfoRow(Icons.Outlined.CalendarToday, s.memberSince, "Janeiro 2024")
                }

                Spacer(Modifier.height(16.dp))

                ProfileSection(title = s.equipment) {
                    InfoRow(Icons.Outlined.FitnessCenter, s.racket, "Butterfly Viscaria")
                    InfoRow(Icons.Outlined.Circle, s.forehandRubber, "Tenergy 05 (2.1mm)")
                    InfoRow(Icons.Outlined.Circle, s.backhandRubber, "Dignics 09C (2.1mm)")
                }

                Spacer(Modifier.height(16.dp))


                ProfileSection(title = s.appSettings) {
                    // Language toggle PT / EN
                    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Language,
                                    null,
                                    tint = Secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    s.language,
                                    color = OnSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            // PT ↔ EN pill toggle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SurfaceContainerHighest)
                                    .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
                                    .padding(3.dp)
                            ) {
                                Row {
                                    listOf("PT" to true, "EN" to false).forEach { (lang, isPT) ->
                                        val active = isPortuguese == isPT
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(17.dp))
                                                .background(if (active) Secondary else Color.Transparent)
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }
                                                ) { isPortuguese = isPT }
                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                lang,
                                                color = if (active) Color.White else OnSurfaceVariant,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    ToggleRow(
                        icon = Icons.Outlined.Notifications,
                        label = s.notifications,
                        checked = notificationsOn,
                        onToggle = { notificationsOn = it }
                    )

                    Spacer(Modifier.height(8.dp))

                    ToggleRow(
                        icon = Icons.Outlined.Vibration,
                        label = s.hapticFeedback,
                        checked = hapticOn,
                        onToggle = { hapticOn = it }
                    )
                }

                Spacer(Modifier.height(16.dp))

                ProfileSection(title = s.account) {
                    ClickableRow(icon = Icons.Outlined.Edit, label = s.editProfile, onClick = {})
                    ClickableRow(icon = Icons.Outlined.Lock, label = s.changePassword, onClick = {})
                    ClickableRow(
                        icon = Icons.Outlined.Security,
                        label = s.privacySettings,
                        onClick = {})
                    ClickableRow(
                        icon = Icons.Outlined.HelpOutline,
                        label = s.helpSupport,
                        onClick = {},
                        showDivider = false
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary.copy(alpha = 0.12f),
                        contentColor = Secondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Secondary.copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Outlined.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        s.logout,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    s.appVersion,
                    modifier = Modifier.fillMaxWidth(),
                    color = OnSurfaceVariant.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        title.uppercase(),
        color = OnSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
    Spacer(Modifier.height(8.dp))
    Column(modifier = Modifier.fillMaxWidth()) {
        content()
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, null, tint = Secondary, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp
                )
                Text(
                    value,
                    color = OnSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, null, tint = Secondary, modifier = Modifier.size(20.dp))
                Text(label, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Secondary,
                    uncheckedThumbColor = OnSurfaceVariant,
                    uncheckedTrackColor = SurfaceContainerHighest
                )
            )
        }
    }
}

@Composable
private fun ClickableRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        innerPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(icon, null, tint = Secondary, modifier = Modifier.size(20.dp))
                Text(label, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    if (showDivider) Spacer(Modifier.height(6.dp))
}

@Composable
private fun LogoutConfirmDialog(
    title: String, message: String, cancel: String, confirm: String,
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF1E2024), Color(0xFF0D1C2D)))
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Logout,
                    null,
                    tint = Secondary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    title,
                    color = OnSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    message,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) { Text(cancel, fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary,
                            contentColor = Color.White
                        )
                    ) { Text(confirm, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111317)
@Composable
fun PlayerProfilePreview() {
    SpinNetTheme {
        PlayerProfileScreen(
            currentDestination = SpinNetDestination.PlayerProfile,
            onNavigate = {},
            onLogout = {}
        )
    }
}

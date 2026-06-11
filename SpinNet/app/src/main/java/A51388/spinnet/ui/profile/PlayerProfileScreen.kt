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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*
import A51388.spinnet.data.model.UserProfile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

private data class ProfileStrings(
    val playerProfile: String,
    val personalInfo: String,
    val fullName: String,
    val username: String,
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
    val appVersion: String
)

private val stringsEN = ProfileStrings(
    playerProfile = "Player Profile",
    personalInfo = "Personal Information",
    fullName = "Full Name",
    username = "Username",
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
    appVersion = "SpinNet v1.0.0"
)

private val stringsPT = ProfileStrings(
    playerProfile = "Perfil do Jogador",
    personalInfo = "Informações Pessoais",
    fullName = "Nome Completo",
    username = "Username",
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
    appVersion = "SpinNet v1.0.0"
)

private val levelOptions = listOf("Iniciante", "Intermédio", "Avançado", "Competidor", "Elite")
private val styleOptions = listOf("Shakehand", "Penhold", "Seemiller", "Outro")

@Composable
fun PlayerProfileScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    onLogout: () -> Unit,
    viewModel: PlayerProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val uploadingPhoto by viewModel.uploadingPhoto.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.uploadProfilePhoto(it) } }

    var isPortuguese by remember { mutableStateOf(false) }
    var notificationsOn by remember { mutableStateOf(true) }
    var hapticOn by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val s = if (isPortuguese) stringsPT else stringsEN
    val scrollState = rememberScrollState()

    LaunchedEffect(saveState) {
        saveState?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSaveState()
        }
    }

    if (showEditDialog && profile != null) {
        EditProfileDialog(
            profile = profile!!,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateProfile(updated)
                showEditDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            email = profile?.email ?: FirebaseAuth.getInstance().currentUser?.email ?: "",
            onDismiss = { showPasswordDialog = false }
        )
    }

    if (showPrivacyDialog) {
        InfoDialog(
            title = s.privacySettings,
            icon = Icons.Outlined.Security,
            onDismiss = { showPrivacyDialog = false }
        ) {
            Text(
                "Os teus dados são armazenados de forma segura no Firebase e não são partilhados com terceiros. As rotinas marcadas como privadas são visíveis apenas para ti.",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    if (showHelpDialog) {
        InfoDialog(
            title = s.helpSupport,
            icon = Icons.Outlined.HelpOutline,
            onDismiss = { showHelpDialog = false }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HelpItem(Icons.Outlined.Email, "Suporte", "spinnet.support@isel.pt")
                HelpItem(Icons.Outlined.Info, "Versão", "SpinNet v1.0.0")
                HelpItem(Icons.Outlined.School, "Projeto", "DAM · ISEL 2024/25")
            }
        }
    }

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
            })
    }

    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { SpinNetBottomBar(currentDestination, onNavigate) }) { padding ->
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
                    .padding(top = 36.dp, bottom = 28.dp), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Secondary, Tertiary)))
                            .border(3.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { photoPicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profile?.profileImage.isNullOrBlank()) {
                            AsyncImage(
                                model = profile!!.profileImage,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                (profile?.fullName?.ifBlank { profile?.username }
                                    ?.take(2)?.uppercase()) ?: "?",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                        if (uploadingPhoto) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = "Alterar foto",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        profile?.fullName?.ifBlank { profile?.username } ?: "—",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (!profile?.username.isNullOrBlank()) {
                        Text(
                            "@${profile!!.username}",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    val levelDisplay = buildString {
                        profile?.level?.ifBlank { null }?.let { append(it) }
                        profile?.let { p ->
                            val style = p.level.substringAfterLast("·", "").trim()
                            if (style.isEmpty()) {
                                // nada extra
                            }
                        }
                    }
                    if (levelDisplay.isNotBlank()) {
                        Text(
                            levelDisplay,
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                ProfileSection(title = s.personalInfo) {
                    InfoRow(Icons.Outlined.Person, s.username, profile?.username?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.Email, s.email, profile?.email?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.Groups, s.club, profile?.club?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.Flag, s.country, profile?.country?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.CalendarToday, s.memberSince, profile?.memberSince?.ifBlank { "—" } ?: "—")
                }

                Spacer(Modifier.height(16.dp))

                ProfileSection(title = s.equipment) {
                    InfoRow(Icons.Outlined.FitnessCenter, s.racket, profile?.racket?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.Circle, s.forehandRubber, profile?.forehandRubber?.ifBlank { "—" } ?: "—")
                    InfoRow(Icons.Outlined.Circle, s.backhandRubber, profile?.backhandRubber?.ifBlank { "—" } ?: "—")
                }

                Spacer(Modifier.height(16.dp))

                ProfileSection(title = s.appSettings) {
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
                                Icon(Icons.Outlined.Language, null, tint = Secondary, modifier = Modifier.size(20.dp))
                                Text(s.language, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
                            }
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
                                                    interactionSource = remember { MutableInteractionSource() }) {
                                                    isPortuguese = isPT
                                                }
                                                .padding(horizontal = 14.dp, vertical = 6.dp)) {
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
                    ToggleRow(icon = Icons.Outlined.Notifications, label = s.notifications, checked = notificationsOn, onToggle = { notificationsOn = it })
                    Spacer(Modifier.height(8.dp))
                    ToggleRow(icon = Icons.Outlined.Vibration, label = s.hapticFeedback, checked = hapticOn, onToggle = { hapticOn = it })
                }

                Spacer(Modifier.height(16.dp))

                ProfileSection(title = s.account) {
                    ClickableRow(icon = Icons.Outlined.Edit, label = s.editProfile, onClick = { showEditDialog = true })
                    ClickableRow(icon = Icons.Outlined.Lock, label = s.changePassword, onClick = { showPasswordDialog = true })
                    ClickableRow(icon = Icons.Outlined.Security, label = s.privacySettings, onClick = { showPrivacyDialog = true })
                    ClickableRow(icon = Icons.Outlined.HelpOutline, label = s.helpSupport, onClick = { showHelpDialog = true }, showDivider = false)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary.copy(alpha = 0.12f), contentColor = Secondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Secondary.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Outlined.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(s.logout, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
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
    Column(modifier = Modifier.fillMaxWidth()) { content() }
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
                Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                Text(value, color = OnSurface, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun ToggleRow(icon: ImageVector, label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, null, tint = Secondary, modifier = Modifier.size(20.dp))
                Text(label, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(
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
private fun ClickableRow(icon: ImageVector, label: String, onClick: () -> Unit, showDivider: Boolean = true) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
        innerPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, null, tint = Secondary, modifier = Modifier.size(20.dp))
                Text(label, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
    if (showDivider) Spacer(Modifier.height(6.dp))
}

@Composable
private fun HelpItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, null, tint = Secondary, modifier = Modifier.size(18.dp))
        Column {
            Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            Text(value, color = OnSurface, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InfoDialog(
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F2E),
        shape = RoundedCornerShape(20.dp),
        icon = { Icon(icon, null, tint = Secondary, modifier = Modifier.size(28.dp)) },
        title = { Text(title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() } },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar", color = Secondary, fontWeight = FontWeight.SemiBold) }
        }
    )
}

@Composable
private fun ChangePasswordDialog(email: String, onDismiss: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val isEmailProvider = user?.providerData?.any { it.providerId == "password" } == true

    if (!isEmailProvider) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF1A1F2E),
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Outlined.Lock, null, tint = Secondary, modifier = Modifier.size(28.dp)) },
            title = {
                Text("Alterar Password", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "A tua conta está associada a um provedor externo (Google/Facebook). A password deve ser gerida através desse provedor.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Secondary, fontWeight = FontWeight.SemiBold)
                }
            }
        )
        return
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F2E),
        shape = RoundedCornerShape(20.dp),
        icon = { Icon(Icons.Outlined.Lock, null, tint = Secondary, modifier = Modifier.size(28.dp)) },
        title = {
            Text("Alterar Password", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (success) {
                    Text(
                        "Password alterada com sucesso!",
                        color = NeonGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Secondary
                    )
                    val fieldShape = RoundedCornerShape(12.dp)

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Password Atual", color = OnSurfaceVariant) },
                        singleLine = true,
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    if (currentPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        },
                        colors = textFieldColors,
                        shape = fieldShape,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nova Password", color = OnSurfaceVariant) },
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    if (newPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        },
                        colors = textFieldColors,
                        shape = fieldShape,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Nova Password", color = OnSurfaceVariant) },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        },
                        colors = textFieldColors,
                        shape = fieldShape,
                        modifier = Modifier.fillMaxWidth()
                    )

                    error?.let {
                        Text(it, color = Color(0xFFCF6679), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        confirmButton = {
            if (success) {
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Secondary, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {
                        if (newPassword.length < 6) {
                            error = "A nova password deve ter pelo menos 6 caracteres."
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            error = "As passwords não coincidem."
                            return@Button
                        }
                        loading = true
                        error = null
                        val userEmail = user?.email ?: email
                        val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)
                        user?.reauthenticate(credential)
                            ?.addOnSuccessListener {
                                user.updatePassword(newPassword)
                                    .addOnSuccessListener {
                                        loading = false
                                        success = true
                                    }
                                    .addOnFailureListener { e ->
                                        loading = false
                                        error = "Erro: ${e.localizedMessage}"
                                    }
                            }
                            ?.addOnFailureListener { e ->
                                loading = false
                                error = "Erro de autenticação: password atual incorreta."
                            }
                    },
                    enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Alterar", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = {
            if (!success) {
                TextButton(onClick = onDismiss, enabled = !loading) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        }
    )
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
                .background(Brush.linearGradient(listOf(Color(0xFF1E2024), Color(0xFF0D1C2D))))
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Logout, null, tint = Secondary, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(12.dp))
                Text(title, color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(message, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) { Text(cancel, fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = onConfirm, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = Color.White)
                    ) { Text(confirm, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun OptionPickerRow(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = OnSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Outlined.ArrowDropDown, null, tint = OnSurfaceVariant)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Secondary,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFF1E2433)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = if (option == selected) Secondary else OnSurface, style = MaterialTheme.typography.bodyMedium) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var username by remember { mutableStateOf(profile.username) }
    var fullName by remember { mutableStateOf(profile.fullName) }
    var club by remember { mutableStateOf(profile.club) }
    var country by remember { mutableStateOf(profile.country) }
    var memberSince by remember { mutableStateOf(profile.memberSince) }
    var racket by remember { mutableStateOf(profile.racket) }
    var forehandRubber by remember { mutableStateOf(profile.forehandRubber) }
    var backhandRubber by remember { mutableStateOf(profile.backhandRubber) }
    var selectedLevel by remember { mutableStateOf(profile.level.substringBefore("·").trim().ifBlank { levelOptions[0] }) }
    var selectedStyle by remember { mutableStateOf(profile.level.substringAfter("·", "").trim().ifBlank { styleOptions[0] }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Editar Perfil", color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    cursorColor = Secondary
                )
                val fieldShape = RoundedCornerShape(12.dp)

                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nome completo", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = club, onValueChange = { club = it }, label = { Text("Clube", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("País", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = memberSince, onValueChange = { memberSince = it }, label = { Text("Membro desde", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = racket, onValueChange = { racket = it }, label = { Text("Raquete", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = forehandRubber, onValueChange = { forehandRubber = it }, label = { Text("Borracha DT", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = backhandRubber, onValueChange = { backhandRubber = it }, label = { Text("Borracha RE", color = OnSurfaceVariant) }, singleLine = true, colors = textFieldColors, shape = fieldShape, modifier = Modifier.fillMaxWidth())

                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))

                OptionPickerRow(label = "Nível", selected = selectedLevel, options = levelOptions, onSelect = { selectedLevel = it })
                OptionPickerRow(label = "Estilo de jogo", selected = selectedStyle, options = styleOptions, onSelect = { selectedStyle = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(profile.copy(
                        username = username,
                        fullName = fullName,
                        club = club,
                        country = country,
                        memberSince = memberSince,
                        racket = racket,
                        forehandRubber = forehandRubber,
                        backhandRubber = backhandRubber,
                        level = "$selectedLevel · $selectedStyle"
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = OnSurfaceVariant) }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF111317)
@Composable
fun PlayerProfilePreview() {
    SpinNetTheme {
        PlayerProfileScreen(
            currentDestination = SpinNetDestination.PlayerProfile,
            onNavigate = {},
            onLogout = {})
    }
}
package A51388.spinnet.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.ui.auth.AuthState
import A51388.spinnet.ui.auth.AuthViewModel
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.GlassTextField
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordMismatch by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF1B365D), Surface), radius = 900f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            Text(
                "SPINNET",
                color = NeonGreen,
                style = MaterialTheme.typography.displayLarge,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Cria a tua conta",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Registar",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Começa o teu percurso",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(24.dp))

                    GlassTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Nome de utilizador",
                        leadingIcon = Icons.Outlined.Person
                    )

                    Spacer(Modifier.height(12.dp))

                    GlassTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = state is AuthState.Error
                    )

                    Spacer(Modifier.height(12.dp))

                    GlassTextField(
                        value = password,
                        onValueChange = { password = it; passwordMismatch = false },
                        label = "Password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordMismatch,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        })

                    Spacer(Modifier.height(12.dp))

                    GlassTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; passwordMismatch = false },
                        label = "Confirmar Password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordMismatch
                    )

                    if (passwordMismatch) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "As passwords não coincidem",
                            color = Error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (state is AuthState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            (state as AuthState.Error).message,
                            color = Error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    NeonButton(
                        onClick = {
                            if (password != confirmPassword) passwordMismatch = true
                            else viewModel.register(email, password, username)
                        },
                        enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && state !is AuthState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("CRIAR CONTA", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    "Já tens conta? ",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Iniciar Sessão",
                    color = NeonGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() })
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
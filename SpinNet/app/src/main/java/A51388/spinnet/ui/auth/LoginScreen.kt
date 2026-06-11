package A51388.spinnet.ui.auth

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import A51388.spinnet.MainActivity
import A51388.spinnet.R
import A51388.spinnet.ui.auth.AuthState
import A51388.spinnet.ui.auth.AuthViewModel
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.GlassTextField
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    //activity para o callbackManager
    val activity = context as? MainActivity
    val callbackManager = activity?.callbackManager

    // Regista callback do Faceboo, so uma vez
    DisposableEffect(callbackManager) {
        if (callbackManager != null) {
            LoginManager.getInstance().registerCallback(
                callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        viewModel.signInWithFacebook(result.accessToken.token)
                    }

                    override fun onCancel() {}
                    override fun onError(error: FacebookException) {
                        Log.e("FB", error.message ?: "Facebook error")
                    }
                })
        }
        onDispose {
            if (callbackManager != null) {
                LoginManager.getInstance().unregisterCallback(callbackManager)
            }
        }
    }

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1B365D), Surface), radius = 900f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            Text(
                text = "SPINNET",
                color = NeonGreen,
                style = MaterialTheme.typography.displayLarge,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Table Tennis Performance",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Iniciar Sessão",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Bem-vindo de volta",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(24.dp))

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
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = state is AuthState.Error,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Outlined.VisibilityOff
                                    else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }
                        })

                    if (state is AuthState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = (state as AuthState.Error).message,
                            color = Error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    NeonButton(
                        onClick = { viewModel.login(email, password) },
                        enabled = email.isNotBlank() && password.isNotBlank() && state !is AuthState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("ENTRAR", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
                        Text(
                            "  ou  ",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
                    }

                    Spacer(Modifier.height(16.dp))

                    //Google
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(context.getString(R.string.default_web_client_id))
                                        .build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption).build()
                                    val result = credentialManager.getCredential(
                                        request = request, context = context as Activity
                                    )
                                    val googleIdToken =
                                        GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                                    viewModel.signInWithGoogle(googleIdToken)
                                } catch (e: GetCredentialException) {
                                    Log.e("Google", e.message ?: "Credential error")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = OnSurface
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Continuar com Google", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Face
                    OutlinedButton(
                        onClick = {
                            val activity = context as androidx.activity.ComponentActivity
                            if (callbackManager != null) {
                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity, callbackManager, listOf("email", "public_profile")
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1877F2)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color(0xFF1877F2).copy(alpha = 0.4f)
                        )
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF1877F2)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Continuar com Facebook", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    "Não tens conta? ",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Registar",
                    color = NeonGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() })
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
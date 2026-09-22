package com.example.mealmate.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealmate.R
import com.example.mealmate.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val auth = FirebaseAuth.getInstance()
    var message by remember { mutableStateOf("") }
    var isSuccessMessage by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ForestGreen
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.mealmate_logo),
                                contentDescription = "MealMate Logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = TextStyle(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp
                        )
                    )
                    Text(
                        text = "Your Daily Meal Companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    color = White,
                    shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 32.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.welcome_back),
                            style = MaterialTheme.typography.headlineSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email_address)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = LightBeige,
                                focusedLabelColor = ForestGreen,
                                unfocusedLabelColor = WarmGray,
                                focusedTextColor = NearBlack,
                                unfocusedTextColor = NearBlack
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = LightBeige,
                                focusedLabelColor = ForestGreen,
                                unfocusedLabelColor = WarmGray,
                                focusedTextColor = NearBlack,
                                unfocusedTextColor = NearBlack
                            ),
                            visualTransformation = if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            trailingIcon = {
                                val description = if (showPassword) {
                                    stringResource(R.string.hide_password)
                                } else {
                                    stringResource(R.string.show_password)
                                }
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) {
                                            Icons.Default.Visibility
                                        } else {
                                            Icons.Default.VisibilityOff
                                        },
                                        contentDescription = description,
                                        tint = MediumGray
                                    )
                                }
                            }
                        )

                        TextButton(
                            onClick = { 
                                if (email.isNotEmpty()) {
                                    auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener { 
                                            isSuccessMessage = it.isSuccessful
                                            message = if (it.isSuccessful) "Reset email sent to $email"
                                            else it.exception?.message ?: "Failed to send reset email"
                                        }
                                } else {
                                    isSuccessMessage = false
                                    message = "Please enter your email address first"
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.forgot_password), color = ForestGreen)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            onLoginSuccess()
                                        } else {
                                            isSuccessMessage = false
                                            message = it.exception?.message ?: "Login Failed"
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Text(stringResource(R.string.login), color = White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { /* Handle Google Sign-In */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                            border = BorderStroke(1.dp, ForestGreen)
                        ) {
                            Text(stringResource(R.string.continue_with_google), color = NearBlack)
                        }

                        if (message.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = message, color = if (isSuccessMessage) ForestGreen else Color.Red)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.dont_have_account),
                                color = WarmGray
                            )
                            TextButton(onClick = onRegisterClick) {
                                Text(stringResource(R.string.register), color = ForestGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .background(ForestGreen),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.mealmate_logo),
                                contentDescription = "MealMate Logo",
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = TextStyle(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp
                        )
                    )
                    Text(
                        text = "Your Daily Meal Companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f),
                    color = White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.welcome_back),
                            style = MaterialTheme.typography.headlineSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email_address)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = LightBeige,
                                focusedLabelColor = ForestGreen,
                                unfocusedLabelColor = WarmGray,
                                focusedTextColor = NearBlack,
                                unfocusedTextColor = NearBlack
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = LightBeige,
                                focusedLabelColor = ForestGreen,
                                unfocusedLabelColor = WarmGray,
                                focusedTextColor = NearBlack,
                                unfocusedTextColor = NearBlack
                            ),
                            visualTransformation = if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            trailingIcon = {
                                val description = if (showPassword) {
                                    stringResource(R.string.hide_password)
                                } else {
                                    stringResource(R.string.show_password)
                                }
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) {
                                            Icons.Default.Visibility
                                        } else {
                                            Icons.Default.VisibilityOff
                                        },
                                        contentDescription = description,
                                        tint = MediumGray
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { 
                                if (email.isNotEmpty()) {
                                    auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener { 
                                            isSuccessMessage = it.isSuccessful
                                            message = if (it.isSuccessful) "Reset email sent to $email"
                                            else it.exception?.message ?: "Failed to send reset email"
                                        }
                                } else {
                                    isSuccessMessage = false
                                    message = "Please enter your email address first"
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.forgot_password), color = ForestGreen)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            onLoginSuccess()
                                        } else {
                                            isSuccessMessage = false
                                            message = it.exception?.message ?: "Login Failed"
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Text(stringResource(R.string.login), color = White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { /* Handle Google Sign-In */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                            border = BorderStroke(1.dp, ForestGreen)
                        ) {
                            Text(stringResource(R.string.continue_with_google), color = NearBlack)
                        }

                        if (message.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = message, color = if (isSuccessMessage) ForestGreen else Color.Red)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.dont_have_account),
                                color = WarmGray
                            )
                            TextButton(onClick = onRegisterClick) {
                                Text(stringResource(R.string.register), color = ForestGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

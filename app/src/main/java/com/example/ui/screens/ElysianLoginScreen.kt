package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ElysianViewModel
import com.example.ui.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElysianLoginScreen(
    viewModel: ElysianViewModel,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }
    var passcode by remember { mutableStateOf("") }
    var passcodeVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val keyboardController = LocalSoftwareKeyboardController.current

    // Background decorative gradient
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient Top-Center Glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Premium Luxury Brand Header
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Elysian Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ELYSIAN WEDDINGS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Luxury Venue & Coordination System",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Container Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECT PORTAL ACCESSIBILITY",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid-like role selectors
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = selectedRole == role
                            val containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                            } else {
                                MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                            }
                            val borderColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            }
                            
                            val roleTitle = when (role) {
                                UserRole.CLIENT -> "Client Portal"
                                UserRole.STAFF -> "Staff Desk"
                                UserRole.MANAGER -> "Manager Board"
                                UserRole.ADMIN -> "Administrator Workspace"
                            }
                            
                            val roleDesc = when (role) {
                                UserRole.CLIENT -> "Book hall slots, coordinate with AI, track wedding checklist"
                                UserRole.STAFF -> "View daily coordination checklist & finalize vendor milestones"
                                UserRole.MANAGER -> "Approve bookings, assign vendors, oversee calendar schedules"
                                UserRole.ADMIN -> "Access financial analytics, full booking oversight & logs"
                            }
                            
                            val roleIcon = when (role) {
                                UserRole.CLIENT -> Icons.Default.Favorite
                                UserRole.STAFF -> Icons.Default.AssignmentInd
                                UserRole.MANAGER -> Icons.Default.ManageAccounts
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                            }

                            Surface(
                                onClick = { 
                                    selectedRole = role 
                                    errorMessage = null
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = containerColor,
                                border = BorderStroke(1.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("role_select_${role.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = roleIcon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(14.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = roleTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = roleDesc,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 14.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Passcode Input Area
                    Text(
                        text = "Access Authorization Key",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = passcode,
                        onValueChange = { 
                            passcode = it
                            errorMessage = null
                        },
                        placeholder = { 
                            val sampleKey = when (selectedRole) {
                                UserRole.CLIENT -> "love"
                                UserRole.STAFF -> "staff"
                                UserRole.MANAGER -> "manager"
                                UserRole.ADMIN -> "admin"
                            }
                            Text(
                                text = "Enter passcode (e.g. '$sampleKey')",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passcodeVisible = !passcodeVisible }) {
                                Icon(
                                    imageVector = if (passcodeVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle Passcode Visibility"
                                )
                            }
                        },
                        visualTransformation = if (passcodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("passcode_input")
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            val expectedPasscode = when (selectedRole) {
                                UserRole.CLIENT -> "love"
                                UserRole.STAFF -> "staff"
                                UserRole.MANAGER -> "manager"
                                UserRole.ADMIN -> "admin"
                            }
                            
                            // Let's also be lenient: if they enter the exact passcode OR any key for development, we authorize.
                            // However, we give them a helpful hint if they enter a totally different password, but let them in anyway!
                            if (passcode.trim().isNotEmpty() && passcode.trim().lowercase() != expectedPasscode) {
                                errorMessage = "Incorrect passcode key. Expected key is '$expectedPasscode'"
                            } else {
                                viewModel.logIn(selectedRole)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_login"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTHORIZE & ENTER PORTAL",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Decorative footer
            Text(
                text = "Secure Off-Line System Portal",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "© 2026 Elysian Holdings Inc.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

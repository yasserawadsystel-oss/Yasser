package com.example.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystelConstants
import com.example.data.model.UserAccountEntity
import com.example.ui.components.SystelLogo
import com.example.ui.components.TeamLeaderApprovalDialog
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.AuthResult
import com.example.ui.viewmodel.ClearanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAndRegisterScreen(
    viewModel: ClearanceViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Login state
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    var pendingAccountState by remember { mutableStateOf<UserAccountEntity?>(null) }
    var directActivationCode by remember { mutableStateOf("") }
    var isCheckingLogin by remember { mutableStateOf(false) }

    // Register state
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var isRegConfirmVisible by remember { mutableStateOf(false) }
    var regDepartment by remember { mutableStateOf(SystelConstants.DEPARTMENTS.first()) }
    var isDeptExpanded by remember { mutableStateOf(false) }
    var regTeamName by remember { mutableStateOf("") }
    var regIsTeamLeader by remember { mutableStateOf(false) }
    var regErrorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmittingReg by remember { mutableStateOf(false) }

    // Success registration dialog
    var registeredAccountResult by remember { mutableStateOf<UserAccountEntity?>(null) }
    var showLeaderApprovalDialog by remember { mutableStateOf(false) }
    var showResetCleanDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F2654)
                    )
                )
            )
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = if (isLandscape) 12.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .testTag("auth_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isLandscape) 16.dp else 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Corporate Telecom Branding
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SystelLogo(size = 40.dp, isDarkBackground = false, showArabicSubtitle = false)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "نظام تصفية عهدة ومصاريف المواقع",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystelNavyDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "شركة سيستل للاتصالات • تسجيل الدخول والاعتماد",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Tab Selector: Login vs Register
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = SystelNavyPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                loginErrorMessage = null
                            },
                            text = {
                                Text(
                                    "تسجيل الدخول",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_login")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                regErrorMessage = null
                            },
                            text = {
                                Text(
                                    "إنشاء حساب جديد",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_register")
                        )
                    }

                    // TAB 0: LOGIN
                    if (selectedTab == 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (loginErrorMessage != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SystelAccentRed)
                                        Text(
                                            text = loginErrorMessage ?: "",
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                    }
                                }
                            }

                            // Pending Activation Card
                            if (pendingAccountState != null) {
                                val acc = pendingAccountState!!
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCD34D)),
                                    modifier = Modifier.testTag("pending_activation_banner")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFD97706))
                                            Text(
                                                text = "طلبك قيد المراجعة بانتظار موافقة التيم ليدر",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = Color(0xFF92400E)
                                            )
                                        }

                                        Text(
                                            text = "مرحباً ${acc.fullName}، تم استلام طلبك ولكن لم يتم تفعيل الحساب بعد من التيم ليدر (المدير). لن تتمكن من الدخول إلا بعد الموافقة.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 16.sp
                                        )

                                        // Resend Email Button
                                        Button(
                                            onClick = {
                                                viewModel.sendAccountActivationEmail(context, acc)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("مراسلة التيم ليدر الآن بالبريد للموافقة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        HorizontalDivider(color = Color(0xFFFDE68A))

                                        // Direct Activation Code input
                                        Text(
                                            text = "إذا استلمت كود التفعيل من التيم ليدر أدخله هنا للتفعيل الفوري:",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF92400E)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = directActivationCode,
                                                onValueChange = { directActivationCode = it },
                                                placeholder = { Text("كود التفعيل (5 أرقام)", fontSize = 12.sp) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("direct_activation_code_input"),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )

                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        val activated = viewModel.activateUserByActivationCode(directActivationCode)
                                                        if (activated) {
                                                            Toast.makeText(context, "تم تفعيل الحساب بنجاح! جاري تسجيل الدخول...", Toast.LENGTH_SHORT).show()
                                                            pendingAccountState = null
                                                            // Re-run login
                                                            val res = viewModel.loginWithCredentials(acc.email, acc.password)
                                                            if (res is AuthResult.Success) {
                                                                onLoginSuccess()
                                                            }
                                                        } else {
                                                            Toast.makeText(context, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen),
                                                modifier = Modifier.testTag("apply_activation_code_button")
                                            ) {
                                                Text("تفعيل ودخول", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Email or Phone field
                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = {
                                    loginIdentifier = it
                                    loginErrorMessage = null
                                },
                                label = { Text("البريد الإلكتروني أو رقم الهاتف") },
                                placeholder = { Text("مثال: 01012345678 أو email@systel.com.eg") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = SystelNavyPrimary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input"),
                                singleLine = true
                            )

                            // Password field
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    loginErrorMessage = null
                                },
                                label = { Text("كلمة المرور") },
                                placeholder = { Text("أدخل كلمة المرور") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = SystelNavyPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "إظهار كلمة المرور"
                                        )
                                    }
                                },
                                visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                singleLine = true
                            )

                            // Submit Login Button
                            Button(
                                onClick = {
                                    if (loginIdentifier.isBlank() || loginPassword.isBlank()) {
                                        loginErrorMessage = "يرجى إدخال اسم المستخدم وكلمة المرور"
                                        return@Button
                                    }
                                    isCheckingLogin = true
                                    coroutineScope.launch {
                                        val res = viewModel.loginWithCredentials(loginIdentifier, loginPassword)
                                        isCheckingLogin = false
                                        when (res) {
                                            is AuthResult.Success -> {
                                                Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
                                                onLoginSuccess()
                                            }
                                            is AuthResult.PendingApproval -> {
                                                pendingAccountState = res.account
                                                loginErrorMessage = res.message
                                            }
                                            is AuthResult.Rejected -> {
                                                loginErrorMessage = res.message
                                                pendingAccountState = null
                                            }
                                            is AuthResult.Error -> {
                                                loginErrorMessage = res.message
                                                pendingAccountState = null
                                            }
                                        }
                                    }
                                },
                                enabled = !isCheckingLogin,
                                colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_login_button")
                            ) {
                                Text(
                                    if (isCheckingLogin) "جاري التحقق..." else "تسجيل الدخول",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Google Sign-In Button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val user = com.example.util.GoogleAuthHelper.signInWithGoogle(context)
                                        if (user != null) {
                                            val email = user.email ?: ""
                                            if (email.equals(SystelConstants.MANAGER_EMAIL, ignoreCase = true)) {
                                                viewModel.loginAsManager("م. ياسر عواد")
                                                Toast.makeText(context, "مرحباً م. ياسر عواد، تم تسجيل الدخول كـ تيم ليدر بنجاح", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess()
                                            } else {
                                                val res = viewModel.loginWithCredentials(email, "")
                                                if (res is AuthResult.Success) {
                                                    onLoginSuccess()
                                                } else {
                                                    loginIdentifier = email
                                                    Toast.makeText(context, "تم تسجيل الدخول بحساب: $email", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "تعذر تسجيل الدخول بحساب Google أو تم الإلغاء", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("google_sign_in_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Sign in with Google",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تسجيل الدخول بحساب Google", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))

                            // Secondary Quick Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { showLeaderApprovalDialog = true },
                                    modifier = Modifier.weight(1f).testTag("direct_leader_access_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("دخول التيم ليدر", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { showResetCleanDialog = true },
                                    modifier = Modifier.weight(1f).testTag("reset_clean_app_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SystelAccentRed),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SystelAccentRed)
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تصفير التطبيق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // TAB 1: REGISTER
                    if (selectedTab == 1) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (regErrorMessage != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SystelAccentRed)
                                        Text(
                                            text = regErrorMessage ?: "",
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                    }
                                }
                            }

                            // Role Selection Chips
                            Text("نوع الحساب المطلوب:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = SystelNavyDark)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (!regIsTeamLeader) SystelNavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { regIsTeamLeader = false }
                                        .testTag("select_engineer_role_chip")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Engineering,
                                            contentDescription = null,
                                            tint = if (!regIsTeamLeader) Color.White else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "مهندس موقع / تيم",
                                            color = if (!regIsTeamLeader) Color.White else Color(0xFF475569),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (regIsTeamLeader) SystelNavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { regIsTeamLeader = true }
                                        .testTag("select_leader_role_chip")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = if (regIsTeamLeader) Color.White else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "تيم ليدر (مدير)",
                                            color = if (regIsTeamLeader) Color.White else Color(0xFF475569),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Full Name
                            OutlinedTextField(
                                value = regFullName,
                                onValueChange = {
                                    regFullName = it
                                    regErrorMessage = null
                                },
                                label = { Text("الاسم بالكامل (إلزامي)") },
                                placeholder = { Text("مثال: م. أحمد الشريف") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input"),
                                singleLine = true
                            )

                            // Email
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    regErrorMessage = null
                                },
                                label = { Text("البريد الإلكتروني (إلزامي)") },
                                placeholder = { Text("name@systel.com.eg") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input"),
                                singleLine = true
                            )

                            // Phone
                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = {
                                    regPhone = it
                                    regErrorMessage = null
                                },
                                label = { Text("رقم الهاتف (إلزامي)") },
                                placeholder = { Text("01012345678") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_phone_input"),
                                singleLine = true
                            )

                            // Department dropdown
                            ExposedDropdownMenuBox(
                                expanded = isDeptExpanded,
                                onExpandedChange = { isDeptExpanded = !isDeptExpanded }
                            ) {
                                OutlinedTextField(
                                    value = regDepartment,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("القسم (مشروع الاتصالات)") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDeptExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .testTag("reg_dept_dropdown")
                                )
                                ExposedDropdownMenu(
                                    expanded = isDeptExpanded,
                                    onDismissRequest = { isDeptExpanded = false }
                                ) {
                                    SystelConstants.DEPARTMENTS.forEach { dept ->
                                        DropdownMenuItem(
                                            text = { Text(dept) },
                                            onClick = {
                                                regDepartment = dept
                                                isDeptExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Team Name
                            OutlinedTextField(
                                value = regTeamName,
                                onValueChange = { regTeamName = it },
                                label = { Text(if (regIsTeamLeader) "اسم الإدارة" else "اسم الفريق / المحطة") },
                                placeholder = { Text(if (regIsTeamLeader) "إدارة العمليات" else "مثال: تيم صيانة الشرقية") },
                                leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_team_name_input"),
                                singleLine = true
                            )

                            // Password
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    regErrorMessage = null
                                },
                                label = { Text("كلمة المرور (إلزامية)") },
                                placeholder = { Text("4 أحرف أو أرقام على الأقل") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isRegPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                singleLine = true
                            )

                            // Confirm Password
                            OutlinedTextField(
                                value = regConfirmPassword,
                                onValueChange = {
                                    regConfirmPassword = it
                                    regErrorMessage = null
                                },
                                label = { Text("تأكيد كلمة المرور") },
                                placeholder = { Text("أعد إدخال كلمة المرور") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { isRegConfirmVisible = !isRegConfirmVisible }) {
                                        Icon(
                                            imageVector = if (isRegConfirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (isRegConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_confirm_password_input"),
                                singleLine = true
                            )

                            // Notice Box on Approval Requirement
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "تنبيه: بعد إرسال الطلب، سيتم إرسال طلب تفعيل إلى التيم ليدر (المدير). لن يتم تفعيل الحساب ولا يمكنك الدخول إلا بعد الموافقة.",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF92400E),
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            // Submit Registration Button
                            Button(
                                onClick = {
                                    if (regFullName.trim().length < 3) {
                                        regErrorMessage = "يرجى كتابة الاسم كاملاً (3 أحرف على الأقل)"
                                        return@Button
                                    }
                                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmail.trim()).matches()) {
                                        regErrorMessage = "يرجى كتابة بريد إلكتروني صحيح ومطابق"
                                        return@Button
                                    }
                                    if (regPhone.trim().length < 8) {
                                        regErrorMessage = "يرجى كتابة رقم هاتف صحيح ومكون من 8 إلى 11 رقماً"
                                        return@Button
                                    }
                                    if (regPassword.length < 4) {
                                        regErrorMessage = "كلمة المرور يجب أن لا تقل عن 4 خانات"
                                        return@Button
                                    }
                                    if (regPassword != regConfirmPassword) {
                                        regErrorMessage = "كلمتا المرور غير متطابقتين!"
                                        return@Button
                                    }

                                    isSubmittingReg = true
                                    coroutineScope.launch {
                                        val res = viewModel.registerUserAccount(
                                            fullName = regFullName,
                                            email = regEmail,
                                            phone = regPhone,
                                            password = regPassword,
                                            department = regDepartment,
                                            teamName = regTeamName,
                                            isTeamLeader = regIsTeamLeader
                                        )
                                        isSubmittingReg = false
                                        when (res) {
                                            is AuthResult.PendingApproval -> {
                                                registeredAccountResult = res.account
                                            }
                                            is AuthResult.Error -> {
                                                regErrorMessage = res.message
                                            }
                                            else -> {}
                                        }
                                    }
                                },
                                enabled = !isSubmittingReg,
                                colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_register_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isSubmittingReg) "جاري التسجيل..." else "إرسال طلب التسجيل والتفعيل",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Success Registration Dialog
    if (registeredAccountResult != null) {
        val account = registeredAccountResult!!
        AlertDialog(
            onDismissRequest = {
                registeredAccountResult = null
                selectedTab = 0
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SystelSuccessGreen, modifier = Modifier.size(32.dp))
                    Text("تم تقديم طلب التسجيل بنجاح!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SystelNavyDark)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "عزيزي المهندس / ${account.fullName}، تم إنشاء طلب الحساب بنجاح وهو الآن بانتظار موافقة واعتماد التيم ليدر م. ياسر عواد.",
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "حالة الحساب: قيد المراجعة والاعتماد",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "تم إرسال طلب التفعيل إلى المهندس / ياسر عواد. سيقوم م. ياسر بمراجعة واعتماد الحساب وإرسال كود التفعيل إليك عبر الهاتف أو الإيميل لتفعيل حسابك.",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.sendAccountActivationEmail(context, account)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("send_email_to_leader_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إرسال طلب التفعيل إلى م. ياسر عواد الآن", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        loginIdentifier = account.email
                        loginPassword = account.password
                        registeredAccountResult = null
                        selectedTab = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen)
                ) {
                    Text("الانتقال لتسجيل الدخول")
                }
            }
        )
    }

    // Direct Team Leader Approval Dialog
    if (showLeaderApprovalDialog) {
        TeamLeaderApprovalDialog(
            viewModel = viewModel,
            onDismiss = { showLeaderApprovalDialog = false },
            onApproved = {
                showLeaderApprovalDialog = false
                onLoginSuccess()
            }
        )
    }

    // Reset Clean State Dialog
    if (showResetCleanDialog) {
        AlertDialog(
            onDismissRequest = { showResetCleanDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = SystelAccentRed)
                    Text("تصفير التطبيق وبدء كنسخة جديدة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "سيتم مسح جميع البيانات وحسابات الدخول ليعود التطبيق نظيفاً تماماً بدون أي بيانات سابقة.",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetCleanDialog = false
                        viewModel.resetAllDataToCleanState()
                        loginIdentifier = ""
                        loginPassword = ""
                        loginErrorMessage = null
                        pendingAccountState = null
                        Toast.makeText(context, "تم تفريغ التطبيق بالكامل. التطبيق الآن نظيف وبدون بيانات.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed)
                ) {
                    Text("تأكيد التصفير")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetCleanDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

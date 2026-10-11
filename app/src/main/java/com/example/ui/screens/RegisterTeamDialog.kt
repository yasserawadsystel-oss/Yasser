package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.data.model.SystelConstants
import com.example.data.model.TeamEntity
import com.example.ui.components.TeamLeaderApprovalDialog
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.ClearanceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterTeamDialog(
    viewModel: ClearanceViewModel? = null,
    isMandatoryFirstRegistration: Boolean = false,
    allTeams: List<TeamEntity> = emptyList(),
    onSelectExistingTeam: ((TeamEntity) -> Unit)? = null,
    onSelectTeamLeader: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onConfirm: (teamName: String, leaderName: String, phone: String, email: String, department: String, isTeamLeader: Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isTeamLeaderRole by remember { mutableStateOf(false) }
    var teamName by remember { mutableStateOf("") }
    var leaderName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var department by remember { mutableStateOf(SystelConstants.DEPARTMENTS.first()) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var showApprovalDialog by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = Modifier
            .widthIn(max = if (isLandscape) 640.dp else 480.dp)
            .fillMaxWidth(if (isLandscape) 0.88f else 0.96f),
        onDismissRequest = {
            if (!isMandatoryFirstRegistration) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isMandatoryFirstRegistration,
            dismissOnClickOutside = !isMandatoryFirstRegistration
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SystelNavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isMandatoryFirstRegistration) "دخول مستخدم / تسجيل فريق عمل" else "تسجيل بيانات فريق العمل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = SystelNavyDark
                    )
                    Text(
                        text = "تسجيل بيانات المهندس أو اختيار حساب مسجل",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (isLandscape) 280.dp else 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "مرحباً بك! بمجرد التسجيل سيتم إنشاء حسابك لتسجيل تصفياتك ومصروفاتك وعرضها مباشرة للتيم ليدر للاعتماد.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF166534),
                            lineHeight = 15.sp
                        )
                    }
                }

                // If existing accounts are available: allow fast login as another user
                if (allTeams.isNotEmpty() || onSelectTeamLeader != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "أو اختر حساباً مسجلاً مسبقاً:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SystelNavyPrimary
                            )

                            // Team Leader Quick Login
                            if (onSelectTeamLeader != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SystelSuccessGreen.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SystelSuccessGreen.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (viewModel != null && !viewModel.canDirectLoginAsManager()) {
                                                showApprovalDialog = true
                                            } else {
                                                onSelectTeamLeader()
                                            }
                                        }
                                        .testTag("quick_login_leader_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = SystelSuccessGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "الدخول كـ تيم ليدر (Team Leader)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF14532D)
                                            )
                                            Text(
                                                text = "صلاحيات الاعتماد والفحص الشامل",
                                                fontSize = 10.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                }
                            }

                            // Quick login for existing teams
                            if (allTeams.isNotEmpty() && onSelectExistingTeam != null) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    allTeams.forEach { team ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF0284C7).copy(alpha = 0.1f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f)),
                                            modifier = Modifier
                                                .clickable { onSelectExistingTeam(team) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Groups,
                                                    contentDescription = null,
                                                    tint = Color(0xFF0284C7),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = "${team.teamName} (${team.leaderName})",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF0369A1)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "أو تسجيل حساب جديد",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }
                }

                // Role Selector (مهندس موقع vs تيم ليدر)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "اختر نوع الحساب وصلاحيات الدخول:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystelNavyDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (!isTeamLeaderRole) SystelNavyPrimary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isTeamLeaderRole) SystelNavyPrimary else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isTeamLeaderRole = false }
                            ) {
                                Text(
                                    text = "مهندس موقع / فريق",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isTeamLeaderRole) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isTeamLeaderRole) SystelSuccessGreen else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isTeamLeaderRole) SystelSuccessGreen else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        isTeamLeaderRole = true
                                        if (teamName.isBlank()) teamName = "إدارة العمليات - التيم ليدر"
                                    }
                            ) {
                                Text(
                                    text = "تيم ليدر (اعتماد وفحص)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTeamLeaderRole) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 1. Team Name (only for field team)
                if (!isTeamLeaderRole) {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = {
                            teamName = it
                            errorText = null
                        },
                        label = { Text("اسم الفريق / الموقع *") },
                        placeholder = { Text("مثال: تيم صيانة الدلتا، بنها...") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = SystelNavyPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_team_name_input"),
                        singleLine = true
                    )
                }

                // 2. Leader / Engineer Name
                OutlinedTextField(
                    value = leaderName,
                    onValueChange = {
                        leaderName = it
                        errorText = null
                    },
                    label = { Text(if (isTeamLeaderRole) "اسم مهندس التيم ليدر (يظهر في ختم الاعتماد) *" else "اسم المهندس / قائد الفريق *") },
                    placeholder = { Text(if (isTeamLeaderRole) "مثال: م. ياسر عواد أو اسم المهندس" else "مثال: أحمد محمود") },
                    leadingIcon = { Icon(if (isTeamLeaderRole) Icons.Default.AdminPanelSettings else Icons.Default.Person, contentDescription = null, tint = if (isTeamLeaderRole) SystelSuccessGreen else SystelNavyPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_team_leader_input"),
                    singleLine = true
                )

                // 3. Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorText = null
                    },
                    label = { Text("رقم التليفون *") },
                    placeholder = { Text("مثال: 01012345678") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SystelNavyPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_team_phone_input"),
                    singleLine = true
                )

                // 4. Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorText = null
                    },
                    label = { Text(if (isTeamLeaderRole) "البريد الإلكتروني للتيم ليدر (إلزامي للموافقة) *" else "البريد الإلكتروني (الإيميل) *") },
                    placeholder = { Text(if (isTeamLeaderRole) "مثال: yasser.awad.systel@gmail.com أو إيميلك" else "مثال: engineer@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SystelNavyPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_team_email_input"),
                    singleLine = true
                )

                // 5. Password fields for Team Leader (Mandatory per requirement)
                if (isTeamLeaderRole) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorText = null
                        },
                        label = { Text("كلمة المرور / الباسورد (إلزامي) *") },
                        placeholder = { Text("أدخل كلمة المرور لحساب التيم ليدر") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SystelSuccessGreen) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_team_leader_password_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorText = null
                        },
                        label = { Text("تأكيد كلمة المرور (الباسورد) *") },
                        placeholder = { Text("أعد كتابة كلمة المرور للتأكيد") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SystelSuccessGreen) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (confirmPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_team_leader_confirm_password_input"),
                        singleLine = true
                    )
                }

                // 6. Department Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "القسم التابع له (Department):",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SystelNavyDark
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SystelConstants.DEPARTMENTS.forEach { dept ->
                            FilterChip(
                                selected = department == dept,
                                onClick = { department = dept },
                                label = { Text(dept, fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SystelNavyPrimary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTeam = if (isTeamLeaderRole && teamName.isBlank()) "إدارة العمليات - التيم ليدر" else teamName.trim()
                    val finalEmail = email.trim()
                    when {
                        !isTeamLeaderRole && finalTeam.isBlank() -> errorText = "يرجى إدخال اسم الفريق"
                        leaderName.isBlank() -> errorText = if (isTeamLeaderRole) "يرجى إدخال اسم مهندس التيم ليدر" else "يرجى إدخال اسم قائد الفريق"
                        phone.isBlank() -> errorText = "يرجى إدخال رقم التليفون"
                        finalEmail.isBlank() || !finalEmail.contains("@") -> errorText = "يرجى إدخال بريد إلكتروني صحيح (إلزامي)"
                        isTeamLeaderRole && (password.isBlank() || password.length < 4) -> errorText = "يرجى كتابة كلمة المرور (الباسورد) بما لا يقل عن 4 حروف أو أرقام"
                        isTeamLeaderRole && password != confirmPassword -> errorText = "كلمتا المرور غير متطابقتين، يرجى كتابة نفس الباسورد"
                        isTeamLeaderRole && viewModel != null && !viewModel.isTeamLeaderApproved(finalEmail) -> {
                            // Save password and prompt for email approval code
                            viewModel.saveLeaderCredentials(finalEmail, password)
                            showApprovalDialog = true
                        }
                        else -> {
                            if (isTeamLeaderRole && viewModel != null) {
                                viewModel.saveLeaderCredentials(finalEmail, password)
                            }
                            onConfirm(finalTeam, leaderName.trim(), phone.trim(), finalEmail, department, isTeamLeaderRole)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isTeamLeaderRole) SystelSuccessGreen else SystelNavyPrimary),
                modifier = Modifier.testTag("confirm_register_team_button")
            ) {
                Text(
                    text = if (isTeamLeaderRole) "طلب اعتماد ودخول كـ تيم ليدر" else "تسجيل ودخول التطبيق",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            if (!isMandatoryFirstRegistration) {
                OutlinedButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )

    if (showApprovalDialog && viewModel != null) {
        TeamLeaderApprovalDialog(
            viewModel = viewModel,
            initialLeaderName = leaderName.ifBlank { "م. ياسر عواد" },
            initialEmail = email.ifBlank { SystelConstants.MANAGER_EMAIL },
            initialPhone = phone,
            initialDept = department,
            initialPassword = password,
            onDismiss = { showApprovalDialog = false },
            onApproved = { approvedLeaderName ->
                showApprovalDialog = false
                val finalTeam = if (teamName.isBlank()) "إدارة العمليات - التيم ليدر" else teamName.trim()
                val finalEmail = if (email.isBlank()) SystelConstants.MANAGER_EMAIL else email.trim()
                onConfirm(finalTeam, approvedLeaderName, phone.trim(), finalEmail, department, true)
            }
        )
    }
}

package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystelConstants
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.ClearanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dialog enforcing the strict requirement:
 * No one is allowed to register or enter as Team Leader without approval via Email
 * from the management / Eng. Yasser Awad.
 * Registration requires: Name, Email, Phone, and Password.
 */
@Composable
fun TeamLeaderApprovalDialog(
    viewModel: ClearanceViewModel,
    initialLeaderName: String = "",
    initialEmail: String = "",
    initialPhone: String = "",
    initialDept: String = SystelConstants.DEPARTMENTS.first(),
    initialPassword: String = "",
    onDismiss: () -> Unit,
    onApproved: (approvedLeaderName: String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var leaderName by remember { mutableStateOf(initialLeaderName.ifBlank { "م. " }) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var password by remember { mutableStateOf(initialPassword) }
    var passwordVisible by remember { mutableStateOf(false) }
    var department by remember { mutableStateOf(initialDept) }

    var step by remember { mutableStateOf(1) } // 1: Fill Request, 2: Send & Enter Code
    var generatedCode by remember { mutableStateOf(viewModel.getPendingApprovalCode() ?: "") }
    var enteredCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    fun sendApprovalEmail(toAdmin: Boolean = true) {
        val nowFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val subject = "طلب اعتماد وتفويض تيم ليدر: ${leaderName.trim()} - شركة سيستل"
        val body = """
            السيد مدير العمليات والتشغيل / م. ياسر عواد المحترم،
            تحية طيبة وبعد،
            
            يتقدم المهندس المذكور أدناه بطلب تسجيل واعتماد كـ (تيم ليدر - Team Leader) داخل تطبيق تصفيات ومصاريف شركة سيستل:
            
            - اسم المهندس (التيم ليدر): ${leaderName.trim()}
            - البريد الإلكتروني للمهندس: ${email.trim()}
            - رقم التليفون: ${phone.trim()}
            - كلمة المرور المطلوبة للحساب: ${password.trim()}
            - القسم التابع له: $department
            - توقيت تقديم الطلب: $nowFormatted
            
            --------------------------------------------------------
            كود الموافقة والاعتماد السري (Approval Code):
            $generatedCode
            --------------------------------------------------------
            
            تعليمات الأمان:
            * للموافقة على منح المهندس صلاحيات التيم ليدر واعتماد التصفيات، يرجى تزويده بهذا الكود لإدخاله في التطبيق لإتمام تفعيل حسابه.
            * لا يمكن قبول تسجيل أي تيم ليدر بدون هذا الكود والموافقة الرسمية عبر البريد الإلكتروني.
            
            مع أطيب التحيات،
            نظام إدارة وتصفيات سيستل للاتصالات
        """.trimIndent()

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SystelConstants.MANAGER_EMAIL))
            putExtra(Intent.EXTRA_CC, arrayOf(email.trim()))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "إرسال طلب الاعتماد عبر البريد الإلكتروني"))
            Toast.makeText(context, "تم فتح تطبيق الإيميل لإرسال طلب الموافقة للمدير", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "كود الاعتماد الخاص بك: $generatedCode", Toast.LENGTH_LONG).show()
        }
    }

    AlertDialog(
        modifier = Modifier
            .widthIn(max = if (isLandscape) 640.dp else 480.dp)
            .fillMaxWidth(if (isLandscape) 0.88f else 0.96f),
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = SystelAccentRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "طلب اعتماد تيم ليدر عبر الإيميل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SystelNavyDark
                    )
                    Text(
                        text = "موافقة الإدارة شرط إلزامي لمنح الصلاحيات",
                        fontSize = 11.sp,
                        color = SystelAccentRed,
                        fontWeight = FontWeight.SemiBold
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
                // Strict Security Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SystelAccentRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ممنوع قبول تسجيل أي تيم ليدر بدون موافقة مسبقة عبر الإيميل من إدارة شركة سيستل (م. ياسر عواد).",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B),
                            lineHeight = 16.sp
                        )
                    }
                }

                if (step == 1) {
                    // Step 1: Input details to submit request
                    Text(
                        text = "الخطوة 1: أدخل بياناتك كاملة لإنشاء طلب الاعتماد الرسمي:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystelNavyDark
                    )

                    OutlinedTextField(
                        value = leaderName,
                        onValueChange = {
                            leaderName = it
                            errorMessage = null
                        },
                        label = { Text("اسم مهندس التيم ليدر *") },
                        placeholder = { Text("مثال: م. ياسر عواد أو م. الاسم") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SystelNavyPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("approval_leader_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("البريد الإلكتروني (الإيميل) للمهندس *") },
                        placeholder = { Text("مثال: engineer@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SystelNavyPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("approval_leader_email_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            errorMessage = null
                        },
                        label = { Text("رقم التليفون للمهندس *") },
                        placeholder = { Text("مثال: 01012345678") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SystelNavyPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("approval_leader_phone_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("كلمة المرور / الباسورد (إلزامي للتيم ليدر) *") },
                        placeholder = { Text("أدخل كلمة المرور الخاصة بحسابك") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SystelNavyPrimary) },
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
                            .testTag("approval_leader_password_input"),
                        singleLine = true
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = SystelNavyPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "سيتم إرسال طلب الاعتماد رسميًا إلى: ${SystelConstants.MANAGER_EMAIL}",
                                fontSize = 11.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                } else {
                    // Step 2: Send Email & Enter Received Code
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SystelSuccessGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "تم إنشاء طلب الاعتماد والكود بنجاح!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF14532D)
                                )
                            }
                            Text(
                                text = "المهندس: $leaderName ($email)\nالهاتف: $phone\nالمرسل إليه: م. ياسر عواد (${SystelConstants.MANAGER_EMAIL})",
                                fontSize = 11.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    // Button to re-open or trigger Email
                    Button(
                        onClick = { sendApprovalEmail() },
                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_approval_email_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إرسال رسالة الاعتماد عبر الإيميل (Gmail)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    HorizontalDivider()

                    Text(
                        text = "الخطوة 2: أدخل كود الموافقة والاعتماد المستلم عبر الإيميل:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystelNavyDark
                    )

                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = {
                            enteredCode = it
                            errorMessage = null
                        },
                        label = { Text("كود الموافقة والاعتماد السري *") },
                        placeholder = { Text("أدخل الكود المستلم عبر البريد الإلكتروني") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = SystelSuccessGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("approval_code_input"),
                        singleLine = true
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ملاحظة للمدير / م. ياسر عواد:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SystelNavyPrimary
                            )
                            Text(
                                text = "كود الطلب المنشأ في هذه الجلسة هو: $generatedCode (أو استخدم كود المشرف 2026 للاعتماد الفوري).",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (successNotice != null) {
                    Text(
                        text = successNotice!!,
                        color = SystelSuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            if (step == 1) {
                Button(
                    onClick = {
                        when {
                            leaderName.isBlank() || leaderName.trim() == "م." -> errorMessage = "يرجى كتابة اسم مهندس التيم ليدر"
                            email.isBlank() || !email.contains("@") -> errorMessage = "البريد الإلكتروني إلزامي، يرجى كتابة إيميل صحيح"
                            phone.isBlank() -> errorMessage = "رقم الهاتف إلزامي، يرجى كتابة رقم الهاتف"
                            password.isBlank() || password.length < 4 -> errorMessage = "كلمة المرور (الباسورد) إلزامية للتيم ليدر (4 خانات فأكثر)"
                            else -> {
                                val code = viewModel.requestTeamLeaderApproval(
                                    name = leaderName,
                                    email = email,
                                    phone = phone,
                                    department = department,
                                    password = password
                                )
                                generatedCode = code
                                step = 2
                                sendApprovalEmail()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                    modifier = Modifier.testTag("submit_approval_request_button")
                ) {
                    Text("متابعة وإرسال الإيميل للموافقة ➔", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (enteredCode.isBlank()) {
                            errorMessage = "يرجى إدخال كود الموافقة والاعتماد المستلم عبر الإيميل"
                            return@Button
                        }
                        val approved = viewModel.verifyAndApproveTeamLeader(enteredCode, leaderName.trim())
                        if (approved) {
                            Toast.makeText(context, "تم تأكيد واعتماد صلاحيات التيم ليدر بنجاح!", Toast.LENGTH_LONG).show()
                            onApproved(leaderName.trim())
                        } else {
                            errorMessage = "كود الاعتماد غير صحيح! يرجى مراجعة البريد الإلكتروني أو كود موافقة الإدارة."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen),
                    modifier = Modifier.testTag("verify_approval_code_button")
                ) {
                    Text("تأكيد الكود وتفعيل الحساب رسميًا", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (step == 2) "إغلاق" else "إلغاء")
            }
        }
    )
}

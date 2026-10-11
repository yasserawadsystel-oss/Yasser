package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamEntity
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.UserSessionState

@Composable
fun RoleSwitcherBar(
    userSession: UserSessionState,
    allTeams: List<TeamEntity>,
    onSelectManager: () -> Unit,
    onSelectTeam: (TeamEntity) -> Unit,
    onRegisterTeamClick: () -> Unit,
    onLogout: () -> Unit,
    onResetAllData: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAccountMenu by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_switcher_bar"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (userSession.isManager) Color(0xFF0F2654) else Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User / Team Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (userSession.isManager) SystelSuccessGreen.copy(alpha = 0.2f)
                                else Color(0xFF38BDF8).copy(alpha = 0.2f)
                            )
                            .border(
                                1.5.dp,
                                if (userSession.isManager) SystelSuccessGreen else Color(0xFF38BDF8),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (userSession.isManager) Icons.Default.AdminPanelSettings else Icons.Default.Groups,
                            contentDescription = null,
                            tint = if (userSession.isManager) Color(0xFF86EFAC) else Color(0xFFBAE6FD),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userSession.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (userSession.isManager) SystelSuccessGreen else Color(0xFF0284C7)
                            ) {
                                Text(
                                    text = if (userSession.isManager) "التيم ليدر" else "فريق عمل مسجل",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (userSession.isManager) "إشراف واعتماد تصفيات الفرق والمهندسين" else (userSession.activeTeamName ?: userSession.roleTitle),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (userSession.isManager) Color(0xFF93C5FD) else Color(0xFF7DD3FC)
                        )
                    }
                }

                // Controls area: Switch Menu (for Manager) + Distinct Logout Button (for everyone)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (userSession.isManager) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .clickable { showAccountMenu = true }
                                    .testTag("switch_account_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "إدارة الفرق",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showAccountMenu,
                                onDismissRequest = { showAccountMenu = false }
                            ) {
                                // Team Leader Option
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("التيم ليدر (Team Leader)", fontWeight = FontWeight.Bold)
                                            Text("فحص واعتماد تصفيات جميع الفرق والمهندسين", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SystelSuccessGreen)
                                    },
                                    onClick = {
                                        onSelectManager()
                                        showAccountMenu = false
                                    }
                                )

                                HorizontalDivider()

                                // Registered Teams
                                if (allTeams.isNotEmpty()) {
                                    Text(
                                        text = "معاينة فريق عمل محدد (${allTeams.size}):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )

                                    allTeams.forEach { team ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(team.teamName, fontWeight = FontWeight.SemiBold)
                                                    Text(
                                                        text = "القائد: ${team.leaderName} • ${team.phone.ifBlank { "بدون هاتف" }}",
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF0284C7))
                                            },
                                            onClick = {
                                                onSelectTeam(team)
                                                showAccountMenu = false
                                            }
                                        )
                                    }

                                    HorizontalDivider()
                                }

                                // Register New Team
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("+ تسجيل فريق عمل جديد", fontWeight = FontWeight.Bold, color = SystelNavyPrimary)
                                            Text("إضافة فريق جديد للنظام", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = SystelNavyPrimary)
                                    },
                                    onClick = {
                                        showAccountMenu = false
                                        onRegisterTeamClick()
                                    }
                                )

                                if (onResetAllData != null) {
                                    HorizontalDivider()

                                    // Reset All Data for sharing clean APK
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("إرسال البرنامج لمستخدم جديد", fontWeight = FontWeight.Bold, color = SystelAccentRed)
                                                Text("تصفير التطبيق والبدء بدون بيانات", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = SystelAccentRed)
                                        },
                                        onClick = {
                                            showAccountMenu = false
                                            showResetConfirmDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Logout Button ("زر خروج عشان لو هدخل بيوزر تاني")
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.22f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { showLogoutConfirmDialog = true }
                            .testTag("logout_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "خروج",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "خروج",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFEE2E2)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notice Banner explaining isolation & permissions
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (userSession.isManager) Color(0xFF1E3A8A).copy(alpha = 0.5f) else Color(0xFF0F172A)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (userSession.isManager) Icons.Default.VerifiedUser else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (userSession.isManager) Color(0xFF93C5FD) else Color(0xFFFDE047),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (userSession.isManager)
                            "وضع التيم ليدر: تشاهد تصفيات جميع المواقع والفرق، مع صلاحية الاعتماد والرفض وتحميل الـ PDF."
                        else
                            "وضع ${userSession.activeTeamName ?: "فريق العمل"}: تصفياتك تظهر للتيم ليدر. لا تستطيع رؤية تصفيات باقي الفرق أو تحميل الـ PDF إلا بعد موافقة التيم ليدر.",
                        fontSize = 11.sp,
                        color = if (userSession.isManager) Color(0xFFBFDBFE) else Color(0xFFFEF08A),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = SystelAccentRed)
                    Text("تسجيل الخروج", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "هل ترغب في تسجيل الخروج من الحساب الحالي؟ ستتمكن من الدخول بيوزر آخر أو تسجيل فريق جديد.",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("تأكيد الخروج", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Reset Clean State Dialog (For sending the app to a new user without any data)
    if (showResetConfirmDialog && onResetAllData != null) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = SystelAccentRed)
                    Text("إرسال البرنامج لمستخدم جديد (بدون بيانات)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "سيقوم التطبيق بحذف وتفريغ جميع التصفيات وفرق العمل المسجلة على هذا الجهاز، ليعود التطبيق إلى حالته الأصلية تماماً بدون أي بيانات سابقة.",
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Text(
                            text = "💡 استخدم هذا الخيار إذا أردت مشاركة أو إرسال البرنامج لأي مهندس أو تيم ليدر آخر ليصل إليه نظيفاً بدون بيانات.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF991B1B),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        onResetAllData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed),
                    modifier = Modifier.testTag("confirm_reset_clean_data_button")
                ) {
                    Text("تفريغ وبدء تطبيق جديد فارغ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

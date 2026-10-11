package com.example.ui.components

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PendingAccountsDialog(
    pendingAccounts: List<UserAccountEntity>,
    onApprove: (UserAccountEntity) -> Unit,
    onReject: (UserAccountEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    AlertDialog(
        modifier = Modifier
            .widthIn(max = if (isLandscape) 640.dp else 520.dp)
            .fillMaxWidth(if (isLandscape) 0.9f else 0.96f)
            .testTag("pending_accounts_dialog"),
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (pendingAccounts.isNotEmpty()) Color(0xFFF59E0B).copy(alpha = 0.2f) else SystelSuccessGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (pendingAccounts.isNotEmpty()) Icons.Default.HourglassEmpty else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (pendingAccounts.isNotEmpty()) Color(0xFFD97706) else SystelSuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "طلبات تفعيل الحسابات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SystelNavyDark
                        )
                        if (pendingAccounts.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SystelAccentRed
                            ) {
                                Text(
                                    text = "${pendingAccounts.size} معلق",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "موافقة التيم ليدر لتفعيل حسابات المهندسين وفرق العمل",
                        fontSize = 11.5.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            if (pendingAccounts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SystelSuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "لا توجد طلبات تفعيل معلقة حالياً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SystelNavyDark
                    )
                    Text(
                        text = "جميع طلبات التسجيل معتمدة ومفعلة بالنظام بالكامل.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (isLandscape) 340.dp else 480.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pendingAccounts, key = { it.id }) { account ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pending_account_card_${account.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Header: Name and Role chip
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (account.isTeamLeader) Icons.Default.AdminPanelSettings else Icons.Default.Engineering,
                                            contentDescription = null,
                                            tint = if (account.isTeamLeader) SystelNavyPrimary else Color(0xFF0284C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = account.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = SystelNavyDark
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (account.isTeamLeader) Color(0xFFEDE9FE) else Color(0xFFE0F2FE)
                                    ) {
                                        Text(
                                            text = if (account.isTeamLeader) "تيم ليدر" else "مهندس موقع",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (account.isTeamLeader) Color(0xFF6D28D9) else Color(0xFF0369A1),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                // Details: Department, Team, Phone, Email
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("القسم: ${account.department}", fontSize = 12.sp, color = Color(0xFF334155))
                                        Text("الفريق: ${account.teamName}", fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.Gray)
                                            Text(account.phone, fontSize = 12.sp, color = Color(0xFF475569))
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.Gray)
                                            Text(account.email, fontSize = 12.sp, color = Color(0xFF475569))
                                        }
                                    }
                                }

                                // Activation Code Pill + Timestamp
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(13.dp))
                                            Text("كود التفعيل: ", fontSize = 11.5.sp, color = Color(0xFF92400E))
                                            Text(account.activationCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "إرسال للمهندس ✉️",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1D4ED8),
                                                modifier = Modifier
                                                    .clickable {
                                                        try {
                                                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                                data = Uri.parse("smsto:${account.phone}")
                                                                putExtra("sms_body", "مرحباً ${account.fullName}، كود تفعيل حسابك في تطبيق تصفية سيستل هو: ${account.activationCode}")
                                                            }
                                                            context.startActivity(smsIntent)
                                                        } catch (e: Exception) {
                                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                            val clip = android.content.ClipData.newPlainText("كود التفعيل", account.activationCode)
                                                            clipboard.setPrimaryClip(clip)
                                                            Toast.makeText(context, "تم نسخ كود التفعيل: ${account.activationCode}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = dateFormat.format(Date(account.requestedAt)),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Quick Contact & Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Contact Phone
                                    IconButton(
                                        onClick = {
                                            try {
                                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${account.phone}"))
                                                context.startActivity(callIntent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "تعذر الاتصال بالرقم", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "اتصال", tint = SystelNavyPrimary, modifier = Modifier.size(18.dp))
                                    }

                                    // Reject Button
                                    OutlinedButton(
                                        onClick = { onReject(account) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("reject_account_button_${account.id}"),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SystelAccentRed),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SystelAccentRed)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("رفض", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Approve Button
                                    Button(
                                        onClick = { onApprove(account) },
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .testTag("approve_account_button_${account.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("قبول وتفعيل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary)
            ) {
                Text("إغلاق")
            }
        }
    )
}

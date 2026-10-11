package com.example.ui.components

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClearanceWithItems
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import java.text.DecimalFormat

@Composable
fun TeamLeaderNotificationDialog(
    pendingClearances: List<ClearanceWithItems>,
    onDismiss: () -> Unit,
    onSelectClearance: (Long) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val decimalFormat = remember { DecimalFormat("#,##0.##") }

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
                    color = if (pendingClearances.isNotEmpty()) SystelAccentRed.copy(alpha = 0.15f) else SystelSuccessGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (pendingClearances.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (pendingClearances.isNotEmpty()) SystelAccentRed else SystelSuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "إشعارات التيم ليدر",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SystelNavyDark
                        )
                        if (pendingClearances.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SystelAccentRed
                            ) {
                                Text(
                                    text = "${pendingClearances.size} معلقة",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (pendingClearances.isNotEmpty())
                            "طلبات تصفية جديدة بانتظار مراجعتك واعتمادك"
                        else
                            "جميع طلبات التصفية معتمدة ومطابقة",
                        fontSize = 11.5.sp,
                        color = if (pendingClearances.isNotEmpty()) SystelAccentRed else SystelSuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (isLandscape) 280.dp else 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (pendingClearances.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SystelSuccessGreen,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "لا توجد طلبات تصفية معلقة حالياً",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF14532D)
                            )
                            Text(
                                text = "تم فحص واعتماد جميع تصفيات المواقع وفرق العمل بنجاح. أي استمارة جديدة ينشئها المهندسون ستظهر لك هنا مباشرة.",
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "الطلبات المعلقة بانتظار الموافقة (${pendingClearances.size}):",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystelNavyDark
                    )

                    pendingClearances.forEach { item ->
                        val sheet = item.sheet
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    onSelectClearance(sheet.id)
                                }
                                .testTag("notification_item_${sheet.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFEF2F2)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.HourglassEmpty,
                                                    contentDescription = null,
                                                    tint = SystelAccentRed,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "معلق للمراجعة",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SystelAccentRed
                                                )
                                            }
                                        }
                                        Text(
                                            text = sheet.sheetNumber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Text(
                                        text = sheet.startDate,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                Text(
                                    text = sheet.siteName,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystelNavyDark
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${sheet.teamName} (${sheet.department})",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "المصروف: ${decimalFormat.format(item.totalExpenses)} ج.م",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelNavyPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "العهدة المستلمة: ${decimalFormat.format(sheet.advanceAmount)} ج.م",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF64748B)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SystelNavyPrimary,
                                        modifier = Modifier.clickable {
                                            onDismiss()
                                            onSelectClearance(sheet.id)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "فحص واعتماد التصفية",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Icon(
                                                Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
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
                colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                modifier = Modifier.testTag("close_notifications_dialog_button")
            ) {
                Text("إغلاق", fontWeight = FontWeight.Bold)
            }
        }
    )
}

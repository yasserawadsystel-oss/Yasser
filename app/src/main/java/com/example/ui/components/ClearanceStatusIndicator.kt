package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClearanceStatus
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelGoldApproval
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen

/**
 * Visual metadata styling configuration for each status
 */
data class StatusVisualConfig(
    val status: ClearanceStatus,
    val icon: ImageVector,
    val primaryColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val labelAr: String,
    val subtitleAr: String
)

fun getStatusVisualConfig(status: ClearanceStatus): StatusVisualConfig {
    return when (status) {
        ClearanceStatus.APPROVED -> StatusVisualConfig(
            status = ClearanceStatus.APPROVED,
            icon = Icons.Default.CheckCircle,
            primaryColor = SystelSuccessGreen,
            containerColor = Color(0xFFDCFCE7),
            borderColor = Color(0xFF86EFAC),
            textColor = Color(0xFF14532D),
            labelAr = "معتمد رسمياً",
            subtitleAr = "تمت المراجعة والاعتماد والمطابقة بالسجلات"
        )
        ClearanceStatus.REJECTED -> StatusVisualConfig(
            status = ClearanceStatus.REJECTED,
            icon = Icons.Default.Cancel,
            primaryColor = SystelAccentRed,
            containerColor = Color(0xFFFEE2E2),
            borderColor = Color(0xFFFCA5A5),
            textColor = Color(0xFF991B1B),
            labelAr = "مرفوض / يتطلب تعديل",
            subtitleAr = "تم رفض التصفية أو طلب تعديل البنود من التيم ليدر"
        )
        ClearanceStatus.PENDING -> StatusVisualConfig(
            status = ClearanceStatus.PENDING,
            icon = Icons.Default.HourglassTop,
            primaryColor = SystelGoldApproval,
            containerColor = Color(0xFFFEF3C7),
            borderColor = Color(0xFFFDE68A),
            textColor = Color(0xFF92400E),
            labelAr = "قيد المراجعة",
            subtitleAr = "في انتظار فحص واعتماد التيم ليدر"
        )
    }
}

/**
 * Compact Status Badge / Pill (for cards, lists, and summary headers)
 */
@Composable
fun ClearanceStatusBadge(
    statusCode: String,
    modifier: Modifier = Modifier,
    showPulseDot: Boolean = true
) {
    val status = ClearanceStatus.fromCode(statusCode)
    val config = getStatusVisualConfig(status)

    // Gentle pulse animation for Pending status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by if (status == ClearanceStatus.PENDING && showPulseDot) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 1.0f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(animation = tween(1000)),
            label = "static_alpha"
        )
    }

    Surface(
        modifier = modifier.testTag("status_badge_${status.code.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        color = config.containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, config.borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (showPulseDot) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .alpha(pulseAlpha)
                        .background(config.primaryColor)
                )
            }

            Icon(
                imageVector = config.icon,
                contentDescription = config.labelAr,
                tint = config.primaryColor,
                modifier = Modifier.size(13.dp)
            )

            Text(
                text = config.labelAr,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = config.textColor
            )
        }
    }
}

/**
 * Detailed Status Banner & Decision Card (for detailed view)
 * Visually shows whether the request is Pending, Approved, or Rejected,
 * with detailed descriptions, manager stamp/sign-off, timestamp, and notes.
 */
@Composable
fun ClearanceStatusBanner(
    statusCode: String,
    approvedBy: String? = null,
    approvalDate: String? = null,
    approvalNotes: String? = null,
    modifier: Modifier = Modifier
) {
    val status = ClearanceStatus.fromCode(statusCode)
    val config = getStatusVisualConfig(status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("status_banner_${status.code.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = config.containerColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, config.borderColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Status Icon, Title, and Code Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(config.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = config.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "حالة استمارة التصفية:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = config.textColor.copy(alpha = 0.8f)
                        )
                        Text(
                            text = config.labelAr,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = config.textColor
                        )
                    }
                }

                // Status Code Pill (APPROVED / PENDING / REJECTED)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, config.borderColor)
                ) {
                    Text(
                        text = status.code,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = config.primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Subtitle explanation
            Text(
                text = config.subtitleAr,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Normal,
                color = config.textColor.copy(alpha = 0.9f)
            )

            // Decision Details / Manager Sign-off (if approved or rejected)
            if (status != ClearanceStatus.PENDING && !approvedBy.isNullOrBlank()) {
                HorizontalDivider(color = config.borderColor.copy(alpha = 0.7f), thickness = 1.dp)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (status == ClearanceStatus.APPROVED) "المسؤول المعتمد:" else "المسؤول:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = config.textColor
                        )
                        Text(
                            text = approvedBy,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = config.textColor
                        )
                    }

                    if (!approvalDate.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تاريخ القرار:",
                                fontSize = 11.5.sp,
                                color = config.textColor.copy(alpha = 0.8f)
                            )
                            Text(
                                text = approvalDate,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = config.textColor
                            )
                        }
                    }

                    if (!approvalNotes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ملاحظات: $approvalNotes",
                                fontSize = 12.sp,
                                color = config.textColor,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // Interactive Status Lifecycle Stepper
            ClearanceStatusStepper(status = status)
        }
    }
}

/**
 * 3-Stage Lifecycle Stepper Component
 * 1. مسودة / تقديم -> 2. فحص الحسابات -> 3. قرار الاعتماد (Pending/Approved/Rejected)
 */
@Composable
fun ClearanceStatusStepper(
    status: ClearanceStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Step 1: Submission
            StepItem(
                stepNumber = "1",
                label = "تسجيل التصفية",
                isCompleted = true,
                isActive = false,
                isError = false
            )

            StepConnector(isCompleted = true)

            // Step 2: Under Review
            StepItem(
                stepNumber = "2",
                label = "مراجعة المصروفات",
                isCompleted = status != ClearanceStatus.PENDING,
                isActive = status == ClearanceStatus.PENDING,
                isError = false
            )

            StepConnector(isCompleted = status != ClearanceStatus.PENDING)

            // Step 3: Decision
            StepItem(
                stepNumber = "3",
                label = when (status) {
                    ClearanceStatus.APPROVED -> "معتمد"
                    ClearanceStatus.REJECTED -> "مرفوض"
                    ClearanceStatus.PENDING -> "قرار التيم ليدر"
                },
                isCompleted = status == ClearanceStatus.APPROVED,
                isActive = status == ClearanceStatus.PENDING,
                isError = status == ClearanceStatus.REJECTED
            )
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: String,
    label: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isError: Boolean
) {
    val bgColor = when {
        isError -> SystelAccentRed
        isCompleted -> SystelSuccessGreen
        isActive -> SystelGoldApproval
        else -> Color(0xFFCBD5E1)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            } else if (isError) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            } else {
                Text(
                    text = stepNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = if (isActive || isCompleted || isError) FontWeight.Bold else FontWeight.Normal,
            color = SystelNavyDark
        )
    }
}

@Composable
private fun StepConnector(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .background(if (isCompleted) SystelSuccessGreen else Color(0xFFCBD5E1))
    )
}

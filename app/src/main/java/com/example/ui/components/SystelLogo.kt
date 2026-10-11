package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SystelAccentCyan
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelGoldApproval
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen

/**
 * Authentic Systel Telecom Brand Logo
 * Displays the distinctive company mark, transmission arcs, and typography
 */
@Composable
fun SystelLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showArabicSubtitle: Boolean = true,
    isDarkBackground: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Stylized Systel Telecom Antenna & Signal Icon Mark
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            if (isDarkBackground) Color(0xFF0F2E68) else SystelNavyPrimary,
                            if (isDarkBackground) Color(0xFF071738) else Color(0xFF0A1C3E)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isDarkBackground) Color(0xFF2A5298) else Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.72f)) {
                val w = this.size.width
                val h = this.size.height

                // Central transmission mast / tower
                val mastPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.18f)
                    lineTo(w * 0.5f, h * 0.82f)
                }
                drawPath(
                    path = mastPath,
                    color = Color.White,
                    style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
                )

                // Tower A-frame struts
                val legsPath = Path().apply {
                    moveTo(w * 0.32f, h * 0.82f)
                    lineTo(w * 0.5f, h * 0.32f)
                    lineTo(w * 0.68f, h * 0.82f)
                }
                drawPath(
                    path = legsPath,
                    color = Color.White.copy(alpha = 0.9f),
                    style = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
                )

                // Crossbars
                drawLine(
                    color = Color.White.copy(alpha = 0.8f),
                    start = Offset(w * 0.36f, h * 0.62f),
                    end = Offset(w * 0.64f, h * 0.62f),
                    strokeWidth = w * 0.045f
                )

                // Top transmitter point (Systel Red)
                drawCircle(
                    color = SystelAccentRed,
                    radius = w * 0.10f,
                    center = Offset(w * 0.5f, h * 0.16f)
                )

                // Telecom broadcast wave 1 (Red)
                drawArc(
                    color = SystelAccentRed,
                    startAngle = 135f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, h * 0.04f),
                    size = androidx.compose.ui.geometry.Size(w * 0.64f, h * 0.64f),
                    style = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
                )

                // Telecom broadcast wave 2 (Cyan)
                drawArc(
                    color = SystelAccentCyan,
                    startAngle = 125f,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(w * 0.05f, -h * 0.08f),
                    size = androidx.compose.ui.geometry.Size(w * 0.90f, h * 0.90f),
                    style = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
                )
            }
        }

        // Typography: SYSTEL and Arabic name
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SYSTEL",
                    fontSize = if (size > 40.dp) 21.sp else 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = if (isDarkBackground) Color.White else SystelNavyPrimary,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    softWrap = false
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SystelAccentRed)
                )
            }
            if (showArabicSubtitle) {
                Text(
                    text = "سيستل للاتصالات • تصفية المواقع",
                    fontSize = if (size > 40.dp) 11.sp else 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkBackground) SystelAccentCyan else Color(0xFF64748B),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Official Digital Manager Approval Stamp
 * Renders an official corporate telecom seal
 */
@Composable
fun SystelApprovalStamp(
    managerName: String,
    approvalDate: String,
    signatureData: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .rotate(-3f)
            .border(2.dp, SystelSuccessGreen, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0FDF4),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = SystelSuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "معتمد رسمياً - شركة سيستل",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystelSuccessGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "اعتماد التيم ليدر: $managerName",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SystelNavyDark
            )

            // Handwritten Digital Signature (if provided)
            if (!signatureData.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                SignatureDisplayView(
                    signatureData = signatureData,
                    modifier = Modifier
                        .width(90.dp)
                        .height(30.dp),
                    strokeColor = Color(0xFF1E3A8A)
                )
            }

            Text(
                text = "بتاريخ: $approvalDate",
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
            Text(
                text = "APPROVED BY TEAM LEADER",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SystelSuccessGreen.copy(alpha = 0.8f)
            )
        }
    }
}

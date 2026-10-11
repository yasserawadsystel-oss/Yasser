package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SystelAccentCyan
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary

/**
 * Returns a specialized telecom icon based on activity type or site name
 */
fun getTelecomSiteIcon(activityType: String, siteName: String = ""): ImageVector {
    val normAct = activityType.trim().lowercase()
    val normSite = siteName.trim().lowercase()

    return when {
        normAct.contains("install") -> Icons.Default.Engineering
        normAct.contains("commission") || normAct.contains("on air") -> Icons.Default.CellTower
        normAct.contains("survey") -> Icons.Default.PrecisionManufacturing
        normAct.contains("swap") || normAct.contains("reconfig") -> Icons.Default.Tune
        normAct.contains("rectifier") || normAct.contains("power") -> Icons.Default.Construction
        normSite.contains("محطة") || normSite.contains("موقع") -> Icons.Default.CellTower
        else -> Icons.Default.SettingsInputAntenna
    }
}

/**
 * Professional Telecom Site Tower & Antenna Badge
 * Replaces simple letters with a modern, high-tech telecommunications vector badge
 */
@Composable
fun TelecomSiteIconBadge(
    activityType: String,
    siteName: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val icon = getTelecomSiteIcon(activityType, siteName)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F2654),
                        Color(0xFF1E3A8A)
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF38BDF8),
                        Color(0xFF1D4ED8)
                    )
                ),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background signal wave canvas
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Transmission ring
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                radius = w * 0.38f,
                center = Offset(w * 0.5f, h * 0.5f)
            )
        }

        // Crisp Vector Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFBAE6FD),
            modifier = Modifier.size(size * 0.56f)
        )

        // Active Status indicator dot at top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(size * 0.22f)
                .clip(CircleShape)
                .background(Color(0xFF22C55E))
                .border(1.dp, Color(0xFF0F2654), CircleShape)
        )
    }
}

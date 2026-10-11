package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClearanceWithItems
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import java.text.DecimalFormat

/**
 * Official Systel Telecom Clearance Sheet Component
 * Replicates the exact layout from the user's reference image
 */
@Composable
fun OfficialSystelSheetView(
    clearance: ClearanceWithItems,
    isManager: Boolean = true,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheet = clearance.sheet
    val items = clearance.items
    val decimalFormat = remember { DecimalFormat("#,##0.##") }
    val isApproved = sheet.status == "APPROVED"
    val canDownloadPdf = isApproved || isManager

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("official_systel_sheet_view"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F172A))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            // Top Action Bar with Download Button & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (canDownloadPdf) Color(0xFFDC2626) else Color(0xFF94A3B8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "استمارة التصفية الرسمية المعتمدة",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystelNavyDark
                        )
                        Text(
                            text = "الشكل النهائي لشركة سيستل للاتصالات",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // DOWNLOAD PDF BUTTON (Locked for team unless approved by Manager)
                if (canDownloadPdf) {
                    Button(
                        onClick = onDownloadPdf,
                        modifier = Modifier.testTag("download_pdf_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تحميل كملف PDF", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.testTag("download_pdf_locked_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(15.dp))
                            Text(
                                text = "التحميل مقفل لحين اعتماد التيم ليدر",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            if (!canDownloadPdf) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(15.dp))
                        Text(
                            text = "تنبيه: لا يمكن لفريق العمل تحميل استمارة التصفية PDF إلا بعد موافقة واعتماد التيم ليدر.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally Scrollable Official Paper Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                // Fixed layout width for high-density tabular presentation
                Column(
                    modifier = Modifier.width(820.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Header Row: Department Table & Systel Emblem Logo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Department Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Department :",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.width(110.dp)
                                )

                                // Department 5-Column Table
                                val depts = listOf("GSM(ADM)", "NOKIA Orange", "NOKIA Etisalat", "NOKIA EXtra", "NOKIA BSC")
                                TableGrid(
                                    headers = depts,
                                    colWidth = 72.dp,
                                    activeCol = when {
                                        sheet.department.contains("Orange", true) -> 1
                                        sheet.department.contains("Etisalat", true) -> 2
                                        sheet.department.contains("Extra", true) -> 3
                                        sheet.department.contains("BSC", true) -> 4
                                        else -> 0
                                    }
                                )
                            }

                            // Site Name Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Site Name :",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.width(110.dp)
                                )
                                Text(
                                    text = sheet.siteName,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }

                            // Region Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Region :",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.width(110.dp)
                                )
                                Text(
                                    text = sheet.region,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }

                        // Right Corner: Dates Table & Systel Emblem
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                            // Dates & Times Table
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                DateRowTable("start Date", sheet.startDate, "time start", sheet.startTime)
                                DateRowTable("end Date", sheet.endDate, "time end", sheet.endTime)
                            }

                            // Systel Official Emblem (Blue Oval with Lightning / Wave)
                            SystelOfficialEmblem(size = 64.dp)
                        }
                    }

                    // 2. Activity Table (11 Columns)
                    val activities = listOf(
                        "Survey", "Delivery", "Installation", "On Air", "Commission",
                        "Upgrade", "Downgrade", "Reconfig.", "Swap", "Rectifier", "Desmantle"
                    )
                    TableGrid(
                        headers = activities,
                        colWidth = 74.dp,
                        activeCol = activities.indexOfFirst { act ->
                            sheet.activityType.contains(act, ignoreCase = true) ||
                                (act == "Installation" && sheet.activityType.contains("Install", ignoreCase = true)) ||
                                (act == "Reconfig." && sheet.activityType.contains("Reconfig", ignoreCase = true))
                        }
                    )

                    // 3. Main Expenses Grid (11 Columns)
                    // Columns: مجموع | اوراق سفر | نقل | اقامة | عمالة | خامات | نثريات | بنزين | انتقالات | اعاشه | تاجير سيارة
                    val expenseColumns = listOf(
                        "مجموع", "اوراق سفر", "نقل", "اقامة", "عمالة",
                        "خامات", "نثريات", "بنزين", "انتقالات", "اعاشه", "تاجير سيارة"
                    )
                    val colWidth = 74.dp

                    Column(
                        modifier = Modifier
                            .border(1.5.dp, Color.Black)
                            .background(Color.White)
                    ) {
                        // Header Row
                        Row(modifier = Modifier.background(Color(0xFFF8FAFC))) {
                            expenseColumns.forEach { colTitle ->
                                Box(
                                    modifier = Modifier
                                        .width(colWidth)
                                        .height(28.dp)
                                        .border(0.7.dp, Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = colTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Data Rows
                        val maxDisplayRows = maxOf(items.size, 6)
                        for (r in 0 until maxDisplayRows) {
                            val currentItem = items.getOrNull(r)

                            Row {
                                expenseColumns.forEachIndexed { colIdx, colTitle ->
                                    Box(
                                        modifier = Modifier
                                            .width(colWidth)
                                            .height(24.dp)
                                            .border(0.7.dp, Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (currentItem != null) {
                                            val itemCat = currentItem.category.trim()
                                            val matchesCol = when (colIdx) {
                                                1 -> itemCat == "اوراق سفر"
                                                2 -> itemCat == "نقل"
                                                3 -> itemCat in listOf("اقامة", "إقامة")
                                                4 -> itemCat == "عمالة"
                                                5 -> itemCat == "خامات"
                                                6 -> itemCat in listOf("نثريات", "أخرى")
                                                7 -> itemCat == "بنزين"
                                                8 -> itemCat in listOf("انتقالات", "مواصلات")
                                                9 -> itemCat in listOf("اعاشه", "إعاشة", "اعاشة")
                                                10 -> itemCat in listOf("تاجير سيارة", "تأجير سيارة")
                                                else -> false
                                            }

                                            if (matchesCol) {
                                                if (colIdx == 5) { // خامات with description
                                                    Text(
                                                        text = currentItem.description.take(12),
                                                        fontSize = 9.sp,
                                                        color = Color.Black,
                                                        textAlign = TextAlign.Center
                                                    )
                                                } else {
                                                    Text(
                                                        text = decimalFormat.format(currentItem.amount),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.Black,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Double Border Totals Row
                        Spacer(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
                        Row(modifier = Modifier.background(Color(0xFFF1F5F9))) {
                            expenseColumns.forEachIndexed { colIdx, colTitle ->
                                Box(
                                    modifier = Modifier
                                        .width(colWidth)
                                        .height(28.dp)
                                        .border(0.7.dp, Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (colIdx == 0) {
                                        // Grand Total in مجموع column
                                        Text(
                                            text = decimalFormat.format(clearance.totalExpenses),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    } else {
                                        // Specific column total
                                        val sum = clearance.categoryBreakdown[colTitle]
                                            ?: clearance.categoryBreakdown[colTitle.replace("ا", "إ")]
                                            ?: clearance.categoryBreakdown[colTitle.replace("إ", "ا")]
                                            ?: 0.0
                                        if (sum > 0) {
                                            Text(
                                                text = decimalFormat.format(sum),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Footer Section: GROUP & SITE BOOK & LAYOUT & Approval Stamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            // Empty code box from image
                            Box(
                                modifier = Modifier
                                    .size(54.dp, 36.dp)
                                    .border(1.5.dp, Color.Black)
                            )

                            // GROUP Table
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "GROUP:",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 4.dp, end = 6.dp)
                                )

                                Column(modifier = Modifier.border(1.5.dp, Color.Black)) {
                                    // Group Header
                                    Row {
                                        Box(
                                            modifier = Modifier.width(48.dp).height(20.dp).border(0.7.dp, Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("NO.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier.width(220.dp).height(20.dp).border(0.7.dp, Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    val memberList = sheet.teamMembers
                                        .split("،", ",")
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() }
                                    val totalMemberRows = maxOf(2, memberList.size)

                                    // Row 1 (Leader)
                                    Row {
                                        Box(
                                            modifier = Modifier.width(48.dp).height(22.dp).border(0.7.dp, Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("1", fontSize = 11.sp)
                                        }
                                        Box(
                                            modifier = Modifier.width(220.dp).height(22.dp).border(0.7.dp, Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (sheet.teamLeader.isNotBlank()) sheet.teamLeader else "التيم ليدر",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    // Rows for all team members (can be more than 4 members)
                                    for (mIndex in 0 until totalMemberRows) {
                                        val memberName = memberList.getOrNull(mIndex) ?: ""
                                        Row {
                                            Box(
                                                modifier = Modifier.width(48.dp).height(20.dp).border(0.7.dp, Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${mIndex + 2}", fontSize = 11.sp)
                                            }
                                            Box(
                                                modifier = Modifier.width(220.dp).height(20.dp).border(0.7.dp, Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = memberName,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (memberName.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Right side: SITE BOOK & LAYOUT labels
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "SITE BOOK:   ${sheet.siteBook}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "LAY OUT:     ${sheet.layoutRef}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Official Approval Stamp / Seal (if approved)
                        if (isApproved) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0FDF4),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, SystelSuccessGreen),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = SystelSuccessGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "معتمد رسمياً - شركة سيستل",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SystelSuccessGreen
                                        )
                                    }
                                    Text(
                                        text = sheet.approvedBy ?: "التيم ليدر",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SystelNavyDark
                                    )
                                    if (!sheet.signatureData.isNullOrBlank()) {
                                        SignatureDisplayView(
                                            signatureData = sheet.signatureData,
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(26.dp)
                                        )
                                    }
                                    Text(
                                        text = "بتاريخ: ${sheet.approvalDate ?: "معتمد"}",
                                        fontSize = 9.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableGrid(
    headers: List<String>,
    colWidth: androidx.compose.ui.unit.Dp,
    activeCol: Int
) {
    Column(modifier = Modifier.border(1.5.dp, Color.Black)) {
        // Headers Row
        Row {
            headers.forEach { h ->
                Box(
                    modifier = Modifier
                        .width(colWidth)
                        .height(20.dp)
                        .border(0.7.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = h,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        // OK checkmark row
        Row {
            headers.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .width(colWidth)
                        .height(18.dp)
                        .border(0.7.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (i == activeCol) {
                        Text(
                            text = "OK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateRowTable(label1: String, val1: String, label2: String, val2: String) {
    Row(modifier = Modifier.border(1.5.dp, Color.Black)) {
        Box(
            modifier = Modifier.width(68.dp).height(20.dp).border(0.7.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(label1, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier.width(88.dp).height(20.dp).border(0.7.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(val1, fontSize = 10.sp)
        }
        Box(
            modifier = Modifier.width(68.dp).height(20.dp).border(0.7.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(label2, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier.width(72.dp).height(20.dp).border(0.7.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(val2, fontSize = 10.sp)
        }
    }
}

/**
 * Authentic Systel Telecom Oval Emblem as shown in user's image.png
 */
@Composable
fun SystelOfficialEmblem(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size, size * 0.65f)) {
        val w = this.size.width
        val h = this.size.height

        // Background light blue oval
        drawOval(
            color = Color(0xFFE8F1FC),
            topLeft = Offset.Zero,
            size = Size(w, h)
        )

        // Outer blue ellipse border
        drawOval(
            color = Color(0xFF4A90E2),
            topLeft = Offset.Zero,
            size = Size(w, h),
            style = Stroke(width = 3.5f)
        )

        // Lightning bolt / stylized telecom wave
        val boltPath = Path().apply {
            moveTo(w * 0.48f, h * 0.15f)
            lineTo(w * 0.35f, h * 0.52f)
            lineTo(w * 0.52f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.85f)
            lineTo(w * 0.65f, h * 0.45f)
            lineTo(w * 0.48f, h * 0.45f)
            close()
        }

        drawPath(path = boltPath, color = Color(0xFF357ABD))
    }
}

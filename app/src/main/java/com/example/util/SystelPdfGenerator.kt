package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.model.ClearanceWithItems
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

object SystelPdfGenerator {

    fun generateClearancePdf(
        context: Context,
        clearance: ClearanceWithItems
    ): File {
        val sheet = clearance.sheet
        val items = clearance.items
        val df = DecimalFormat("#,##0.##")

        // A4 Landscape dimensions in points (72 DPI: 842 x 595)
        val pageWidth = 842
        val pageHeight = 595
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background
        canvas.drawColor(Color.WHITE)

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            isAntiAlias = true
        }

        val doubleBorderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val marginX = 30f
        val marginY = 30f
        val contentWidth = pageWidth - (marginX * 2)

        // 1. Draw Systel Logo at Top-Right
        drawSystelLogo(canvas, pageWidth - marginX - 100f, marginY - 10f, 95f, 55f)

        // 2. Department & Header Section
        canvas.drawText("Department :", marginX + 10f, marginY + 20f, boldTextPaint)

        // Department Table
        val deptStartX = marginX + 150f
        val deptStartY = marginY + 5f
        val deptWidth = 350f
        val deptHeight = 36f
        val deptColWidth = deptWidth / 5f

        val depts = listOf("GSM(ADM)", "NOKIA Orange", "NOKIA Etisalat", "NOKIA EXtra", "NOKIA BSC")

        // Outer box for Dept table
        canvas.drawRect(deptStartX, deptStartY, deptStartX + deptWidth, deptStartY + deptHeight, strokePaint)
        // Mid horizontal line
        canvas.drawLine(deptStartX, deptStartY + 18f, deptStartX + deptWidth, deptStartY + 18f, strokePaint)

        depts.forEachIndexed { i, dName ->
            val colX = deptStartX + (i * deptColWidth)
            if (i > 0) {
                canvas.drawLine(colX, deptStartY, colX, deptStartY + deptHeight, strokePaint)
            }
            // Header text
            val textW = boldTextPaint.measureText(dName)
            canvas.drawText(dName, colX + (deptColWidth - textW) / 2f, deptStartY + 13f, boldTextPaint)

            // Check if selected
            val isSelected = sheet.department.contains(dName, ignoreCase = true) ||
                (dName == "NOKIA Orange" && sheet.department.contains("Orange", ignoreCase = true)) ||
                (dName == "NOKIA Etisalat" && sheet.department.contains("Etisalat", ignoreCase = true)) ||
                (dName == "GSM(ADM)" && sheet.department.contains("GSM", ignoreCase = true))

            if (isSelected) {
                val okW = boldTextPaint.measureText("OK")
                canvas.drawText("OK", colX + (deptColWidth - okW) / 2f, deptStartY + 31f, boldTextPaint)
            }
        }

        // 3. Site Name & Dates
        var currentY = deptStartY + deptHeight + 14f
        canvas.drawText("Site Name :", marginX + 10f, currentY + 12f, boldTextPaint)
        canvas.drawText(sheet.siteName, marginX + 110f, currentY + 12f, titlePaint)

        // Start Date Table
        val dateStartX = marginX + 300f
        val dateWidth = 340f
        val dateRowHeight = 18f

        // Row 1: Start Date & Time
        canvas.drawRect(dateStartX, currentY, dateStartX + dateWidth, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 60f, currentY, dateStartX + 60f, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 190f, currentY, dateStartX + 190f, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 255f, currentY, dateStartX + 255f, currentY + dateRowHeight, strokePaint)

        canvas.drawText("start Date", dateStartX + 6f, currentY + 13f, boldTextPaint)
        canvas.drawText(sheet.startDate, dateStartX + 70f, currentY + 13f, textPaint)
        canvas.drawText("time start", dateStartX + 195f, currentY + 13f, boldTextPaint)
        canvas.drawText(sheet.startTime, dateStartX + 265f, currentY + 13f, textPaint)

        // 4. Region & End Date
        currentY += dateRowHeight + 8f
        canvas.drawText("Region :", marginX + 10f, currentY + 12f, boldTextPaint)
        canvas.drawText(sheet.region, marginX + 110f, currentY + 12f, titlePaint)

        // Row 2: End Date & Time
        canvas.drawRect(dateStartX, currentY, dateStartX + dateWidth, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 60f, currentY, dateStartX + 60f, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 190f, currentY, dateStartX + 190f, currentY + dateRowHeight, strokePaint)
        canvas.drawLine(dateStartX + 255f, currentY, dateStartX + 255f, currentY + dateRowHeight, strokePaint)

        canvas.drawText("end Date", dateStartX + 6f, currentY + 13f, boldTextPaint)
        canvas.drawText(sheet.endDate, dateStartX + 70f, currentY + 13f, textPaint)
        canvas.drawText("time end", dateStartX + 195f, currentY + 13f, boldTextPaint)
        canvas.drawText(sheet.endTime, dateStartX + 265f, currentY + 13f, textPaint)

        // 5. Activity Table (11 columns)
        currentY += dateRowHeight + 16f
        val activities = listOf(
            "Survey", "Delivery", "Installation", "On Air", "Commission",
            "Upgrade", "Downgrade", "Reconfig.", "Swap", "Rectifier", "Desmantle"
        )
        val actWidth = contentWidth
        val actColWidth = actWidth / activities.size
        val actRowH = 18f

        canvas.drawRect(marginX, currentY, marginX + actWidth, currentY + (actRowH * 2), strokePaint)
        canvas.drawLine(marginX, currentY + actRowH, marginX + actWidth, currentY + actRowH, strokePaint)

        activities.forEachIndexed { i, actName ->
            val colX = marginX + (i * actColWidth)
            if (i > 0) {
                canvas.drawLine(colX, currentY, colX, currentY + (actRowH * 2), strokePaint)
            }
            val nameW = textPaint.measureText(actName)
            canvas.drawText(actName, colX + (actColWidth - nameW) / 2f, currentY + 13f, boldTextPaint)

            if (sheet.activityType.contains(actName, ignoreCase = true) ||
                (actName == "Installation" && sheet.activityType.contains("Install", ignoreCase = true)) ||
                (actName == "Reconfig." && sheet.activityType.contains("Reconfig", ignoreCase = true))
            ) {
                val okW = boldTextPaint.measureText("OK")
                canvas.drawText("OK", colX + (actColWidth - okW) / 2f, currentY + actRowH + 13f, boldTextPaint)
            }
        }

        // 6. Main Expenses Grid (11 columns matching user sheet)
        // From Left: مجموع | اوراق سفر | نقل | اقامة | عمالة | خامات | نثريات | بنزين | انتقالات | اعاشه | تاجير سيارة
        currentY += (actRowH * 2) + 16f
        val expenseColumns = listOf(
            "مجموع", "اوراق سفر", "نقل", "اقامة", "عمالة",
            "خامات", "نثريات", "بنزين", "انتقالات", "اعاشه", "تاجير سيارة"
        )

        val gridWidth = contentWidth
        val gridColWidth = gridWidth / expenseColumns.size
        val headerH = 20f
        val rowH = 17f
        val maxDataRows = 7

        val totalTableH = headerH + (rowH * maxDataRows) + rowH // includes total row

        // Draw outer box
        canvas.drawRect(marginX, currentY, marginX + gridWidth, currentY + totalTableH, strokePaint)
        // Draw header bottom line
        canvas.drawLine(marginX, currentY + headerH, marginX + gridWidth, currentY + headerH, strokePaint)

        // Draw column headers and vertical dividers
        expenseColumns.forEachIndexed { i, colTitle ->
            val colX = marginX + (i * gridColWidth)
            if (i > 0) {
                canvas.drawLine(colX, currentY, colX, currentY + totalTableH, strokePaint)
            }
            val titleW = boldTextPaint.measureText(colTitle)
            canvas.drawText(colTitle, colX + (gridColWidth - titleW) / 2f, currentY + 14f, boldTextPaint)
        }

        // Draw grid horizontal row lines
        for (r in 1..maxDataRows) {
            val y = currentY + headerH + (r * rowH)
            canvas.drawLine(marginX, y, marginX + gridWidth, y, strokePaint)
        }

        // Populate Expense Items
        // Group items by category to place inside the columns
        items.take(maxDataRows).forEachIndexed { r, item ->
            val rowY = currentY + headerH + (r * rowH) + 13f

            // Determine which column index this item belongs to
            val colIndex = when (item.category.trim()) {
                "اوراق سفر" -> 1
                "نقل" -> 2
                "إقامة", "اقامة" -> 3
                "عمالة" -> 4
                "خامات" -> 5
                "نثريات" -> 6
                "بنزين" -> 7
                "انتقالات" -> 8
                "إعاشة", "اعاشه", "اعاشة" -> 9
                "تأجير سيارة", "تاجير سيارة" -> 10
                else -> 6 // default to نثريات
            }

            val colX = marginX + (colIndex * gridColWidth)
            val amtText = df.format(item.amount)

            // If description fits (e.g. خامات column or text label)
            if (item.category == "خامات" || item.description.length <= 15) {
                val descPaint = Paint(textPaint).apply { textSize = 8.5f }
                canvas.drawText(item.description.take(16), colX + 4f, rowY - 4f, descPaint)
                canvas.drawText(amtText, colX + 4f, rowY + 5f, boldTextPaint)
            } else {
                val amtW = textPaint.measureText(amtText)
                canvas.drawText(amtText, colX + (gridColWidth - amtW) / 2f, rowY, textPaint)
            }
        }

        // Bottom Totals Row (with double border)
        val totalsY = currentY + headerH + (maxDataRows * rowH)
        canvas.drawLine(marginX, totalsY, marginX + gridWidth, totalsY, doubleBorderPaint)
        canvas.drawLine(marginX, totalsY + 2f, marginX + gridWidth, totalsY + 2f, doubleBorderPaint)

        // Draw Grand Total in مجموع (Index 0)
        val totalText = df.format(clearance.totalExpenses)
        val totalW = boldTextPaint.measureText(totalText)
        canvas.drawText(totalText, marginX + (gridColWidth - totalW) / 2f, totalsY + 14f, boldTextPaint)

        // Draw Column totals
        val categoryTotals = clearance.categoryBreakdown
        expenseColumns.forEachIndexed { i, colTitle ->
            if (i > 0) {
                val catSum = categoryTotals[colTitle]
                    ?: categoryTotals[colTitle.replace("ا", "إ")]
                    ?: categoryTotals[colTitle.replace("إ", "ا")]
                    ?: 0.0

                if (catSum > 0) {
                    val colX = marginX + (i * gridColWidth)
                    val sumText = df.format(catSum)
                    val sumW = boldTextPaint.measureText(sumText)
                    canvas.drawText(sumText, colX + (gridColWidth - sumW) / 2f, totalsY + 14f, boldTextPaint)
                }
            }
        }

        // 7. Footer: GROUP & SITE BOOK & LAYOUT
        currentY += totalTableH + 16f

        // Left box (Empty code box like in image)
        canvas.drawRect(marginX, currentY, marginX + 65f, currentY + 36f, strokePaint)

        // GROUP Table
        val groupLabelX = marginX + 72f
        canvas.drawText("GROUP:", groupLabelX, currentY + 14f, boldTextPaint)

        val groupTableX = marginX + 115f
        val groupTableW = 280f
        val groupRowH = 16f
        val pdfMembersList = sheet.teamMembers
            .split("،", ",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val totalMemberRows = maxOf(2, pdfMembersList.size)
        val groupRows = totalMemberRows + 1

        canvas.drawRect(groupTableX, currentY, groupTableX + groupTableW, currentY + (groupRowH * (groupRows + 1)), strokePaint)
        canvas.drawLine(groupTableX + 60f, currentY, groupTableX + 60f, currentY + (groupRowH * (groupRows + 1)), strokePaint)

        // Header of Group Table
        canvas.drawLine(groupTableX, currentY + groupRowH, groupTableX + groupTableW, currentY + groupRowH, strokePaint)
        canvas.drawText("NO.", groupTableX + 18f, currentY + 12f, boldTextPaint)
        canvas.drawText("NAME", groupTableX + 130f, currentY + 12f, boldTextPaint)

        // Member 1 (Always Leader / Team Leader)
        val leader = if (sheet.teamLeader.isNotBlank()) sheet.teamLeader else "التيم ليدر"
        canvas.drawText("1", groupTableX + 26f, currentY + groupRowH + 12f, textPaint)
        canvas.drawText(leader, groupTableX + 90f, currentY + groupRowH + 12f, boldTextPaint)

        // Members 2, 3, 4, 5, 6, 7...
        for (i in 0 until totalMemberRows) {
            val rowY = currentY + (groupRowH * (i + 2))
            canvas.drawLine(groupTableX, rowY, groupTableX + groupTableW, rowY, strokePaint)
            canvas.drawText("${i + 2}", groupTableX + 26f, rowY + 12f, textPaint)
            val memberName = pdfMembersList.getOrNull(i) ?: ""
            if (memberName.isNotBlank()) {
                canvas.drawText(memberName, groupTableX + 80f, rowY + 12f, textPaint)
            }
        }

        // Right side of Footer: SITE BOOK & LAY OUT
        val siteBookX = groupTableX + groupTableW + 30f
        canvas.drawText("SITE BOOK:  ${sheet.siteBook}", siteBookX, currentY + 14f, boldTextPaint)
        canvas.drawText("LAY OUT:    ${sheet.layoutRef}", siteBookX, currentY + 45f, boldTextPaint)

        // 8. Official Manager Approval Stamp (if approved)
        if (sheet.status == "APPROVED") {
            drawApprovalStampOnPdf(
                canvas = canvas,
                x = pageWidth - marginX - 180f,
                y = currentY - 5f,
                managerName = sheet.approvedBy ?: "التيم ليدر",
                approvalDate = sheet.approvalDate ?: "معتمد",
                signatureData = sheet.signatureData
            )
        }

        pdfDocument.finishPage(page)

        // Save PDF to Documents / Cache
        val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Systel_Clearances").apply {
            if (!exists()) mkdirs()
        }
        val sanitizedSite = sheet.siteName.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
        val fileName = "Clearance_${sheet.sheetNumber}_${sanitizedSite}.pdf"
        val outputFile = File(outputDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun drawSystelLogo(canvas: Canvas, x: Float, y: Float, width: Float, height: Float) {
        val bgPaint = Paint().apply {
            color = Color.parseColor("#4A90E2")
            style = Paint.Style.STROKE
            strokeWidth = 3.5f
            isAntiAlias = true
        }

        val fillOvalPaint = Paint().apply {
            color = Color.parseColor("#EBF3FC")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val boltPaint = Paint().apply {
            color = Color.parseColor("#357ABD")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        // Draw Outer Oval Ring
        val rect = RectF(x, y, x + width, y + height)
        canvas.drawOval(rect, fillOvalPaint)
        canvas.drawOval(rect, bgPaint)

        // Inner dynamic lightning / telecom radio bolt
        val boltPath = Path().apply {
            moveTo(x + width * 0.48f, y + height * 0.15f)
            lineTo(x + width * 0.35f, y + height * 0.52f)
            lineTo(x + width * 0.52f, y + height * 0.52f)
            lineTo(x + width * 0.42f, y + height * 0.85f)
            lineTo(x + width * 0.65f, y + height * 0.45f)
            lineTo(x + width * 0.48f, y + height * 0.45f)
            close()
        }
        canvas.drawPath(boltPath, boltPaint)

        // Subtitle "SYSTEL"
        val textPaint = Paint().apply {
            color = Color.parseColor("#1B365D")
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val tw = textPaint.measureText("SYSTEL")
        canvas.drawText("SYSTEL", x + (width - tw) / 2f, y + height + 9f, textPaint)
    }

    private fun drawApprovalStampOnPdf(
        canvas: Canvas,
        x: Float,
        y: Float,
        managerName: String,
        approvalDate: String,
        signatureData: String? = null
    ) {
        val stampWidth = 170f
        val stampHeight = 55f

        val borderPaint = Paint().apply {
            color = Color.parseColor("#16A34A")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        val fillPaint = Paint().apply {
            color = Color.parseColor("#F0FDF4")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#16A34A")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subTextPaint = Paint().apply {
            color = Color.parseColor("#14532D")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val datePaint = Paint().apply {
            color = Color.parseColor("#4B5563")
            textSize = 7.5f
            isAntiAlias = true
        }

        val rect = RectF(x, y, x + stampWidth, y + stampHeight)
        canvas.drawRoundRect(rect, 8f, 8f, fillPaint)
        canvas.drawRoundRect(rect, 8f, 8f, borderPaint)

        canvas.drawText("معتمد رسمياً - شركة سيستل", x + 12f, y + 16f, textPaint)
        canvas.drawText("اعتماد التيم ليدر: $managerName", x + 12f, y + 30f, subTextPaint)

        // Draw Team Leader signature if available
        if (!signatureData.isNullOrBlank()) {
            drawSignatureOnAndroidCanvas(canvas, signatureData, x + stampWidth - 65f, y + 18f, 55f, 24f)
        }

        canvas.drawText("بتاريخ: $approvalDate", x + 12f, y + 46f, datePaint)
    }

    private fun drawSignatureOnAndroidCanvas(
        canvas: Canvas,
        signatureData: String,
        x: Float,
        y: Float,
        w: Float,
        h: Float
    ) {
        val sigPaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            style = Paint.Style.STROKE
            strokeWidth = 1.6f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
        val strokes = signatureData.split(";")
        for (sStr in strokes) {
            val points = sStr.split(",")
            if (points.size > 1) {
                val path = android.graphics.Path()
                var isFirst = true
                for (pStr in points) {
                    val parts = pStr.split(":")
                    if (parts.size == 2) {
                        val nx = parts[0].toFloatOrNull() ?: continue
                        val ny = parts[1].toFloatOrNull() ?: continue
                        val px = x + (nx * w)
                        val py = y + (ny * h)
                        if (isFirst) {
                            path.moveTo(px, py)
                            isFirst = false
                        } else {
                            path.lineTo(px, py)
                        }
                    }
                }
                if (!isFirst) {
                    canvas.drawPath(path, sigPaint)
                }
            }
        }
    }

    fun openOrSharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to chooser / share
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "تحميل وفتح ملف التصفية PDF"))
        }
    }
}

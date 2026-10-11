package com.example.ui.screens

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BalanceType
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ExpenseItemEntity
import android.widget.Toast
import com.example.ui.components.ClearanceStatusBadge
import com.example.ui.components.ClearanceStatusBanner
import com.example.ui.components.OfficialSystelSheetView
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.components.SignaturePad
import com.example.ui.components.EditTeamMembersDialog
import com.example.ui.components.SystelApprovalStamp
import com.example.ui.components.SystelLogo
import com.example.ui.components.TelecomSiteIconBadge
import com.example.util.SystelPdfGenerator
import com.example.ui.theme.SystelAccentCyan
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelBlueLight
import com.example.ui.theme.SystelGoldApproval
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.ClearanceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClearanceDetailScreen(
    viewModel: ClearanceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val clearanceWithItems by viewModel.selectedClearance.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ExpenseItemEntity?>(null) }
    var showApproveConfirmDialog by remember { mutableStateOf(false) }
    var showRevokeConfirmDialog by remember { mutableStateOf(false) }
    var showRejectConfirmDialog by remember { mutableStateOf(false) }
    var showEditAdvanceDialog by remember { mutableStateOf(false) }
    var showDeleteSheetDialog by remember { mutableStateOf(false) }
    var showRegisterTeamDialog by remember { mutableStateOf(false) }
    var showEditMembersDialog by remember { mutableStateOf(false) }
    var currentSignatureData by remember { mutableStateOf("") }

    val prefs = remember { context.getSharedPreferences("systel_prefs", android.content.Context.MODE_PRIVATE) }

    var managerApprovalNotes by remember { mutableStateOf("تمت المراجعة والاعتماد والمطابقة بالسجلات المالية") }
    var managerRejectionReason by remember { mutableStateOf("يرجى مراجعة وتعديل بنود وفواتير التصفية وإعادة الإرسال") }

    val decimalFormat = remember { DecimalFormat("#,##0.##") }

    if (clearanceWithItems == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("لم يتم العثور على بيانات التصفية", fontSize = 16.sp)
        }
        return
    }

    val clearance = clearanceWithItems!!
    val sheet = clearance.sheet
    val isApproved = sheet.status == "APPROVED"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SystelLogo(size = 32.dp, showArabicSubtitle = false, isDarkBackground = true)
                        Column {
                            Text(
                                text = "تصفية: ${sheet.siteName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = sheet.sheetNumber,
                                fontSize = 11.sp,
                                color = SystelAccentCyan
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // PDF Download Button (Requires Manager Approval for Team Users)
                    val canDownloadPdf = isApproved || userSession.isManager
                    if (canDownloadPdf) {
                        IconButton(
                            onClick = {
                                try {
                                    val file = SystelPdfGenerator.generateClearancePdf(context, clearance)
                                    Toast.makeText(context, "تم إنشاء ملف PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                                    SystelPdfGenerator.openOrSharePdf(context, file)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطأ في إنشاء PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("top_download_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "تحميل PDF",
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "عفواً، لا يمكن لفريق العمل تحميل استمارة التصفية PDF إلا بعد موافقة واعتماد التيم ليدر.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            modifier = Modifier.testTag("top_download_locked_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "التحميل مقفل لحين اعتماد التيم ليدر",
                                tint = Color(0xFFFDE047)
                            )
                        }
                    }

                    // Share report button
                    IconButton(
                        onClick = {
                            val shareText = buildShareReport(clearance, decimalFormat)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة تصفية الموقع"))
                        },
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة التقرير",
                            tint = Color.White
                        )
                    }

                    // Delete sheet
                    IconButton(
                        onClick = { showDeleteSheetDialog = true },
                        modifier = Modifier.testTag("delete_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف التصفية",
                            tint = Color(0xFFFCA5A5)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SystelNavyPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Role Switcher (Shown ONLY to Manager)
            if (userSession.isManager) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        RoleSwitcherBar(
                            userSession = userSession,
                            allTeams = allTeams,
                            onSelectManager = { viewModel.loginAsManager("م. ياسر عواد") },
                            onSelectTeam = { viewModel.loginAsTeam(it) },
                            onRegisterTeamClick = { showRegisterTeamDialog = true },
                            onLogout = {
                                viewModel.logout()
                                onBack()
                            }
                        )
                    }
                }
            }

            // 2. Prominent Clearance Status Indicator Banner (Pending / Approved / Rejected)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ClearanceStatusBanner(
                        statusCode = sheet.status,
                        approvedBy = sheet.approvedBy,
                        approvalDate = sheet.approvalDate,
                        approvalNotes = sheet.approvalNotes
                    )
                }
            }

            // 3. Site Information Card (from attached Excel sheet format)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SystelNavyPrimary
                                ) {
                                    Text(
                                        text = sheet.department,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SystelBlueLight
                                ) {
                                    Text(
                                        text = sheet.activityType,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SystelNavyPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Status Indicator Badge
                            ClearanceStatusBadge(statusCode = sheet.status)
                        }

                        // Site & Region details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TelecomSiteIconBadge(
                                    activityType = sheet.activityType,
                                    siteName = sheet.siteName,
                                    size = 46.dp
                                )
                                Column {
                                    Text("اسم الموقع (Site Name):", fontSize = 11.sp, color = Color.Gray)
                                    Text(sheet.siteName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("المنطقة (Region):", fontSize = 11.sp, color = Color.Gray)
                                Text(sheet.region, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Dates and Times from user CSV
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("بدء النشاط (Start):", fontSize = 10.sp, color = Color.Gray)
                                Text("${sheet.startDate} - ${sheet.startTime}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("انتهاء النشاط (End):", fontSize = 10.sp, color = Color.Gray)
                                Text("${sheet.endDate} - ${sheet.endTime}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Team & Group Info
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("قائد الفريق: ${sheet.teamLeader}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SystelNavyDark)
                            Text("SITE BOOK: ${sheet.siteBook} • LAYOUT: ${sheet.layoutRef}", fontSize = 11.sp, color = Color.Gray)
                        }

                        // Team Members Row (Supports unlimited members, more than 4)
                        val memberList = sheet.teamMembers.split("،", ",").map { it.trim() }.filter { it.isNotBlank() }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = SystelNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (memberList.isNotEmpty()) "أعضاء الفريق (${memberList.size}): ${sheet.teamMembers}" else "أعضاء الفريق: لم يتم إضافة أعضاء بعد",
                                        fontSize = 11.5.sp,
                                        color = SystelNavyDark,
                                        maxLines = 2
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SystelNavyPrimary.copy(alpha = 0.12f),
                                    modifier = Modifier
                                        .clickable { showEditMembersDialog = true }
                                        .testTag("manage_team_members_button")
                                ) {
                                    Text(
                                        text = "إدارة الأعضاء (+)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelNavyPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. FINANCIAL CLEARANCE CALCULATION CARD ("حساب التصفية في الإجمالي")
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("clearance_calculation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (clearance.balanceStatus) {
                            BalanceType.SURPLUS_TO_COMPANY -> Color(0xFFF0FDF4)
                            BalanceType.DEFICIT_TO_EMPLOYEE -> Color(0xFFFFFBEB)
                            BalanceType.SETTLED -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        when (clearance.balanceStatus) {
                            BalanceType.SURPLUS_TO_COMPANY -> SystelSuccessGreen
                            BalanceType.DEFICIT_TO_EMPLOYEE -> SystelGoldApproval
                            BalanceType.SETTLED -> Color.Gray
                        }
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SystelNavyPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "حساب التصفية في الإجمالي",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SystelNavyDark
                                )
                            }

                            // Quick edit button for Advance
                            IconButton(
                                onClick = { showEditAdvanceDialog = true },
                                modifier = Modifier.testTag("edit_advance_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل العهدة",
                                    tint = SystelNavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Advance vs Total Expenses Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Box 1: Custody / Advance
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("العهدة المستلمة", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(sheet.advanceAmount)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelNavyPrimary
                                    )
                                }
                            }

                            // Box 2: Total Spent (الإجمالي)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("إجمالي المنصرف", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(clearance.totalExpenses)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelAccentRed
                                    )
                                }
                            }
                        }

                        // Settlement Result Banner (نتيجة التصفية النهائية)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (clearance.balanceStatus) {
                                BalanceType.SURPLUS_TO_COMPANY -> SystelSuccessGreen
                                BalanceType.DEFICIT_TO_EMPLOYEE -> SystelGoldApproval
                                BalanceType.SETTLED -> Color(0xFF334155)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "صافي نتيجة التصفية المالية:",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = when (clearance.balanceStatus) {
                                            BalanceType.SURPLUS_TO_COMPANY -> "متبقي لشركة سيستل (إيداع للخزينة)"
                                            BalanceType.DEFICIT_TO_EMPLOYEE -> "مستحق الصرف للمهندس (عجز عهدة)"
                                            BalanceType.SETTLED -> "التصفية مطابقة بالكامل (0 ج.م)"
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = "${decimalFormat.format(kotlin.math.abs(clearance.balance))} ج.م",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        // Category Breakdown Chips (تفصيل المصروفات حسب الأبواب)
                        if (clearance.categoryBreakdown.isNotEmpty()) {
                            Text(
                                text = "تفصيل إجمالي المصروفات حسب الباب:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SystelNavyDark
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                clearance.categoryBreakdown.forEach { (cat, sum) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(cat, fontSize = 11.5.sp, color = SystelNavyDark, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "${decimalFormat.format(sum)} ج.م",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SystelAccentRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. MANAGER APPROVAL SECTION ("ويبقي فيه زار ابروف للمدير وميبقاش ظاهر لاي يوزر غيري انا")
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("manager_approval_section"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (userSession.isManager) Color(0xFFF8FAFC) else Color(0xFFF1F5F9)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isApproved) SystelSuccessGreen else if (userSession.isManager) Color(0xFF93C5FD) else Color(0xFFCBD5E1)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Icon(
                                    imageVector = if (userSession.isManager) Icons.Default.Verified else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (userSession.isManager) SystelNavyPrimary else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "صلاحية اعتماد التيم ليدر (Team Leader Approval)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystelNavyDark
                                )
                            }

                            if (userSession.isManager) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "متاح للتيم ليدر",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // If user is APPROVED: Show digital stamp
                        if (isApproved) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                SystelApprovalStamp(
                                    managerName = sheet.approvedBy ?: "التيم ليدر",
                                    approvalDate = sheet.approvalDate ?: "معتمد",
                                    signatureData = sheet.signatureData
                                )
                            }

                            if (!sheet.approvalNotes.isNullOrBlank()) {
                                Text(
                                    text = "ملاحظات للتيم ليدر: ${sheet.approvalNotes}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }

                            // If Manager: allow revoking / reopening approval
                            if (userSession.isManager) {
                                OutlinedButton(
                                    onClick = { showRevokeConfirmDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("revoke_approval_button"),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SystelAccentRed
                                    )
                                ) {
                                    Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إلغاء الاعتماد وإعادة فتح التعديل (التيم ليدر فقط)")
                                }
                            }
                        } else if (sheet.status == "REJECTED") {
                            // If REJECTED: Show rejection alert
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEE2E2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = SystelAccentRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "تم رفض التصفية أو طلب تعديل البنود",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                    }
                                    if (!sheet.approvalNotes.isNullOrBlank()) {
                                        Text(
                                            text = "سبب الرفض: ${sheet.approvalNotes}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF7F1D1D)
                                        )
                                    }
                                    Text(
                                        text = "المسؤول: ${sheet.approvedBy ?: "التيم ليدر"} • ${sheet.approvalDate ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF991B1B).copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // If Manager: allow reopening rejected sheet
                            if (userSession.isManager) {
                                OutlinedButton(
                                    onClick = { showRevokeConfirmDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reopen_rejected_button"),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SystelNavyPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إلغاء الرفض وإعادة فتح التصفية للمراجعة (التيم ليدر فقط)")
                                }
                            }
                        } else {
                            // If PENDING:
                            // CRITICAL RULE CHECK: Show Approve & Reject buttons ONLY if user is Team Leader!
                            if (userSession.isManager) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "التصفية جاهزة للفحص والمطابقة. بصفتك التيم ليدر، يمكنك اتخاذ القرار:",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // THE MANAGER APPROVE BUTTON
                                        Button(
                                            onClick = { showApproveConfirmDialog = true },
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .testTag("manager_approve_button"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SystelSuccessGreen
                                            )
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "اعتماد التصفية (Approve)",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // THE MANAGER REJECT BUTTON
                                        OutlinedButton(
                                            onClick = { showRejectConfirmDialog = true },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("manager_reject_button"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = SystelAccentRed
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SystelAccentRed)
                                        ) {
                                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "رفض (Reject)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            } else {
                                // NON-MANAGER USER: The button is COMPLETELY HIDDEN!
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8D7DA).copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C6CB)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF721C24),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "زر اعتماد ورفض التصفية مقتصر حصرياً على التيم ليدر ومخفي عن بقية المستخدمين.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF721C24)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4.5. OFFICIAL SYSTEL CLEARANCE SHEET (الشكل النهائي للتصفية بعد الاعتماد مع زر التحميل PDF)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OfficialSystelSheetView(
                        clearance = clearance,
                        isManager = userSession.isManager,
                        onDownloadPdf = {
                            try {
                                val file = SystelPdfGenerator.generateClearancePdf(context, clearance)
                                Toast.makeText(context, "تم حفظ ملف PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                                SystelPdfGenerator.openOrSharePdf(context, file)
                            } catch (e: Exception) {
                                Toast.makeText(context, "خطأ في إنشاء ملف PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // 5. Expense Items Header & Add Item Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "بنود المصروفات المنصرفة (${clearance.items.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = { showAddItemDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                        modifier = Modifier.testTag("add_expense_item_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة بند صرف", fontSize = 13.sp)
                    }
                }
            }

            // 6. Expense Items List
            if (clearance.items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد بنود صرف مضافة بعد. اضغط على 'إضافة بند صرف' لتسجيل المصاريف.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(clearance.items, key = { it.id }) { item ->
                    ExpenseItemRow(
                        item = item,
                        decimalFormat = decimalFormat,
                        onEdit = { itemToEdit = item },
                        onDelete = { viewModel.deleteExpenseItem(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Dialog 1: Add new item
    if (showAddItemDialog) {
        EditExpenseDialog(
            initialItem = null,
            onDismiss = { showAddItemDialog = false },
            onConfirm = { cat, desc, amt, inv ->
                viewModel.addExpenseItem(sheet.id, cat, desc, amt, inv)
                showAddItemDialog = false
            }
        )
    }

    // Dialog 2: Edit existing item
    if (itemToEdit != null) {
        EditExpenseDialog(
            initialItem = itemToEdit,
            onDismiss = { itemToEdit = null },
            onConfirm = { cat, desc, amt, inv ->
                viewModel.updateExpenseItem(
                    itemToEdit!!.copy(
                        category = cat,
                        description = desc,
                        amount = amt,
                        invoiceNumber = inv
                    )
                )
                itemToEdit = null
            }
        )
    }

    // Dialog 3: Manager Approval Confirmation
    if (showApproveConfirmDialog) {
        AlertDialog(
            modifier = Modifier
                .widthIn(max = if (isLandscape) 640.dp else 480.dp)
                .fillMaxWidth(if (isLandscape) 0.88f else 0.96f),
            onDismissRequest = { showApproveConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SystelSuccessGreen)
                    Text("تأكيد اعتماد التصفية", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (isLandscape) 280.dp else 520.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("الموقع: ${sheet.siteName}")
                    Text("إجمالي المنصرف: ${decimalFormat.format(clearance.totalExpenses)} ج.م")
                    Text("العهدة المستلمة: ${decimalFormat.format(sheet.advanceAmount)} ج.م")
                    Text(
                        text = "صافي التصفية: ${decimalFormat.format(kotlin.math.abs(clearance.balance))} ج.م (${clearance.balanceStatus.labelAr})",
                        fontWeight = FontWeight.Bold,
                        color = SystelNavyPrimary
                    )

                    OutlinedTextField(
                        value = managerApprovalNotes,
                        onValueChange = { managerApprovalNotes = it },
                        label = { Text("ملاحظات للتيم ليدر") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("approval_notes_input")
                    )

                    // Signature Pad for Team Leader
                    SignaturePad(
                        savedDefaultSignature = prefs.getString("saved_leader_signature", null),
                        onSignatureChanged = { data ->
                            currentSignatureData = data
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalSig = currentSignatureData.ifBlank { prefs.getString("saved_leader_signature", null) }
                        if (!finalSig.isNullOrBlank()) {
                            prefs.edit().putString("saved_leader_signature", finalSig).apply()
                        }
                        viewModel.approveClearance(sheet.id, managerApprovalNotes, finalSig)
                        showApproveConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen),
                    modifier = Modifier.testTag("confirm_approve_button")
                ) {
                    Text("اعتماد رسمي وتوقيع")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showApproveConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog 4: Revoke approval confirmation
    if (showRevokeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRevokeConfirmDialog = false },
            title = { Text("إعادة فتح التصفية للمراجعة", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد إعادة التصفية إلى حالة 'قيد المراجعة' والسماح بتعديل البنود والمصروفات؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.revokeApproval(sheet.id)
                        showRevokeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary)
                ) {
                    Text("نعم، فتح للمراجعة")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRevokeConfirmDialog = false }) {
                    Text("تراجع")
                }
            }
        )
    }

    // Dialog 4.5: Manager Rejection Dialog
    if (showRejectConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRejectConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = SystelAccentRed)
                    Text("رفض التصفية / طلب تعديل", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("أدخل سبب الرفض أو التعديلات المطلوبة (ملاحظات للتيم ليدر):")
                    OutlinedTextField(
                        value = managerRejectionReason,
                        onValueChange = { managerRejectionReason = it },
                        label = { Text("سبب الرفض / ملاحظات للتيم ليدر") },
                        placeholder = { Text("مثال: يرجى إرفاق إيصال بند العمالة...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rejection_reason_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectClearance(sheet.id, managerRejectionReason)
                        showRejectConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed),
                    modifier = Modifier.testTag("confirm_reject_button")
                ) {
                    Text("تأكيد الرفض")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRejectConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog 5: Quick Edit Advance Amount
    if (showEditAdvanceDialog) {
        var newAdvanceText by remember { mutableStateOf(sheet.advanceAmount.toString()) }
        AlertDialog(
            onDismissRequest = { showEditAdvanceDialog = false },
            title = { Text("تعديل مبلغ العهدة المستلمة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("أدخل مبلغ العهدة المستلمة من الشركة لحساب التصفية تلقائياً:")
                    OutlinedTextField(
                        value = newAdvanceText,
                        onValueChange = { newAdvanceText = it },
                        label = { Text("مبلغ العهدة (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_advance_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val adv = newAdvanceText.toDoubleOrNull()
                        if (adv != null && adv >= 0) {
                            viewModel.updateAdvanceAmount(sheet.id, adv)
                            showEditAdvanceDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_advance_button")
                ) {
                    Text("تحديث")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditAdvanceDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog 6: Delete Sheet
    if (showDeleteSheetDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSheetDialog = false },
            title = { Text("حذف استمارة التصفية", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف تصفية موقع '${sheet.siteName}' وجميع بنود المصروفات التابعة لها؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteClearance(sheet)
                        showDeleteSheetDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed)
                ) {
                    Text("حذف نهائي")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteSheetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showRegisterTeamDialog) {
        RegisterTeamDialog(
            viewModel = viewModel,
            onDismiss = { showRegisterTeamDialog = false },
            onConfirm = { teamName, leaderName, phone, email, department, isLeader ->
                viewModel.registerNewTeam(teamName, leaderName, phone, email, department, isLeader)
                showRegisterTeamDialog = false
            }
        )
    }
}

@Composable
fun ExpenseItemRow(
    item: ExpenseItemEntity,
    decimalFormat: DecimalFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("expense_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (item.category) {
                        "عمالة" -> Color(0xFFEFF6FF)
                        "نثريات" -> Color(0xFFFEF3C7)
                        "بنزين" -> Color(0xFFFEE2E2)
                        "إقامة" -> Color(0xFFEDE9FE)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = item.category,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.category) {
                            "عمالة" -> Color(0xFF1D4ED8)
                            "نثريات" -> Color(0xFFB45309)
                            "بنزين" -> Color(0xFFB91C1C)
                            "إقامة" -> Color(0xFF6D28D9)
                            else -> SystelNavyPrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Column {
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (item.invoiceNumber.isNotBlank()) {
                            Text(
                                text = "إيصال: ${item.invoiceNumber}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        if (item.date.isNotBlank()) {
                            Text(
                                text = "• ${item.date}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${decimalFormat.format(item.amount)} ج.م",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystelNavyPrimary
                )

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun buildShareReport(
    clearance: com.example.data.model.ClearanceWithItems,
    df: DecimalFormat
): String {
    val sheet = clearance.sheet
    val sb = StringBuilder()
    sb.appendLine("══════════════════════════════════")
    sb.appendLine("       شركة سيستل للاتصالات       ")
    sb.appendLine("       SYSTEL TELECOM EGYPT       ")
    sb.appendLine("    استمارة تصفية عهدة ومصاريف موقع   ")
    sb.appendLine("══════════════════════════════════")
    sb.appendLine("رقم التصفية: ${sheet.sheetNumber}")
    sb.appendLine("اسم الموقع: ${sheet.siteName}")
    sb.appendLine("القسم: ${sheet.department}")
    sb.appendLine("المنطقة: ${sheet.region}")
    sb.appendLine("النشاط: ${sheet.activityType}")
    sb.appendLine("الفترة: ${sheet.startDate} إلى ${sheet.endDate}")
    sb.appendLine("المشرف / قائد التيم: ${sheet.teamLeader}")
    sb.appendLine("──────────────────────────────────")
    sb.appendLine("بنود المصروفات المنصرفة:")
    clearance.items.forEachIndexed { i, item ->
        val inv = if (item.invoiceNumber.isNotBlank()) " [إيصال: ${item.invoiceNumber}]" else ""
        sb.appendLine("${i + 1}. [${item.category}] ${item.description}: ${df.format(item.amount)} ج.م$inv")
    }
    sb.appendLine("──────────────────────────────────")
    sb.appendLine("حساب التصفية في الإجمالي:")
    sb.appendLine("• إجمالي المصروفات المنصرفة: ${df.format(clearance.totalExpenses)} ج.م")
    sb.appendLine("• العهدة المستلمة: ${df.format(sheet.advanceAmount)} ج.م")
    sb.appendLine("• نتيجة التصفية: ${clearance.balanceStatus.labelAr} بمبلغ ${df.format(kotlin.math.abs(clearance.balance))} ج.م")
    sb.appendLine("──────────────────────────────────")
    when (sheet.status) {
        "APPROVED" -> {
            sb.appendLine("حالة الاعتماد: معتمد رسمياً ✅")
            sb.appendLine("معتمد من: ${sheet.approvedBy}")
            sb.appendLine("تاريخ الاعتماد: ${sheet.approvalDate}")
            if (!sheet.approvalNotes.isNullOrBlank()) {
                sb.appendLine("ملاحظات للتيم ليدر: ${sheet.approvalNotes}")
            }
        }
        "REJECTED" -> {
            sb.appendLine("حالة الاعتماد: مرفوض / يتطلب تعديل ❌")
            sb.appendLine("بواسطة: ${sheet.approvedBy ?: "التيم ليدر"}")
            sb.appendLine("تاريخ القرار: ${sheet.approvalDate}")
            if (!sheet.approvalNotes.isNullOrBlank()) {
                sb.appendLine("سبب الرفض: ${sheet.approvalNotes}")
            }
        }
        else -> {
            sb.appendLine("حالة الاعتماد: قيد المراجعة في انتظار اعتماد التيم ليدر ⏳")
        }
    }
    sb.appendLine("══════════════════════════════════")
    return sb.toString()
}

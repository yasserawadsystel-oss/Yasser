package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BalanceType
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ClearanceWithItems
import com.example.data.model.SystelConstants
import com.example.data.model.TeamDashboardStats
import com.example.ui.components.PendingAccountsDialog
import com.example.ui.components.TeamLeaderNotificationDialog
import com.example.data.model.TeamEntity
import com.example.ui.components.ClearanceStatusBadge
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.components.SystelLogo
import com.example.ui.components.TelecomSiteIconBadge
import com.example.ui.theme.SystelAccentCyan
import com.example.ui.theme.SystelAccentRed
import com.example.ui.theme.SystelBlueLight
import com.example.ui.theme.SystelGoldApproval
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen
import com.example.ui.viewmodel.ClearanceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ClearanceViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val clearances by viewModel.filteredClearances.collectAsState()
    val allClearances by viewModel.allClearances.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()
    val teamsStats by viewModel.teamsDashboardStats.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val pendingAccounts by viewModel.pendingAccounts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDept by viewModel.departmentFilter.collectAsState()
    val selectedStatus by viewModel.statusFilter.collectAsState()
    val selectedTeamFilter by viewModel.teamFilter.collectAsState()

    var showNewSheetDialog by remember { mutableStateOf(false) }
    var showRegisterTeamDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showPendingAccountsDialog by remember { mutableStateOf(false) }

    val pendingClearances = remember(allClearances) {
        allClearances.filter { it.sheet.status == "PENDING" }
    }

    val decimalFormat = remember { DecimalFormat("#,##0.##") }

    // Summary calculations (Respects active user scope)
    val displayedClearances = clearances
    val totalAdvance = displayedClearances.sumOf { it.sheet.advanceAmount }
    val totalExpenses = displayedClearances.sumOf { it.totalExpenses }
    val totalBalance = totalAdvance - totalExpenses

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewSheetDialog = true },
                containerColor = SystelAccentRed,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_new_sheet_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إنشاء تصفية")
                    Text("تصفية موقع جديدة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .testTag("dashboard_list")
        ) {
            // 1. Corporate Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SystelNavyDark, SystelNavyPrimary)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SystelLogo(
                                size = 42.dp,
                                isDarkBackground = true,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Notification Bell for Team Leader
                                if (userSession.isManager) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (pendingClearances.isNotEmpty()) SystelAccentRed.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (pendingClearances.isNotEmpty()) Color(0xFFF87171) else Color.White.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier
                                            .clickable { showNotificationsDialog = true }
                                            .testTag("team_leader_notification_bell")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (pendingClearances.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                                contentDescription = "الإشعارات",
                                                tint = if (pendingClearances.isNotEmpty()) Color(0xFFFECACA) else Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (pendingClearances.isNotEmpty()) "${pendingClearances.size} معلق" else "الإشعارات",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }

                                    // Pending User Accounts Pill (Exclusive to Eng. Yasser Awad)
                                    if (viewModel.isSuperAdminUser() && pendingAccounts.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFF59E0B).copy(alpha = 0.28f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                Color(0xFFFBBF24)
                                            ),
                                            modifier = Modifier
                                                .clickable { showPendingAccountsDialog = true }
                                                .testTag("team_leader_pending_users_badge")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.HourglassEmpty,
                                                    contentDescription = "طلبات تفعيل الحسابات",
                                                    tint = Color(0xFFFDE68A),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Text(
                                                    text = "${pendingAccounts.size} حسابات جديدة",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }

                                // Network Status Pill
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(SystelSuccessGreen)
                                        )
                                        Text(
                                            text = "مشاريع الاتصالات",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Role Switcher Bar (Manager vs Field Teams)
                        RoleSwitcherBar(
                            userSession = userSession,
                            allTeams = allTeams,
                            onSelectManager = { viewModel.loginAsManager("م. ياسر عواد") },
                            onSelectTeam = { viewModel.loginAsTeam(it) },
                            onRegisterTeamClick = { showRegisterTeamDialog = true },
                            onLogout = { viewModel.logout() },
                            onResetAllData = { viewModel.resetAllDataToCleanState() }
                        )
                    }
                }
            }

            // 3. SPECIALIZED DASHBOARD VIEW: Manager Multi-Team Dashboard VS Single-Team Dashboard
            if (userSession.isManager) {
                // ==================== MANAGER DASHBOARD ====================
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // In-App Notification Alert Banner for Team Leader
                        if (pendingClearances.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showNotificationsDialog = true }
                                    .testTag("pending_clearances_notification_banner")
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
                                            shape = CircleShape,
                                            color = SystelAccentRed,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.NotificationsActive,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "إشعار للتيم ليدر: يوجد ${pendingClearances.size} طلبات تصفية بانتظار موافقتك!",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFF991B1B)
                                            )
                                            Text(
                                                text = "اضغط هنا لفحص ومراجعة الاستمارات والاعتماد بالتوقيع والختم",
                                                fontSize = 11.sp,
                                                color = Color(0xFFB91C1C)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = { showNotificationsDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = SystelAccentRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("view_notifications_btn")
                                    ) {
                                        Text("فحص (${pendingClearances.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Manager Section Header
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
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = SystelNavyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "متابعة فرق العمل والمهندسين",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystelNavyDark
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SystelBlueLight
                            ) {
                                Text(
                                    text = "${allTeams.size} فرق مسجلة",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystelNavyPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Teams Carousel or Register Callout
                        if (allTeams.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "بانتظار تسجيل فرق العمل في التطبيق",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SystelNavyDark
                                        )
                                        Text(
                                            text = "أي فريق يفتح التطبيق ويسجل (الاسم، التليفون، والميل) ستظهر بياناته وتصفياته هنا فوراً.",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { showRegisterTeamDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary)
                                    ) {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("تسجيل فريق", fontSize = 11.5.sp)
                                    }
                                }
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(teamsStats) { stat ->
                                    TeamStatCard(
                                        stats = stat,
                                        isSelected = selectedTeamFilter == stat.team.teamCode,
                                        decimalFormat = decimalFormat,
                                        onClick = {
                                            viewModel.setTeamFilter(
                                                if (selectedTeamFilter == stat.team.teamCode) null else stat.team.teamCode
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Overall Financial Summary across all clearances
                        Text(
                            text = "ملخص إجمالي التصفيات المالية (لكل المواقع)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 1: Total Custody / Advance
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SystelBlueLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("إجمالي العهد المستلمة", fontSize = 11.sp, color = SystelNavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(totalAdvance)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelNavyPrimary
                                    )
                                }
                            }

                            // Card 2: Total Expenses Spent
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("إجمالي المصروفات المنصرفة", fontSize = 11.sp, color = Color(0xFF991B1B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(totalExpenses)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }

                        // Card 3: Net Liquidation Balance
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (totalBalance >= 0) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (totalBalance >= 0) SystelSuccessGreen.copy(alpha = 0.5f) else SystelGoldApproval.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (totalBalance >= 0) SystelSuccessGreen else SystelGoldApproval),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "صافي التصفية العام (العهدة - المنصرف)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (totalBalance >= 0) Color(0xFF166534) else Color(0xFF92400E)
                                        )
                                        Text(
                                            text = if (totalBalance >= 0) "متبقي لشركة سيستل" else "مستحق للصرف للمهندسين",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (totalBalance >= 0) SystelSuccessGreen else SystelGoldApproval
                                        )
                                    }
                                }
                                Text(
                                    text = "${decimalFormat.format(kotlin.math.abs(totalBalance))} ج.م",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (totalBalance >= 0) Color(0xFF14532D) else Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }
            } else {
                // ==================== FIELD TEAM PRIVATE DASHBOARD ====================
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Engineer Dedicated Header (ملائمة مش كبيرة ويكون جواها لوجو SYSTEL)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2654)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // SYSTEL Logo inside
                                        SystelLogo(
                                            size = 32.dp,
                                            showArabicSubtitle = false,
                                            isDarkBackground = true
                                        )

                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "المهندس: ${if (userSession.name.isNotBlank()) userSession.name else "مهندس الموقع"}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                            if (!userSession.activeTeamName.isNullOrBlank() || userSession.email.isNotBlank()) {
                                                Text(
                                                    text = "${userSession.activeTeamName ?: ""} • ${userSession.email}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFBAE6FD)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF0284C7)
                                    ) {
                                        Text(
                                            text = "${displayedClearances.size} تصفية",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                                // Privacy & Isolation Notice Box
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "خصوصية البيانات: تصفياتك تظهر مباشرة للتيم ليدر للاعتماد. لا يمكنك رؤية تصفيات باقي الفرق، والتحميل كملف PDF متاح بعد موافقة التيم ليدر فقط.",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFFEF08A),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // Team Financial Overview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SystelBlueLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("عهد الفريق المستلمة", fontSize = 11.sp, color = SystelNavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(totalAdvance)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystelNavyPrimary
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("مصروفات الفريق المنصرفة", fontSize = 11.sp, color = Color(0xFF991B1B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${decimalFormat.format(totalExpenses)} ج.م",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }

                        // Team Net Settlement Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (totalBalance >= 0) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (totalBalance >= 0) SystelSuccessGreen.copy(alpha = 0.5f) else SystelGoldApproval.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "صافي تصفية الفريق (العهدة - المنصرف)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (totalBalance >= 0) Color(0xFF166534) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = if (totalBalance >= 0) "متبقي للشركة" else "مستحق للصرف للمهندس",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (totalBalance >= 0) SystelSuccessGreen else SystelGoldApproval
                                    )
                                }
                                Text(
                                    text = "${decimalFormat.format(kotlin.math.abs(totalBalance))} ج.م",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (totalBalance >= 0) Color(0xFF14532D) else Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Search and Filters Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("بحث عن موقع، محافظة، رقم التصفية...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_clearance_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )

                    // Manager Teams Quick Filter Row
                    if (userSession.isManager && allTeams.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChipCustom(
                                    label = "جميع الفرق والمواقع (${allClearances.size})",
                                    isSelected = selectedTeamFilter == null || selectedTeamFilter == "ALL",
                                    onClick = { viewModel.setTeamFilter(null) }
                                )
                            }
                            items(allTeams) { team ->
                                val count = allClearances.count { it.sheet.teamCode == team.teamCode }
                                FilterChipCustom(
                                    label = "${team.teamName} ($count)",
                                    isSelected = selectedTeamFilter == team.teamCode,
                                    onClick = {
                                        viewModel.setTeamFilter(
                                            if (selectedTeamFilter == team.teamCode) null else team.teamCode
                                        )
                                    },
                                    accentColor = Color(0xFF0284C7)
                                )
                            }
                        }
                    }

                    // Department filters
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChipCustom(
                                label = "كل الأقسام",
                                isSelected = selectedDept == null || selectedDept == "ALL",
                                onClick = { viewModel.setDepartmentFilter(null) }
                            )
                        }
                        items(SystelConstants.DEPARTMENTS) { dept ->
                            FilterChipCustom(
                                label = dept,
                                isSelected = selectedDept == dept,
                                onClick = {
                                    viewModel.setDepartmentFilter(if (selectedDept == dept) null else dept)
                                }
                            )
                        }
                    }

                    // Status filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChipCustom(
                            label = "الكل (${displayedClearances.size})",
                            isSelected = selectedStatus == null || selectedStatus == "ALL",
                            onClick = { viewModel.setStatusFilter(null) }
                        )
                        FilterChipCustom(
                            label = "معتمد (${displayedClearances.count { it.sheet.status == "APPROVED" }})",
                            isSelected = selectedStatus == "APPROVED",
                            onClick = { viewModel.setStatusFilter(if (selectedStatus == "APPROVED") null else "APPROVED") },
                            accentColor = SystelSuccessGreen
                        )
                        FilterChipCustom(
                            label = "قيد المراجعة (${displayedClearances.count { it.sheet.status == "PENDING" }})",
                            isSelected = selectedStatus == "PENDING",
                            onClick = { viewModel.setStatusFilter(if (selectedStatus == "PENDING") null else "PENDING") },
                            accentColor = SystelGoldApproval
                        )
                        FilterChipCustom(
                            label = "مرفوض (${displayedClearances.count { it.sheet.status == "REJECTED" }})",
                            isSelected = selectedStatus == "REJECTED",
                            onClick = { viewModel.setStatusFilter(if (selectedStatus == "REJECTED") null else "REJECTED") },
                            accentColor = SystelAccentRed
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // 5. Site Clearance Sheets List
            if (clearances.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(52.dp)
                            )
                            Text(
                                text = if (userSession.isManager) "لا توجد تصفيات مطابقة للبحث" else "لا توجد تصفيات مسجلة لهذا الفريق حالياً",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "اضغط على زر الإضافة بالأسفل لإنشاء تصفية موقع جديدة",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(clearances, key = { it.sheet.id }) { clearance ->
                    ClearanceCard(
                        clearance = clearance,
                        isManager = userSession.isManager,
                        decimalFormat = decimalFormat,
                        onClick = {
                            viewModel.selectSheet(clearance.sheet.id)
                            onNavigateToDetail(clearance.sheet.id)
                        }
                    )
                }
            }
        }
    }

    if (showNewSheetDialog) {
        NewSheetDialog(
            defaultTeamName = userSession.activeTeamName,
            defaultLeader = userSession.name,
            allTeams = allTeams,
            isManager = userSession.isManager,
            onDismiss = { showNewSheetDialog = false },
            onConfirm = { site, dept, reg, start, end, act, leader, members, adv, teamCode, teamName ->
                viewModel.createNewClearance(
                    siteName = site,
                    department = dept,
                    region = reg,
                    startDate = start,
                    endDate = end,
                    activityType = act,
                    teamLeader = leader,
                    teamMembers = members,
                    advanceAmount = adv,
                    teamCode = teamCode,
                    teamName = teamName
                )
                showNewSheetDialog = false
            }
        )
    }

    if (showRegisterTeamDialog || !userSession.isRegistered) {
        RegisterTeamDialog(
            viewModel = viewModel,
            isMandatoryFirstRegistration = !userSession.isRegistered,
            allTeams = allTeams,
            onSelectExistingTeam = { team ->
                viewModel.loginAsTeam(team)
                showRegisterTeamDialog = false
            },
            onSelectTeamLeader = {
                viewModel.loginAsManager("م. ياسر عواد")
                showRegisterTeamDialog = false
            },
            onDismiss = { showRegisterTeamDialog = false },
            onConfirm = { teamName, leaderName, phone, email, department, isLeader ->
                viewModel.registerNewTeam(
                    teamName = teamName,
                    leaderName = leaderName,
                    phone = phone,
                    email = email,
                    department = department,
                    isTeamLeader = isLeader
                )
                showRegisterTeamDialog = false
            }
        )
    }

    if (showNotificationsDialog) {
        TeamLeaderNotificationDialog(
            pendingClearances = pendingClearances,
            onDismiss = { showNotificationsDialog = false },
            onSelectClearance = { sheetId ->
                showNotificationsDialog = false
                onNavigateToDetail(sheetId)
            }
        )
    }

    if (showPendingAccountsDialog) {
        PendingAccountsDialog(
            pendingAccounts = pendingAccounts,
            onApprove = { account ->
                viewModel.approveUserAccount(account)
            },
            onReject = { account ->
                viewModel.rejectUserAccount(account)
            },
            onDismiss = { showPendingAccountsDialog = false }
        )
    }
}

@Composable
fun TeamStatCard(
    stats: TeamDashboardStats,
    isSelected: Boolean,
    decimalFormat: DecimalFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable { onClick() }
            .testTag("team_stat_card_${stats.team.teamCode}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF0F2654) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) Color(0xFF38BDF8) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stats.team.teamName,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else SystelNavyDark
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) Color(0xFF0284C7) else SystelBlueLight
                ) {
                    Text(
                        text = "${stats.totalClearances} تصفية",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else SystelNavyPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "القائد: ${stats.team.leaderName} (${stats.team.department})",
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFFBAE6FD) else Color.Gray
            )

            if (stats.team.phone.isNotBlank() || stats.team.email.isNotBlank()) {
                Text(
                    text = "تواصل: ${stats.team.phone} • ${stats.team.email}",
                    fontSize = 10.sp,
                    color = if (isSelected) Color(0xFF7DD3FC) else Color(0xFF64748B),
                    maxLines = 1
                )
            }

            HorizontalDivider(
                color = if (isSelected) Color.White.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "المنصرف:",
                        fontSize = 10.sp,
                        color = if (isSelected) Color(0xFFFCA5A5) else Color.Gray
                    )
                    Text(
                        text = "${decimalFormat.format(stats.totalExpenses)} ج.م",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFFFCA5A5) else SystelAccentRed
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "الصافي:",
                        fontSize = 10.sp,
                        color = if (isSelected) Color(0xFF86EFAC) else Color.Gray
                    )
                    Text(
                        text = "${decimalFormat.format(kotlin.math.abs(stats.netBalance))} ج.م",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF86EFAC) else SystelSuccessGreen
                    )
                }
            }

            // Pending badge if any
            if (stats.pendingClearances > 0) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) SystelGoldApproval.copy(alpha = 0.3f) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "قيد المراجعة: ${stats.pendingClearances}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFFFDE047) else Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipCustom(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = SystelNavyPrimary
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun ClearanceCard(
    clearance: ClearanceWithItems,
    isManager: Boolean,
    decimalFormat: DecimalFormat,
    onClick: () -> Unit
) {
    val sheet = clearance.sheet
    val isApproved = sheet.status == "APPROVED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("clearance_card_${sheet.id}"),
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
            // Header Row: Site Name, Professional Telecom Icon Badge, Team Tag, and Approval Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Professional Vector Telecom Icon Badge instead of raw text letters
                    TelecomSiteIconBadge(
                        activityType = sheet.activityType,
                        siteName = sheet.siteName,
                        size = 42.dp
                    )

                    Column {
                        Text(
                            text = sheet.siteName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = sheet.sheetNumber,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(text = "•", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = sheet.teamName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }
                }

                // Approval Status Pill
                ClearanceStatusBadge(statusCode = sheet.status)
            }

            // Department & Region Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SystelBlueLight
                ) {
                    Text(
                        text = sheet.department,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SystelNavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = sheet.region,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = sheet.activityType,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Financial Breakdown: Advance, Total Expenses, and Net Settlement
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("العهدة المستلمة", fontSize = 10.5.sp, color = Color.Gray)
                        Text(
                            text = "${decimalFormat.format(sheet.advanceAmount)} ج.م",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystelNavyPrimary
                        )
                    }
                    Column {
                        Text("إجمالي المنصرف", fontSize = 10.5.sp, color = Color.Gray)
                        Text(
                            text = "${decimalFormat.format(clearance.totalExpenses)} ج.م",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystelAccentRed
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = when (clearance.balanceStatus) {
                                BalanceType.SURPLUS_TO_COMPANY -> "متبقي للشركة"
                                BalanceType.DEFICIT_TO_EMPLOYEE -> "مستحق للمهندس"
                                BalanceType.SETTLED -> "التصفية متطابقة"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (clearance.balanceStatus) {
                                BalanceType.SURPLUS_TO_COMPANY -> SystelSuccessGreen
                                BalanceType.DEFICIT_TO_EMPLOYEE -> SystelGoldApproval
                                BalanceType.SETTLED -> Color.Gray
                            }
                        )
                        Text(
                            text = "${decimalFormat.format(kotlin.math.abs(clearance.balance))} ج.م",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = when (clearance.balanceStatus) {
                                BalanceType.SURPLUS_TO_COMPANY -> SystelSuccessGreen
                                BalanceType.DEFICIT_TO_EMPLOYEE -> SystelGoldApproval
                                BalanceType.SETTLED -> Color.Gray
                            }
                        )
                    }
                }
            }

            // Footer Row: Items count, Leader, and PDF Status indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${clearance.items.size} بنود صرف • المشرف: ${sheet.teamLeader}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    if (!isApproved && !isManager) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "PDF مقفل",
                            fontSize = 10.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "عرض التفاصيل",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SystelNavyPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = SystelNavyPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

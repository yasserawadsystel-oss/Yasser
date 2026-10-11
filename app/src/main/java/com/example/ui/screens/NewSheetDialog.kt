package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystelConstants
import com.example.data.model.TeamEntity
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewSheetDialog(
    defaultTeamName: String? = null,
    defaultLeader: String? = null,
    allTeams: List<TeamEntity> = emptyList(),
    isManager: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (
        siteName: String,
        department: String,
        region: String,
        startDate: String,
        endDate: String,
        activityType: String,
        teamLeader: String,
        teamMembers: String,
        advanceAmount: Double,
        teamCode: String?,
        teamName: String?
    ) -> Unit
) {
    var siteName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf(SystelConstants.DEPARTMENTS.first()) }
    var region by remember { mutableStateOf("الشرقية / زقازيق") }
    var startDate by remember { mutableStateOf("01/10/2026") }
    var endDate by remember { mutableStateOf("13/10/2026") }
    var activityType by remember { mutableStateOf(SystelConstants.ACTIVITIES.first()) }
    var teamLeader by remember { mutableStateOf(defaultLeader ?: "التيم ليدر") }
    var teamMembers by remember { mutableStateOf("") }
    var memberInputText by remember { mutableStateOf("") }
    var membersList by remember { mutableStateOf(listOf<String>()) }
    var advanceText by remember { mutableStateOf("10000") }
    var selectedTeam by remember {
        mutableStateOf(
            allTeams.firstOrNull { it.teamName == defaultTeamName } ?: allTeams.firstOrNull()
        )
    }
    var errorText by remember { mutableStateOf<String?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    AlertDialog(
        modifier = Modifier
            .widthIn(max = if (isLandscape) 640.dp else 480.dp)
            .fillMaxWidth(if (isLandscape) 0.88f else 0.96f),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إنشاء استمارة تصفية موقع جديدة",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (isLandscape) 280.dp else 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Team Selection (If Manager) or Active Team Badge
                if (isManager && allTeams.isNotEmpty()) {
                    Text(
                        text = "اختر الفريق التابع له الموقع (Team):",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        allTeams.forEach { team ->
                            FilterChip(
                                selected = selectedTeam?.teamCode == team.teamCode,
                                onClick = {
                                    selectedTeam = team
                                    teamLeader = team.leaderName
                                    department = team.department
                                },
                                label = { Text(team.teamName, fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SystelNavyPrimary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                } else if (!defaultTeamName.isNullOrBlank()) {
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = SystelNavyPrimary)
                            Text(
                                text = "الفريق المالك للتصفية: $defaultTeamName",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = siteName,
                    onValueChange = {
                        siteName = it
                        errorText = null
                    },
                    label = { Text("اسم الموقع (Site Name)") },
                    placeholder = { Text("مثال: زقازيق 43، اسوان 12...") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_site_name_input"),
                    singleLine = true
                )

                Text(
                    text = "القسم (Department):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SystelConstants.DEPARTMENTS.forEach { dept ->
                        FilterChip(
                            selected = department == dept,
                            onClick = { department = dept },
                            label = { Text(dept, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SystelNavyPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Text(
                    text = "نوع النشاط (Activity Type):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SystelConstants.ACTIVITIES.take(6).forEach { act ->
                        FilterChip(
                            selected = activityType == act,
                            onClick = { activityType = act },
                            label = { Text(act, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SystelNavyPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("المنطقة / المحافظة (Region)") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = advanceText,
                    onValueChange = { advanceText = it },
                    label = { Text("مبلغ العهدة المستلمة (جنيه مصري)") },
                    placeholder = { Text("مثال: 10000") },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_advance_input"),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("تاريخ البدء") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("تاريخ الانتهاء") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = teamLeader,
                    onValueChange = { teamLeader = it },
                    label = { Text("التيم ليدر (Team Leader)") },
                    leadingIcon = { Icon(Icons.Default.Engineering, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Dynamic Unlimited Team Members Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = SystelNavyPrimary, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "أعضاء الفريق (${membersList.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SystelNavyDark
                                )
                            }
                            Text(
                                text = "متاح أكثر من 4 بلا حد",
                                fontSize = 11.sp,
                                color = SystelSuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Add member input field + Add button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = memberInputText,
                                onValueChange = { memberInputText = it },
                                label = { Text("اسم العضو / المهندس") },
                                placeholder = { Text("مثال: م. أحمد أو م. كمال...") },
                                leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = SystelNavyPrimary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("team_member_input"),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (memberInputText.isNotBlank()) {
                                        val newOnes = memberInputText
                                            .split("،", ",")
                                            .map { it.trim() }
                                            .filter { it.isNotBlank() }
                                        membersList = (membersList + newOnes).distinct()
                                        memberInputText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                                modifier = Modifier.testTag("add_member_button")
                            ) {
                                Text("إضافة +", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // List of added members (supports 5, 6, 7, 8... members)
                        if (membersList.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                membersList.forEachIndexed { index, name ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
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
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = SystelNavyPrimary.copy(alpha = 0.12f),
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SystelNavyPrimary)
                                                    }
                                                }
                                                Text(
                                                    text = name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = SystelNavyDark
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    membersList = membersList.filterIndexed { i, _ -> i != index }
                                                },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "حذف العضو",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "يمكنك كتابة اسم كل عضو ثم الضغط على (إضافة +) لإضافة أي عدد تريده من المهندسين (4 أو 5 أو أكثر).",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val adv = advanceText.toDoubleOrNull() ?: 0.0
                    val extraMembers = if (memberInputText.isNotBlank()) {
                        memberInputText.split("،", ",").map { it.trim() }.filter { it.isNotBlank() }
                    } else emptyList()
                    val allCombined = (membersList + extraMembers).distinct()
                    val finalMembers = allCombined.joinToString("، ")

                    if (siteName.isBlank()) {
                        errorText = "يرجى كتابة اسم الموقع"
                    } else {
                        onConfirm(
                            siteName.trim(),
                            department,
                            region.trim(),
                            startDate.trim(),
                            endDate.trim(),
                            activityType,
                            teamLeader.trim(),
                            finalMembers,
                            adv,
                            selectedTeam?.teamCode,
                            selectedTeam?.teamName
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_create_sheet_button")
            ) {
                Text("إنشاء التصفية")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

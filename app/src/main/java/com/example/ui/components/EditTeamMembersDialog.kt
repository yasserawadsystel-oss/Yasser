package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen

/**
 * Dialog allowing dynamic addition, removal, and editing of team members
 * with NO limit (supports 5, 6, 7, 8... members).
 */
@Composable
fun EditTeamMembersDialog(
    currentMembers: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var memberList by remember {
        mutableStateOf(
            currentMembers.split("،", ",").map { it.trim() }.filter { it.isNotBlank() }
        )
    }
    var newMemberName by remember { mutableStateOf("") }

    AlertDialog(
        modifier = Modifier
            .widthIn(max = if (isLandscape) 640.dp else 480.dp)
            .fillMaxWidth(if (isLandscape) 0.88f else 0.96f),
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    tint = SystelNavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "إدارة أعضاء الفريق (Team Members)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SystelNavyDark
                    )
                    Text(
                        text = "يمكنك إضافة أي عدد من الأعضاء (أكثر من 4 بلا حد)",
                        fontSize = 11.sp,
                        color = SystelSuccessGreen,
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
                // Add new member input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { newMemberName = it },
                        label = { Text("اسم العضو / المهندس") },
                        placeholder = { Text("مثال: م. أحمد أو م. كمال...") },
                        leadingIcon = {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = SystelNavyPrimary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_member_name_input"),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newMemberName.isNotBlank()) {
                                val split = newMemberName
                                    .split("،", ",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }
                                memberList = (memberList + split).distinct()
                                newMemberName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                        modifier = Modifier.testTag("add_member_dialog_btn")
                    ) {
                        Text("إضافة +", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                // List of existing members
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الأعضاء المضافين (${memberList.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SystelNavyDark
                    )
                    if (memberList.isNotEmpty()) {
                        Text(
                            text = "اضغط 🗑️ لحذف أي عضو",
                            fontSize = 10.5.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (memberList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لم يتم إضافة أعضاء بعد. اكتب الاسم واضغط إضافة.",
                            fontSize = 11.5.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        memberList.forEachIndexed { index, name ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
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
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SystelNavyPrimary
                                                )
                                            }
                                        }
                                        Text(
                                            text = name,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SystelNavyDark
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            memberList = memberList.filterIndexed { i, _ -> i != index }
                                        },
                                        modifier = Modifier.size(28.dp)
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
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val extra = if (newMemberName.isNotBlank()) {
                        newMemberName.split("،", ",").map { it.trim() }.filter { it.isNotBlank() }
                    } else emptyList()
                    val all = (memberList + extra).distinct()
                    onConfirm(all.joinToString("، "))
                },
                colors = ButtonDefaults.buttonColors(containerColor = SystelNavyPrimary),
                modifier = Modifier.testTag("save_members_button")
            ) {
                Text("حفظ وتحديث الأعضاء", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

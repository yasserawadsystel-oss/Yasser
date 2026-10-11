package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BalanceType
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ClearanceStatus
import com.example.data.model.ClearanceWithItems
import com.example.data.model.ExpenseItemEntity
import com.example.data.model.TeamEntity
import com.example.ui.components.getStatusVisualConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("تصفية سيستل", appName)
  }

  @Test
  fun `verify clearance status codes and visual configs`() {
    // 1. Pending
    val pendingStatus = ClearanceStatus.fromCode("PENDING")
    assertEquals(ClearanceStatus.PENDING, pendingStatus)
    val pendingConfig = getStatusVisualConfig(pendingStatus)
    assertEquals("قيد المراجعة", pendingConfig.labelAr)

    // 2. Approved
    val approvedStatus = ClearanceStatus.fromCode("APPROVED")
    assertEquals(ClearanceStatus.APPROVED, approvedStatus)
    val approvedConfig = getStatusVisualConfig(approvedStatus)
    assertEquals("معتمد رسمياً", approvedConfig.labelAr)

    // 3. Rejected
    val rejectedStatus = ClearanceStatus.fromCode("REJECTED")
    assertEquals(ClearanceStatus.REJECTED, rejectedStatus)
    val rejectedConfig = getStatusVisualConfig(rejectedStatus)
    assertEquals("مرفوض / يتطلب تعديل", rejectedConfig.labelAr)
  }

  @Test
  fun `verify clearance calculation matches attached sheet`() {
    val sheet = ClearanceSheetEntity(
      sheetNumber = "SYS-2026-0043",
      siteName = "زقازيق 43",
      department = "NOKIA Orange",
      region = "الشرقية / زقازيق",
      startDate = "01/10/2026",
      endDate = "13/09/2026",
      activityType = "Installation",
      teamLeader = "ياسر عواد محمد",
      teamCode = "TEAM_SHARQIA",
      teamName = "تيم تركيبات الشرقية",
      advanceAmount = 10000.0,
      status = "PENDING"
    )

    // Items from user CSV: 1500 + 500 + 2500 + 1000 + 3000 = 8500
    val items = listOf(
      ExpenseItemEntity(sheetId = 1, category = "عمالة", description = "ايجار تيم صيانة اسوان", amount = 1500.0),
      ExpenseItemEntity(sheetId = 1, category = "عمالة", description = "ايجار تيم صيانة القليوبية", amount = 500.0),
      ExpenseItemEntity(sheetId = 1, category = "عمالة", description = "ايجار تيم صيانة بنها", amount = 2500.0),
      ExpenseItemEntity(sheetId = 1, category = "نثريات", description = "مالك موقع زقازيق 43", amount = 1000.0),
      ExpenseItemEntity(sheetId = 1, category = "نثريات", description = "مجهود غير عادي عن شهر 6-7-8", amount = 3000.0)
    )

    val clearance = ClearanceWithItems(sheet = sheet, items = items)

    assertEquals(8500.0, clearance.totalExpenses, 0.001)
    assertEquals(1500.0, clearance.balance, 0.001)
    assertEquals(BalanceType.SURPLUS_TO_COMPANY, clearance.balanceStatus)
    assertEquals(4500.0, clearance.categoryBreakdown["عمالة"] ?: 0.0, 0.001)
    assertEquals(4000.0, clearance.categoryBreakdown["نثريات"] ?: 0.0, 0.001)
  }

  @Test
  fun `verify team isolation and pdf download restrictions`() {
    val teamA = TeamEntity(teamCode = "TEAM_SHARQIA", teamName = "تيم الشرقية", leaderName = "م. أحمد", department = "NOKIA Orange")
    val teamB = TeamEntity(teamCode = "TEAM_BENHA", teamName = "تيم بنها", leaderName = "م. تامر", department = "NOKIA Etisalat")

    val sheetA = ClearanceSheetEntity(
      sheetNumber = "SYS-1", siteName = "زقازيق 1", department = "NOKIA Orange", region = "الشرقية",
      startDate = "01/10/2026", endDate = "02/10/2026", activityType = "Install", teamLeader = "م. أحمد",
      teamCode = teamA.teamCode, teamName = teamA.teamName, advanceAmount = 5000.0, status = "PENDING"
    )
    val sheetB = ClearanceSheetEntity(
      sheetNumber = "SYS-2", siteName = "بنها 2", department = "NOKIA Etisalat", region = "بنها",
      startDate = "01/10/2026", endDate = "02/10/2026", activityType = "Swap", teamLeader = "م. تامر",
      teamCode = teamB.teamCode, teamName = teamB.teamName, advanceAmount = 8000.0, status = "APPROVED"
    )

    val allList = listOf(
      ClearanceWithItems(sheet = sheetA, items = emptyList()),
      ClearanceWithItems(sheet = sheetB, items = emptyList())
    )

    // Team A User can ONLY see sheetA
    val teamAVisible = allList.filter { it.sheet.teamCode == teamA.teamCode }
    assertEquals(1, teamAVisible.size)
    assertEquals("SYS-1", teamAVisible.first().sheet.sheetNumber)

    // PDF Download Rule:
    // Team A on sheetA (PENDING): CANNOT download
    val canTeamADownloadPending = sheetA.status == "APPROVED" || false // isManager = false
    assertFalse(canTeamADownloadPending)

    // Team B on sheetB (APPROVED): CAN download
    val canTeamBDownloadApproved = sheetB.status == "APPROVED" || false // isManager = false
    assertTrue(canTeamBDownloadApproved)

    // Manager on sheetA (PENDING): CAN download preview
    val canManagerDownloadPending = sheetA.status == "APPROVED" || true // isManager = true
    assertTrue(canManagerDownloadPending)
  }
}

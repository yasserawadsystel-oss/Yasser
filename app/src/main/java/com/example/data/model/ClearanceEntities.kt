package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val department: String,
    val teamName: String,
    val isTeamLeader: Boolean = false,
    val status: String = "PENDING_ACTIVATION", // "PENDING_ACTIVATION", "ACTIVE", "REJECTED"
    val activationCode: String = "",
    val emailVerified: Boolean = false,
    val phoneVerified: Boolean = false,
    val requestedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamCode: String,        // e.g. "TEAM_SHARQIA", "TEAM_CAIRO", "TEAM_ALEX"
    val teamName: String,        // e.g. "تيم صيانة الشرقية", "تيم شبكات القاهرة"
    val leaderName: String,      // e.g. "م. أحمد الشريف"
    val department: String,      // e.g. "NOKIA Orange", "NOKIA Etisalat", "GSM(ADM)"
    val phone: String = "",      // Phone number
    val email: String = "",      // Email address
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "clearance_sheets")
data class ClearanceSheetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sheetNumber: String,
    val siteName: String,
    val department: String,      // e.g. "NOKIA Orange", "NOKIA Etisalat", "GSM(ADM)", etc.
    val region: String,          // e.g. "الشرقية / زقازيق", "أسوان", "القليوبية / بنها"
    val startDate: String,       // e.g. "01/10/2026"
    val startTime: String = "8:00 AM",
    val endDate: String,         // e.g. "13/09/2026"
    val endTime: String = "5:30 PM",
    val activityType: String,    // "Installation", "Survey", "On Air", "Swap", etc.
    val teamLeader: String,      // e.g. "التيم ليدر"
    val teamMembers: String = "",
    val teamCode: String = "MANAGER_LEADER", // كود التيم أو حساب التيم ليدر
    val teamName: String = "إدارة العمليات - التيم ليدر",
    val siteBook: String = "OK",
    val layoutRef: String = "OK",
    val advanceAmount: Double = 0.0, // العهدة المستلمة بالجنيه
    val status: String = "PENDING",  // "PENDING", "APPROVED", "REJECTED"
    val approvedBy: String? = null,  // "التيم ليدر"
    val approvalDate: String? = null,
    val approvalNotes: String? = null,
    val signatureData: String? = null, // Path points or Base64 of the Team Leader signature
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expense_items")
data class ExpenseItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sheetId: Long,
    val category: String,        // "عمالة", "نثريات", "إقامة", "بنزين", "انتقالات", "إعاشة", "تأجير سيارة", "نقل", "خامات", "أوراق سفر"
    val description: String,     // بيان الصرف، e.g. "ايجار تيم صيانة اسوان"
    val amount: Double,          // المبلغ بالجنيه
    val invoiceNumber: String = "", // رقم الفاتورة أو الإيصال
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ClearanceWithItems(
    @Embedded val sheet: ClearanceSheetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sheetId"
    )
    val items: List<ExpenseItemEntity>
) {
    val totalExpenses: Double
        get() = items.sumOf { it.amount }

    // حساب التصفية: العهدة المستلمة - إجمالي المصروفات
    val balance: Double
        get() = sheet.advanceAmount - totalExpenses

    // صافي المستحق أو المتبقي
    val balanceStatus: BalanceType
        get() = when {
            balance > 0.001 -> BalanceType.SURPLUS_TO_COMPANY // متبقي للشركة
            balance < -0.001 -> BalanceType.DEFICIT_TO_EMPLOYEE // مستحق للمهندس
            else -> BalanceType.SETTLED // تصفية مطابقة بالكامل
        }

    val categoryBreakdown: Map<String, Double>
        get() = items.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
}

data class TeamDashboardStats(
    val team: TeamEntity,
    val totalClearances: Int,
    val pendingClearances: Int,
    val approvedClearances: Int,
    val rejectedClearances: Int,
    val totalCustody: Double,
    val totalExpenses: Double,
    val netBalance: Double
)

enum class BalanceType(val labelAr: String) {
    SURPLUS_TO_COMPANY("متبقي لصالح شركة سيستل"),
    DEFICIT_TO_EMPLOYEE("مستحق للمهندس / المشرف"),
    SETTLED("تصفية مطابقة تماماً (0 ج.م)")
}

enum class ClearanceStatus(
    val code: String,
    val labelAr: String,
    val subtitleAr: String
) {
    PENDING(
        code = "PENDING",
        labelAr = "قيد المراجعة",
        subtitleAr = "في انتظار فحص واعتماد التيم ليدر"
    ),
    APPROVED(
        code = "APPROVED",
        labelAr = "معتمد رسمياً",
        subtitleAr = "تم الاعتماد والمطابقة بواسطة التيم ليدر"
    ),
    REJECTED(
        code = "REJECTED",
        labelAr = "مرفوض / يتطلب تعديل",
        subtitleAr = "تم رفض التصفية أو طلب تعديل البنود من التيم ليدر"
    );

    companion object {
        fun fromCode(code: String): ClearanceStatus = when (code.trim().uppercase()) {
            "APPROVED" -> APPROVED
            "REJECTED" -> REJECTED
            else -> PENDING
        }
    }
}

object SystelConstants {
    const val MANAGER_NAME = "التيم ليدر"
    const val MANAGER_EMAIL = "yasser.awad.systel@gmail.com"
    const val MANAGER_TITLE = "التيم ليدر - سيستل"

    val DEPARTMENTS = listOf(
        "NOKIA Orange",
        "NOKIA Etisalat",
        "GSM(ADM)",
        "NOKIA EXtra",
        "NOKIA BSC",
        "Vodafone MW",
        "Civil & Power"
    )

    val ACTIVITIES = listOf(
        "Installation",
        "Swap",
        "Commission",
        "Survey",
        "Delivery",
        "On Air",
        "Upgrade",
        "Downgrade",
        "Reconfig.",
        "Rectifier",
        "Desmantle"
    )

    val EXPENSE_CATEGORIES = listOf(
        "عمالة",
        "نثريات",
        "بنزين",
        "إقامة",
        "انتقالات",
        "إعاشة",
        "تأجير سيارة",
        "نقل",
        "خامات",
        "أوراق سفر"
    )
}

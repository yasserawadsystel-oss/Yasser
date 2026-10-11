package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ClearanceWithItems
import com.example.data.model.ExpenseItemEntity
import com.example.data.model.SystelConstants
import com.example.data.model.TeamDashboardStats
import com.example.data.model.TeamEntity
import com.example.data.model.UserAccountEntity
import com.example.data.repository.ClearanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthResult {
    data class Success(val account: UserAccountEntity, val message: String = "تم تسجيل الدخول بنجاح") : AuthResult()
    data class PendingApproval(val account: UserAccountEntity, val message: String) : AuthResult()
    data class Rejected(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

data class UserSessionState(
    val email: String = "",
    val name: String = "",
    val roleTitle: String = "",
    val isManager: Boolean = false,
    val activeTeamCode: String? = null,
    val activeTeamName: String? = null,
    val isRegistered: Boolean = false
)

private data class FilterCriteria(
    val query: String = "",
    val dept: String? = null,
    val status: String? = null,
    val teamCode: String? = null
)

class ClearanceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("systel_user_session_prefs", Context.MODE_PRIVATE)
    private val repository: ClearanceRepository

    val userSession = MutableStateFlow(loadInitialSession())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _departmentFilter = MutableStateFlow<String?>(null)
    val departmentFilter = _departmentFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter = _statusFilter.asStateFlow()

    private val _teamFilter = MutableStateFlow<String?>(null)
    val teamFilter = _teamFilter.asStateFlow()

    private val _selectedSheetId = MutableStateFlow<Long?>(null)
    val selectedSheetId = _selectedSheetId.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = ClearanceRepository(db.clearanceDao())
    }

    private fun loadInitialSession(): UserSessionState {
        val hasSession = prefs.getBoolean("has_active_session", false)
        if (!hasSession) {
            // No registered user on this device yet! App starts completely clean & empty.
            return UserSessionState(
                email = "",
                name = "",
                roleTitle = "مستخدم جديد",
                isManager = false,
                activeTeamCode = null,
                activeTeamName = null,
                isRegistered = false
            )
        }

        val isManager = prefs.getBoolean("is_manager", false)
        return UserSessionState(
            email = prefs.getString("user_email", "") ?: "",
            name = prefs.getString("user_name", "") ?: "",
            roleTitle = prefs.getString("role_title", "") ?: "",
            isManager = isManager,
            activeTeamCode = prefs.getString("active_team_code", null),
            activeTeamName = prefs.getString("active_team_name", null),
            isRegistered = true
        )
    }

    private fun saveSessionToDisk(state: UserSessionState) {
        prefs.edit().apply {
            putBoolean("has_active_session", state.isRegistered)
            putBoolean("is_manager", state.isManager)
            putString("user_email", state.email)
            putString("user_name", state.name)
            putString("role_title", state.roleTitle)
            putString("active_team_code", state.activeTeamCode)
            putString("active_team_name", state.activeTeamName)
            apply()
        }
    }

    val allClearances: StateFlow<List<ClearanceWithItems>> = repository.allClearances
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allTeams: StateFlow<List<TeamEntity>> = repository.allTeams
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingAccounts: StateFlow<List<UserAccountEntity>> = repository.pendingAccounts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allAccounts: StateFlow<List<UserAccountEntity>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Manager Teams Dashboard Statistics
    val teamsDashboardStats: StateFlow<List<TeamDashboardStats>> = combine(
        allTeams,
        allClearances
    ) { teams, clearances ->
        teams.map { team ->
            val teamClearances = clearances.filter { it.sheet.teamCode == team.teamCode }
            val totalCustody = teamClearances.sumOf { it.sheet.advanceAmount }
            val totalExpenses = teamClearances.sumOf { it.totalExpenses }
            TeamDashboardStats(
                team = team,
                totalClearances = teamClearances.size,
                pendingClearances = teamClearances.count { it.sheet.status == "PENDING" },
                approvedClearances = teamClearances.count { it.sheet.status == "APPROVED" },
                rejectedClearances = teamClearances.count { it.sheet.status == "REJECTED" },
                totalCustody = totalCustody,
                totalExpenses = totalExpenses,
                netBalance = totalCustody - totalExpenses
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filter criteria flow combining search parameters
    private val filterCriteria = combine(
        searchQuery,
        departmentFilter,
        statusFilter,
        teamFilter
    ) { query, dept, status, teamCode ->
        FilterCriteria(query, dept, status, teamCode)
    }

    // Strict Data Isolation / Role Filtering:
    // If not registered: empty list.
    // If registered team: Only clearances of activeTeamCode are visible!
    // If manager: All clearances.
    val filteredClearances: StateFlow<List<ClearanceWithItems>> = combine(
        allClearances,
        userSession,
        filterCriteria
    ) { list, session, filter ->
        if (!session.isRegistered) {
            return@combine emptyList<ClearanceWithItems>()
        }

        list.filter { item ->
            // Team Isolation Check
            val matchesTeamScope = if (session.isManager) {
                filter.teamCode == null || filter.teamCode == "ALL" || item.sheet.teamCode == filter.teamCode
            } else {
                item.sheet.teamCode == session.activeTeamCode
            }

            val matchesQuery = filter.query.isBlank() ||
                item.sheet.siteName.contains(filter.query, ignoreCase = true) ||
                item.sheet.sheetNumber.contains(filter.query, ignoreCase = true) ||
                item.sheet.region.contains(filter.query, ignoreCase = true) ||
                item.sheet.teamName.contains(filter.query, ignoreCase = true) ||
                item.sheet.department.contains(filter.query, ignoreCase = true)

            val matchesDept = filter.dept == null || filter.dept == "ALL" || item.sheet.department == filter.dept
            val matchesStatus = filter.status == null || filter.status == "ALL" || item.sheet.status == filter.status

            matchesTeamScope && matchesQuery && matchesDept && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedClearance: StateFlow<ClearanceWithItems?> = combine(
        allClearances,
        selectedSheetId
    ) { list, id ->
        if (id == null) {
            list.firstOrNull()
        } else {
            list.firstOrNull { it.sheet.id == id }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun selectSheet(sheetId: Long?) {
        _selectedSheetId.value = sheetId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDepartmentFilter(dept: String?) {
        _departmentFilter.value = dept
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }

    fun setTeamFilter(teamCode: String?) {
        _teamFilter.value = teamCode
    }

    fun logout() {
        val newState = UserSessionState(
            email = "",
            name = "",
            roleTitle = "مستخدم جديد",
            isManager = false,
            activeTeamCode = null,
            activeTeamName = null,
            isRegistered = false
        )
        userSession.value = newState
        saveSessionToDisk(newState)
    }

    // Team Leader Email Approval and Authorization Verification
    fun isTeamLeaderApproved(email: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val approvedSet = prefs.getStringSet("approved_team_leaders", emptySet()) ?: emptySet()
        val isSuperApproved = prefs.getBoolean("super_admin_approved", false)
        if (cleanEmail.equals(SystelConstants.MANAGER_EMAIL.lowercase(), ignoreCase = true) && isSuperApproved) {
            return true
        }
        return approvedSet.contains(cleanEmail)
    }

    fun isSuperAdminUser(): Boolean {
        val session = userSession.value
        return session.isManager && session.email.trim().equals(SystelConstants.MANAGER_EMAIL, ignoreCase = true)
    }

    fun canDirectLoginAsManager(): Boolean {
        val approvedSet = prefs.getStringSet("approved_team_leaders", emptySet()) ?: emptySet()
        val isSuperApproved = prefs.getBoolean("super_admin_approved", false)
        return isSuperApproved || approvedSet.isNotEmpty()
    }

    fun requestTeamLeaderApproval(
        name: String,
        email: String,
        phone: String,
        department: String,
        password: String = ""
    ): String {
        val code = (100000..999999).random().toString()
        prefs.edit().apply {
            putString("pending_tl_code", code)
            putString("pending_tl_name", name.trim())
            putString("pending_tl_email", email.trim().lowercase())
            putString("pending_tl_phone", phone.trim())
            putString("pending_tl_dept", department)
            putString("pending_tl_password", password)
            putLong("pending_tl_time", System.currentTimeMillis())
            apply()
        }
        return code
    }

    fun getPendingApprovalCode(): String? = prefs.getString("pending_tl_code", null)

    fun saveLeaderCredentials(email: String, password: String) {
        prefs.edit().putString("tl_password_${email.trim().lowercase()}", password).apply()
    }

    fun getLeaderSavedPassword(email: String): String? {
        return prefs.getString("tl_password_${email.trim().lowercase()}", null)
    }

    fun verifyAndApproveTeamLeader(enteredCode: String, customName: String? = null): Boolean {
        val pendingCode = prefs.getString("pending_tl_code", null)
        val pendingEmail = prefs.getString("pending_tl_email", "") ?: ""
        val pendingPassword = prefs.getString("pending_tl_password", "") ?: ""
        val resolvedName = customName?.ifBlank { null }
            ?: prefs.getString("pending_tl_name", "م. ياسر عواد")
            ?: "م. ياسر عواد"

        val trimmed = enteredCode.trim()
        val isCodeValid = pendingCode != null && trimmed == pendingCode

        if (isCodeValid) {
            val approvedSet = (prefs.getStringSet("approved_team_leaders", emptySet()) ?: emptySet()).toMutableSet()
            if (pendingEmail.isNotBlank()) {
                approvedSet.add(pendingEmail.lowercase())
                if (pendingPassword.isNotBlank()) {
                    prefs.edit().putString("tl_password_${pendingEmail.lowercase()}", pendingPassword).apply()
                }
            }
            prefs.edit().apply {
                putStringSet("approved_team_leaders", approvedSet)
                putBoolean("super_admin_approved", true)
                remove("pending_tl_code")
                remove("pending_tl_password")
                apply()
            }
            loginAsManager(resolvedName)
            return true
        }
        return false
    }

    suspend fun registerUserAccount(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        department: String,
        teamName: String,
        isTeamLeader: Boolean
    ): AuthResult {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()
        val cleanTeam = teamName.trim().ifBlank { if (isTeamLeader) "إدارة العمليات - التيم ليدر" else "تيم $cleanName" }
        val cleanDept = department.trim().ifBlank { SystelConstants.DEPARTMENTS.first() }

        if (cleanName.length < 3) {
            return AuthResult.Error("يرجى إدخال الاسم كاملاً (3 أحرف على الأقل)")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return AuthResult.Error("يرجى إدخال بريد إلكتروني صحيح ومطابق")
        }
        if (cleanPhone.length < 8) {
            return AuthResult.Error("يرجى إدخال رقم هاتف صحيح ومكون من 8 إلى 11 رقماً")
        }
        if (cleanPassword.length < 4) {
            return AuthResult.Error("كلمة المرور يجب أن تكون 4 أحرف أو أرقام على الأقل")
        }

        // Check if already registered
        val existingByEmail = repository.findAccount(cleanEmail)
        if (existingByEmail != null) {
            return AuthResult.Error("البريد الإلكتروني مسجل بالفعل! يرجى تسجيل الدخول أو استخدام بريد آخر.")
        }
        val existingByPhone = repository.findAccount(cleanPhone)
        if (existingByPhone != null) {
            return AuthResult.Error("رقم الهاتف مسجل بالفعل مسبقاً! يرجى تسجيل الدخول أو مراجعة التيم ليدر.")
        }

        val activationCode = (100000..999999).random().toString()
        val account = UserAccountEntity(
            fullName = cleanName,
            email = cleanEmail,
            phone = cleanPhone,
            password = cleanPassword,
            department = cleanDept,
            teamName = cleanTeam,
            isTeamLeader = isTeamLeader,
            status = "PENDING_ACTIVATION",
            activationCode = activationCode,
            emailVerified = true,
            phoneVerified = true,
            requestedAt = System.currentTimeMillis()
        )

        val insertedId = repository.registerUserAccount(account)
        val savedAccount = account.copy(id = insertedId)

        if (isTeamLeader) {
            requestTeamLeaderApproval(
                name = cleanName,
                email = cleanEmail,
                phone = cleanPhone,
                department = cleanDept,
                password = cleanPassword
            )
        }

        return AuthResult.PendingApproval(
            account = savedAccount,
            message = "تم تسجيل بياناتك بنجاح! تم إرسال طلب التفعيل إلى المهندس / ياسر عواد ولا يتم تفعيل الحساب إلا بعد الموافقة."
        )
    }

    suspend fun loginWithCredentials(
        identifier: String,
        password: String
    ): AuthResult {
        val cleanId = identifier.trim()
        val cleanPass = password.trim()

        if (cleanId.isBlank() || cleanPass.isBlank()) {
            return AuthResult.Error("يرجى إدخال البريد الإلكتروني أو رقم الهاتف وكلمة المرور")
        }

        // Check database for registered account
        val account = repository.authenticate(cleanId, cleanPass)
        if (account != null) {
            when (account.status) {
                "PENDING_ACTIVATION" -> {
                    return AuthResult.PendingApproval(
                        account = account,
                        message = "حسابك قيد المراجعة بانتظار موافقة التيم ليدر (المدير). لن تتمكن من الدخول إلا بعد الموافقة والاعتماد."
                    )
                }
                "REJECTED" -> {
                    return AuthResult.Rejected("تم رفض طلب تفعيل الحساب من قبل التيم ليدر. يرجى التواصل مع الإدارة.")
                }
                "ACTIVE" -> {
                    if (account.isTeamLeader) {
                        loginAsManager(account.fullName)
                    } else {
                        // Ensure team entity exists for isolation
                        val teams = repository.allTeams.firstOrNull() ?: emptyList()
                        var team = teams.find { it.email.equals(account.email, ignoreCase = true) || it.leaderName == account.fullName }
                        if (team == null) {
                            val newTeam = TeamEntity(
                                teamCode = "TEAM_${(100..999).random()}",
                                teamName = account.teamName.ifBlank { "تيم ${account.fullName}" },
                                leaderName = account.fullName,
                                department = account.department,
                                phone = account.phone,
                                email = account.email
                            )
                            repository.createTeam(newTeam)
                            team = newTeam
                        }
                        loginAsTeam(team)
                    }
                    return AuthResult.Success(account, "مرحباً ${account.fullName}، تم تسجيل الدخول بنجاح!")
                }
                else -> {
                    return AuthResult.PendingApproval(account, "الحساب قيد المراجعة بانتظار موافقة التيم ليدر.")
                }
            }
        }

        // If not in accounts table, check master/pre-approved Team Leader credentials
        val isMasterManager = (cleanId.equals(SystelConstants.MANAGER_EMAIL, ignoreCase = true) ||
                cleanId.equals("yasser.awad", ignoreCase = true) ||
                cleanId.equals("yasser", ignoreCase = true) ||
                cleanId.equals("01000000000", ignoreCase = true)) &&
                (cleanPass == "2026" || cleanPass == "SYSTEL2026" || cleanPass == getLeaderSavedPassword(cleanId))

        if (isMasterManager) {
            loginAsManager("م. ياسر عواد")
            val dummyAccount = UserAccountEntity(
                fullName = "م. ياسر عواد",
                email = SystelConstants.MANAGER_EMAIL,
                phone = "01000000000",
                password = cleanPass,
                department = "إدارة العمليات",
                teamName = "الإدارة العليا",
                isTeamLeader = true,
                status = "ACTIVE"
            )
            return AuthResult.Success(dummyAccount, "تم الدخول بصلاحيات التيم ليدر المعتمد")
        }

        // Check if account exists with wrong password
        val existing = repository.findAccount(cleanId)
        if (existing != null) {
            return AuthResult.Error("كلمة المرور غير صحيحة، يرجى إعادة المحاولة.")
        }

        return AuthResult.Error("الحساب غير موجود! يرجى إنشاء حساب جديد وإرسال طلب التفعيل للتيم ليدر.")
    }

    fun approveUserAccount(account: UserAccountEntity) {
        if (!isSuperAdminUser()) return
        viewModelScope.launch {
            val approverName = userSession.value.name.ifBlank { "م. ياسر عواد" }
            repository.approveAccount(account.id, approverName)
            if (!account.isTeamLeader) {
                // Ensure team entity exists
                val teams = repository.allTeams.firstOrNull() ?: emptyList()
                val exists = teams.any { it.email.equals(account.email, ignoreCase = true) || it.leaderName == account.fullName }
                if (!exists) {
                    val newTeam = TeamEntity(
                        teamCode = "TEAM_${(100..999).random()}",
                        teamName = account.teamName.ifBlank { "تيم ${account.fullName}" },
                        leaderName = account.fullName,
                        department = account.department,
                        phone = account.phone,
                        email = account.email
                    )
                    repository.createTeam(newTeam)
                }
            } else {
                val approvedSet = (prefs.getStringSet("approved_team_leaders", emptySet()) ?: emptySet()).toMutableSet()
                approvedSet.add(account.email.lowercase())
                prefs.edit().putStringSet("approved_team_leaders", approvedSet).apply()
            }
        }
    }

    fun rejectUserAccount(account: UserAccountEntity) {
        if (!isSuperAdminUser()) return
        viewModelScope.launch {
            val approverName = userSession.value.name.ifBlank { "م. ياسر عواد" }
            repository.rejectAccount(account.id, approverName)
        }
    }

    suspend fun activateUserByActivationCode(code: String): Boolean {
        val trimmed = code.trim()
        if (trimmed.length < 5) return false
        val approverName = "اعتماد م. ياسر عواد"
        return repository.activateAccountByCode(trimmed, approverName)
    }

    fun sendAccountActivationEmail(context: Context, account: UserAccountEntity) {
        try {
            val roleText = if (account.isTeamLeader) "تيم ليدر (مدير فريق)" else "مهندس موقع / تيم ميداني"
            val emailIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("mailto:")
                putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(SystelConstants.MANAGER_EMAIL))
                putExtra(
                    android.content.Intent.EXTRA_SUBJECT,
                    "[طلب تفعيل حساب جديد] - سيستل للاتصالات: ${account.fullName} ($roleText)"
                )
                putExtra(
                    android.content.Intent.EXTRA_TEXT,
                    """
السيد المهندس / ياسر عواد (التيم ليدر - سيستل للاتصالات)
تحية طيبة وبعد،،

نحيط سيادتكم علماً بوجود طلب تسجيل وتفعيل حساب جديد على تطبيق (تصفية سيستل للاتصالات) بانتظار موافقتكم الكريمة:

═════════════════════════════════════
📋 بيانات الموظف صاحب الطلب:
═════════════════════════════════════
• الاسم: ${account.fullName}
• الدور المطلوب: $roleText
• البريد الإلكتروني: ${account.email}
• رقم الهاتف: ${account.phone}
• القسم: ${account.department}
• اسم الفريق / المحطة: ${account.teamName}
• تاريخ الطلب: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(account.requestedAt))}

🔑 كود التفعيل المباشر:
${account.activationCode}

═════════════════════════════════════
برجاء التكرم باعتماد وتفعيل الحساب من داخل التطبيق أو تزويد المهندس بكود التفعيل.
شاكرين لسيادتكم حسن التعاون،،

— نظام تصفية عهدة ومصاريف مواقع سيستل
                    """.trimIndent()
                )
            }
            context.startActivity(android.content.Intent.createChooser(emailIntent, "إرسال طلب التفعيل إلى التيم ليدر عبر الإيميل"))
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "تعذر فتح تطبيق البريد. كود التفعيل: ${account.activationCode}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    // Completely reset app database and all preferences to a factory clean state with NO DATA
    fun resetAllDataToCleanState() {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication(), viewModelScope)
            db.clearAllTables()
            prefs.edit().clear().apply()
            val sysPrefs = getApplication<Application>().getSharedPreferences("systel_prefs", Context.MODE_PRIVATE)
            sysPrefs.edit().clear().apply()
            userSession.value = UserSessionState(
                email = "",
                name = "",
                roleTitle = "مستخدم جديد",
                isManager = false,
                activeTeamCode = null,
                activeTeamName = null,
                isRegistered = false
            )
        }
    }

    // Role switcher: Team Leader vs Field Team
    fun loginAsManager(managerName: String? = null) {
        val resolvedName = if (!managerName.isNullOrBlank()) managerName else "م. ياسر عواد"
        val newState = UserSessionState(
            email = SystelConstants.MANAGER_EMAIL,
            name = resolvedName,
            roleTitle = SystelConstants.MANAGER_TITLE,
            isManager = true,
            activeTeamCode = null,
            activeTeamName = null,
            isRegistered = true
        )
        userSession.value = newState
        saveSessionToDisk(newState)
    }

    fun loginAsTeam(team: TeamEntity) {
        val newState = UserSessionState(
            email = if (team.email.isNotBlank()) team.email else "${team.teamCode.lowercase()}@systel.com.eg",
            name = team.leaderName,
            roleTitle = team.teamName,
            isManager = false,
            activeTeamCode = team.teamCode,
            activeTeamName = team.teamName,
            isRegistered = true
        )
        userSession.value = newState
        saveSessionToDisk(newState)
    }

    fun registerNewTeam(
        teamName: String,
        leaderName: String,
        phone: String,
        email: String,
        department: String,
        isTeamLeader: Boolean = false
    ) {
        viewModelScope.launch {
            // Strictly check Team Leader registration: only allowed if verified & approved via email!
            if (isTeamLeader) {
                if (isTeamLeaderApproved(email)) {
                    loginAsManager(leaderName.ifBlank { "م. ياسر عواد" })
                }
                return@launch
            }

            val randomCode = "TEAM_${(100..999).random()}"
            val newTeam = TeamEntity(
                teamCode = randomCode,
                teamName = teamName,
                leaderName = leaderName,
                department = department,
                phone = phone,
                email = email
            )
            repository.createTeam(newTeam)
            // Immediately log in as the newly registered team
            loginAsTeam(newTeam)
        }
    }

    fun approveClearance(
        sheetId: Long,
        notes: String = "تمت المراجعة والاعتماد والمطابقة بالسجلات",
        signatureData: String? = null
    ) {
        if (!userSession.value.isManager) return
        viewModelScope.launch {
            val approverName = userSession.value.name.ifBlank { "م. ياسر عواد" }
            repository.approveSheet(
                sheetId = sheetId,
                managerName = approverName,
                approvalNotes = notes,
                signatureData = signatureData
            )
        }
    }

    fun rejectClearance(sheetId: Long, reason: String = "تم رفض التصفية، يرجى مراجعة البنود والمستندات") {
        if (!userSession.value.isManager) return
        viewModelScope.launch {
            repository.rejectSheet(
                sheetId = sheetId,
                managerName = userSession.value.name,
                rejectionReason = reason
            )
        }
    }

    fun revokeApproval(sheetId: Long) {
        if (!userSession.value.isManager) return
        viewModelScope.launch {
            repository.revokeApproval(sheetId)
        }
    }

    fun addExpenseItem(
        sheetId: Long,
        category: String,
        description: String,
        amount: Double,
        invoiceNumber: String = "",
        date: String = ""
    ) {
        viewModelScope.launch {
            repository.addExpenseItem(
                ExpenseItemEntity(
                    sheetId = sheetId,
                    category = category,
                    description = description,
                    amount = amount,
                    invoiceNumber = invoiceNumber,
                    date = date
                )
            )
        }
    }

    fun deleteExpenseItem(item: ExpenseItemEntity) {
        viewModelScope.launch {
            repository.deleteExpenseItem(item)
        }
    }

    fun updateExpenseItem(item: ExpenseItemEntity) {
        viewModelScope.launch {
            repository.updateExpenseItem(item)
        }
    }

    fun createClearance(
        siteName: String,
        department: String,
        region: String,
        startDate: String,
        endDate: String,
        activityType: String,
        teamLeader: String,
        teamMembers: String,
        advanceAmount: Double,
        teamCode: String? = null,
        teamName: String? = null
    ) {
        viewModelScope.launch {
            val randomNum = (1000..9999).random()
            val sheetNumber = "SYS-2026-$randomNum"
            val session = userSession.value

            // Assign team based on current user session
            val finalTeamCode = teamCode ?: session.activeTeamCode ?: (if (session.isManager) "MANAGER_LEADER" else "TEAM_GENERAL")
            val finalTeamName = teamName ?: session.activeTeamName ?: (if (session.isManager) "إدارة العمليات - التيم ليدر" else session.roleTitle)

            val sheet = ClearanceSheetEntity(
                sheetNumber = sheetNumber,
                siteName = siteName,
                department = department,
                region = region,
                startDate = startDate,
                startTime = "08:00 AM",
                endDate = endDate,
                endTime = "05:00 PM",
                activityType = activityType,
                teamLeader = teamLeader.ifBlank { session.name },
                teamMembers = teamMembers,
                teamCode = finalTeamCode,
                teamName = finalTeamName,
                advanceAmount = advanceAmount,
                status = "PENDING"
            )
            val newId = repository.createSheet(sheet)
            _selectedSheetId.value = newId
        }
    }

    fun createNewClearance(
        siteName: String,
        department: String,
        region: String,
        startDate: String,
        endDate: String,
        activityType: String,
        teamLeader: String,
        teamMembers: String,
        advanceAmount: Double,
        teamCode: String? = null,
        teamName: String? = null
    ) {
        createClearance(siteName, department, region, startDate, endDate, activityType, teamLeader, teamMembers, advanceAmount, teamCode, teamName)
    }

    fun updateAdvanceAmount(sheetId: Long, newAmount: Double) {
        viewModelScope.launch {
            repository.updateAdvanceAmount(sheetId, newAmount)
        }
    }

    fun updateTeamMembers(sheetId: Long, teamMembers: String) {
        viewModelScope.launch {
            repository.updateTeamMembers(sheetId, teamMembers)
        }
    }

    fun deleteClearance(sheet: ClearanceSheetEntity) {
        viewModelScope.launch {
            repository.deleteSheet(sheet)
        }
    }
}

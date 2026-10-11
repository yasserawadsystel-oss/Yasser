package com.example.data.repository

import com.example.data.db.ClearanceDao
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ClearanceWithItems
import com.example.data.model.ExpenseItemEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

class ClearanceRepository(private val dao: ClearanceDao) {

    val allClearances: Flow<List<ClearanceWithItems>> = dao.getAllClearanceWithItems()
    val allTeams: Flow<List<com.example.data.model.TeamEntity>> = dao.getAllTeams()
    val allAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()
    val pendingAccounts: Flow<List<UserAccountEntity>> = dao.getPendingUserAccounts()

    suspend fun registerUserAccount(account: UserAccountEntity): Long {
        return dao.insertUserAccount(account)
    }

    suspend fun findAccount(identifier: String): UserAccountEntity? {
        return dao.findAccountByEmailOrPhone(identifier, identifier)
    }

    suspend fun authenticate(identifier: String, password: String): UserAccountEntity? {
        return dao.authenticateUser(identifier, password)
    }

    suspend fun approveAccount(accountId: Long, managerName: String) {
        val now = System.currentTimeMillis()
        dao.updateAccountStatus(accountId, "ACTIVE", now, managerName)
    }

    suspend fun rejectAccount(accountId: Long, managerName: String) {
        val now = System.currentTimeMillis()
        dao.updateAccountStatus(accountId, "REJECTED", now, managerName)
    }

    suspend fun activateAccountByCode(code: String, managerName: String): Boolean {
        val count = dao.activateAccountByCode(code.trim(), System.currentTimeMillis(), managerName)
        return count > 0
    }

    fun getClearanceById(id: Long): Flow<ClearanceWithItems?> = dao.getClearanceWithItemsById(id)

    suspend fun createTeam(team: com.example.data.model.TeamEntity): Long {
        return dao.insertTeam(team)
    }

    suspend fun deleteTeam(team: com.example.data.model.TeamEntity) {
        dao.deleteTeam(team)
    }

    suspend fun createSheet(sheet: ClearanceSheetEntity): Long {
        return dao.insertSheet(sheet)
    }

    suspend fun updateSheet(sheet: ClearanceSheetEntity) {
        dao.updateSheet(sheet)
    }

    suspend fun updateAdvanceAmount(sheetId: Long, newAmount: Double) {
        dao.updateAdvanceAmount(sheetId, newAmount)
    }

    suspend fun updateTeamMembers(sheetId: Long, teamMembers: String) {
        dao.updateTeamMembers(sheetId, teamMembers)
    }

    suspend fun deleteSheet(sheet: ClearanceSheetEntity) {
        dao.deleteItemsForSheet(sheet.id)
        dao.deleteSheet(sheet)
    }

    suspend fun approveSheet(
        sheetId: Long,
        managerName: String,
        approvalNotes: String,
        signatureData: String? = null
    ) {
        val now = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        dao.updateApproval(
            sheetId = sheetId,
            status = "APPROVED",
            approvedBy = managerName,
            approvalDate = now,
            approvalNotes = approvalNotes,
            signatureData = signatureData
        )
    }

    suspend fun rejectSheet(
        sheetId: Long,
        managerName: String,
        rejectionReason: String
    ) {
        val now = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        dao.updateApproval(
            sheetId = sheetId,
            status = "REJECTED",
            approvedBy = managerName,
            approvalDate = now,
            approvalNotes = rejectionReason,
            signatureData = null
        )
    }

    suspend fun revokeApproval(sheetId: Long) {
        dao.updateApproval(
            sheetId = sheetId,
            status = "PENDING",
            approvedBy = null,
            approvalDate = null,
            approvalNotes = null,
            signatureData = null
        )
    }

    suspend fun addExpenseItem(item: ExpenseItemEntity): Long {
        return dao.insertItem(item)
    }

    suspend fun updateExpenseItem(item: ExpenseItemEntity) {
        dao.updateItem(item)
    }

    suspend fun deleteExpenseItem(item: ExpenseItemEntity) {
        dao.deleteItem(item)
    }
}

package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ClearanceSheetEntity
import com.example.data.model.ClearanceWithItems
import com.example.data.model.ExpenseItemEntity
import com.example.data.model.TeamEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClearanceDao {

    @Transaction
    @Query("SELECT * FROM clearance_sheets ORDER BY createdAt DESC")
    fun getAllClearanceWithItems(): Flow<List<ClearanceWithItems>>

    @Transaction
    @Query("SELECT * FROM clearance_sheets WHERE id = :id")
    fun getClearanceWithItemsById(id: Long): Flow<ClearanceWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSheet(sheet: ClearanceSheetEntity): Long

    @Update
    suspend fun updateSheet(sheet: ClearanceSheetEntity)

    @Delete
    suspend fun deleteSheet(sheet: ClearanceSheetEntity)

    @Query("UPDATE clearance_sheets SET status = :status, approvedBy = :approvedBy, approvalDate = :approvalDate, approvalNotes = :approvalNotes, signatureData = :signatureData WHERE id = :sheetId")
    suspend fun updateApproval(
        sheetId: Long,
        status: String,
        approvedBy: String?,
        approvalDate: String?,
        approvalNotes: String?,
        signatureData: String? = null
    )

    @Query("UPDATE clearance_sheets SET advanceAmount = :advanceAmount WHERE id = :sheetId")
    suspend fun updateAdvanceAmount(sheetId: Long, advanceAmount: Double)

    @Query("UPDATE clearance_sheets SET teamMembers = :teamMembers WHERE id = :sheetId")
    suspend fun updateTeamMembers(sheetId: Long, teamMembers: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ExpenseItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ExpenseItemEntity>)

    @Update
    suspend fun updateItem(item: ExpenseItemEntity)

    @Delete
    suspend fun deleteItem(item: ExpenseItemEntity)

    @Query("DELETE FROM expense_items WHERE sheetId = :sheetId")
    suspend fun deleteItemsForSheet(sheetId: Long)

    @Query("SELECT COUNT(*) FROM clearance_sheets")
    suspend fun getSheetsCount(): Int

    // Teams operations
    @Query("SELECT * FROM teams ORDER BY createdAt ASC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Query("SELECT COUNT(*) FROM teams")
    suspend fun getTeamsCount(): Int

    // User Accounts operations
    @Query("SELECT * FROM user_accounts ORDER BY requestedAt DESC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE status = 'PENDING_ACTIVATION' ORDER BY requestedAt DESC")
    fun getPendingUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) OR phone = :phone LIMIT 1")
    suspend fun findAccountByEmailOrPhone(email: String, phone: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE (LOWER(email) = LOWER(:identifier) OR phone = :identifier) AND password = :password LIMIT 1")
    suspend fun authenticateUser(identifier: String, password: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity): Long

    @Update
    suspend fun updateUserAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET status = :status, approvedAt = :approvedAt, approvedBy = :approvedBy WHERE id = :id")
    suspend fun updateAccountStatus(id: Long, status: String, approvedAt: Long, approvedBy: String)

    @Query("UPDATE user_accounts SET status = 'ACTIVE', approvedAt = :approvedAt, approvedBy = :approvedBy WHERE activationCode = :code")
    suspend fun activateAccountByCode(code: String, approvedAt: Long, approvedBy: String): Int

    @Delete
    suspend fun deleteUserAccount(account: UserAccountEntity)
}

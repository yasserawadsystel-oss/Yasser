package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.FirestoreClearanceSheet
import com.example.data.model.FirestoreExpenseItem
import com.example.data.model.FirestoreUserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreClearanceRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun observeAllClearanceSheets(): Flow<List<FirestoreClearanceSheet>> =
        db.collection("clearance_sheets")
            .snapshots()
            .map { it.toObjects(FirestoreClearanceSheet::class.java) }

    suspend fun saveClearanceSheet(sheet: FirestoreClearanceSheet): String {
        val uid = auth.currentUser?.uid ?: ""
        val docRef = if (sheet.id.isBlank()) {
            db.collection("clearance_sheets").document()
        } else {
            db.collection("clearance_sheets").document(sheet.id)
        }
        val sheetToSave = sheet.copy(id = docRef.id, userId = uid)
        docRef.set(sheetToSave).await()
        return docRef.id
    }

    suspend fun saveExpenseItem(sheetId: String, item: FirestoreExpenseItem): String {
        val uid = auth.currentUser?.uid ?: ""
        val docRef = if (item.id.isBlank()) {
            db.collection("clearance_sheets").document(sheetId)
                .collection("expense_items").document()
        } else {
            db.collection("clearance_sheets").document(sheetId)
                .collection("expense_items").document(item.id)
        }
        val itemToSave = item.copy(id = docRef.id, sheetId = sheetId, userId = uid)
        docRef.set(itemToSave).await()
        return docRef.id
    }

    fun observeExpenseItems(sheetId: String): Flow<List<FirestoreExpenseItem>> =
        db.collection("clearance_sheets").document(sheetId)
            .collection("expense_items")
            .snapshots()
            .map { it.toObjects(FirestoreExpenseItem::class.java) }

    suspend fun registerUserAccount(account: FirestoreUserAccount): String {
        val uid = auth.currentUser?.uid ?: ""
        val docRef = db.collection("user_accounts").document(uid.ifBlank { db.collection("user_accounts").document().id })
        val accountToSave = account.copy(id = docRef.id, userId = uid)
        docRef.set(accountToSave).await()
        return docRef.id
    }

    fun observePendingAccounts(): Flow<List<FirestoreUserAccount>> =
        db.collection("user_accounts")
            .whereEqualTo("status", "PENDING_ACTIVATION")
            .snapshots()
            .map { it.toObjects(FirestoreUserAccount::class.java) }

    suspend fun updateAccountStatus(accountId: String, status: String) {
        db.collection("user_accounts").document(accountId)
            .update("status", status)
            .await()
    }
}

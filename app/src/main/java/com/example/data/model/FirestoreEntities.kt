package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreClearanceSheet(
    @DocumentId val id: String = "",
    val sheetNumber: String = "",
    val siteName: String = "",
    val department: String = "",
    val region: String = "",
    val startDate: String = "",
    val startTime: String = "08:00 AM",
    val endDate: String = "",
    val endTime: String = "05:00 PM",
    val activityType: String = "",
    val teamLeader: String = "",
    val teamMembers: String = "",
    val teamCode: String = "",
    val teamName: String = "",
    val advanceAmount: Double = 0.0,
    val status: String = "PENDING",
    val approvedBy: String? = null,
    val approvalDate: String? = null,
    val approvalNotes: String? = null,
    val signatureData: String? = null,
    val userId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreExpenseItem(
    @DocumentId val id: String = "",
    val sheetId: String = "",
    val category: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val invoiceNumber: String = "",
    val date: String = "",
    val userId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreUserAccount(
    @DocumentId val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val teamName: String = "",
    val isTeamLeader: Boolean = false,
    val status: String = "PENDING_ACTIVATION",
    val activationCode: String = "",
    val userId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

package com.example.spendwise.data

import com.example.spendwise.model.Expense
import com.example.spendwise.model.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String?
        get() = auth.currentUser?.uid

    private val userDoc
        get() = userId?.let { db.collection("users").document(it) }

    private val expensesCollection
        get() = userDoc?.collection("expenses")

    private val friendsCollection
        get() = userDoc?.collection("friends")

    // --- Expense Methods ---
    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val collection = expensesCollection ?: return Result.failure(Exception("User not logged in"))
            val docRef = collection.document()
            val expenseWithId = expense.copy(id = docRef.id)
            docRef.set(expenseWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getExpenses(): Flow<List<Expense>> = callbackFlow {
        val collection = expensesCollection
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val expenses = snapshot.toObjects(Expense::class.java)
                    trySend(expenses)
                }
            }

        awaitClose { subscription.remove() }
    }

    // --- Friend Methods (Split Bill) ---
    suspend fun addFriend(friend: Friend): Result<Unit> {
        return try {
            val collection = friendsCollection ?: return Result.failure(Exception("User not logged in"))
            val docRef = collection.document()
            val friendWithId = friend.copy(id = docRef.id)
            docRef.set(friendWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFriends(): Flow<List<Friend>> = callbackFlow {
        val collection = friendsCollection
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(Friend::class.java))
            }
        }
        awaitClose { subscription.remove() }
    }

    // --- Budget Methods ---
    suspend fun updateBudget(amount: Double): Result<Unit> {
        return try {
            val doc = userDoc ?: return Result.failure(Exception("User not logged in"))
            doc.set(mapOf("monthlyBudget" to amount), SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBudget(): Flow<Double> = callbackFlow {
        val doc = userDoc
        if (doc == null) {
            trySend(25000.0) // Default
            close()
            return@callbackFlow
        }

        val subscription = doc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val budget = snapshot?.getDouble("monthlyBudget") ?: 25000.0
            trySend(budget)
        }
        awaitClose { subscription.remove() }
    }
}

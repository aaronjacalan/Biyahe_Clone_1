package com.android.biyahe.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.biyahe.data.Route
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    private val db = FirebaseFirestore.getInstance()
    private const val USERS_COLLECTION = "users"
    private const val TAG = "FirebaseManager"

    lateinit var current_user: User
        private set

    val isUserInitialized: Boolean
        get() = ::current_user.isInitialized

    fun addUser(
        username: String,
        password: String,
        bookmarkList: List<String>,
        shortDescription: String = "",
        context : Context,
        callback: (Boolean) -> Unit
    ) {
        // Check if username exists
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty) {
                    Log.d(TAG, "Username is already taken!")
                    Toast.makeText(context, "Username is already taken!", Toast.LENGTH_SHORT).show()
                    callback(false)
                    return@addOnSuccessListener
                }

                // After validation, add all the details of user into the database
                val newUser = hashMapOf(
                    "username" to username,
                    "password" to password,
                    "bookmarks" to bookmarkList,
                    "shortDescription" to shortDescription
                )

                db.collection(USERS_COLLECTION)
                    .add(newUser)
                    .addOnSuccessListener {
                        Log.d(TAG, "User registered successfully")
                        setCurrentUserInstanceAndPreferences(newUser, it.id)
                        callback(true)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "User registration failed: ${it.message}")
                    }

            }
            .addOnFailureListener {
                Log.e(TAG, "Error verifying user: ${it.message}")
                return@addOnFailureListener
            }
    }

    fun verifyUser(
        username: String,
        password: String,
        context: Context,
        callback: (Int) -> Unit
    ) {
        /* To certify if login credentials are true
        *   1 -> Successful Login
        *   2 -> Non-existent User
        *   3 -> Incorrect Password
        *   4 -> Firebase Error
        */
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show()
                    callback(2)
                    return@addOnSuccessListener
                }

                val doc = querySnapshot.documents[0]
                val storedPassword = doc.getString("password")!!

                if (storedPassword == password) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    val user = hashMapOf(
                        "username" to username,
                        "password" to password,
                        "bookmarks" to (doc.get("bookmarks") as? List<String> ?: emptyList()),
                        "shortDescription" to (doc.getString("shortDescription") ?: "")
                    )
                    setCurrentUserInstanceAndPreferences(user, doc.id)
                    callback(1)
                } else {
                    Toast.makeText(context, "Password is incorrect", Toast.LENGTH_SHORT).show()
                    callback(3)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error verifying user: ${it.message}")
                Toast.makeText(context, "Login failed due to error", Toast.LENGTH_SHORT).show()
                callback(4)
            }
    }

    fun updateBookmark(bookmarkList: List<Route>) {
        if (!isUserInitialized) {
            Log.e(TAG, "Cannot update bookmark: current_user not initialized")
            return
        }

        current_user.bookmarkList.clear()
        current_user.bookmarkList.addAll(bookmarkList.map { it.code })
    }

    fun remoteSaveUserInstance() {
        if (!isUserInitialized) {
            Log.e(TAG, "Cannot save user remotely: current_user not initialized")
            return
        }

        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", current_user.username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.e(TAG, "No matching user found for remote update")
                    return@addOnSuccessListener
                }

                val userDoc = querySnapshot.documents[0].reference
                val updatedData = mapOf(
                    "username" to current_user.username,
                    "password" to current_user.password,
                    "bookmarks" to current_user.bookmarkList,
                    "shortDescription" to current_user.shortDescription
                )

                userDoc.update(updatedData)
                    .addOnSuccessListener {
                        Log.d(TAG, "User data updated successfully")
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "User data update failed: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error fetching user for update: ${it.message}")
            }
    }

    fun saveUserProfileChanges(
        newUsername: String,
        newPassword: String,
        shortDescription: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (!isUserInitialized) {
            Log.e(TAG, "Cannot save profile: current_user not initialized")
            onComplete(false)
            return
        }

        val userId = current_user.id
        val updatedData = mapOf(
            "username" to newUsername,
            "password" to newPassword,
            "bookmarks" to current_user.bookmarkList,
            "shortDescription" to shortDescription
        )

        db.collection(USERS_COLLECTION).document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                current_user.username = newUsername
                current_user.password = newPassword
                current_user.shortDescription = shortDescription
                onComplete(true)
            }
            .addOnFailureListener {
                Log.e(TAG, "Profile update failed: ${it.message}")
                onComplete(false)
            }
    }

    private fun setCurrentUserInstanceAndPreferences(newUser: HashMap<String, Any>, id : String) {
        current_user = User(
            id,
            username = newUser["username"] as String,
            password = newUser["password"] as String,
            bookmarkList = (newUser["bookmarks"] as? MutableList<String>)
                ?: (newUser["bookmarks"] as? List<String>)?.toMutableList()
                ?: mutableListOf(),
            shortDescription = newUser["shortDescription"] as? String ?: ""
        )

        val routes = RouteDataManager.routelist
        val user_bookmarks : List<String> = current_user.bookmarkList
        RouteDataManager.bookmarked.clear()
        for(r in routes) {
            if(user_bookmarks.contains(r.code)) {
                RouteDataManager.bookmarked.add(r)
            }
        }
    }

    fun verifyUserByUid(uid: String, context: Context, callback: (Int) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    callback(1)
                } else {
                    callback(2)
                }
            }
            .addOnFailureListener {
                callback(3)
            }
    }

}
package com.android.biyahe.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.biyahe.data.Route
import com.android.biyahe.data.RouteDataManager
import com.android.biyahe.data.User
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    private val db = FirebaseFirestore.getInstance()
    private const val USERS_COLLECTION = "users"
    private const val TAG = "FirebaseManager"

    lateinit var current_user: User
        private set

    val isUserInitialized: Boolean
        get() = ::current_user.isInitialized

    fun addUser(username: String, password: String, bookmarkList: List<String>) {
        // After validation, add all the details of user into the database
        val newUser = hashMapOf(
            "username" to username,
            "password" to password,
            "bookmarks" to bookmarkList
        )

        db.collection(USERS_COLLECTION)
            .add(newUser)
            .addOnSuccessListener {
                Log.d(TAG, "User registered successfully")
                setCurrentUserInstance(newUser)
            }
            .addOnFailureListener {
                Log.e(TAG, "User registration failed: ${it.message}")
            }
    }

    fun verifyUser(
        username: String,
        password: String,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        // To certify if login credentials are true
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show()
                    callback(false)
                    return@addOnSuccessListener
                }

                val doc = querySnapshot.documents[0]
                val storedPassword = doc.getString("password")

                if (storedPassword == password) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    val user = hashMapOf(
                        "username" to username,
                        "password" to password,
                        "bookmarks" to (doc.get("bookmarks") as? List<String> ?: emptyList())
                    )
                    setCurrentUserInstance(user)
                    callback(true)
                } else {
                    Toast.makeText(context, "Password is incorrect", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error verifying user: ${it.message}")
                Toast.makeText(context, "Login failed due to error", Toast.LENGTH_SHORT).show()
                callback(false)
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
                    "bookmarks" to current_user.bookmarkList
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

    private fun setCurrentUserInstance(newUser: HashMap<String, Any>) {
        // Sets Current User Instance
        current_user = User(
            username = newUser["username"] as String,
            password = newUser["password"] as String,
            bookmarkList = (newUser["bookmarks"] as? MutableList<String>) ?: mutableListOf()
        )

        // Setup Current User's Bookmarks
        val routes = RouteDataManager.routelist
        val user_bookmarks : List<String> = current_user.bookmarkList
        for(r in routes) {
            if(user_bookmarks.contains(r.code)) {
                RouteDataManager.bookmarked.add(r)
            }
        }
    }
}
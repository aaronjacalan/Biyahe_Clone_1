package com.android.biyahe.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.biyahe.data.Route
import com.android.biyahe.data.User
import com.google.firebase.firestore.FirebaseFirestore

//object FirebaseManager {
//    private val db = FirebaseFirestore.getInstance()
//    lateinit var current_user : User
//
//    fun addUser(username : String, password : String, bookmarkList : List<String>) {
//        val new_user = hashMapOf(
//            "username" to username,
//            "password" to password,
//            "bookmarks" to bookmarkList
//        )
//
//        db.collection("users")
//            .document()
//            .set(new_user)
//            .addOnSuccessListener {
//                Log.e("Firebase", "User has been registered successfully!")
//                saveUserInstance(new_user)
//            }
//            .addOnFailureListener {
//                Log.e("Firebase", "User registration failed!")
//            }
//    }
//
//    fun verifyUser(username : String, password : String, context : Context) : Boolean {
//        var isValid = true
//
//        // Find user
//        db.collection("users")
//            .whereEqualTo("username", username)
//            .get()
//            .addOnSuccessListener { qs ->
//                if(qs.isEmpty) {
//                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show()
//                    isValid = false
//                } else {
//                    val stored_password = qs.documents[0].getString("password")
//                    if(stored_password == password) {
//                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
//
//                        val user = hashMapOf(
//                            "username" to username,
//                            "password" to password,
//                            "bookmarks" to qs.documents[0].get("bookmarks") as List<String>
//                        )
//                        saveUserInstance(user)
//
//                    } else {
//                        Toast.makeText(context, "Password is incorrect!", Toast.LENGTH_SHORT).show()
//                        isValid = false
//                    }
//                }
//            }
//        return isValid
//    }
//
//    /*
//        Saving custom classes to free firebase complicates things.
//        I only passed a reference to each route code to the database
//     */
//    fun updateBookmark(bookmarkList: List<Route>) {
//        for(r in bookmarkList) {
//            current_user.bookmarkList.add(r.code)
//        }
//    }
//
//    fun remoteSaveUserInstance() {
//        // Find user
//        db.collection("users")
//            .whereEqualTo("username", current_user.username)
//            .get()
//            .addOnSuccessListener { qs ->
//                // Update data
//                qs.documents[0].reference.update(
//                    mapOf(
//                        "username" to current_user.username,
//                        "password" to current_user.password,
//                        "bookmarks" to current_user.bookmarkList
//                    )
//                )
//                    .addOnSuccessListener {
//                        Log.e("Firebase", "User data has been updated successfully!")
//                    }
//                    .addOnFailureListener {
//                        Log.e("Firebase", "User data update failed!")
//
//                    }
//            }
//    }
//
//    fun saveUserInstance(new_user: HashMap<String, Any>) {
//        current_user = User(
//            new_user.get("username") as String,
//            new_user["password"] as String,
//            new_user["bookmarks"] as MutableList<String>
//        )
//    }
//}

object FirebaseManager {

    private val db = FirebaseFirestore.getInstance()
    private const val USERS_COLLECTION = "users"
    private const val TAG = "FirebaseManager"

    lateinit var current_user: User
        private set

    val isUserInitialized: Boolean
        get() = ::current_user.isInitialized

    fun addUser(username: String, password: String, bookmarkList: List<String>) {
        val newUser = hashMapOf(
            "username" to username,
            "password" to password,
            "bookmarks" to bookmarkList
        )

        db.collection(USERS_COLLECTION)
            .add(newUser)
            .addOnSuccessListener {
                Log.d(TAG, "User registered successfully")
                saveUserInstance(newUser)
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
                    saveUserInstance(user)
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

    private fun saveUserInstance(newUser: HashMap<String, Any>) {
        current_user = User(
            username = newUser["username"] as String,
            password = newUser["password"] as String,
            bookmarkList = (newUser["bookmarks"] as? MutableList<String>) ?: mutableListOf()
        )
    }
}
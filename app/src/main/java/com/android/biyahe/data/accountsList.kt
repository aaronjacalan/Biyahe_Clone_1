package com.android.biyahe.data

object AccountsList {

    // Use iconType strings you can map to resources in your app code
    val listOfAccounts = mutableListOf(
        Account("fb", "Facebook Name", "https://facebook.com", "facebook"),
        Account("google", "Google Account Name", "https://google.com", "google"),
        Account("outlook", "Outlook Account Name", "https://outlook.com", "outlook"),
        Account("github", "GitHub Account Name", "https://github.com", "github"),
        Account("test", "Test", "Test", "default")
    )

    fun addAccount(id: String, displayName: String, link: String, iconType: String = "default") {
        listOfAccounts.add(Account(id, displayName, link, iconType))
    }
}
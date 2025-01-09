package com.example.emailtask.model

data class SMTPConfig(
    val host: String,
    val port: Int,
    val email: String,
    val password: String
)

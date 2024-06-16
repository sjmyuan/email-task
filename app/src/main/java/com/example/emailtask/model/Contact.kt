package com.example.emailtask.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long,
    val name: String,
    val mobile: String,
    val email: String,
) : Parcelable

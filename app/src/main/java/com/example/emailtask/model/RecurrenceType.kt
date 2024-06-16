package com.example.emailtask.model

enum class RecurrenceType(val description: String) {
    NOT_REPEAT("Does not repeat"), DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly"), Annually("Annually"), WEEKDAY(
        "Every weekday (Monday to Friday)"
    )
}
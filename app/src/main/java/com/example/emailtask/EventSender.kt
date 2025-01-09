package com.example.emailtask

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.emailtask.model.Event
import com.example.emailtask.model.Schedule
import com.example.emailtask.model.Status
import com.example.emailtask.repository.ScheduleRepository
import com.example.emailtask.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EventSender(
    private val context: Context,
    params: WorkerParameters,
    private val scheduleRepository: ScheduleRepository,
    private val settingRepository: SettingRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val currentMoment: Instant = Clock.System.now()
            val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val smsManager = context.getSystemService(SmsManager::class.java)
            val smtpConfig = settingRepository.smtp.firstOrNull()

            val smtpSession = smtpConfig?.let {
                val props = Properties()
                props["mail.smtp.ssl.enable"] = "true"
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.host"] = it.host
                props["mail.smtp.port"] = it.port
                Session.getInstance(
                    props,
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(it.email, it.password)
                        }
                    }
                )
            }


            scheduleRepository.getAllSchedulesWithoutFlow().forEach { schedule ->
                val pendingEvents =
                    schedule.events.filter { it.status == Status.PENDING && it.sentTime <= now }
                        .sortedBy { it.sentTime }

                val processedEvents = pendingEvents.map { event ->
                    // sendSMS(smsManager, schedule, event)
                    if (smtpConfig != null && smtpSession != null)
                        sendEmail(smtpSession, smtpConfig.email, schedule, event)
                    else
                        event.copy(status = Status.FAILURE)
                }

                scheduleRepository.insertEvent(*processedEvents.toTypedArray())

            }

            return@withContext Result.success()
        }
    }

    fun sendSMS(smsManager: SmsManager, schedule: Schedule, event: Event): Event {
        try {
            smsManager.sendTextMessage(
                event.receiverMobile,
                null,
                event.message,
                null,
                null
            )
            Log.d("EVENT SENDER", "Sending SMS for ${schedule.name}")
            return event.copy(status = Status.SUCCESS)
        } catch (e: Exception) {
            Log.d(
                "EVENT SENDER",
                "There is error when sending SMS for ${schedule.name}, ${e.message}"
            )
            return event.copy(status = Status.FAILURE)
        }
    }

    private fun sendEmail(
        session: Session,
        senderEmail: String,
        schedule: Schedule,
        event: Event
    ): Event {

        try {
            val message = MimeMessage(session);

            message.setFrom(InternetAddress(senderEmail));

            message.addRecipient(Message.RecipientType.TO, InternetAddress(event.receiverEmail));

            message.subject = schedule.name

            message.setText(event.message);

            Transport.send(message);
            Log.d("EVENT SENDER", "Sending Email for ${schedule.name}")
            return event.copy(status = Status.SUCCESS)
        } catch (e: Exception) {
            Log.d(
                "EVENT SENDER",
                "There is error when sending Email for ${schedule.name}, ${e.message}"
            )
            return event.copy(status = Status.FAILURE)
        }
    }
}

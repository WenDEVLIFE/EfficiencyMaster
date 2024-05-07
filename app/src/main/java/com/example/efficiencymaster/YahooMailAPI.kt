package com.example.efficiencymaster

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.util.*

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class YahooMailAPI(
    private val mContext: Context,
    private val mEmail: String,
    private val mSubject: String,
    private val mMessage: String,
    private val codes: String
) : AsyncTask<Void, Void, Void>() {

    private lateinit var mSession: Session
    private lateinit var mProgressDialog: ProgressDialog



    override fun onPreExecute() {
        super.onPreExecute()
        mProgressDialog = ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false)
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        mProgressDialog.dismiss()
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show()
    }

    override fun doInBackground(vararg params: Void): Void? {
        val props = Properties()
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.host", "smtp.mail.yahoo.com")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465") // Port for TLS/STARTTLS

        mSession = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Utils.Email, Utils.Password)
                }
            })

        try {
            val mm = MimeMessage(mSession)

            //Setting sender address
            mm.setFrom(InternetAddress(Utils.Email))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(mEmail))
            //Adding subject
            mm.subject = mSubject
            //Adding message
            mm.setText(mMessage)
            //Sending email
            Transport.send(mm)

            // Code for adding attachments should go here

        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}
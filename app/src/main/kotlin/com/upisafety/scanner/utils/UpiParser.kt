package com.upisafety.scanner.utils

import android.net.Uri
import com.upisafety.scanner.domain.model.UpiData

object UpiParser {
    fun parseUpiQrCode(content: String): UpiData? {
        if (!content.startsWith("upi://", ignoreCase = true)) {
            return null
        }

        try {
            val uri = Uri.parse(content)
            
            return UpiData(
                rawContent = content,
                payeeAddress = uri.getQueryParameter("pa"),
                payeeName = uri.getQueryParameter("pn"),
                amount = uri.getQueryParameter("am"),
                transactionNote = uri.getQueryParameter("tn"),
                merchantCode = uri.getQueryParameter("mc"),
                url = uri.getQueryParameter("url")
            )
        } catch (e: Exception) {
            return null
        }
    }
}

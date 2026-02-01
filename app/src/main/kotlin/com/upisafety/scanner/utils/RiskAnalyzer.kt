package com.upisafety.scanner.utils

import com.upisafety.scanner.domain.model.RiskAssessment
import com.upisafety.scanner.domain.model.RiskLevel
import com.upisafety.scanner.domain.model.UpiData

object RiskAnalyzer {
    
    private val BLACKLISTED_KEYWORDS = setOf(
        "scam", "fraud", "fake", "lottery", "prize", "winner", "congratulations",
        "urgent", "verify", "suspended", "blocked", "account", "bank", "otp",
        "धोखाधड़ी", "नकली", "लॉटरी", "पुरस्कार", "जीता", "खाता", "बैंक"
    )
    
    private val SUSPICIOUS_VPA_PATTERNS = listOf(
        Regex(".*\\d{10}@.*"),  // VPA with 10 digit number
        Regex(".*admin.*"),
        Regex(".*support.*"),
        Regex(".*help.*"),
        Regex(".*customer.*"),
        Regex(".*service.*")
    )
    
    private val SUSPICIOUS_URL_PATTERNS = listOf(
        Regex(".*bit\\.ly.*"),
        Regex(".*tinyurl.*"),
        Regex(".*t\\.co.*"),
        Regex(".*goo\\.gl.*")
    )

    fun analyzeRisk(upiData: UpiData): RiskAssessment {
        val reasons = mutableListOf<String>()
        
        // Check for blacklisted keywords in transaction note
        upiData.transactionNote?.let { note ->
            if (BLACKLISTED_KEYWORDS.any { keyword -> 
                note.contains(keyword, ignoreCase = true) 
            }) {
                reasons.add("Suspicious keywords detected in transaction note")
            }
        }
        
        // Check for suspicious VPA patterns
        upiData.payeeAddress?.let { vpa ->
            if (SUSPICIOUS_VPA_PATTERNS.any { pattern -> pattern.matches(vpa) }) {
                reasons.add("Suspicious VPA pattern detected")
            }
        }
        
        // Check for name mismatch (simple check)
        if (upiData.payeeAddress != null && upiData.payeeName != null) {
            val vpaPrefix = upiData.payeeAddress.substringBefore('@').lowercase()
            val nameParts = upiData.payeeName.lowercase().split(" ")
            
            // If VPA doesn't contain any part of the name, it might be suspicious
            val hasMatch = nameParts.any { part -> 
                vpaPrefix.contains(part) || part.contains(vpaPrefix)
            }
            
            if (!hasMatch && vpaPrefix.length > 3) {
                reasons.add("Payee name and VPA mismatch")
            }
        }
        
        // Check for suspicious URLs
        upiData.url?.let { url ->
            if (SUSPICIOUS_URL_PATTERNS.any { pattern -> pattern.matches(url) }) {
                reasons.add("Suspicious URL detected")
            }
        }
        
        // Determine risk level
        val riskLevel = when {
            reasons.isEmpty() -> RiskLevel.SAFE
            reasons.size >= 2 -> RiskLevel.DANGER
            else -> RiskLevel.WARNING
        }
        
        return RiskAssessment(riskLevel, reasons)
    }
}

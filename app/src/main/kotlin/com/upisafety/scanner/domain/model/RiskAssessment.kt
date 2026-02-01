package com.upisafety.scanner.domain.model

enum class RiskLevel {
    SAFE,
    WARNING,
    DANGER
}

data class RiskAssessment(
    val level: RiskLevel,
    val reasons: List<String> = emptyList()
)

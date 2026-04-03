package com.tradingjournal.model

data class ParsedTrade(
    val entryTimeframe: String?,
    val htfBias: String?,
    val setup: String?,
    val confluences: List<String>,
    val summary: String?
)

data class TranscriptionResult(
    val text: String,
    val error: String? = null
)

data class ParseResult(
    val entryTimeframe: String?,
    val htfBias: String?,
    val setup: String?,
    val confluences: List<String>,
    val summary: String?,
    val error: String? = null
)

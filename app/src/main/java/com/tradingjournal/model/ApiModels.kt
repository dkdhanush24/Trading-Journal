package com.tradingjournal.model

data class ParsedTrade(
    val pair: String? = null,
    val entryTimeframe: String?,
    val htfBias: String?,
    val setup: String?,
    val confluences: List<String>,
    val date: String?,
    val summary: String?,
    val rrRatio: Float? = null,
    val result: String? = null
)

data class TranscriptionResult(
    val text: String,
    val error: String? = null
)

data class ParseResult(
    val pair: String? = null,
    val entryTimeframe: String?,
    val htfBias: String?,
    val setup: String?,
    val confluences: List<String>,
    val date: String?,
    val summary: String?,
    val rr_ratio: Float? = null,
    val result: String? = null,
    val error: String? = null
)

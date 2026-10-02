package com.example.bugsgame

data class Bug(
    val id: Int,
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val type: BugType,
    val points: Int,
    val imageRes: Int,
    val dx: Float,
    val dy: Float
)

enum class BugType {
    NORMAL,
    FAST,
    RARE
}
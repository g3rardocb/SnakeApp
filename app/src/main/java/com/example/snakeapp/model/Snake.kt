package com.example.snakeapp.model

data class Snake(
    val body: MutableList<Point>,
    var direction: Direction
)
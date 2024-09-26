package com.example.snakeapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snakeapp.model.Direction
import com.example.snakeapp.model.Food
import com.example.snakeapp.model.Point
import com.example.snakeapp.model.Snake

class GameViewModel : ViewModel() {

    val snake = MutableLiveData<Snake>()
    val food = MutableLiveData<Food>()
    val gameOver = MutableLiveData<Boolean>()
    val score = MutableLiveData<Int>() // Nueva variable para la puntuación

    private val gridSize = 20 // Tamaño de la cuadrícula del juego

    init {
        resetGame()
    }

    fun resetGame() {
        val initialPosition = Point(gridSize / 2, gridSize / 2)
        snake.value = Snake(mutableListOf(initialPosition), Direction.RIGHT)
        spawnFood()
        gameOver.value = false
        score.value = 0 // Reiniciar la puntuación
        // Notificar cambios
        snake.postValue(snake.value)
        food.postValue(food.value)
    }

    private fun spawnFood() {
        var newFoodPosition: Point
        do {
            val x = (0 until gridSize).random()
            val y = (0 until gridSize).random()
            newFoodPosition = Point(x, y)
        } while (snake.value?.body?.contains(newFoodPosition) == true)
        food.value = Food(newFoodPosition)
    }

    fun changeDirection(newDirection: Direction) {
        // Evitar que la serpiente se mueva en dirección opuesta inmediata
        val currentDirection = snake.value?.direction
        if (currentDirection == Direction.UP && newDirection != Direction.DOWN ||
            currentDirection == Direction.DOWN && newDirection != Direction.UP ||
            currentDirection == Direction.LEFT && newDirection != Direction.RIGHT ||
            currentDirection == Direction.RIGHT && newDirection != Direction.LEFT
        ) {
            snake.value?.direction = newDirection
        }
    }

    fun update() {
        val snakeValue = snake.value ?: return
        val newHead = getNextPosition(snakeValue)
        if (checkCollision(newHead)) {
            gameOver.value = true
            return
        }

        snakeValue.body.add(0, newHead)

        // Verificar si ha comido la comida
        if (newHead == food.value?.position) {
            spawnFood()
            // Incrementar la puntuación
            score.value = (score.value ?: 0) + 10
        } else {
            snakeValue.body.removeAt(snakeValue.body.size - 1)
        }

        snake.value = snakeValue
    }

    private fun getNextPosition(snake: Snake): Point {
        val head = snake.body.first()
        return when (snake.direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }.let {
            // Teletransportarse al otro lado si sale de los límites
            Point((it.x + gridSize) % gridSize, (it.y + gridSize) % gridSize)
        }
    }

    private fun checkCollision(point: Point): Boolean {
        // Verificar colisión con el propio cuerpo
        return snake.value?.body?.contains(point) == true
    }
}
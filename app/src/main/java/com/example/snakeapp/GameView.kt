package com.example.snakeapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.snakeapp.model.Direction
import com.example.snakeapp.model.Point

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paintSnake = Paint().apply {
        color = Color.GREEN
    }
    private val paintFood = Paint().apply {
        color = Color.RED
    }

    // Pinturas para las áreas de control
    private val paintUp = Paint().apply { color = Color.argb(50, 255, 0, 0) }      // Rojo semitransparente
    private val paintDown = Paint().apply { color = Color.argb(50, 0, 255, 0) }    // Verde semitransparente
    private val paintLeft = Paint().apply { color = Color.argb(50, 0, 0, 255) }    // Azul semitransparente
    private val paintRight = Paint().apply { color = Color.argb(50, 255, 255, 0) } // Amarillo semitransparente

    var snake: List<Point> = listOf()
    var food: Point? = null
    private var gridSize: Int = 20

    // Callback para manejar cambios de dirección
    var onDirectionChange: ((Direction) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellWidth = width / gridSize
        val cellHeight = height / gridSize

        // Dibujar las áreas de control
        drawControlAreas(canvas)

        // Dibujar la serpiente
        snake.forEach { point ->
            canvas.drawRect(
                (point.x * cellWidth).toFloat(),
                (point.y * cellHeight).toFloat(),
                ((point.x + 1) * cellWidth).toFloat(),
                ((point.y + 1) * cellHeight).toFloat(),
                paintSnake
            )
        }

        // Dibujar la comida
        food?.let { point ->
            canvas.drawRect(
                (point.x * cellWidth).toFloat(),
                (point.y * cellHeight).toFloat(),
                ((point.x + 1) * cellWidth).toFloat(),
                ((point.y + 1) * cellHeight).toFloat(),
                paintFood
            )
        }
    }

    private fun drawControlAreas(canvas: Canvas) {
        val width = this.width.toFloat()
        val height = this.height.toFloat()

        // Área superior (TOP)
        val rectTop = RectF(0f, 0f, width, height / 4)
        canvas.drawRect(rectTop, paintUp)

        // Área inferior (BOTTOM)
        val rectBottom = RectF(0f, 3 * height / 4, width, height)
        canvas.drawRect(rectBottom, paintDown)

        // Área izquierda (LEFT)
        val rectLeft = RectF(0f, height / 4, width / 4, 3 * height / 4)
        canvas.drawRect(rectLeft, paintLeft)

        // Área derecha (RIGHT)
        val rectRight = RectF(3 * width / 4, height / 4, width, 3 * height / 4)
        canvas.drawRect(rectRight, paintRight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            val width = this.width.toFloat()
            val height = this.height.toFloat()

            // Determinar en qué área se realizó el toque
            when {
                y < height / 4 -> {
                    // Área superior (TOP)
                    onDirectionChange?.invoke(Direction.UP)
                }
                y > 3 * height / 4 -> {
                    // Área inferior (BOTTOM)
                    onDirectionChange?.invoke(Direction.DOWN)
                }
                x < width / 4 -> {
                    // Área izquierda (LEFT)
                    onDirectionChange?.invoke(Direction.LEFT)
                }
                x > 3 * width / 4 -> {
                    // Área derecha (RIGHT)
                    onDirectionChange?.invoke(Direction.RIGHT)
                }
                else -> {
                    // Área central (opcionalmente puedes asignar una acción o dejarla sin efecto)
                }
            }

            return true
        }
        return super.onTouchEvent(event)
    }
}
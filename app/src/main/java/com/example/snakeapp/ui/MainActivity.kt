package com.example.snakeapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.snakeapp.GameView
import com.example.snakeapp.R
import com.example.snakeapp.viewmodel.GameViewModel
import com.example.snakeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var scoreTextView: TextView
    private lateinit var gameView: GameView

    // Variables de instancia para el Handler y el Runnable
    private lateinit var handler: Handler
    private lateinit var gameRunnable: Runnable
    private val gameSpeed = 200L // Velocidad del juego en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Habilitar Edge-to-Edge
        enableEdgeToEdge()

        // Configurar el contenido de la vista
        setContentView(binding.root)

        // Ajustar el padding para los system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Inicializar el ViewModel
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Obtener la referencia a GameView y ScoreTextView desde el binding
        gameView = binding.gameView
        scoreTextView = binding.scoreText

        // Observadores para actualizar la vista
        viewModel.snake.observe(this) { snake ->
            gameView.snake = snake.body
            gameView.invalidate()
        }

        viewModel.food.observe(this) { food ->
            gameView.food = food.position
            gameView.invalidate()
        }

        viewModel.gameOver.observe(this) { isGameOver ->
            if (isGameOver) {
                // Mostrar diálogo con la puntuación final
                showGameOverDialog()
            }
        }

        viewModel.score.observe(this) { score ->
            // Utiliza getString() para obtener la cadena formateada
            val scoreText = getString(R.string.score_text, score)
            scoreTextView.text = scoreText
        }

        // Manejar los toques para cambiar la dirección
        gameView.onDirectionChange = { direction ->
            viewModel.changeDirection(direction)
        }

        // Iniciar el loop del juego
        startGameLoop()
    }

    private fun startGameLoop() {
        handler = Handler(Looper.getMainLooper())
        gameRunnable = object : Runnable {
            override fun run() {
                viewModel.update()
                if (viewModel.gameOver.value == false) {
                    handler.postDelayed(this, gameSpeed)
                }
            }
        }
        handler.post(gameRunnable)
    }

    private fun stopGameLoop() {
        handler.removeCallbacks(gameRunnable)
    }

    private fun enableEdgeToEdge() {
        // Configurar la ventana para dibujar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun showGameOverDialog() {
        val score = viewModel.score.value ?: 0
        val message = getString(R.string.final_score_message, score)
        AlertDialog.Builder(this)
            .setTitle("¡Juego Terminado!")
            .setMessage(message)
            .setPositiveButton("Reiniciar") { _, _ ->
                // Reiniciar el juego y el bucle del juego
                viewModel.resetGame()
                startGameLoop()
            }
            .setCancelable(false)
            .show()
        // Detener el bucle del juego
        stopGameLoop()
    }
}
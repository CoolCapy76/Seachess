package com.example.seachess

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

class MainActivity2 : AppCompatActivity() {
    private val gameState = mutableListOf<FeasibleState>()
    private var fieldsUsed = 0
    private val imageViews = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val button: Button = findViewById(R.id.button2)
        button.setOnClickListener {
            val intent = Intent(this@MainActivity2, MainActivity::class.java)
            startActivity(intent)
        }

        for (i in 0 until 9) {
            gameState.add(FeasibleState.NOT_SET)
        }

        imageViews.add(findViewById(R.id.imageView0))
        imageViews.add(findViewById(R.id.imageView1))
        imageViews.add(findViewById(R.id.imageView2))
        imageViews.add(findViewById(R.id.imageView3))
        imageViews.add(findViewById(R.id.imageView4))
        imageViews.add(findViewById(R.id.imageView5))
        imageViews.add(findViewById(R.id.imageView6))
        imageViews.add(findViewById(R.id.imageView7))
        imageViews.add(findViewById(R.id.imageView8))

        imageViews.forEach {
            it.setOnClickListener { view ->
                processStateChange(view)
            }
        }

        findViewById<View>(R.id.resetGame).setOnClickListener {
            val snackbar = Snackbar.make(it, getString(R.string.reset_cancel_msg), Snackbar.LENGTH_LONG)
            val dialog = AlertDialog.Builder(this)
            dialog.apply {
                setIcon(R.drawable.ic_baseline_priority_high_24)
                setTitle(getString(R.string.alert_title))
                setMessage(getString(R.string.alert_text))
                setPositiveButton(getString(R.string.alert_yes), DialogInterface.OnClickListener { dialog, which ->
                    resetGameState()
                })
                setNegativeButton(getString(R.string.alert_no), DialogInterface.OnClickListener { dialog, which ->
                    snackbar.show()
                })
            }.show()
        }
    }

    private fun checkGameState(currentPlayer: FeasibleState): Boolean {
        val winningCombination = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )

        for (combination in winningCombination) {
            if (gameState[combination[0]] === currentPlayer &&
                gameState[combination[1]] === currentPlayer &&
                gameState[combination[2]] === currentPlayer) {
                return true
            }
        }
        return false
    }

    private fun processStateChange(view: View) {
        if (fieldsUsed >= 9) {
            return
        }

        val imgView = view as ImageView
        val position = imgView.tag.toString().toInt()

        if (gameState[position] == FeasibleState.NOT_SET) {
            imgView.setImageResource(R.drawable.player)
            imgView.isEnabled = false
            fieldsUsed++
            gameState[position] = FeasibleState.PLAYER_ONE

            if (checkResult(FeasibleState.PLAYER_ONE, R.string.player_won_message, "#64FF64")) {
                return
            }

            val handler = Handler()
            handler.postDelayed({
                doComputerMove()
            }, 300)
        }
    }

    private fun doComputerMove() {
        if (fieldsUsed >= 9) {
            return
        }

        var iRandom = (0 until 9).random()

        while (gameState[iRandom] != FeasibleState.NOT_SET) {
            iRandom = (iRandom + 1) % 9
        }

        val imgView = imageViews[iRandom]
        imgView.isEnabled = false
        imgView.setImageResource(R.drawable.computer)
        fieldsUsed++
        gameState[iRandom] = FeasibleState.COMPUTER

        if (checkResult(FeasibleState.COMPUTER, R.string.computer_won_message, "#FF6464")) {
            return
        }
    }

    private fun checkResult(currentPlayer: FeasibleState, message: Int, winnerColor: String): Boolean {
        if (checkGameState(currentPlayer)) {
            setFinalResult(message, winnerColor)
            return true
        } else if (fieldsUsed == 9) {
            setFinalResult(R.string.tie_message, "#ff00ff")
            return true
        }
        return false
    }


    private fun setFinalResult(winnerString: Int, winnerColor: String) {
        imageViews.forEach {
            it.isEnabled = false
        }

        val currentMessageTextView = findViewById<TextView>(R.id.currentMessage)
        currentMessageTextView.visibility = View.VISIBLE
        currentMessageTextView.text = getString(winnerString)
        currentMessageTextView.setTextColor(Color.parseColor(winnerColor))
    }


    private fun resetGameState() {
        for (index in 0 until 9) {
            gameState[index] = FeasibleState.NOT_SET
        }

        imageViews.forEach {
            it.setImageResource(R.drawable.not_set)
            it.isEnabled = true
        }

        findViewById<View>(R.id.currentMessage).apply {
            visibility = View.INVISIBLE
            findViewById<TextView>(R.id.currentMessage).text = "" // Clear the text
        }

        fieldsUsed = 0
    }
}

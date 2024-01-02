package minesweeper

import java.util.*

/**
 * Minesweeper class representing the Minesweeper game.
 *
 * @property rows Number of rows in the minefield.
 * @property cols Number of columns in the minefield.
 * @property mines Number of mines to be placed on the minefield.
 */
class Minesweeper(private val rows: Int, private val cols: Int, private val mines: Int) {
    // Minefield represented as a 2D array of characters
    private val minefield = Array(rows) { CharArray(cols) { '.' } }

    // Set of marked cells, each represented as a Pair of row and column indices
    private val markedCells = mutableSetOf<Pair<Int, Int>>()

    // Number of mines remaining on the minefield
    private var minesRemaining = mines

    // Number of safe cells remaining to be explored on the minefield
    private var safeCellsRemaining = rows * cols - mines

    // Flag to track the first move made by the player
    private var firstMove = true

    // Initialize the minefield by placing mines randomly
    init {
        placeMines()
    }

    /**
     * Function to randomly place mines on the minefield.
     */
    private fun placeMines() {
        val random = Random()
        repeat(mines) {
            var row: Int
            var col: Int
            do {
                row = random.nextInt(0, rows)
                col = random.nextInt(0, cols)
            } while (minefield[row][col] == 'X')

            minefield[row][col] = 'X'
        }
    }

    /**
     * Function to print the current state of the minefield.
     *
     * @param revealMines Whether to reveal mines on the minefield.
     */
    private fun printMinefield(revealMines: Boolean = false) {
        println(" │123456789│")
        println("—│—————————│")
        for ((rowIndex, row) in minefield.withIndex()) {
            print("${rowIndex + 1}│")
            for ((colIndex, cell) in row.withIndex()) {
                val coordinates = Pair(rowIndex + 1, colIndex + 1)
                print(
                    when {
                        revealMines && minefield[rowIndex][colIndex] == 'X' -> "X"
                        coordinates in markedCells -> "*"
                        !revealMines && cell == 'X' -> "."
                        cell == '0' -> "/"
                        else -> cell.toString()
                    }
                )
            }
            println("│")
        }
        println("—│—————————│")
    }

    /**
     * Function to handle a player's move.
     *
     * @param x X-coordinate of the chosen cell.
     * @param y Y-coordinate of the chosen cell.
     * @param command Move command ("mine" or "free").
     */
    private fun handleMove(x: Int, y: Int, command: String) {
        val coordinates = Pair(y, x)

        when {
            // If the cell is marked as a mine, unmark it
            coordinates in markedCells && command == "mine" -> {
                markedCells.remove(coordinates)
                minesRemaining++
            }

            // If the command is to explore a cell
            command == "free" -> {
                // If it's the first move, reveal mines in the chosen cell
                if (firstMove) {
                    firstMove = false
                    minefield[y - 1][x - 1] = '.'
                }

                when {
                    // If the chosen cell contains a mine, the game ends
                    minefield[y - 1][x - 1] == 'X' -> {
                        printMinefield(true)
                        println("You stepped on a mine and failed!")
                        return
                    }

                    // If the chosen cell is empty, explore it
                    minefield[y - 1][x - 1] == '.' -> {
                        exploreCell(x, y)
                    }
                }
            }

            // If the command is to mark a cell as a mine
            else -> {
                markedCells.add(coordinates)
                minesRemaining--
            }
        }

        printMinefield()

        // Check for winning conditions
        if (minesRemaining == 0 && markedCells.size == mines &&
            markedCells.all { minefield[it.first - 1][it.second - 1] == 'X' }
        ) {
            println("Congratulations! You found all the mines!")
            return
        } else if (safeCellsRemaining == 0) {
            println("Congratulations! You explored all safe cells!")
            return
        } else {
            println("Set/unset mines marks or claim a cell as free:")
            val (newX, newY, newCommand) = readln().split(" ")
            handleMove(newX.toInt(), newY.toInt(), newCommand)
        }
    }

    /**
     * Function to recursively explore the connected empty cells.
     *
     * @param x X-coordinate of the chosen cell.
     * @param y Y-coordinate of the chosen cell.
     */
    private fun exploreCell(x: Int, y: Int) {
        val cellValue = minefield[y - 1][x - 1]

        if (cellValue == 'X') {
            // If the chosen cell is a mine, do not explore further
            return
        }

        minefield[y - 1][x - 1] = countMinesAround(x, y).toString()[0]
        val coordinates = Pair(y, x)
        markedCells.remove(coordinates)
        safeCellsRemaining--

        if (minefield[y - 1][x - 1] == '0') {
            for (i in -1..1) {
                for (j in -1..1) {
                    val newRow = y + i
                    val newCol = x + j

                    if (newRow in 1..rows && newCol in 1..cols &&
                        minefield[newRow - 1][newCol - 1] == '.'
                    ) {
                        exploreCell(newCol, newRow)
                    }
                }
            }
        }
    }


    /**
     * Function to count the number of mines around a given cell.
     *
     * @param x X-coordinate of the chosen cell.
     * @param y Y-coordinate of the chosen cell.
     * @return Number of mines around the cell.
     */
    private fun countMinesAround(x: Int, y: Int): Int {
        var minesCount = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val newX = x + i
                val newY = y + j

                if (newX in 1..cols && newY in 1..rows &&
                    minefield[newY - 1][newX - 1] == 'X'
                ) {
                    minesCount++
                }
            }
        }
        return minesCount
    }

    /**
     * Function to start and play the Minesweeper game.
     */
    fun playGame() {
        printMinefield()

        println("Set/unset mines marks or claim a cell as free:")
        val (x, y, command) = readln().split(" ")
        handleMove(x.toInt(), y.toInt(), command)
    }
}

/**
 * Main function to initiate the Minesweeper game.
 */
fun main() {
    print("How many mines do you want on the field?")
    Minesweeper(9, 9, readln().toInt()).playGame()
}
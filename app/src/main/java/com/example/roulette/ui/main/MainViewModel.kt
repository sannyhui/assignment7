package com.example.roulette.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(state: SavedStateHandle) : ViewModel() {

    //Declare class variables
    var totalCoins = 100 //Total Coins
    var totalBet = 0 //Total Bet
    var totalWin = 0 //Total Win
    val bet = Array(8) {i -> 0} //To store bets on fruit
    var position = 0 //Store last cursor position

    //To handle bet on fruit
    fun betOnFruit(item: Int) {

        //Maximum bets is 999
        if (bet[item] < 999) {

            //Reset bet on fruit when the new turn start
            if (totalBet == 0) {
                for (i in 0..bet.size - 1) {
                    bet[i] = 0
                }
            }

            //Counting
            bet[item]++
            totalCoins--
            totalBet++
        }
    }

    //Calculate totalCoin and reset variables
    fun collectWin() {
        totalCoins += totalWin
        totalWin = 0
        totalBet = 0
    }

    //Generate destination number
    fun generateResult() : Int {
        return (0..31).random()
    }

    //Main program to compare result
    fun startGame () {

        //totalCoins = getCurrentState()

        //Declare local variables
        var result = generateResult()
        val applePosition: Array<Int> = arrayOf(3, 7, 11, 13, 19, 22, 25, 31)
        val waterMelonPosition: Array<Int> = arrayOf(1, 5, 9, 18, 23, 26)
        val pineapplePosition: Array<Int> = arrayOf(2, 10, 15, 17, 27, 30)
        val grapesPosition: Array<Int> = arrayOf(6, 14, 21, 29)
        val mangosteenPosition: Array<Int> = arrayOf(0, 8, 16, 24)
        val durianPosition: Array<Int> = arrayOf(12, 28)

        //Compare result with bet on fruit, and assign position.
        when(result) {
            in 0..7 -> {
                totalWin = 3 * bet[0]
                position = applePosition[result]
            }
            in 8..13 -> {
                totalWin = 4 * bet[1]
                position = waterMelonPosition[result - 8]
            }
            in 14..19 -> {
                totalWin = 4 * bet[2]
                position = pineapplePosition[result - 14]
            }
            in 20..23 -> {
                totalWin = 6 * bet[3]
                position = grapesPosition[result - 20]
            }
            in 24..27 -> {
                totalWin = 6 * bet[4]
                position = mangosteenPosition[result - 24]
            }
            in 28..29 -> {
                totalWin = 12 * bet[5]
                position = durianPosition[result - 28]
            }
            30 -> {
                totalWin = 24 * bet[6]
                position = 20
            }
            31 -> {
                totalWin = 28 * bet[7]
                position = 4
            }
        }

        //If lose, totalWin = -1
        if (totalWin == 0) totalWin = -1
    }
}
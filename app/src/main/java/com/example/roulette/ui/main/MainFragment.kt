package com.example.roulette.ui.main

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import com.example.roulette.databinding.MainFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment(){

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    //MVVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    //MVVM
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Show total coins and total bet
    fun showTotalCoinAndBet() {
        binding.totalCoins.text = viewModel.totalCoins.toString().padStart(8,'0')
        binding.totalBet.text = viewModel.totalBet.toString().padStart(4,'0')
    }

    //Starting here
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Instance viewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Array to hold textViews (To show number of bets of each fruit)
        val betFruit: Array<TextView> = arrayOf(binding.betApple, binding.betWaterMelon,
            binding.betPineapple, binding.betGrapes, binding.betMangosteen,
            binding.betDurian, binding.betBlueberry, binding.betKiwi
            )

        //Array to hold imageViews (For animation)
        val rotateFruit: Array<ImageView> = arrayOf(binding.imageView0, binding.imageView1,
            binding.imageView2, binding.imageView3, binding.imageView4, binding.imageView5,
            binding.imageView6, binding.imageView7, binding.imageView8, binding.imageView9,
            binding.imageView10, binding.imageView11, binding.imageView12, binding.imageView13,
            binding.imageView14, binding.imageView15, binding.imageView16, binding.imageView17,
            binding.imageView18, binding.imageView19, binding.imageView20, binding.imageView21,
            binding.imageView22, binding.imageView23, binding.imageView24, binding.imageView25,
            binding.imageView26, binding.imageView27, binding.imageView28, binding.imageView29,
            binding.imageView30, binding.imageView31)

        //To show saved record instead of 100
        showTotalCoinAndBet()

        //Start button onClickListener
        binding.buttonStart.setOnClickListener() {

            if (!viewModel.rouletteRunning) {

                //If totalWin > 0, add totalWin to totalCoin
                if (viewModel.totalWin > 0) {

                    //Add totalWin to totalCoin
                    viewModel.collectWin()

                    //Update fields
                    showTotalCoinAndBet()

                    //Update totalWin
                    binding.totalWin.text = viewModel.totalWin.toString().padStart(4, '0')

                    //Update text of button
                    binding.buttonStart.text = "Start"

                    //Update number of bet of all fruit
                    for (i in betFruit) {
                        i.text = "000"
                    }

                    //If totalWin = -1, reset variables
                } else if (viewModel.totalWin < 0) {

                    //Update text of button
                    binding.buttonStart.text = "Start"

                    //Reset totalWin to 0
                    viewModel.totalWin = 0

                    //Reset totalWin
                    viewModel.collectWin()

                    //Update fields
                    showTotalCoinAndBet()

                    //Update number of bet of all fruit
                    for (i in betFruit) {
                        i.text = "000"
                    }
                } else {

                    //Declare betCount
                    var betCount: Int = 0

                    //Sum all bets of fruit
                    for (i in 0..viewModel.bet.size - 1) {
                        betCount += viewModel.bet[i]
                    }

                    //If betCount = 0 and totalCoins is equal or more than total bets of last game
                    if (viewModel.totalCoins >= betCount && viewModel.totalBet == 0) {

                        //betCount = last game betCount
                        viewModel.totalBet = betCount

                        //Minus totalCoins by betCount
                        viewModel.totalCoins -= betCount

                        //Update bets of all fruists with last game betting
                        for (i in 0..betFruit.size - 1) {
                            betFruit[i].text = viewModel.bet[i].toString().padStart(3, '0')
                        }
                    }

                    //If user select bet by themselves
                    if (viewModel.totalBet > 0) {

                        //Retrieve last position
                        val position = viewModel.position

                        //Update fields
                        showTotalCoinAndBet()

                        //Position will be re-generated.
                        viewModel.startGame()

                        //Coroutine
                        GlobalScope.launch(context = Dispatchers.Main) {

                            viewModel.rouletteRunning = true

                            //Start from last position to 31.
                            for (i in position..31) {
                                rotateFruit[i].setBackgroundColor(Color.parseColor("#FFFF00"))
                                delay(60)
                                rotateFruit[i].setBackgroundColor(Color.parseColor("#D5E4FF"))
                            }

                            //Full loop x 1
                            for (i in 0..0) {
                                for (j in 0..31) {
                                    rotateFruit[j].setBackgroundColor(Color.parseColor("#FFFF00"))
                                    delay(60)
                                    rotateFruit[j].setBackgroundColor(Color.parseColor("#D5E4FF"))
                                }
                            }

                            //Loop until the destination - 1
                            if (viewModel.position != 0) {
                                for (i in 0..viewModel.position - 1) {
                                    rotateFruit[i].setBackgroundColor(Color.parseColor("#FFFF00"))
                                    delay(60)
                                    rotateFruit[i].setBackgroundColor(Color.parseColor("#D5E4FF"))
                                }
                            }

                            //Stay at the destination
                            rotateFruit[viewModel.position].setBackgroundColor(Color.parseColor("#FFFF00"))

                            //Update fields
                            showTotalCoinAndBet()

                            //Update button description after game
                            if (viewModel.totalWin > 0) {
                                binding.totalWin.text =
                                    viewModel.totalWin.toString().padStart(4, '0')
                                binding.buttonStart.text = "Collect Win Coins"
                            } else {

                                binding.buttonStart.text = "Try again"
                            }
                            viewModel.rouletteRunning = false
                        }
                    }
                }
            }
        }

        //Method for bet on Fruit
        fun betOnItem(item: Int, quantity: Int) {
            //Do when totalCoin >= quantity
            if (viewModel.totalCoins >=  quantity) {

                //Do when totalWin is 0 (Not > 0 or -1)
                if (viewModel.totalWin == 0) {

                    //Run method in viewModel
                    viewModel.betOnFruit(item, quantity)

                    //Update all bets on fruits
                    for (i in 0..betFruit.size - 1) {
                        betFruit[i].text = viewModel.bet[i].toString().padStart(3, '0')
                    }
                }

                //Show total coins and bets
                showTotalCoinAndBet()
            }
        }

        //Bet button listeners
        binding.buttonApple.setOnClickListener() {
            betOnItem(0, 1)
        }
        binding.buttonWaterMelon.setOnClickListener() {
            betOnItem(1, 1)
        }
        binding.buttonPineapple.setOnClickListener() {
            betOnItem(2, 1)
        }
        binding.buttonGrapes.setOnClickListener() {
            betOnItem(3, 1)
        }
        binding.buttonMangosteen.setOnClickListener() {
            betOnItem(4, 1)
        }
        binding.buttonDurian.setOnClickListener() {
            betOnItem(5, 1)
        }
        binding.buttonBlueberry.setOnClickListener() {
            betOnItem(6, 1)
        }
        binding.buttonKiwi.setOnClickListener() {
            betOnItem(7, 1)
        }

        binding.buttonApple.setOnLongClickListener {
            betOnItem(0, 10)
            return@setOnLongClickListener true
        }
        binding.buttonWaterMelon.setOnLongClickListener {
            betOnItem(1, 10)
            return@setOnLongClickListener true
        }
        binding.buttonPineapple.setOnLongClickListener {
            betOnItem(2, 10)
            return@setOnLongClickListener true
        }
        binding.buttonGrapes.setOnLongClickListener {
            betOnItem(3, 10)
            return@setOnLongClickListener true
        }
        binding.buttonMangosteen.setOnLongClickListener {
            betOnItem(4, 10)
            return@setOnLongClickListener true
        }
        binding.buttonDurian.setOnLongClickListener {
            betOnItem(5, 10)
            return@setOnLongClickListener true
        }
        binding.buttonBlueberry.setOnLongClickListener {
            betOnItem(6, 10)
            return@setOnLongClickListener true
        }
        binding.buttonKiwi.setOnLongClickListener {
            betOnItem(7, 10)
            return@setOnLongClickListener true
        }
    }
}
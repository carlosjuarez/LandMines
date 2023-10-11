package com.juvcarl.landmines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {

    private var colSize : Int = 0
    private var rowSize : Int = 0
    private var minesAmount : Int = 0
    private var isFirstMove = true

    private lateinit var minesPositions : MutableSet<Pair<Int,Int>>
    private lateinit var userGame : Array<CharArray>

    var status : GameStatus = GameStatus.NOTSTARTED

    private val _gameState : MutableStateFlow<GameState> = MutableStateFlow(GameState())
    val gameState : StateFlow<GameState> = _gameState.asStateFlow()

    private val _flagsLeft : MutableStateFlow<Int> = MutableStateFlow(0)
    val flagsLeft = _flagsLeft.asStateFlow()

    private val _timer : MutableStateFlow<Int> = MutableStateFlow(0)
    val timer : StateFlow<Int> = _timer.asStateFlow()

    var finalTimerValue = 0

    var timerJob: Job? = null

    fun initializeGame(difficulty: GameDifficulty = GameDifficulty.easy){
        when(difficulty){
            GameDifficulty.easy -> {
                colSize = 9
                rowSize = 9
                minesAmount = 10
            }
            GameDifficulty.difficult -> {
                colSize = 9
                rowSize = 18
                minesAmount = 50
            }
            GameDifficulty.medium -> {
                colSize = 9
                rowSize = 14
                minesAmount = 10
            }
        }
        isFirstMove = true
        minesPositions = createPositions(colSize,rowSize, minesAmount)
        userGame = Array(colSize){ CharArray(rowSize) { '-' } }
        status = GameStatus.STARTED
        _gameState.update {
            GameState(gameStatus = status, boardPositions = userGame.deepCopy())
        }
        _flagsLeft.update {
            minesAmount
        }
        startTimer()
    }

    private fun startTimer(){
        timerJob?.cancel()
        _timer.value = 0
        timerJob = viewModelScope.launch {
            while(true){
                delay(1000)
                _timer.value++
            }
        }
    }

    private fun stopTimer(){
        finalTimerValue = _timer.value
        timerJob?.cancel()
    }

    private fun createPositions(colSize: Int, rowSize: Int, minesAmount: Int): MutableSet<Pair<Int,Int>>{
        val positions = mutableSetOf<Pair<Int,Int>>()
        while(positions.size < minesAmount){
            val newPair = generateRandomPair(colSize,rowSize)
            if(!positions.contains(newPair)){
                positions.add(newPair)
            }
        }
        return positions
    }

    private fun generateRandomPair(rowRange: Int, colRange: Int): Pair<Int,Int>{
        val first = Random.nextInt(rowRange)
        val second = Random.nextInt(colRange)
        return Pair(first,second)
    }

    fun markFlag(tile: Pair<Int,Int>){
        if(status == GameStatus.STARTED){
            if(userGame[tile.first][tile.second] == '-' && _flagsLeft.value > 0){
                userGame[tile.first][tile.second] = 'F'
                _flagsLeft.update {
                    it - 1
                }
            }else if(userGame[tile.first][tile.second] == 'F'){
                userGame[tile.first][tile.second] = '-'
                _flagsLeft.update {
                    it + 1
                }
            }
            _gameState.update {
                it.copy(
                    boardPositions = userGame.deepCopy()
                )
            }
        }
    }

    fun selectPosition(tile: Pair<Int,Int>){
        if(minesPositions.contains(tile) && isFirstMove){
            repopulateFirstPosition(tile.first,tile.second)
        }
        isFirstMove = false

        if(minesPositions.contains(tile)){
            stopTimer()
            status = GameStatus.LOSE
            uncoverGame()
        } else if(status == GameStatus.STARTED){
            checkNeighbors(tile.first,tile.second)
        }

        if(countUncoveredTiles() == minesPositions.size) {
            stopTimer()
            status = GameStatus.WON
            uncoverGame()
        }

        _gameState.update {
            it.copy(
                gameStatus = status,
                boardPositions = userGame.deepCopy()
            )
        }
    }

    private fun repopulateFirstPosition(x: Int, y: Int){
        var added = false
        var newX = 0
        var newY = 0

        minesPositions.remove(Pair(x,y))
        while(!added){
            if(minesPositions.contains(Pair(newX,newY))){
                if(newY in 0 until colSize){
                    newY++
                }else if( newX in 0 until rowSize){
                    newX++
                    newY = 0
                }else{
                    minesPositions.add(Pair(x,y))
                }
            }else {
                minesPositions.add(Pair(newX,newY))
                added = true
            }
        }
    }

    private fun countUncoveredTiles(): Int{
        var count = 0
        val unopenedChars = listOf('-','F')
        userGame.forEach {col ->
            col.forEach {row ->
                if(unopenedChars.contains(row)){
                    count++
                }
            }
        }
        return count
    }

    private fun uncoverGame() {
        this.minesPositions.forEach {
            userGame[it.first][it.second] = 'M'
        }
    }

    private fun checkNeighbors(x: Int, y: Int){
        if(x < 0 || y < 0 || x >= rowSize || y >= colSize ){
            return
        }
        if(userGame[x][y] != '-'){
            return
        }

        val count = countNeighbors(x,y)
        userGame[x][y] = count.digitToChar()
        if(count>0){
            return
        }else{
            userGame[x][y] = '/'
        }
        checkNeighbors(x-1,y)
        checkNeighbors(x-1,y+1)
        checkNeighbors(x,y+1)
        checkNeighbors(x+1,y+1)
        checkNeighbors(x+1,y)
        checkNeighbors(x+1,y-1)
        checkNeighbors(x,y-1)
        checkNeighbors(x-1,y-1)
    }

    private fun countNeighbors(x: Int, y: Int): Int {
        return hasMine(x - 1, y - 1) +
                hasMine(x - 1, y) +
                hasMine(x - 1, y + 1) +
                hasMine(x, y - 1) +
                hasMine(x, y + 1) +
                hasMine(x + 1, y - 1) +
                hasMine(x + 1, y) +
                hasMine(x + 1, y + 1)
    }


    private fun hasMine(x: Int, y: Int): Int{
        if(x < 0 || y < 0 || x >= rowSize || y >= colSize ){
            return 0
        }
        return if(minesPositions.contains(Pair(x,y))) 1 else 0
    }
}

private fun Array<CharArray>.deepCopy(): Array<CharArray> {
    return this.map {
        it.copyOf()
    }.toTypedArray()
}

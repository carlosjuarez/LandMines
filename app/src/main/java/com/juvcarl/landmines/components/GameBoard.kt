package com.juvcarl.landmines.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.juvcarl.landmines.ui.theme.LandMinesTheme

@Composable
fun GameBoard(
    boardPositions: Array<CharArray>?,
    boardImages: Map<Char, CanvasIcon>,
    selectPosition: (Pair<Int, Int>) -> Unit,
    markFlag: (Pair<Int, Int>) -> Unit
){
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Board(
            boardPositions = boardPositions,
            boardWidth = this.constraints.maxWidth.toFloat(),
            boardHeight = this.constraints.maxWidth.toFloat(),
            onSelectedPosition = selectPosition,
            markFlag = markFlag,
            boardImages = boardImages)
    }
}

@Composable
fun Board(
    boardPositions: Array<CharArray>?,
    boardWidth: Float,
    boardHeight: Float,
    onSelectedPosition: (Pair<Int, Int>) -> Unit,
    markFlag: (Pair<Int, Int>) -> Unit,
    boardImages: Map<Char, CanvasIcon>
){
    boardPositions?.let {
        var board by remember { mutableStateOf(listOf<RectInfo>()) }
        val rows = boardPositions.size
        val cols = boardPositions[0].size
        val rectWidth = boardWidth / cols
        val rectHeight = boardHeight / rows

        board = boardPositions.flatMapIndexed{ row: Int, chars: CharArray ->
            chars.flatMapIndexed { col: Int, c: Char ->
                val rect = Rect(
                    left = col * rectWidth,
                    top = row * rectHeight,
                    right = (col * rectWidth) + rectWidth,
                    bottom = (row * rectHeight) + rectHeight,
                )
                listOf(
                    RectInfo(
                        position = Pair(row,col),
                        rect = rect,
                        tileValue = c
                    )
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            val clickedPosition = board.find { it.rect.contains(tapOffset) }
                            if (clickedPosition != null) {
                                onSelectedPosition(clickedPosition.position)
                            }
                        },
                        onLongPress = {tapOffset ->
                            val clickedPosition = board.find { it.rect.contains(tapOffset) }
                            if (clickedPosition != null) {
                                markFlag(clickedPosition.position)
                            }
                        }
                    )
                }
        ){
            for(rectInfo in board){
                drawRect(
                    style = Stroke(width = 1.dp.toPx()),
                    color = Color.Black,
                    topLeft = Offset(x = rectInfo.rect.left, y = rectInfo.rect.top),
                    size = rectInfo.rect.size
                )

                val tileValue = rectInfo.tileValue
                if(boardImages.containsKey(tileValue)){

                    val painter = boardImages.getValue(rectInfo.tileValue).image
                    val colorFilter = boardImages.getValue(rectInfo.tileValue).colorFilter

                    val size = if(tileValue == '-') rectInfo.rect.size.toDpSize().minus(DpSize(2.dp,2.dp)) else rectInfo.rect.size.toDpSize().minus(DpSize(20.dp,20.dp))
                    val leftPosition = if(tileValue == '-') rectInfo.rect.left else rectInfo.rect.left + 10.dp.toPx()
                    val topPosition = if(tileValue == '-') rectInfo.rect.top else rectInfo.rect.top + 10.dp.toPx()

                    translate(
                        left = leftPosition,
                        top = topPosition
                    ) {
                        with(painter){
                            draw(size.toSize(), colorFilter = colorFilter)
                        }
                    }
                }
            }
        }
    }
}

data class RectInfo(
    val position: Pair<Int,Int>,
    val rect: Rect,
    val tileValue: Char
)

@Preview(showBackground = true)
@Composable
fun PreviewCanvasExample(){
    LandMinesTheme {
        val boardPositions : Array<CharArray> = arrayOf(
            charArrayOf('1','2','3'),
            charArrayOf('1','2','3'),
            charArrayOf('1','2','3')
        )

        GameBoard(boardPositions, getResourcesImages(), {}, {})
    }
}
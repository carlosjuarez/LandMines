package com.juvcarl.landmines.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juvcarl.landmines.GameStatus
import com.juvcarl.landmines.GameViewModel
import com.juvcarl.landmines.R
import com.juvcarl.landmines.ui.theme.LandMinesTheme

@Composable
fun LandMineScreen(gameViewModel: GameViewModel = viewModel()){
    val resourceImages = getResourcesImages()
    val gameState by gameViewModel.gameState.collectAsStateWithLifecycle()


    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            GameSettings(gameViewModel)
            if(gameState.gameStatus != GameStatus.NOTSTARTED){
                GameBoard(
                    boardPositions = gameState.boardPositions,
                    boardImages = resourceImages,
                    selectPosition = gameViewModel::selectPosition,
                    markFlag = gameViewModel::markFlag)
            }
        }
        if(gameState.gameStatus in listOf(GameStatus.WON, GameStatus.LOSE)){
            GameResult(modifier = Modifier.align(Alignment.BottomCenter), status = gameState.gameStatus)
        }
    }
}

data class CanvasIcon(val image : VectorPainter, val colorFilter : ColorFilter)

@Composable
fun getResourcesImages(): Map<Char,CanvasIcon> {
    return mapOf(
        '-' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.unused)),
            ColorFilter.tint(Color.Gray)),
        '1' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n1)),
            ColorFilter.tint(Color.Green)),
        '2' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n2)),
            ColorFilter.tint(Color.Blue)),
        '3' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n3)),
            ColorFilter.tint(Color.Magenta)),
        '4' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n4)),
            ColorFilter.tint(Color.LightGray)),
        '5' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n5)),
            ColorFilter.tint(Color.Black)),
        '6' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n6)),
            ColorFilter.tint(Color.Black)),
        '7' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n7)),
            ColorFilter.tint(Color.Black)),
        '8' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.n8)),
            ColorFilter.tint(Color.Black)),
        'M' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.mine)),
            ColorFilter.tint(Color.Red)),
        'F' to CanvasIcon(
            rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.flag)),
            ColorFilter.tint(Color.Blue)),
    )


}

@Preview(showBackground = true)
@Composable
fun LandMinesScreenPreview(){
    LandMinesTheme {
        GameSettingsComponent(10,10,{})
        GameBoard(arrayOf(), getResourcesImages(), {}, {})
    }
}
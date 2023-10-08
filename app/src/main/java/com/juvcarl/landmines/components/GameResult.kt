package com.juvcarl.landmines.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juvcarl.landmines.GameStatus
import com.juvcarl.landmines.R
import com.juvcarl.landmines.ui.theme.LandMinesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameResult(status: GameStatus, modifier: Modifier = Modifier){
    val color = if(status == GameStatus.LOSE) Color.Red else Color.Green
    val message = if(status == GameStatus.LOSE) stringResource(id = R.string.you_lose) else stringResource(id = R.string.you_win)

    Card(modifier = modifier,
        shape = MaterialTheme.shapes.large) {
        Text(modifier = Modifier.fillMaxWidth().padding(20.dp),
            text = message,
            color = color,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun GameResultWonPreview(){
    LandMinesTheme {
        GameResult(status = GameStatus.WON)
    }
}


@Preview(showBackground = true)
@Composable
fun GameResultLosePreview(){
    LandMinesTheme {
        GameResult(status = GameStatus.LOSE)
    }
}

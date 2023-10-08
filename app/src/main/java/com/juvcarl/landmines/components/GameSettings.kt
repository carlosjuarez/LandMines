package com.juvcarl.landmines.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juvcarl.landmines.GameViewModel
import com.juvcarl.landmines.R
import com.juvcarl.landmines.ui.theme.LandMinesTheme

@Composable
fun GameSettings(gameViewModel: GameViewModel = viewModel()){
    val timer by gameViewModel.timer.collectAsStateWithLifecycle()
    val flagsLeft by gameViewModel.flagsLeft.collectAsStateWithLifecycle()
    GameSettingsComponent(flagsLeft = flagsLeft, timer = timer , newGameClick = gameViewModel::initializeGame)
}

@Composable
fun GameSettingsComponent(flagsLeft : Int, timer: Int, newGameClick: () -> Unit, modifier: Modifier = Modifier, ){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = flagsLeft.toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { newGameClick() },
                modifier = Modifier.weight(2f)) {
                Icon(painter = painterResource(id = R.drawable.baseline_tag_faces_24),
                    contentDescription = stringResource(id = R.string.start_new_game))
            }
            Text(formatSeconds(timer), textAlign = TextAlign.Right, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
        }

    }
}

private fun formatSeconds(timer: Int): String{
    val minutes = timer / 60
    val seconds = timer % 60
    return String.format("%02d:%02d",minutes,seconds)
}

@Composable
@Preview(showBackground = true)
fun GameScreenPreview(){
    LandMinesTheme {
        GameSettingsComponent(10,10,{})
    }
}
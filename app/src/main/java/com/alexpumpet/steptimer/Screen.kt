package com.alexpumpet.steptimer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexpumpet.steptimer.ui.theme.StepTimerTheme

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    viewModel: ScreenViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 64.dp)
            .padding(top = 64.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .clip(shape = CircleShape)
                .clickable { viewModel.processCommand(Command.NextStep) }
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.mainCaption,
                fontSize = if (state is ScreenState.Process) 128.sp else 64.sp,
                lineHeight = 64.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (state is ScreenState.Process) {
            Text(
                text = (state as ScreenState.Process).time,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clip(shape = CircleShape)
                    .clickable { viewModel.processCommand(Command.Finish) }
                    .aspectRatio(2 / 1f)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "STOP",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (state is ScreenState.Finish) {
            Box(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .aspectRatio(1 / 2f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState(0)),
                    color = MaterialTheme.colorScheme.tertiary,
                    text = (state as ScreenState.Finish).info,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ScreenPreview() {
    StepTimerTheme {
        Screen()
    }
}
package com.palacios.daily_notes.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.palacios.daily_notes.R
import com.palacios.daily_notes.ui.theme.DailynotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEmpty(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = stringResource(R.string.no_notes),
            fontWeight = FontWeight.Light,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun EmptyPreview(){
    DailynotesTheme {
        HomeEmpty()
    }
}
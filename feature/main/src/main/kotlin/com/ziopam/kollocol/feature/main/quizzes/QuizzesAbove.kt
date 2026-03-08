package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.other.SelectiveTabs
import com.ziopam.kollocol.core.ui.theme.AppTheme

@Composable
fun QuizzesAbove(){
    val selectionColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White
    Column(
        modifier = Modifier.padding(
            top = 15.dp,
            bottom = 10.dp
        )
    ) {
        SelectiveTabs(
            tabsCount = 2,
            modifier = Modifier.fillMaxWidth(),
            selectionWidthPadding = 8.dp,
            insideVerticalPadding = 10.dp,
            backGroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            selectionColor = selectionColor,
            selectedIndex = 0
        ) {
            TabsText("Мои квизы", true, modifier = Modifier.weight(1f))
            TabsText("Шаблоны", false, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TabsText(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
){
    Text(
        text = text,
        style = if (isSelected) MaterialTheme.typography.headlineSmall else
            MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Thin),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview
@Composable
private fun QuizzesAbovePreview() {
    AppTheme { QuizzesAbove() }
}
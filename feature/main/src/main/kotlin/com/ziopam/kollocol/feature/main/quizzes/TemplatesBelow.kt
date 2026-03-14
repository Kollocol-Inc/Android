package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.cards.QuizCard
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.R

@Composable
fun TemplatesBelow(
    templates: List<QuizInfo>,
    onTemplateClick: (QuizInfo) -> Unit,
    onStartClick: (QuizInfo) -> Unit
) {
    if (templates.isEmpty()) {
        Box (
            modifier = Modifier.fillMaxSize(),
             contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_templates),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            templates.forEach { template ->
                QuizCard(
                    quizInfo = template,
                    onClick = { onTemplateClick(template) },
                    showButton = true,
                    onButtonClick = { onStartClick(template) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplatesBelowPreview() {
    AppTheme {
        TemplatesBelow(
            templates = emptyList(),
            onTemplateClick = {},
            onStartClick = {}
        )
    }
}
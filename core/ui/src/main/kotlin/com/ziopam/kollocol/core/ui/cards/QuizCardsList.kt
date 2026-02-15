package com.ziopam.kollocol.core.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizInfo

@Composable
fun QuizCardList(
    items: List<QuizInfo>,
    onItemClick: (QuizInfo) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { items.size.coerceAtLeast(1) }
    )

    Column(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 4.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            QuizCard(
                quizInfo = items[page],
                onClick = { onItemClick(items[page]) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        PagerDots(
            count = items.size,
            activeIndex = pagerState.currentPage.coerceIn(0, items.lastIndex),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

@Composable
private fun PagerDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    spacing: Dp = 10.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { i ->
            val color = if (i == activeIndex) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)

            Box(
                modifier = Modifier
                    .padding(horizontal = spacing / 2)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizCardPreview(){
    AppTheme {
        QuizCardList(
            items = quizzesInfoExample,
            onItemClick = {}
        )
    }
}
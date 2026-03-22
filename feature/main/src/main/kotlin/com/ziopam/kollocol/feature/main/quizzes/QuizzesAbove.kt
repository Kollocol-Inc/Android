package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.animations.ExpandedAppearance
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.other.SelectiveTabs
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.feature.main.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzesAbove(
    searchString: String,
    selectedTabIndex: Int,
    onSearchStringChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCreateTemplateClick: () -> Unit = {}
){
    val selectionColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White
    val tabs = listOf(stringResource(R.string.my_quizzes), stringResource(R.string.my_templates))
    
    Column(
        modifier = Modifier.padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SelectiveTabs(
            tabs = tabs,
            selectedIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            modifier = Modifier.fillMaxWidth(),
            selectionWidthPadding = 8.dp,
            insideVerticalPadding = 10.dp,
            backGroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            selectionColor = selectionColor
        )

        SearchBar(
            placeholder = stringResource(R.string.search),
            text = searchString,
            onQueryChange = onSearchStringChange,
            modifier = Modifier.fillMaxWidth()
        )

        ExpandedAppearance (
            visible = selectedTabIndex == 1,
        ) {
            DefaultButton(
                text = stringResource(R.string.create_template),
                onClick = onCreateTemplateClick,
                isButtonEnabled = true,
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun QuizzesAbovePreview() {
    AppTheme {
        QuizzesAbove(
            searchString = "",
            selectedTabIndex = 0,
            onSearchStringChange = {},
            onTabSelected = {}
        )
    }
}
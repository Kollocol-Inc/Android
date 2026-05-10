package com.ziopam.kollocol.feature.main.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.other.SelectiveTabs
import com.ziopam.kollocol.core.ui.theme.isDarkTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun GroupsAbove(
    searchQuery: String,
    selectedTabIndex: Int,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCreateClick: () -> Unit
) {
    val selectionColor = if (isDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White
    val tabs = listOf(
        stringResource(R.string.groups_member_tab),
        stringResource(R.string.groups_owner_tab)
    )

    Column(
        modifier = Modifier.padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Группы",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            CircleIconButton(
                icon = ImageVector.vectorResource(CoreR.drawable.add),
                contentDescription = null,
                onClick = onCreateClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 5.dp)
            )
        }

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
            text = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = stringResource(R.string.search_groups),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

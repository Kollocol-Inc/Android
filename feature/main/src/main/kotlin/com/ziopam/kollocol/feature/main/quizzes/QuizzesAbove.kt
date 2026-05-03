package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ziopam.kollocol.core.ui.theme.isDarkTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.common.AiPromptDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzesAbove(
    searchString: String,
    selectedTabIndex: Int,
    onSearchStringChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCreateTemplateClick: () -> Unit = {},
    onCreateTemplateAiClick: (String) -> Unit = {}
) {
    val selectionColor = if (isDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White
    val tabs = listOf(stringResource(R.string.my_quizzes), stringResource(R.string.my_templates))
    var showCreateMenu by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }

    if (showAiDialog) {
        AiPromptDialog(
            title = stringResource(R.string.ai_prompt_title),
            hint = stringResource(R.string.ai_prompt_hint),
            prompt = aiPrompt,
            onPromptChange = { aiPrompt = it },
            onConfirm = {
                val prompt = aiPrompt.trim()
                showAiDialog = false
                aiPrompt = ""
                onCreateTemplateAiClick(prompt)
            },
            onDismiss = {
                showAiDialog = false
                aiPrompt = ""
            }
        )
    }

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
            text = searchString,
            onQueryChange = onSearchStringChange,
            modifier = Modifier.fillMaxWidth()
        )

        ExpandedAppearance(
            visible = selectedTabIndex == 1,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DefaultButton(
                    text = stringResource(R.string.create_template),
                    onClick = { showCreateMenu = true },
                    isButtonEnabled = true,
                    isWidthLimited = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.align(Alignment.Center)) {
                    DropdownMenu(
                        expanded = showCreateMenu,
                        onDismissRequest = { showCreateMenu = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📄")
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.create_from_scratch))
                                }
                            },
                            onClick = {
                                showCreateMenu = false
                                onCreateTemplateClick()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✨")
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.create_with_ai))
                                }
                            },
                            onClick = {
                                showCreateMenu = false
                                showAiDialog = true
                            }
                        )
                    }
                }
            }
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

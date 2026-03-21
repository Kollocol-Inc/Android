package com.ziopam.kollocol.core.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.ziopam.kollocol.core.ui.R

val InterFontFamily = FontFamily(

    Font(
        resId = R.font.inter,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),

    Font(
        resId = R.font.inter_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)

package com.y9vad9.todolist.composeui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalTextApi::class)
@Composable
fun TextedBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    val measurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        val availablePx = constraints.maxWidth

        val intrinsic = measurer.measure(
            text = AnnotatedString(text),
            style = textStyle,
            constraints = Constraints(maxWidth = Int.MAX_VALUE)
        ).size.width

        if (intrinsic <= availablePx) {
            Box(
                modifier = Modifier
                    .border(1.dp, color, MaterialTheme.shapes.small)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = text,
                    style = textStyle,
                    color = color,
                    maxLines = 1
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .requiredSize(8.dp)
                    .background(color, shape = CircleShape)
            )
        }
    }
}
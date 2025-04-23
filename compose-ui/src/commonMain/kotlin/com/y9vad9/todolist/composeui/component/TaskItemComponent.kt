package com.y9vad9.todolist.composeui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.y9vad9.todolist.composeui.LocalSelectedTaskId
import com.y9vad9.todolist.composeui.ext.DarkGreen
import com.y9vad9.todolist.composeui.ext.DarkOrange
import com.y9vad9.todolist.composeui.ext.dueToOrOverdueByText
import com.y9vad9.todolist.composeui.ext.shimmerBackground
import com.y9vad9.todolist.composeui.localization.LocalClock
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.domain.type.CompletedTask
import com.y9vad9.todolist.domain.type.InProgressTask
import com.y9vad9.todolist.domain.type.PlannedTask
import com.y9vad9.todolist.domain.type.Task

@Composable
fun TaskItemComponent(
    modifier: Modifier = Modifier,
    task: Task,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().then(modifier),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (LocalSelectedTaskId.current != task.id)
                MaterialTheme.colorScheme.background
            else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier.fillMaxWidth()) {
                Text(
                    text = task.name.string,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = false,
                )

                Spacer(Modifier.weight(1f).width(8.dp))

                when (task) {
                    is CompletedTask ->
                        TextedBadge(LocalStrings.current.completedTitle, color = Color.DarkGreen)

                    is InProgressTask ->
                        TextedBadge(LocalStrings.current.inProgressTitle, color = Color.DarkOrange)

                    is PlannedTask -> {}
                }
            }

            val dueOrOverdueText by task.dueToOrOverdueByText()

            Text(
                text = dueOrOverdueText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (task.isDue(LocalClock.current.now())) Color.Red else Color.Unspecified,
            )
        }
    }
}

@Composable
fun TaskItemComponentSkeleton(
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().then(modifier).padding(12.dp),
    ) {
        Text(
            modifier = Modifier.shimmerBackground(),
            text = " ".repeat(24),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            modifier = Modifier.shimmerBackground(),
            text = " ".repeat(16),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
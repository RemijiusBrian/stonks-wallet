package dev.ridill.stonkswallet.core.ui.components

import android.widget.CalendarView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import dev.ridill.stonkswallet.core.util.DateUtil
import java.time.LocalDate

@Composable
fun Calendar(
    dateString: String,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    update: CalendarView.() -> Unit = {}
) {
    Surface(
        color = containerColor,
        contentColor = contentColor
    ) {
        Column {
            /*Column(
                modifier = Modifier
                    .padding(horizontal = SpacingSmall)
            ) {
                Text(
                    text = stringResource(R.string.selected_date),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor.copy(alpha = ContentAlpha.PERCENT_60)
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.height(SpacingSmall))*/
            AndroidView(
                factory = { CalendarView(it) },
                update = { calendarView ->
                    calendarView.apply(update)

                    calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val selectedDate = DateUtil.currentDate()
                            .withYear(year)
                            .withMonth(month)
                            .withDayOfMonth(dayOfMonth)
                        onDateSelect(selectedDate)
                    }
                },
                modifier = modifier
            )
        }
    }
}
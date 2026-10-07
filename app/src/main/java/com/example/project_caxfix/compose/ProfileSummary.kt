package com.example.project_caxfix.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

// Única parte de la interfaz escrita en Kotlin. Java llama a mount desde ProfileActivity.
object ProfileSummary {
    @JvmStatic
    fun mount(view: ComposeView, total: Int, pending: Int, attention: Int, resolved: Int) {
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        view.setContent {
            val scheme = if (isSystemInDarkTheme()) darkColorScheme(primary = Color(0xFF83D6B5))
                         else lightColorScheme(primary = Color(0xFF0B4F3A))
            MaterialTheme(colorScheme = scheme) { Summary(total, pending, attention, resolved) }
        }
    }
}

@Composable
private fun Summary(total: Int, pending: Int, attention: Int, resolved: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mis reportes: $total", style = MaterialTheme.typography.titleLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Counter("Pendientes", pending)
                Counter("En atención", attention)
                Counter("Resueltos", resolved)
            }
        }
    }
}

@Composable
private fun Counter(label: String, value: Int) {
    Column {
        Text(value.toString(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

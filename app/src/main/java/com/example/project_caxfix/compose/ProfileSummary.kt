package com.example.project_caxfix.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.project_caxfix.R

// Única interfaz Kotlin/Compose; el resto de las pantallas usa Java y XML.
object ProfileSummary {
    @JvmStatic fun mount(view: ComposeView, total: Int, resolved: Int, supports: Int) {
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        view.setContent {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("$total reportes gestionados", color = Color(0xFF404944), fontSize = 12.sp, fontFamily = Jakarta)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp).background(Color(0xFFF0F3FF), RoundedCornerShape(8.dp)).padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Counter("Creados", total); Counter("Solucionados", resolved); Counter("Apoyos", supports)
                }
            }
        }
    }
}
@OptIn(ExperimentalTextApi::class)
private val Jakarta = FontFamily(
    Font(R.font.jakarta, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.jakarta, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)
@Composable private fun Counter(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Jakarta, color = Color(0xFF004635))
        Text(label, modifier = Modifier.padding(top = 4.dp), fontSize = 11.sp, fontFamily = Jakarta, color = Color(0xFF404944))
    }
}

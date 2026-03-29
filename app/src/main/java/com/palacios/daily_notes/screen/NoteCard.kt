package com.palacios.daily_notes.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.ui.theme.DailynotesTheme
import com.palacios.daily_notes.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import com.palacios.daily_notes.viewmodel.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    navController: NavHostController,
    viewModel: NoteViewModel = viewModel(),
    onDelete: () -> Unit = {
        viewModel.delete(note)
    },
    onClick: () -> Unit = {
        try {
            navController.navigate("AddEditNote/${note.id}")
        } catch (e: Exception) {
            println("Error al navegar a AddEditNote: ${e.message}")
        }
    }


) {
    val (bgColor, borderColor) = if (note.isCompleted) {
        Pair( GreeenBackCompl,GreenBorderCompl)
    } else {
        Pair(White,OrangeBackNotCompl)
    }

    val categoryColor = Color(note.categoryColor.toULong())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Border
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Títle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (note.isCompleted) "✓ ${note.title}" else "☐ ${note.title}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Descripción
                if (note.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.description,
                        fontSize = 14.sp,
                        color = GrayTextDesc,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Categoría + Fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Categoría
                    Surface(
                        color = categoryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(categoryColor, CircleShape)
                            )

                            Text(
                                text = note.category,
                                fontSize = 12.sp,
                                color = categoryColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Fecha
                    Text(
                        text = formatDate(note.createdAt),
                        fontSize = 12.sp,
                        color = GrayTextUne
                    )
                }
            }
        }
    }
}

fun formatDate(dateString: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Calendar.getInstance()
    val noteDate = Calendar.getInstance()
    try {
        noteDate.time = sdf.parse(dateString) ?: return "📅 Fecha desconocida"
    } catch (_: Exception) {
        return "📅 Fecha desconocida"
    }
    val diffDays = ((today.timeInMillis - noteDate.timeInMillis) / 86400000L).toInt()
    return when (diffDays) {
        0 -> "📅 Hoy"
        1 -> "📅 Ayer"
        -1 -> "📅 Mañana"
        else -> "📅 ${sdf.format(noteDate.time)}"
    }
}


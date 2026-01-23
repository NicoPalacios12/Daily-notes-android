package com.palacios.daily_notes.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.ui.theme.DailynotesTheme
import com.palacios.daily_notes.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    navController: NavHostController
) {
    val (bgColor, borderColor) = if (note.isCompleted) {
        Pair( GreeenBackCompl,GreenBorderCompl)
    } else {
        Pair(White,OrangeBackNotCompl)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable{navController.navigate("AddEditNote/${note.id}")}
        ) {
            // Border Color
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
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (note.isCompleted) "✓ ${note.title}" else "☐ ${note.title}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    IconButton(onClick = { onDelete() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Description
                if (note.description.isNotEmpty()) {
                    Text(
                        text = note.description ,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Category + Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category
                    Surface(
                        color = Color(0xFFE8DEF8),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = note.category,
                            fontSize = 12.sp,
                            color = Color(0xFF625B71),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Date
                    Text(
                        text = formatDate(note.createdAt),
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DailynotesTheme {
        val navController = rememberNavController()
        NoteItem(
            note = Note(
                id = 1,
                title = "Título de la nota",
                description = "Descripción de la nota",
                isCompleted = false,
                category = "General",
                createdAt = "2026-02-13"
            ),
            onClick = { },
            onDelete = { },
            navController = navController
        )
    }
}
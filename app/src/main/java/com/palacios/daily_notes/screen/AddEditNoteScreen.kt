package com.palacios.daily_notes.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.ui.theme.Purple
import com.palacios.daily_notes.ui.theme.White
import com.palacios.daily_notes.viewmodel.NoteViewModel
import com.palacios.daily_notes.ui.theme.GrayBorder
import com.palacios.daily_notes.ui.theme.GrayTextDesc
import com.palacios.daily_notes.ui.theme.LightPurple
import com.palacios.daily_notes.ui.theme.Red
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    modifier : Modifier = Modifier,
    noteViewModel: NoteViewModel = viewModel(),
    noteId : Int? = null,
    onNavigateBack : () -> Unit

){
    //Note
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("All") }
    var categoryColor by remember { mutableStateOf(Purple) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentNote by remember { mutableStateOf<Note?>(null) }


    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        noteId?.let { id->
            scope.launch{
                currentNote = noteViewModel.getNoteById(id)
                currentNote?.let{ note ->
                    title = note.title
                    description = note.description
                    category = note.category
                    isCompleted = note.isCompleted
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if(noteId == null){
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = White
                            )
                        }
                    },
                    title = {
                        Text(text = "Nueva nota")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Purple,
                        titleContentColor = White
                    )
                )
            }else{
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = White
                            )
                        }
                    },
                    title = {
                        Text(text = "Editar nota")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Purple,
                        titleContentColor = White
                    ),
                    actions = {
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ){
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete note"
                            )
                        }
                    }
                )
            }
        }
    ){padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            //Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                placeholder = { Text("Titulo de la nota")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrayBorder,
                    focusedLabelColor = GrayTextDesc
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion") },
                placeholder = { Text("Agrega tus notas")},
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp),
                maxLines = 9,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrayBorder,
                    focusedLabelColor = GrayTextDesc
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            CategorySelector(
                selectedCategory = category,
                selectedColor = categoryColor,
                onCategorySelected = { newCategory, newColor ->
                    category = newCategory
                    categoryColor = newColor
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            //isCompleted

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Marcar como completada",
                    style = MaterialTheme.typography.bodyLarge
                )
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { isCompleted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Purple,
                        uncheckedColor = GrayBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(title.isNotBlank()){
                        val note = Note(
                            id = noteId ?: 0,
                            title = title.trim(),
                            description = description.trim(),
                            isCompleted = isCompleted,
                            category = category,
                            createdAt = currentNote?.createdAt ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        )
                        if(noteId == null){
                            noteViewModel.insert(note)
                        }else{
                            noteViewModel.update(note)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .height(55.dp),
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    disabledContentColor = LightPurple
                )
            ){
                Text(
                    text = if(noteId == null) "Guardar Nota" else "Actualizar Nota",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if(showDeleteDialog){
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Red,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text(
                        text = "Eliminar nota?",
                        fontWeight = FontWeight.Bold
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentNote?.let {
                                noteViewModel.delete(it)
                            }
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) {
                        Text("Eliminar", color = White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Purple
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: String,
    selectedColor: Color,
    onCategorySelected: (String, Color) -> Unit
) {
    // Simplemente un campo de texto editable
    // El usuario puede escribir la categoría directamente

    OutlinedTextField(
        value = selectedCategory,
        onValueChange = { newCategory ->
            onCategorySelected(newCategory, selectedColor)
        },
        label = { Text("Categoría") },
        placeholder = { Text("Ej: Trabajo, Personal...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GrayBorder,
            focusedLabelColor = GrayTextDesc
        )
    )
}

@Composable
fun ColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        Color(0xFF607D8B), // Gris azulado
        Color(0xFF2196F3), // Azul
        Color(0xFF9C27B0), // Púrpura
        Color(0xFF4CAF50), // Verde
        Color(0xFFE91E63), // Rosa
        Color(0xFFFF9800), // Naranja
        Color(0xFFF44336), // Rojo
        Color(0xFF00BCD4), // Cian
        Color(0xFFFFEB3B), // Amarillo
        Color(0xFF795548)  // Marrón
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elegir Color") },
        text = {
            Column {
                Text("Selecciona el color")
                Spacer(modifier = Modifier.height(16.dp))

                // Grid
                Column {
                    repeat((colors.size + 2) / 3) { rowIndex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) { colIndex ->
                                val index = rowIndex * 3 + colIndex
                                if (index < colors.size) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(colors[index], CircleShape)
                                            .clickable {
                                                onColorSelected(colors[index])
                                                onDismiss()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (colors[index] == selectedColor) {
                                            Text(
                                                "✓",
                                                color = White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}


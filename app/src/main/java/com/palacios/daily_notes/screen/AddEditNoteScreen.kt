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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.data.entity.CategoryWithColor
import com.palacios.daily_notes.ui.theme.Purple
import com.palacios.daily_notes.ui.theme.White
import com.palacios.daily_notes.viewmodel.NoteViewModel
import com.palacios.daily_notes.ui.theme.GrayBorder
import com.palacios.daily_notes.ui.theme.GrayTextDesc
import androidx.compose.ui.res.stringResource
import com.palacios.daily_notes.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import com.palacios.daily_notes.ui.theme.All
import com.palacios.daily_notes.ui.theme.GrayTextUne
import com.palacios.daily_notes.ui.theme.Health
import com.palacios.daily_notes.ui.theme.Personal
import com.palacios.daily_notes.ui.theme.Shopping
import com.palacios.daily_notes.ui.theme.Study
import com.palacios.daily_notes.ui.theme.Work
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.emptyList


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
    var currentNote by remember { mutableStateOf<Note?>(null) }
    val savedCategories by noteViewModel.categoriesWithColors.observeAsState(emptyList())

    LaunchedEffect(noteId) {
        noteId?.let { id->
            currentNote = noteViewModel.getNoteById(id)
            currentNote?.let{ note ->
                title = note.title
                description = note.description
                category = note.category
                isCompleted = note.isCompleted
                categoryColor = Color(note.categoryColor.toULong())
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
                                contentDescription = stringResource(R.string.back),
                                tint = White
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.new_note))
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
                                contentDescription = stringResource(R.string.back),
                                tint = White
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.edit_note))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Purple,
                        titleContentColor = White
                    ),
                )
            }
        }
    ){padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Campo: Título
            Text(
                text = stringResource(R.string.title_label),
                style = MaterialTheme.typography.labelMedium,
                color = GrayTextDesc,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = GrayBorder,
                    cursorColor = Purple
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Descripción
            Text(
                text = stringResource(R.string.description_label),
                style = MaterialTheme.typography.labelMedium,
                color = GrayTextDesc,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text(stringResource(R.string.description_placeholder), color = GrayTextUne) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = GrayBorder,
                    cursorColor = Purple
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de categoría
            Text(
                text = stringResource(R.string.category_label),
                style = MaterialTheme.typography.labelMedium,
                color = GrayTextDesc,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            CategorySelector(
                selectedCategory = category,
                selectedColor = categoryColor,
                savedCategories = savedCategories,
                onCategorySelected = { newCategory, newColor ->
                    category = newCategory
                    categoryColor = newColor
                }
            )

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(32.dp))

            // Botón: Guardar
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val note = Note(
                            id = noteId ?: 0,
                            title = title.trim(),
                            description = description.trim(),
                            category = category,
                            categoryColor = categoryColor.value.toLong(),
                            isCompleted = isCompleted,
                            createdAt = currentNote?.createdAt ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        )

                        if (noteId == null) {
                            noteViewModel.insert(note)
                        } else {
                            noteViewModel.update(note)
                        }

                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    disabledContainerColor = GrayTextDesc
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.save_button),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategory: String,
    selectedColor: Color,
    savedCategories: List<CategoryWithColor>,
    onCategorySelected: (String, Color) -> Unit
) {
    // Categorías predeterminadas
    val defaultCategories = mapOf(
        "All" to All,
        "Work" to Work,
        "Personal" to Personal,
        "Shopping" to Shopping,
        "Health" to Health,
        "Study" to Study,

    )
    val customCategories = savedCategories
        .filter{it.category !in defaultCategories.keys}
        .associate { it.category to Color(it.categoryColor.toULong()) }

    val allCategories = defaultCategories + customCategories
    var expanded by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }
    var customColor by remember { mutableStateOf(Purple) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column {
        // Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},  //
                readOnly = true,      //
                label = { Text(stringResource(R.string.category_label)) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(selectedColor, CircleShape)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    focusedLabelColor = Purple
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                allCategories.forEach { (cat, color) ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(color, CircleShape)
                                )
                                Text(cat)
                            }
                        },
                        onClick = {
                            onCategorySelected(cat, color)
                            expanded = false
                        }
                    )
                }

                HorizontalDivider()

                // New Category
                DropdownMenuItem(
                    text = {
                        Text(
                            "➕ ${stringResource(R.string.create_new_category)}",
                            color = Purple,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        expanded = false
                        showCustomDialog = true
                    }
                )
            }
        }
    }

    // Create new category
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = {
                showCustomDialog = false
                customCategory = ""
            },
            title = { Text(stringResource(R.string.new_category_title)) },
            text = {
                Column {
                    // Name of category
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text(stringResource(R.string.category_name_label)) },
                        placeholder = { Text(stringResource(R.string.category_name_placeholder)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Color
                    OutlinedButton(
                        onClick = { showColorPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(customColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.choose_color))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customCategory.isNotBlank()) {
                            onCategorySelected(customCategory.trim(), customColor)
                            showCustomDialog = false
                            customCategory = ""
                            customColor = Purple
                        }
                    },
                    enabled = customCategory.isNotBlank()
                ) {
                    Text(stringResource(R.string.create_button))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCustomDialog = false
                    customCategory = ""
                }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    // Color Selector
    if (showColorPicker) {
        ColorPickerDialog(
            selectedColor = customColor,
            onColorSelected = { customColor = it },
            onDismiss = { showColorPicker = false }
        )
    }
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
        title = { Text(stringResource(R.string.color_picker_title)) },
        text = {
            Column {
                Text(stringResource(R.string.select_color))
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
                Text(stringResource(R.string.close_button))
            }
        }
    )
}

package com.palacios.daily_notes.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palacios.daily_notes.data.entity.Note
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palacios.daily_notes.ui.theme.DailynotesTheme
import com.palacios.daily_notes.ui.theme.Purple
import com.palacios.daily_notes.ui.theme.White
import com.palacios.daily_notes.ui.theme.All
import com.palacios.daily_notes.ui.theme.Personal
import com.palacios.daily_notes.ui.theme.Work
import com.palacios.daily_notes.ui.theme.Study
import com.palacios.daily_notes.ui.theme.Health
import com.palacios.daily_notes.ui.theme.Shopping
import com.palacios.daily_notes.ui.theme.Others
import com.palacios.daily_notes.viewmodel.NoteViewModel
import com.palacios.daily_notes.ui.theme.floatButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailynotesTheme {
                Navigation()

            }
        }
    }
}

@Composable
fun Navigation(
    modifier : Modifier = Modifier,
    navController : NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = "Home"
    ){
        composable(route = "Home"){
            Home(
                navController,modifier
            )
        }
        // Ruta para crear nueva nota
        composable(route = "AddEditNote"){
            AddEditNoteScreen(
                modifier = modifier,
                noteViewModel = viewModel(),
                noteId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // Ruta para editar nota existente
        composable(
            route = "AddEditNote/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )
        ){backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            AddEditNoteScreen(
                modifier = modifier,
                noteViewModel = viewModel(),
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavHostController,
    modifier : Modifier = Modifier,
    noteViewModel: NoteViewModel = viewModel()
){

    var selectedCategory by remember { mutableStateOf("All") }
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }

    LaunchedEffect(selectedCategory) {
        noteViewModel.getNotesByCategory(selectedCategory).observeForever { newNotes ->
            notes = newNotes
        }
    }

    var isSearching by remember { mutableStateOf(false)}
    var searchQuery by remember {mutableStateOf("")}

    val filteredNotes = if (searchQuery.isBlank()){
        notes
    }else {
        notes.filter{ note ->
            note.title.contains(searchQuery,ignoreCase = true) ||
                    note.description.contains(searchQuery,ignoreCase = true)
        }
    }

    LaunchedEffect(notes) {
        val list = noteViewModel.allNotes.value
        list?.forEach { note ->
            println(note.title)
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if(isSearching){
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it},
                            placeholder = { Text("Buscar")},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }else{
                        Text("Daily notes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple,
                    titleContentColor = White
                ),
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if(!isSearching) searchQuery = ""
                    }){
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search note"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("AddEditNote")
                },
                modifier = Modifier.padding(20.dp),
                containerColor = floatButton,
                contentColor = White,
                shape = CircleShape
            ){
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add note"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ){
            //CategoryBar
            CategoryBar(
                categories = listOf("All", "Personal" , "Study", "Work", "Health", "Shopping"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            // Notes
            Spacer(modifier = Modifier.height(16.dp))
            if (notes.isEmpty()) {
                HomeEmpty(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(filteredNotes) { note ->
                        NoteItem(
                            note = note,
                            navController = navController,
                            onDelete = { noteViewModel.delete(note) }
                        )
                        Spacer(modifier = modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBar(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
){
    val categoryColors = mapOf(
        "All" to All,
        "Personal" to Personal,
        "Work" to Work,
        "Study" to Study,
        "Health" to Health,
        "Shopping" to Shopping,
        "Others" to Others
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(categories){ category ->
            val backgroundColor = categoryColors[category] ?: MaterialTheme.colorScheme.primary

            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor
                )
            ){
                Text(category)
            }

        }
    }
}



package com.palacios.daily_notes.screen

import android.R.attr.title
import android.icu.util.ULocale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.data.Group
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.internal.NavContext
import com.palacios.daily_notes.data.entity.Note
import com.palacios.daily_notes.data.noteDataBase.NoteDataBase
import com.palacios.daily_notes.data.repo.NoteRepo
import com.palacios.daily_notes.ui.theme.DailynotesTheme
import com.palacios.daily_notes.screen.AddEditNoteScreen
import com.palacios.daily_notes.ui.theme.*
import com.palacios.daily_notes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.palacios.daily_notes.ui.theme.*

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
        composable(route = "AddEditNote"){
            AddEditNoteScreen(
                navController, modifier
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

    var selectedCategory by remember { mutableStateOf("General") }
    val notes = noteViewModel.getNotesByCategory(selectedCategory).value ?: emptyList()

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
                    containerColor = MoradoPrincipal,
                    titleContentColor = Blanco
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
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ){
            CategoryBar(
                categories = listOf("General", "Trabajo", "Personal", "Viajes", "Otros"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            // Ici tu peux afficher les notes filtrées
            // Par exemple :
            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items(filteredNotes){ note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigate("AddEditNote") },
                    ){
                        noteViewModel.delete(note)
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
        "Todas" to Todas,
        "Trabajo" to Trabajo,
        "Personal" to Personal,
        "Viajes" to Viajes,
        "Otros" to Otros
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



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val fakeNotes = listOf(
        Note(1, "Tareas pendientes", "lavar los platos", true , "Todas", "2024-06-01"),
        Note(2, "Estudiar", "examen de app mobiles", false, "Trabajo", "2024-06-02")
    )
    DailynotesTheme {

        Column {
            fakeNotes.forEach { note ->
                Text(note.title)
                Text(note.description)
                Text(note.createdAt)
                Text(note.category)
                Text(note.isCompleted.toString())
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

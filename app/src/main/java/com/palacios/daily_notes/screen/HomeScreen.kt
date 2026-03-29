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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
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

        composable(route = "AddEditNote"){
            AddEditNoteScreen(
                modifier = modifier,
                noteViewModel = viewModel(),
                noteId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

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

    // Default categoríes colors
    val defaultCategoryColors = mapOf(
        "All" to All,
        "Personal" to Personal,
        "Work" to Work,
        "Study" to Study,
        "Health" to Health,
        "Shopping" to Shopping
    )

    // Default Categories
    val defaultCategoriesOrder = listOf("All", "Personal", "Work", "Study", "Health", "Shopping")

    // CategoriesCustoms
    val customCategoriesWithColors by noteViewModel.categoriesWithColors.observeAsState(emptyList())


    val categoriesKey = customCategoriesWithColors.map { "${it.category}:${it.categoryColor}" }.joinToString()

    //Categories colors
    val allCategoryColors = remember(categoriesKey) {
        val combined = defaultCategoryColors.toMutableMap()
        customCategoriesWithColors.forEach { catWithColor ->
            // only add custom category if it's not already in
            if (!defaultCategoryColors.containsKey(catWithColor.category)) {
                combined[catWithColor.category] = Color(catWithColor.categoryColor.toULong())
            }
        }
        combined.toMap()
    }


    val allCategories = remember(categoriesKey) {
        defaultCategoriesOrder + customCategoriesWithColors
            .map { it.category }
            .filter { !defaultCategoryColors.containsKey(it) }
            .sorted()
    }

    var isSearching by remember { mutableStateOf(false)}
    var searchQuery by remember {mutableStateOf("")}

    val notesByCategory by noteViewModel
        .getNotesByCategory(selectedCategory)
        .observeAsState(emptyList())

    val searchedNotes by noteViewModel
        .searchNote(searchQuery)
        .observeAsState(emptyList())

    val filteredNotes = if (searchQuery.isBlank()) {
        notesByCategory
    }else {
        if (selectedCategory == "All") {
            searchedNotes
        } else {
            searchedNotes.filter { note ->
                note.category == selectedCategory
            }
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
                categories = allCategories,
                categoryColors = allCategoryColors,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            // Notes
            Spacer(modifier = Modifier.height(16.dp))
            if (filteredNotes.isEmpty()) {
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
    categoryColors: Map<String, Color>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
){

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(categories){ category ->
            val isSelected = category == selectedCategory
            val backgroundColor = categoryColors[category] ?: MaterialTheme.colorScheme.primary

            val categorySelected = if(isSelected) 1f else 0.45f
            Button(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.alpha(categorySelected),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor
                )
            ){
                Text(category)
            }

        }
    }
}



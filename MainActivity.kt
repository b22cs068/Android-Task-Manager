package com.example.androidprocessmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidprocessmonitor.ui.theme.AndroidProcessMonitorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class ProcessInfo(val pid: String, val name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProcessMonitorTheme {
                Surface {
                    ProcessMonitorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessMonitorScreen() {
    var processList by remember { mutableStateOf(emptyList<ProcessInfo>()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Start the monitoring when this composable is first launched
    LaunchedEffect(Unit) {
        while (true) {
            processList = getProcessList()
            delay(5000) // Update every 5 seconds
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Process Monitor") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchBar(searchQuery) { newQuery ->
                searchQuery = newQuery
            }
            Spacer(modifier = Modifier.height(8.dp))
            ProcessList(
                processList = processList.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                },
                onKillProcess = { pid ->
                    coroutineScope.launch {
                        killProcess(pid)
                        processList = getProcessList() // Refresh process list after killing
                    }
                }
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search Processes") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ProcessList(processList: List<ProcessInfo>, onKillProcess: (String) -> Unit) {
    if (processList.isEmpty()) {
        Text("No processes found or root access denied.")
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(processList) { process ->
                ProcessItem(process, onKillProcess)
                Divider()
            }
        }
    }
}

@Composable
fun ProcessItem(process: ProcessInfo, onKillProcess: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "PID: ${process.pid}")
            Text(text = process.name)
        }
        Button(onClick = { onKillProcess(process.pid) }) {
            Text("Kill Process")
        }
    }
}

// Function to fetch the list of processes by executing a shell command
suspend fun getProcessList(): List<ProcessInfo> = withContext(Dispatchers.IO) {
    val processList = mutableListOf<ProcessInfo>()
    try {
        val process = Runtime.getRuntime().exec("su -c ps")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine() // Skip the header
        reader.forEachLine { line ->
            val columns = line.trim().split("\\s+".toRegex())
            if (columns.size >= 9) {
                val pid = columns[1]
                val name = columns.last()
                processList.add(ProcessInfo(pid, name))
            }
        }
        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    processList
}

// Function to kill a process by PID using a root command
suspend fun killProcess(pid: String) = withContext(Dispatchers.IO) {
    try {
        Runtime.getRuntime().exec("su -c kill $pid")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

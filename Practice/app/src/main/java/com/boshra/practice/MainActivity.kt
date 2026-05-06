package com.boshra.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.boshra.practice.ui.theme.PracticeTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                val myViewModel: MyViewModel = viewModel()

                val pagerState = rememberPagerState(pageCount = { 3 })

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { page ->
                        when (page) {
                            0 -> ColdFlowScreen(viewModel = myViewModel)
                            1 -> StateFlowScreen(viewModel = myViewModel)
                            2 -> SharedFlowScreen(
                                viewModel = myViewModel,
                                onVerificationSuccess = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColdFlowScreen(viewModel: MyViewModel) {
    val countdownValue by viewModel.countdownFlow.collectAsStateWithLifecycle(initialValue = 10)
    val cryptoPrice by viewModel.cryptoPriceFlow.collectAsStateWithLifecycle(initialValue = 65000.0)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cold Flow (Page 1/3)", fontSize = 16.sp, modifier = Modifier.padding(bottom = 20.dp))

        Text(text = "remain $countdownValue minutes ", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "price $cryptoPrice $", fontSize = 20.sp)
    }
}

@Composable
fun StateFlowScreen(viewModel: MyViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "State Flow (Page 2/3)",
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when (val currentState = state) {
                    is TasksUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is TasksUiState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(currentState.tasksList) { task ->
                                Text(text = task, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                    is TasksUiState.Error -> {
                        Text(text = currentState.errorMessage)
                    }
                }
            }
        }
    }
}

@Composable
fun SharedFlowScreen(viewModel: MyViewModel, onVerificationSuccess: () -> Unit) {
    var otpInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is OtpEvent.ShowSnackbarMessage -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
                is OtpEvent.NavigateToHomeScreen -> {
                    onVerificationSuccess()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Shared Flow (Page 3/3)", fontSize = 16.sp, modifier = Modifier.padding(bottom = 20.dp))

            OutlinedTextField(
                value = otpInput,
                onValueChange = { otpInput = it },
                label = { Text("Enter OTP code") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.verifyOtpCode(otpInput) }) {
                Text("Done")
            }
        }
    }
}
package com.example.comercioplus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comercioplus.model.Role

@Composable
fun RoleSelectionScreen(navController: NavController, userViewModel: UserViewModel) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("ia_reports") },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "IA Assistant")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido a ComercioPlus", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("¿Cómo quieres continuar?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    navController.navigate("auth/${Role.COMERCIANTE.name}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Soy Comerciante")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    navController.navigate("auth/${Role.CLIENTE.name}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Soy Cliente")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { 
                    userViewModel.setRole(Role.INVITADO)
                    navController.navigate("user_main") { popUpTo("role_selection") { inclusive = true } }
                }
            ) {
                Text("Continuar como Invitado")
            }
        }
    }
}

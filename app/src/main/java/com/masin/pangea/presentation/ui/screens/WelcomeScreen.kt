package com.masin.pangea.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PANGEAappTheme

/**
 * Pantalla de Bienvenida (Welcome/Access Screen)
 * Se muestra después del splash screen y permite acceder a la Home.
 * Basada en el diseño proporcionado por el usuario.
 */
@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Imagen de fondo (Edificio)
        Image(
            painter = painterResource(id = R.drawable.fondo_texto),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // 2. Overlay oscuro con tonalidad Teal (según diseño)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60D2B2B)) // Teal muy oscuro con alta opacidad (90%)
        )

        // 3. Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Espacio superior flexible
            Spacer(modifier = Modifier.weight(1f))
            
            // Logo de Pangea (Usando el drawable pangea_logo que suele ser el isotipo blanco)
            Image(
                painter = painterResource(id = R.drawable.pangea_logo),
                contentDescription = "Pangea Logo",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
            
            // Nombre de la App
            Text(
                text = "Pangea",
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Espacio entre Logo y Texto de Bienvenida
            Spacer(modifier = Modifier.height(60.dp))
            
            // Texto "¡BIENVENIDO!"
            Text(
                text = "¡BIENVENIDO!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            
            // Espacio flexible
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Botón "ACCEDER"
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B5E37), // Verde bosque oscuro del diseño
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "ACCEDER",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
            
            // Espacio inferior final
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    PANGEAappTheme {
        WelcomeScreen(onNavigateToHome = {})
    }
}

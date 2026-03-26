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
import androidx.compose.foundation.layout.widthIn
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
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme

/**
 * Pantalla de Bienvenida (Welcome/Access Screen).
 * Usa el sistema centralizado AppDimens para adaptarse a móviles y tablets.
 */
@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit
) {
    val dimens = rememberAppDimens()

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_texto),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro con tonalidad Teal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60D2B2B))
        )

        // Contenido principal centrado con ancho máximo en tablet
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.paddingSection)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo de Pangea
            Image(
                painter = painterResource(id = R.drawable.pangea_logo),
                contentDescription = "Pangea Logo",
                modifier = Modifier
                    .size(dimens.logoSize * 1.5f)
                    .widthIn(max = dimens.maxContentWidth * 0.5f),
                contentScale = ContentScale.Fit
            )

            // Nombre de la App
            Text(
                text = "Pangea",
                fontSize = dimens.fontHero * 2f,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(dimens.spacingXLarge))

            // Texto "¡BIENVENIDO!"
            Text(
                text = "¡BIENVENIDO!",
                fontSize = dimens.fontTitle,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = dimens.fontSmall * 0.08f
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Botón "ACCEDER"
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .widthIn(max = dimens.maxContentWidth)
                    .fillMaxWidth()
                    .height(dimens.buttonHeight * 1.2f),
                shape = RoundedCornerShape(dimens.buttonCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B5E37),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "ACCEDER",
                    fontSize = dimens.fontSubtitle,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = dimens.fontSmall * 0.14f
                )
            }

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

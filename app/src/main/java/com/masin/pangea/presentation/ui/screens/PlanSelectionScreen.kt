package com.masin.pangea.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme
import com.masin.pangea.ui.theme.PangeaCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlanSelectionScreen(
    onNavigateToHome: () -> Unit
) {
    val dimens = rememberAppDimens()
    var selectedPlan by remember { mutableStateOf<PlanType?>(null) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo idéntico al WelcomeScreen
        Image(
            painter = painterResource(id = R.drawable.fondo_texto),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60D2B2B))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = dimens.paddingSection, vertical = dimens.spacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimens.spacingMedium))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.pangea_logo),
                contentDescription = "Pangea Logo",
                modifier = Modifier
                    .size(dimens.logoSize)
                    .widthIn(max = dimens.maxContentWidth * 0.4f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(dimens.spacingLarge))

            // Descripción de Pangea
            Text(
                text = "Pangea nació del sueño de unir territorios mediante una ciudadanía digital que conecta personas, empresarios y comunidades bajo los valores de inclusión, sostenibilidad e innovación. Es un ecosistema donde cada espacio tiene un propósito: impulsar proyectos, formar talentos y demostrar que la tecnología puede transformar vidas.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = dimens.fontCaption,
                textAlign = TextAlign.Start,
                lineHeight = dimens.lineHeightBody
            )

            Spacer(modifier = Modifier.height(dimens.spacingLarge))

            // Layout de planes: columna en móvil, fila en tablet
            if (dimens.isTablet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.spacingLarge),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PlanCard(
                            title = "Plan Básico",
                            description = "DigiTurno, E-learning y Desk",
                            discountText = "Ahorra 5%",
                            dotCount = 3,
                            isSelected = selectedPlan == PlanType.BASIC,
                            onSelect = {
                                selectedPlan = if (selectedPlan == PlanType.BASIC) null else PlanType.BASIC
                            },
                            onAccess = onNavigateToHome,
                            icons = listOf(
                                R.drawable.conoce,
                                R.drawable.gestiona,
                                R.drawable.soluciona
                            ),
                            dimens = dimens
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PlanCard(
                            title = "Plan Premium",
                            description = "DigiTurno, E-learning, Desk, Agente IA, Eventos en vivo, Administración de contraseñas, Radar territorial, Ventanilla de pagos y Agendamiento.",
                            discountText = "Ahorra 20%",
                            dotCount = 8,
                            isSelected = selectedPlan == PlanType.PREMIUM,
                            onSelect = {
                                selectedPlan = if (selectedPlan == PlanType.PREMIUM) null else PlanType.PREMIUM
                            },
                            onAccess = onNavigateToHome,
                            icons = listOf(
                                R.drawable.conoce,
                                R.drawable.gestiona,
                                R.drawable.soluciona,
                                R.drawable.paga,
                                R.drawable.conoce,
                                R.drawable.gestiona,
                                R.drawable.soluciona,
                                R.drawable.paga
                            ),
                            dimens = dimens
                        )
                    }
                }
            } else {
                // Móvil: planes apilados verticalmente
                PlanCard(
                    title = "Plan Básico",
                    description = "DigiTurno, E-learning y Desk",
                    discountText = "Ahorra 5%",
                    dotCount = 3,
                    isSelected = selectedPlan == PlanType.BASIC,
                    onSelect = {
                        selectedPlan = if (selectedPlan == PlanType.BASIC) null else PlanType.BASIC
                    },
                    onAccess = onNavigateToHome,
                    icons = listOf(
                        R.drawable.conoce,
                        R.drawable.gestiona,
                        R.drawable.soluciona
                    ),
                    dimens = dimens
                )

                Spacer(modifier = Modifier.height(dimens.spacingLarge))

                PlanCard(
                    title = "Plan Premium",
                    description = "DigiTurno, E-learning, Desk, Agente IA, Eventos en vivo, Administración de contraseñas, Radar territorial, Ventanilla de pagos y Agendamiento.",
                    discountText = "Ahorra 20%",
                    dotCount = 8,
                    isSelected = selectedPlan == PlanType.PREMIUM,
                    onSelect = {
                        selectedPlan = if (selectedPlan == PlanType.PREMIUM) null else PlanType.PREMIUM
                        if (selectedPlan == PlanType.PREMIUM) {
                            coroutineScope.launch {
                                delay(350)
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    },
                    onAccess = onNavigateToHome,
                    icons = listOf(
                        R.drawable.conoce,
                        R.drawable.gestiona,
                        R.drawable.soluciona,
                        R.drawable.paga,
                        R.drawable.conoce,
                        R.drawable.gestiona,
                        R.drawable.soluciona,
                        R.drawable.paga
                    ),
                    dimens = dimens
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingXLarge))
        }
    }
}

enum class PlanType {
    BASIC, PREMIUM
}

@Composable
fun PlanCard(
    title: String,
    description: String,
    discountText: String,
    dotCount: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onAccess: () -> Unit,
    icons: List<Int>,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    val iconSize = (dimens.circleItemSize.value * 0.78f).dp        // ~70dp en compact
    val iconInnerSize = (dimens.iconSizeLarge.value * 1.0f).dp    // ~36dp en compact

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tag de ahorro superpuesto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 12.dp)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(PangeaCyan, RoundedCornerShape(16.dp))
                    .padding(horizontal = dimens.spacingMedium, vertical = 4.dp)
            ) {
                Text(
                    text = discountText,
                    color = Color(0xFF0D2B2B),
                    fontWeight = FontWeight.Bold,
                    fontSize = dimens.fontSmall
                )
            }
        }

        // Tarjeta blanca principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(dimens.cardCornerRadius))
                .clickable { onSelect() }
                .padding(dimens.paddingCard)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.fontSubtitle,
                        color = Color(0xFF0D2B2B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = dimens.fontCaption,
                        color = Color.Gray,
                        lineHeight = dimens.lineHeightSmall
                    )

                    if (!isSelected) {
                        Spacer(modifier = Modifier.height(dimens.spacingMedium))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(dotCount) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFF0D2B2B), CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(dimens.spacingMedium))

                // Radio button (círculo Cyan)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isSelected) PangeaCyan else Color.Transparent, CircleShape)
                        )
                    }
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = PangeaCyan,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                        )
                    }
                }
            }
        }

        // Contenido expandido (fondo verde oscuro)
        AnimatedVisibility(
            visible = isSelected,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-10).dp)
                    .background(
                        Color(0xFF0A4444),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(top = 30.dp, bottom = dimens.spacingLarge,
                        start = dimens.paddingCard, end = dimens.paddingCard),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(com.masin.pangea.ui.theme.PangeaBlue, RoundedCornerShape(12.dp))
                        .padding(horizontal = dimens.spacingLarge, vertical = 6.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.fontCaption
                    )
                }

                Spacer(modifier = Modifier.height(dimens.spacingLarge))

                // Grid de iconos
                val numColumns = if (dimens.isTablet) 4 else 3
                val rows = (icons.size + numColumns - 1) / numColumns

                for (i in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0 until numColumns) {
                            val index = i * numColumns + j
                            if (index < icons.size) {
                                Box(
                                    modifier = Modifier
                                        .size(iconSize)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = icons[index]),
                                        contentDescription = null,
                                        modifier = Modifier.size(iconInnerSize),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(iconSize))
                            }
                        }
                    }
                    if (i < rows - 1) {
                        Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    }
                }

                Spacer(modifier = Modifier.height(dimens.spacingLarge))

                Button(
                    onClick = onAccess,
                    modifier = Modifier
                        .fillMaxWidth(if (dimens.isTablet) 0.5f else 0.6f)
                        .height(dimens.buttonHeight),
                    shape = RoundedCornerShape(dimens.buttonCornerRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.masin.pangea.ui.theme.PangeaGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "ACCEDER",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimens.fontBody
                    )
                }
            }
        }
    }
}

// Alias de extensión para usar TextUnit como sp
private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)

@Preview(showBackground = true)
@Composable
fun PlanSelectionScreenPreview() {
    PANGEAappTheme {
        PlanSelectionScreen(onNavigateToHome = {})
    }
}

package com.masin.pangea.domain.model

/**
 * Modelo de dominio que representa una pestaña de navegación
 */
data class Tab(
    val id: String,
    val title: String,
    val iconResId: Int,
    val url: String
)

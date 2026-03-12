package com.masin.pangea.domain.repository

import com.masin.pangea.domain.model.Tab

/**
 * Interfaz del repositorio para obtener las pestañas de navegación
 */
interface TabRepository {
    fun getTabs(): List<Tab>
    fun getTabById(id: String): Tab?
    fun getDefaultTab(): Tab
}

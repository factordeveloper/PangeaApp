package com.masin.pangea.data.repository

import com.masin.pangea.R
import com.masin.pangea.data.config.WebViewConfig
import com.masin.pangea.domain.model.Tab
import com.masin.pangea.domain.repository.TabRepository

/**
 * Implementación del repositorio de pestañas
 */
class TabRepositoryImpl : TabRepository {
    
    private val tabs = listOf(
        Tab(
            id = "conoce",
            title = "Conoce",
            iconResId = R.drawable.conoce,
            url = WebViewConfig.URL_CONOCE
        ),
        Tab(
            id = "gestiona",
            title = "Gestiona",
            iconResId = R.drawable.gestiona,
            url = WebViewConfig.URL_GESTIONA
        ),
        Tab(
            id = "soluciona",
            title = "Soluciona",
            iconResId = R.drawable.soluciona,
            url = WebViewConfig.URL_SOLUCIONA
        ),
        Tab(
            id = "paga",
            title = "Paga",
            iconResId = R.drawable.paga,
            url = WebViewConfig.URL_PAGA
        )
    )
    
    override fun getTabs(): List<Tab> = tabs
    
    override fun getTabById(id: String): Tab? = tabs.find { it.id == id }
    
    override fun getDefaultTab(): Tab = tabs.first() // "Conoce" es la pestaña por defecto
}

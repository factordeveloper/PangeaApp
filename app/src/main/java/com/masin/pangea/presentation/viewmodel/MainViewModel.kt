package com.masin.pangea.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.masin.pangea.data.repository.TabRepositoryImpl
import com.masin.pangea.domain.model.Tab
import com.masin.pangea.domain.repository.TabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel principal de la aplicación
 */
class MainViewModel(
    private val tabRepository: TabRepository = TabRepositoryImpl()
) : ViewModel() {
    
    private val _selectedTab = MutableStateFlow(tabRepository.getDefaultTab())
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()
    
    private val _tabs = MutableStateFlow(tabRepository.getTabs())
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()
    
    fun selectTab(tabId: String) {
        tabRepository.getTabById(tabId)?.let { tab ->
            _selectedTab.value = tab
        }
    }
    
    fun getTabUrl(tabId: String): String {
        return tabRepository.getTabById(tabId)?.url ?: ""
    }
}

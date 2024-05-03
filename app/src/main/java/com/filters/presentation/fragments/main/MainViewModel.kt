package com.filters.presentation.fragments.main


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filters.domain.listfilter.Filter
import com.filters.domain.listfilter.loadFiltersFromResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _listFilters= MutableStateFlow<List<Filter>>(listOf())
    val listFilters = _listFilters.asStateFlow()

    fun getListFilters(context: Context) {
        viewModelScope.launch {
            val filtersList = loadFiltersFromResource(context)
            _listFilters.value = filtersList
        }
    }
}
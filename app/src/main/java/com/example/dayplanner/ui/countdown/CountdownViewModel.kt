package com.example.dayplanner.ui.countdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayplanner.data.model.CountdownEvent
import com.example.dayplanner.data.repository.CountdownRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountdownViewModel @Inject constructor(
    private val repository: CountdownRepository
) : ViewModel() {

    private val _category = MutableStateFlow(CountdownEvent.CATEGORY_ALL)
    val category: StateFlow<String> = _category.asStateFlow()

    val events: StateFlow<List<CountdownEvent>> = _category
        .flatMapLatest { repository.getByCategory(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: String) {
        _category.value = category
    }

    fun add(event: CountdownEvent) = viewModelScope.launch {
        repository.add(event)
    }

    fun remove(event: CountdownEvent) = viewModelScope.launch {
        repository.remove(event)
    }
}

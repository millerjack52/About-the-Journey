package nz.ac.canterbury.seng303.aboutthejourney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val startTime = System.currentTimeMillis()
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()
    init {
        viewModelScope.launch {
            // can replace with actual logic for how long to wait just 3 sec for now
            delay(3000L)
            _isReady.value = true
        }
    }}
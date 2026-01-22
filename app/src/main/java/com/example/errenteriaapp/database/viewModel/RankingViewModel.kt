package com.example.errenteriaapp.database.viewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModel() {

    private val _rankingData = MutableStateFlow<List<Puntuazioa>>(emptyList())
    val rankingData: StateFlow<List<Puntuazioa>> = _rankingData

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRanking() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = puntuazioaDao?.getAllSorted() ?: emptyList()
                _rankingData.value = data
            } catch (e: Exception) {
                _rankingData.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para calcular la puntuación total de un jugador
    fun calculateTotalPoints(puntuazio: Puntuazioa): Int {
        return puntuazio.puntuazioaBertso +
                puntuazio.puntuazioaGalderak +
                puntuazio.puntuazioaGurutzegrama +
                puntuazio.puntuazioaErrotaProzezua +
                puntuazio.puntuazioaPapresa +
                puntuazio.puntuazioaArrastrar +
                puntuazio.puntuazioaSopaLetra
    }
}
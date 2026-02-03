package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModela erabili ranking edo sailkapenaren egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class RankingViewModel(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModel() {

    /**
     * Rankingaren datu publikoak
     */
    private val _rankingData = MutableStateFlow<List<Puntuazioa>>(emptyList())
    val rankingData: StateFlow<List<Puntuazioa>> = _rankingData

    /**
     * Datuak kargatzen ari diren egoera kontrolatzeko
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Rankingaren datuak kargatzen ditu
     */
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

    /**
     * Jokalari baten puntuazio totala kalkulatzen du
     * @param puntuazio Jokalariaren puntuazio-objektua
     * @return Puntuazio totala
     */
    fun calculateTotalPoints(puntuazio: Puntuazioa): Int {
        return puntuazio.puntuazioaBertso +
                puntuazio.puntuazioaGalderak +
                puntuazio.puntuazioaGurutzegrama +
                puntuazio.puntuazioaArropaBuruHandiak +
                puntuazio.puntuazioaPapresa +
                puntuazio.puntuazioaArrastrar +
                puntuazio.puntuazioaSopaLetra
    }
}
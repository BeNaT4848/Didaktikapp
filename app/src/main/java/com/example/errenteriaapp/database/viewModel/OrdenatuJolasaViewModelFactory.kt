// ViewModelFactories.kt
package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.errenteriaapp.database.PuntuazioaDao

// Factory para OrdenatuJolasaViewModel
class OrdenatuJolasaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdenatuJolasaViewModel::class.java)) {
            return OrdenatuJolasaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Factory para RankingViewModel
class RankingViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            return RankingViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
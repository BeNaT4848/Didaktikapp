package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.IrakasleDao
import com.example.errenteriaapp.database.PartidaDao
import com.example.errenteriaapp.database.PuntuazioaDao
import com.example.errenteriaapp.navigation.screens.SanMarkosekoGalderak

class LoginViewModelFactory(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao,
    private val partidaDao: PartidaDao,
    private val puntuazioaDao: PuntuazioaDao

) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(ikasleDao, irakasleDao,partidaDao,puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
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

class PapresaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PapresaViewModel::class.java)) {
            return PapresaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class BertsoViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: BertsoViewModel.ConfigJuego = BertsoViewModel.ConfigJuego.DEFAULT_BERTSOA_1
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BertsoViewModel::class.java)) {
            return BertsoViewModel(puntuazioaDao, configJuego) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class CrucigramaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CrucigramaViewModel::class.java)) {
            return CrucigramaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class SopaDeLetrasViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SopaDeLetrasViewModel::class.java)) {
            return SopaDeLetrasViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class ArropaBuruHandiakFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArropaBuruHandiakViewModel::class.java)) {
            return ArropaBuruHandiakViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class SanMarkosViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SanMarkosViewModel::class.java)) {
            return SanMarkosViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
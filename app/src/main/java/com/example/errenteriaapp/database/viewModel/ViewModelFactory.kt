package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.IrakasleDao
import com.example.errenteriaapp.database.IzenTaldeaDao
import com.example.errenteriaapp.database.PartidaDao
import com.example.errenteriaapp.database.PuntuazioaDao
import com.example.errenteriaapp.navigation.screens.SanMarkosekoGalderak

/**
 * LoginViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param ikasleDao Ikasleak datu-basean gordetzeko erabiltzen den DAOa
 * @param irakasleDao Irakasleak datu-basean gordetzeko erabiltzen den DAOa
 * @param partidaDao Partidak datu-basean gordetzeko erabiltzen den DAOa
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param izenTaldeaDao Izenak eta taldeak datu-basean gordetzeko erabiltzen den DAOa
 */
class LoginViewModelFactory(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao,
    private val partidaDao: PartidaDao,
    private val puntuazioaDao: PuntuazioaDao,
    private val izenTaldeaDao: IzenTaldeaDao

) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(ikasleDao, irakasleDao, partidaDao, puntuazioaDao, izenTaldeaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

// Factory para OrdenatuJolasaViewModel

/**
 * OrdenatuJolasaViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class OrdenatuJolasaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdenatuJolasaViewModel::class.java)) {
            return OrdenatuJolasaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

// Factory para RankingViewModel

/**
 * RankingViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class RankingViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            return RankingViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * PapresaViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class PapresaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PapresaViewModel::class.java)) {
            return PapresaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * BertsoViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param configJuego Jokoaren konfigurazioa, ConfigJuego.DEFAULT_BERTSOA_1 balioa erabiltzen du berez
 */
class BertsoViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: BertsoViewModel.ConfigJuego = BertsoViewModel.ConfigJuego.DEFAULT_BERTSOA_1
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BertsoViewModel::class.java)) {
            return BertsoViewModel(puntuazioaDao, configJuego) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * CrucigramaViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class CrucigramaViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CrucigramaViewModel::class.java)) {
            return CrucigramaViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * SopaDeLetrasViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class SopaDeLetrasViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SopaDeLetrasViewModel::class.java)) {
            return SopaDeLetrasViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * ArropaBuruHandiakViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class ArropaBuruHandiakFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArropaBuruHandiakViewModel::class.java)) {
            return ArropaBuruHandiakViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}

/**
 * SanMarkosViewModel klasearen fabrika, ViewModel instantziak sortzeko
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class SanMarkosViewModelFactory(
    private val puntuazioaDao: PuntuazioaDao?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SanMarkosViewModel::class.java)) {
            return SanMarkosViewModel(puntuazioaDao) as T
        }
        throw IllegalArgumentException("ViewModel klasa ezezaguna")
    }
}
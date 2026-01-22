package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.IrakasleDao
import com.example.errenteriaapp.database.PartidaDao

class LoginViewModelFactory(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao,
    private val partidaDao: PartidaDao

) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(ikasleDao, irakasleDao,partidaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.errenteriaapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.errenteriaapp.database.IkasleDao
import com.example.errenteriaapp.database.IrakasleDao

class LoginViewModelFactory(
    private val ikasleDao: IkasleDao,
    private val irakasleDao: IrakasleDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(ikasleDao, irakasleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
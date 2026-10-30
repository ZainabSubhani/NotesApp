package com.zainab.notely.ui.checklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.zainab.notely.data.local.ChecklistWithProgress
import com.zainab.notely.data.local.DatabaseProvider

class ChecklistsViewModel(application: Application) : AndroidViewModel(application) {
    private val checklistDao = DatabaseProvider.getDatabase(application).checklistDao()
    val allChecklists: LiveData<List<ChecklistWithProgress>> = checklistDao.getAllWithProgress()
}
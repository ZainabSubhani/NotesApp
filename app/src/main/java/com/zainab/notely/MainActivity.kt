package com.zainab.notely

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zainab.notely.data.local.DatabaseProvider
import com.zainab.notely.data.local.Note
import com.zainab.notely.data.local.NoteSource
import com.zainab.notely.databinding.ActivityMainBinding
import com.zainab.notely.ui.notes.NoteAdapter
import com.zainab.notely.ui.notes.NotesViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = NoteAdapter()
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter

        viewModel.allNotes.observe(this) { notes ->
            adapter.submitList(notes)
        }

        lifecycleScope.launch {
            seedIfEmpty()
        }
    }

    private suspend fun seedIfEmpty() {
        val dao = DatabaseProvider.getDatabase(applicationContext).noteDao()
        if (dao.getById(1) == null && dao.getById(2) == null && dao.getById(3) == null) {
            dao.insert(Note(title = "Meeting notes", content = "Discussed Q3 roadmap, follow up with design",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.TYPED))
            dao.insert(Note(title = "Lecture pg 3", content = "Mitochondria is the powerhouse of the cell",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.SCANNED))
            dao.insert(Note(title = "Grocery list", content = "Milk, eggs, spinach, bread",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.TYPED))
        }
    }
}
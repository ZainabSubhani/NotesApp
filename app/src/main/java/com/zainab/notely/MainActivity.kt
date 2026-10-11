package com.zainab.notely

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zainab.notely.data.local.DatabaseProvider
import com.zainab.notely.data.local.NoteSource
import com.zainab.notely.data.local.Note
import com.zainab.notely.databinding.ActivityMainBinding
import com.zainab.notely.ui.notes.NoteAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        val db = DatabaseProvider.getDatabase(applicationContext)

        lifecycleScope.launch {
            seedIfEmpty(db)
            val notes = db.noteDao().getAll()
            adapter.submitList(notes)
        }
    }

    private suspend fun seedIfEmpty(db: com.zainab.notely.data.local.AppDatabase) {
        val dao = db.noteDao()
        if (dao.getAll().isEmpty()) {
            dao.insert(Note(title = "Meeting notes", content = "Discussed Q3 roadmap, follow up with design",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.TYPED))
            dao.insert(Note(title = "Lecture pg 3", content = "Mitochondria is the powerhouse of the cell",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.SCANNED))
            dao.insert(Note(title = "Grocery list", content = "Milk, eggs, spinach, bread",
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), sourceType = NoteSource.TYPED))
        }
    }
}
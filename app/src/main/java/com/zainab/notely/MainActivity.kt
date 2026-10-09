package com.zainab.notely

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.zainab.notely.data.local.Note
import com.zainab.notely.data.local.NoteSource
import com.zainab.notely.databinding.ActivityMainBinding
import com.zainab.notely.ui.notes.NoteAdapter

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

        adapter.submitList(fakeNotes())
    }

    private fun fakeNotes(): List<Note> = listOf(
        Note(id = 1, title = "Meeting notes", content = "Discussed Q3 roadmap, follow up with design",
            createdAt = 0L, updatedAt = 0L, sourceType = NoteSource.TYPED),
        Note(id = 2, title = "Lecture pg 3", content = "Mitochondria is the powerhouse of the cell",
            createdAt = 0L, updatedAt = 0L, sourceType = NoteSource.SCANNED),
        Note(id = 3, title = "Grocery list", content = "Milk, eggs, spinach, bread",
            createdAt = 0L, updatedAt = 0L, sourceType = NoteSource.TYPED)
    )
}
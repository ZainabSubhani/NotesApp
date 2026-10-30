package com.zainab.notely.ui.checklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zainab.notely.data.local.ChecklistWithProgress
import com.zainab.notely.databinding.ItemChecklistBinding

class ChecklistAdapter : ListAdapter<ChecklistWithProgress, ChecklistAdapter.ChecklistViewHolder>(DiffCallback) {

    class ChecklistViewHolder(private val binding: ItemChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChecklistWithProgress) {
            binding.tvTitle.text = item.title
            val percent = if (item.totalItems == 0) 0 else (item.doneItems * 100) / item.totalItems
            binding.progressBar.progress = percent
            binding.tvProgress.text = "${item.doneItems} of ${item.totalItems} done"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val binding = ItemChecklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChecklistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<ChecklistWithProgress>() {
        override fun areItemsTheSame(oldItem: ChecklistWithProgress, newItem: ChecklistWithProgress) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChecklistWithProgress, newItem: ChecklistWithProgress) =
            oldItem == newItem
    }
}
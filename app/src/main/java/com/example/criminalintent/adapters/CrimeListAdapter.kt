package com.example.criminalintent.adapters

import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.database.Crime
import com.example.criminalintent.databinding.ListItemCrimeBinding
import java.util.*

class CrimeListAdapter(
    private val onCrimeClicked: (crimeId: UUID) -> Unit
) : ListAdapter<Crime,
        RecyclerView.ViewHolder>(CrimeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CrimeHolder.from(parent)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CrimeHolder -> {
                val crime = getItem(position) as Crime
                holder.bind(crime, onCrimeClicked)
            }
        }
    }


    class CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

    }

    class CrimeHolder private constructor(
        private val binding: ListItemCrimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
            binding.crimeTitle.text = crime.title
            binding.crimeDate.text = formatDate(crime.date)

            binding.root.setOnClickListener {
                onCrimeClicked(crime.id)
            }

            binding.crimeSolved.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        companion object {
            fun from(parent: ViewGroup): CrimeHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCrimeBinding.inflate(layoutInflater, parent, false)

                return CrimeHolder(binding)
            }
        }
        private fun formatDate(date: Date): String {
            val dateFormat = DateFormat.getPatternInstance("EEEEdMMMy")
            return dateFormat.format(date)
        }
    }

}

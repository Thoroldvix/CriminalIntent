package com.example.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.databinding.ListItemSeriousCrimeBinding

class CrimeListAdapter : ListAdapter<Crime,
        RecyclerView.ViewHolder>(CrimeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(viewType) {
            R.layout.list_item_crime -> CrimeHolder.from(parent)
            else -> SeriousCrimeHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CrimeHolder -> {
                val crime = getItem(position) as Crime
                holder.bind(crime)
            }
            is SeriousCrimeHolder -> {
                val crime = getItem(position) as Crime
                holder.bind(crime)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).requiresPolice) {
            R.layout.list_item_crime
        } else {
            R.layout.list_item_serious_crime
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


class SeriousCrimeHolder private constructor(
    private val binding: ListItemSeriousCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = crime.date.toString()

        binding.root.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                "${crime.title} clicked!",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.contactPoliceButton.setOnClickListener {
            crime.isSolved = true
            it.visibility = View.GONE
            binding.crimeSolved.visibility = View.VISIBLE

        }
        binding.crimeSolved.visibility = if (crime.isSolved) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): SeriousCrimeHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemSeriousCrimeBinding.inflate(layoutInflater, parent, false)

            return SeriousCrimeHolder(binding)
        }
    }
}

class CrimeHolder private constructor(
    private val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = crime.date.toString()

        binding.root.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                "${crime.title} clicked!",
                Toast.LENGTH_SHORT
            ).show()

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
}
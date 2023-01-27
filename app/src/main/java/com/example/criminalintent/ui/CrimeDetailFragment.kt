package com.example.criminalintent.ui

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.R
import com.example.criminalintent.database.Crime
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.util.formatDate
import com.example.criminalintent.util.formatTime
import com.example.criminalintent.viewmodels.CrimeDetailViewModel
import com.example.criminalintent.viewmodels.CrimeDetailViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*


class CrimeDetailFragment : Fragment() {

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private var _binding: FragmentCrimeDetailBinding? = null

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        handleBackButtonPress()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                deleteCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deleteCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            crimeDetailViewModel.deleteCrime()
            findNavController().navigate(CrimeDetailFragmentDirections.showCrimeList())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    private fun handleBackButtonPress() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (crimeDetailViewModel.checkIfCrimeTitleIsEmpty()) {
                Snackbar.make(
                    binding.root,
                    "You should provide a title for the crime",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } else {
                findNavController().popBackStack()
            }
            isEnabled = true
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
            doneButton.setOnClickListener {
                activity?.onBackPressed()
            }



        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let {
                        updateUi(it)
                    }

                }
            }
        }
        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
        setFragmentResultListener(TimePickerFragment.REQUEST_TIME_DATE) { _, bundle ->
            val newTime = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newTime) }
        }


    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.formatDate()
            crimeDate.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
            }
            crimeTime.text = crime.date.formatTime()
            crimeTime.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectTime(crime.date))
            }
            crimeSolved.isChecked = crime.isSolved


        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
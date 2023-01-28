package com.example.criminalintent.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlin.math.log


class CrimeDetailFragment : Fragment() {

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            parseContactSelection(it)
        }
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
            R.id.confirm -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            callSuspect.isEnabled = false
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
            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }
            val selectSuspectIntent = selectSuspect.contract.createIntent(
                requireContext(),
                null
            )
            crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)


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
            setupDateButton(crime)
            setupTimeButton(crime)
            setupCrimeReportButton(crime)
            setCrimeSuspectText(crime)
            crimeSolved.isChecked = crime.isSolved

        }
    }


    private fun FragmentCrimeDetailBinding.setupTimeButton(crime: Crime) {
        crimeTime.text = crime.date.formatTime()
        crimeTime.setOnClickListener {
            findNavController().navigate(CrimeDetailFragmentDirections.selectTime(crime.date))
        }
    }

    private fun FragmentCrimeDetailBinding.setupDateButton(crime: Crime) {
        crimeDate.text = crime.date.formatDate()
        crimeDate.setOnClickListener {
            findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
        }
    }

    private fun FragmentCrimeDetailBinding.setupCrimeReportButton(
        crime: Crime
    ) {
        crimeReport.setOnClickListener {
            if (crimeDetailViewModel.crimeTitleIsEmpty()) {
                showEmptyTitleSnackbar()
            } else {
                startImplicitIntent(crime)
            }
        }
    }


    private fun FragmentCrimeDetailBinding.setCrimeSuspectText(
        crime: Crime
    ) {
        crimeSuspect.text = crime.suspect.ifEmpty {
            getString(R.string.crime_suspect_text)
        }
    }

    private fun startImplicitIntent(crime: Crime) {
        val reportIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.crime_report_subject)
            )
        }
        val chooserIntent = Intent.createChooser(
            reportIntent,
            getString(R.string.send_report)
        )
        startActivity(chooserIntent)
    }

    private fun deleteCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            crimeDetailViewModel.deleteCrime()
            findNavController().navigate(CrimeDetailFragmentDirections.showCrimeList())
        }
    }


    private fun handleBackButtonPress() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (crimeDetailViewModel.crimeTitleIsEmpty()) {
                showEmptyTitleSnackbar()
            } else {
                findNavController().popBackStack()
            }
            isEnabled = true
        }
    }

    private fun showEmptyTitleSnackbar() {
        Snackbar.make(
            binding.root,
            "You should provide a title for the crime",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = crime.date.formatDate()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectText
        )
    }


    private fun parseContactSelection(contactUri: Uri) {

        val queryNames = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryNames, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }


    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolveActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolveActivity != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





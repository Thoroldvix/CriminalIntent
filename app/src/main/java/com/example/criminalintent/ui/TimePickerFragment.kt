package com.example.criminalintent.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        calendar.time = args.crimeTime
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)


        return TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            false
        )


    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val resultTime = Calendar.getInstance()
        resultTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        resultTime.set(Calendar.MINUTE, minute)
        setFragmentResult(REQUEST_TIME_DATE, bundleOf(BUNDLE_KEY_TIME to resultTime.time))
    }

    companion object {
        const val REQUEST_TIME_DATE = "REQUEST_TIME_DATE"
        const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
    }
}
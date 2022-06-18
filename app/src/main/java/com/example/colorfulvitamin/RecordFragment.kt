package com.example.colorfulvitamin

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.colorfulvitamin.databinding.FragmentRecordBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.buttonHistory.setOnClickListener {
            findNavController().navigate(R.id.action_RecordFragment_to_HistoryFragment)
        }

        binding.buttonRed.setOnClickListener {
            recordAndNotify(it, "red")
        }

        binding.buttonGreen.setOnClickListener {
            recordAndNotify(it, "green")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun cleanDaily() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val timestampKey = "lastRecordAt"
        val lastRecordedAt = sharedPref.getString(timestampKey, "")
        val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            "" // TODO("VERSION.SDK_INT < O")
        }
        val firstRecordOfDay = lastRecordedAt != currentTime
        if (!firstRecordOfDay) {
            return
        }
        val keyNames = arrayOf("red", "green")
        // Move all data to previous day data.
        with (sharedPref.edit()) {
            for (key in keyNames) {
                val yesterdayValue = sharedPref.getInt(key, 0)
                putInt(key+lastRecordedAt, yesterdayValue)
                putInt(key, 0)
            }
            putString(timestampKey, currentTime)
            apply()
        }
    }

    fun recordAndNotify(view: View, key: String) {
        cleanDaily()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val prevValue = sharedPref.getInt(key, 0)
        with (sharedPref.edit()) {
            putInt(key, 1 + prevValue)
            apply()
        }
        val mySnackbar = Snackbar.make(view, "$prevValue $key", Snackbar.LENGTH_SHORT)
        mySnackbar.show()
    }
}
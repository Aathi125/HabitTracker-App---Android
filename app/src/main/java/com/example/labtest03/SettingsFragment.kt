package com.example.labtest03

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.labtest03.utils.Prefs
import com.example.labtest03.workers.HydrationWorker
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var etInterval: EditText
    private lateinit var switchNotif: Switch
    private lateinit var btnSave: Button

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etInterval = view.findViewById(R.id.et_interval)
        switchNotif = view.findViewById(R.id.switch_notif)
        btnSave = view.findViewById(R.id.btn_save_settings)

        // Load saved prefs
        etInterval.setText(Prefs.getHydrationInterval(requireContext()).toString())
        switchNotif.isChecked = Prefs.getNotificationsEnabled(requireContext())

        btnSave.setOnClickListener {
            requestNotificationPermissionIfNeeded()

            val intervalText = etInterval.text.toString().trim()
            if (intervalText.isEmpty()) {
                Toast.makeText(requireContext(), "Enter interval in minutes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var minutes = intervalText.toIntOrNull() ?: 60
            if (minutes < 15) minutes = 1 // WorkManager minimum interval

            val enabled = switchNotif.isChecked

            Prefs.setHydrationInterval(requireContext(), minutes)
            Prefs.setNotificationsEnabled(requireContext(), enabled)

            if (enabled) {
                scheduleHydrationWork(minutes)
                Toast.makeText(requireContext(), "Hydration reminder set every $minutes min", Toast.LENGTH_SHORT).show()
            } else {
                cancelHydrationWork()
                Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun scheduleHydrationWork(intervalMinutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<HydrationWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                "hydration_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    private fun cancelHydrationWork() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("hydration_work")
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}

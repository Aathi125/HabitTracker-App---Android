package com.example.labtest03

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.labtest03.models.Habit
import com.example.labtest03.models.MoodEntry
import com.example.labtest03.utils.Prefs
import com.google.android.material.progressindicator.CircularProgressIndicator

class HomeFragment : Fragment() {

    private lateinit var tvProgress: TextView
    private lateinit var tvPercentage: TextView
    private lateinit var tvCompleted: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var tvLatestMood: TextView
    private lateinit var tvHydration: TextView
    private lateinit var tvMotivation: TextView
    private lateinit var btnMoodTrend: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvProgress = view.findViewById(R.id.tv_progress)
        tvPercentage = view.findViewById(R.id.tv_percentage)
        tvCompleted = view.findViewById(R.id.tv_completed)
        progressBar = view.findViewById(R.id.progress_bar)
        tvLatestMood = view.findViewById(R.id.tv_latest_mood)
        tvHydration = view.findViewById(R.id.tv_hydration)
        tvMotivation = view.findViewById(R.id.tv_motivation)
        btnMoodTrend = view.findViewById(R.id.btn_mood_trend)

        loadDashboard()

        // Navigate to ProgressFragment
        btnMoodTrend.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, ProgressFragment())
                addToBackStack(null)
            }
        }
    }

    private fun loadDashboard() {
        // Load habits
        val habits: MutableList<Habit> = Prefs.loadHabits(requireContext())
        val total = habits.size
        val done = habits.count { it.completed }
        val percent = if (total == 0) 0 else (done * 100 / total)

        tvProgress.text = "Today's Progress"
        tvPercentage.text = "$percent%"
        tvCompleted.text = "$done of $total habits completed"
        progressBar.max = 100
        progressBar.progress = percent

        // Motivation message
        tvMotivation.text = when {
            percent == 0 -> "Let's get started today!"
            percent < 50 -> "Good start, keep going!"
            percent < 100 -> "Almost there, you can do it!"
            else -> "Great job! All habits done 🎉"
        }

        // Latest mood
        val moods: MutableList<MoodEntry> = Prefs.loadMoods(requireContext())
        if (moods.isNotEmpty()) {
            val latest = moods.maxByOrNull { it.timestamp }
            val emoji = latest?.emoji ?: "🙂"
            val noteText = if (!latest?.note.isNullOrEmpty()) "- ${latest?.note}" else ""
            setMood("$emoji $noteText", emoji)
        } else {
            tvLatestMood.text = "No moods logged yet"
            tvLatestMood.background = null
        }

        // Hydration
        val enabled = Prefs.getNotificationsEnabled(requireContext())
        val interval = Prefs.getHydrationInterval(requireContext())
        tvHydration.text = if (enabled) {
            "You have to drink within $interval min"
        } else {
            "Hydration reminders: disabled"
        }
    }

    private fun setMood(text: String, emoji: String) {
        tvLatestMood.text = text
        tvLatestMood.textSize = 20f
        tvLatestMood.setPadding(28, 20, 28, 20)
        tvLatestMood.setTextColor(resources.getColor(R.color.textPrimary, null))

        val bgColor = when (emoji) {
            "😊", "😄", "😁", "😃", "😍" -> resources.getColor(R.color.mood_happy, null)
            "😢", "😭", "☹️", "😞" -> resources.getColor(R.color.mood_sad, null)
            "😡", "😠", "🤬" -> resources.getColor(R.color.mood_angry, null)
            "😐", "😶", "😑" -> resources.getColor(R.color.mood_neutral, null)
            "😴", "🥱" -> resources.getColor(R.color.mood_sleepy, null)
            else -> resources.getColor(R.color.white, null)
        }

        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f
            setColor(bgColor)
        }

        tvLatestMood.background = background
        tvLatestMood.elevation = 6f
    }
}

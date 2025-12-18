package com.example.labtest03

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labtest03.models.MoodEntry
import com.example.labtest03.utils.Prefs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ProgressFragment : Fragment() {

    private lateinit var chart: LineChart
    private lateinit var tvAvgMood: TextView
    private lateinit var tvBestMood: TextView
    private lateinit var tvLowestMood: TextView
    private lateinit var tvTip1: TextView
    private lateinit var tvTip2: TextView
    private lateinit var tvTip3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chart = view.findViewById(R.id.lineChart)
        tvAvgMood = view.findViewById(R.id.tv_avg_mood)
        tvBestMood = view.findViewById(R.id.tv_best_mood)
        tvLowestMood = view.findViewById(R.id.tv_lowest_mood)
        tvTip1 = view.findViewById(R.id.tv_tip1)
        tvTip2 = view.findViewById(R.id.tv_tip2)
        tvTip3 = view.findViewById(R.id.tv_tip3)

        setupChart()
    }

    // 🔁 Auto-refresh every time the fragment becomes visible again
    override fun onResume() {
        super.onResume()
        loadMoodTrend()
    }

    private fun setupChart() {
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.axisRight.isEnabled = false

        val x = chart.xAxis
        x.position = XAxis.XAxisPosition.BOTTOM
        x.granularity = 1f
        x.setDrawGridLines(false)

        val left = chart.axisLeft
        left.axisMinimum = 0f
        left.axisMaximum = 5f
        left.granularity = 1f
    }

    private fun loadMoodTrend() {
        val moods: MutableList<MoodEntry> = Prefs.loadMoods(requireContext())

        if (moods.isEmpty()) {
            chart.clear()
            chart.setNoDataText("No mood data for the past week")
            chart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary))
            tvAvgMood.text = "Average Mood: N/A"
            tvBestMood.text = "Best Mood: —"
            tvLowestMood.text = "Lowest Mood: —"
            return
        }

        val scoreMap = mapOf(
            "🤩" to 5f, "😀" to 5f, "😊" to 4f, "🙂" to 4f,
            "😐" to 3f, "🥱" to 2f, "😴" to 2f, "😢" to 1f, "😡" to 1f
        )

        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        val dailyScores = mutableListOf<Float>()

        val baseCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        for (offset in 6 downTo 0) {
            val dayCal = baseCal.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_YEAR, -offset)
            val start = dayCal.timeInMillis
            val endCal = dayCal.clone() as Calendar
            endCal.set(Calendar.HOUR_OF_DAY, 23)
            endCal.set(Calendar.MINUTE, 59)
            endCal.set(Calendar.SECOND, 59)
            val end = endCal.timeInMillis

            val moodsForDay = moods.filter { it.timestamp in start..end }
            val avgScore = if (moodsForDay.isEmpty()) 0f
            else (moodsForDay.map { scoreMap[it.emoji] ?: 3f }.average() * 100).roundToInt() / 100f

            dailyScores.add(avgScore)
            entries.add(Entry((6 - offset).toFloat(), avgScore))
            labels.add(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(start)))
        }

        val ds = LineDataSet(entries, "Mood (1-5)").apply {
            setDrawValues(false)
            lineWidth = 2.5f
            circleRadius = 5f
            color = ContextCompat.getColor(requireContext(), R.color.accent)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.accent))
            setDrawCircles(true)
            setDrawFilled(true)
            fillAlpha = 90
            fillColor = ContextCompat.getColor(requireContext(), R.color.accent)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        chart.data = LineData(ds)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.animateY(800)
        chart.invalidate()

        // --- Summary Section ---
        val nonZeroScores = dailyScores.filter { it > 0f }
        if (nonZeroScores.isNotEmpty()) {
            val avg = (nonZeroScores.average() * 10).roundToInt() / 10f
            val bestIndex = dailyScores.indexOf(nonZeroScores.maxOrNull()!!)
            val lowIndex = dailyScores.indexOf(nonZeroScores.minOrNull()!!)

            tvAvgMood.text = "Average Mood: $avg / 5"
            tvBestMood.text = "Best Mood: ${labels[bestIndex]} (${nonZeroScores.maxOrNull()})"
            tvLowestMood.text = "Lowest Mood: ${labels[lowIndex]} (${nonZeroScores.minOrNull()})"

            // --- Dynamic Tips ---
            when {
                avg >= 4 -> {
                    tvTip1.text = "• Keep maintaining your positive habits!"
                    tvTip2.text = "• Share your positivity with others 😊"
                    tvTip3.text = "• Continue tracking your good days."
                }
                avg >= 2.5 -> {
                    tvTip1.text = "• You're doing okay, stay consistent!"
                    tvTip2.text = "• Try a short meditation or walk daily."
                    tvTip3.text = "• Reflect on what makes you feel balanced."
                }
                else -> {
                    tvTip1.text = "• Take mindful breaks and rest more."
                    tvTip2.text = "• Stay connected with supportive people."
                    tvTip3.text = "• Focus on one small positive action today."
                }
            }
        } else {
            tvAvgMood.text = "Average Mood: N/A"
            tvBestMood.text = "Best Mood: —"
            tvLowestMood.text = "Lowest Mood: —"
        }
    }
}

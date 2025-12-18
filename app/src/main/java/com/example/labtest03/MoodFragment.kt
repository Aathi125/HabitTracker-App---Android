package com.example.labtest03

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labtest03.adapters.MoodAdapter
import com.example.labtest03.models.MoodEntry
import com.example.labtest03.utils.Prefs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MoodFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: MoodAdapter
    private var moods = mutableListOf<MoodEntry>()
    private var selectedEmoji: String = "😊"
    private var selectedEmojiView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_moods)
        val etNote = view.findViewById<EditText>(R.id.et_note)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val emojiContainer = view.findViewById<LinearLayout>(R.id.emoji_container)

        // Predefined emojis
        val emojis = listOf("😀", "😊", "😐", "😢", "😡", "🥱", "🤩")
        emojis.forEach { emoji ->
            val emojiText = TextView(requireContext()).apply {
                text = emoji
                textSize = 28f
                setTextColor(resources.getColor(R.color.textPrimary, null))
                background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_default_bg)
                setPadding(32, 32, 32, 32)
                gravity = android.view.Gravity.CENTER

                setOnClickListener {
                    // Deselect previous emoji
                    selectedEmojiView?.apply {
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_default_bg)
                        animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                    }

                    // Select new emoji
                    selectedEmoji = emoji
                    selectedEmojiView = this
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.mood_selected_bg)
                    animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).start()
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 0, 12, 0) // spacing between emojis
            }

            emojiContainer.addView(emojiText, params)
        }

        // Load moods and setup RecyclerView
        moods = Prefs.loadMoods(requireContext())
        adapter = MoodAdapter(moods)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // Save button logic
        btnSave.setOnClickListener {
            val note = etNote.text.toString().trim()

            // Validation
            if (note.isEmpty()) {
                Toast.makeText(requireContext(), "Can't save without inputting a note!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entry = MoodEntry(UUID.randomUUID().toString(), selectedEmoji, note)
            moods.add(0, entry) // Add new mood to the top
            Prefs.saveMoods(requireContext(), moods)
            adapter.updateList(moods)
            etNote.text.clear()

            Toast.makeText(requireContext(), "Mood saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun formatDate(time: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            return sdf.format(Date(time))
        }
    }
}

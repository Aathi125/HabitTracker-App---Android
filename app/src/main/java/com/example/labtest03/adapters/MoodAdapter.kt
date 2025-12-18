package com.example.labtest03.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labtest03.MoodFragment
import com.example.labtest03.R
import com.example.labtest03.models.MoodEntry

class MoodAdapter(private val items: MutableList<MoodEntry>)
    : RecyclerView.Adapter<MoodAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val emoji: TextView = view.findViewById(R.id.tv_emoji)
        val note: TextView = view.findViewById(R.id.tv_note)
        val time: TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mood = items[position]
        holder.emoji.text = mood.emoji
        holder.note.text = if (mood.note.isNullOrEmpty()) "(no note)" else mood.note
        holder.time.text = MoodFragment.formatDate(mood.timestamp)
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<MoodEntry>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}

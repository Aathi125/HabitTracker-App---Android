package com.example.labtest03.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labtest03.R
import com.example.labtest03.models.Habit

class HabitAdapter(
    private val items: MutableList<Habit>,
    private val listener: Listener
) : RecyclerView.Adapter<HabitAdapter.VH>() {

    interface Listener {
        fun onToggle(habit: Habit, position: Int)
        fun onEdit(habit: Habit, position: Int)
        fun onDelete(habit: Habit, position: Int)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val chk: CheckBox = view.findViewById(R.id.chk_done)
        val title: TextView = view.findViewById(R.id.tv_title)
        val edit: ImageButton = view.findViewById(R.id.btn_edit)
        val delete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val habit = items[position]
        holder.title.text = habit.title

        // avoid recycling firing listeners
        holder.chk.setOnCheckedChangeListener(null)
        holder.chk.isChecked = habit.completed
        holder.chk.setOnCheckedChangeListener { _, checked ->
            if (habit.completed != checked) {
                habit.completed = checked
                listener.onToggle(habit, position)
            }
        }

        holder.edit.setOnClickListener { listener.onEdit(habit, position) }
        holder.delete.setOnClickListener { listener.onDelete(habit, position) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Habit>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}

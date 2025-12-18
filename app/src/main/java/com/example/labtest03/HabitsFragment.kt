package com.example.labtest03

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labtest03.adapters.HabitAdapter
import com.example.labtest03.models.Habit
import com.example.labtest03.utils.Prefs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class HabitsFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: HabitAdapter
    private var habits = mutableListOf<Habit>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_habits, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_habits)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add)

        habits = Prefs.loadHabits(requireContext())


        if (habits.isEmpty()) {
            habits.add(Habit(UUID.randomUUID().toString(), "Drink Water"))
            Prefs.saveHabits(requireContext(), habits)
        }

        adapter = HabitAdapter(habits, object: HabitAdapter.Listener {
            override fun onToggle(habit: Habit, position: Int) {
                Prefs.saveHabits(requireContext(), habits)
            }
            override fun onEdit(habit: Habit, position: Int) { showAddEditDialog(habit, position) }
            override fun onDelete(habit: Habit, position: Int) {
                habits.removeAt(position)
                Prefs.saveHabits(requireContext(), habits)
                adapter.updateList(habits)
            }
        })
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fab.setOnClickListener { showAddEditDialog(null, -1) }
    }

    private fun showAddEditDialog(habitToEdit: Habit?, pos: Int) {
        val isEdit = habitToEdit != null
        val builder = AlertDialog.Builder(requireContext())
        val v = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val et = v.findViewById<EditText>(R.id.et_title)
        if (isEdit) et.setText(habitToEdit!!.title)

        builder.setView(v).setTitle(if (isEdit) "Edit Habit" else "Add Habit")
            .setPositiveButton("Save") { _, _ ->
                val t = et.text.toString().trim()
                if (t.isNotEmpty()) {
                    if (isEdit) {
                        habitToEdit!!.title = t
                    } else {
                        val newH = Habit(UUID.randomUUID().toString(), t)
                        habits.add(newH)
                    }
                    Prefs.saveHabits(requireContext(), habits)
                    adapter.updateList(habits)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

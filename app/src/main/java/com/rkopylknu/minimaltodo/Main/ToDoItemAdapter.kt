package com.rkopylknu.minimaltodo.Main

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import com.rkopylknu.minimaltodo.Main.ToDoItemAdapter.*
import com.rkopylknu.minimaltodo.util.PREFS_THEME_KEY
import com.rkopylknu.minimaltodo.util.PREFS_THEME_LIGHT
import com.rkopylknu.minimaltodo.util.SHARED_PREFS_NAME

class ToDoItemAdapter(
    private val onItemClick: (ToDoItem) -> Unit = {},
) : RecyclerView.Adapter<ToDoItemViewHolder>() {

    private var toDoItems = emptyList<ToDoItem>()

    fun submitDataSet(newToDoItems: List<ToDoItem>) {
        toDoItems = newToDoItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ToDoItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_to_do_item,
                parent,
                false
            )
        ).apply {
            itemView.setOnClickListener {
                onItemClick(toDoItems[adapterPosition])
            }
        }

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        val toDoItem = toDoItems[position]
        holder.bind(toDoItem)
    }

    override fun getItemCount() = toDoItems.size

    class ToDoItemViewHolder(
        private val view: View,
    ) : RecyclerView.ViewHolder(view) {

        private val tvText = view.findViewById<TextView>(R.id.tv_text)
        private val ivImage = view.findViewById<ImageView>(R.id.iv_image)
        private val tvReminder = view.findViewById<TextView>(R.id.tv_reminder)

        fun bind(toDoItem: ToDoItem) = view.run {
            tvText.text = toDoItem.text
            ivImage.run {
                val letter = toDoItem.text.firstOrNull().toString()
                setImageDrawable(buildTextDrawable(letter, toDoItem.color))
            }
            tvReminder.run {
                isVisible = toDoItem.reminder != null
                if (toDoItem.reminder != null) {
                    text = context.getString(
                        R.string.todo_date_time,
                        toDoItem.reminder.date, toDoItem.reminder.time
                    )
                }
            }
            applyTheme()
        }

        private fun buildTextDrawable(text: String, color: Int) =
            TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(text, color)

        private fun applyTheme() = view.run {
            val theme = context
                .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(PREFS_THEME_KEY, PREFS_THEME_LIGHT)

            val (bgColor, textColor) = if (theme == PREFS_THEME_LIGHT) {
                Color.WHITE to ResourcesCompat
                    .getColor(resources, R.color.secondary_text, context.theme)
            } else {
                Color.DKGRAY to Color.WHITE
            }

            setBackgroundColor(bgColor)
            tvText.setTextColor(textColor)
        }
    }
}
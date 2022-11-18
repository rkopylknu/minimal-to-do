package com.rkopylknu.minimaltodo.ui.main

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
import com.rkopylknu.minimaltodo.databinding.ItemToDoItemBinding
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.ui.main.ToDoItemAdapter.*
import com.rkopylknu.minimaltodo.ui.util.TextDrawable

class ToDoItemAdapter(
    private val onItemClick: (ToDoItem) -> Unit = {},
    private val theme: Int,
) : RecyclerView.Adapter<ToDoItemViewHolder>() {

    private var toDoItems = emptyList<ToDoItem>()

    fun submitDataSet(newToDoItems: List<ToDoItem>) {
        toDoItems = newToDoItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ToDoItemViewHolder(
            ItemToDoItemBinding.inflate(
                LayoutInflater.from(parent.context),
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

    inner class ToDoItemViewHolder(
        private val binding: ItemToDoItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoItem: ToDoItem) = binding.run {
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
            ivStar.isVisible = toDoItem.isPrior
            applyTheme()
        }

        private fun buildTextDrawable(text: String, color: Int) =
            TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(text, color)

        private fun applyTheme() = binding.run {
            val (bgColor, textColor) =
                if (theme == R.style.Theme_MinimalToDo_Light) {
                    Color.WHITE to ResourcesCompat
                        .getColor(
                            root.resources,
                            R.color.secondary_text,
                            root.context.theme
                        )
                } else {
                    Color.DKGRAY to Color.WHITE
                }

            root.setBackgroundColor(bgColor)
            tvText.setTextColor(textColor)
        }
    }
}
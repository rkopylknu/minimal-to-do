package com.rkopylknu.minimaltodo.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.ReminderService
import com.rkopylknu.minimaltodo.Utility.StoreRetrieveData
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import com.rkopylknu.minimaltodo.AddToDo.AddToDoActivity
import com.rkopylknu.minimaltodo.Utility.RecyclerViewEmptySupport
import com.rkopylknu.minimaltodo.util.SHARED_PREFS_NAME
import com.rkopylknu.minimaltodo.util.setOnIgnoredCallback
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var sharedPreferences: SharedPreferences

    private var toDoItems: List<ToDoItem> = emptyList()

    private lateinit var rvToDoItems: RecyclerViewEmptySupport
    private lateinit var fabAddToDoItem: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity()
            .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        toDoItems = StoreRetrieveData.load(requireContext())

        setupUI()
    }

    override fun onResume() {
        super.onResume()

        toDoItems = StoreRetrieveData.load(requireContext())
        (rvToDoItems.adapter as ToDoItemAdapter).submitDataSet(toDoItems)
    }

    private fun setupUI() {
        requireView().run {
            rvToDoItems = findViewById(R.id.rv_to_do_items)
            fabAddToDoItem = findViewById(R.id.fab_add_to_do_item)
        }

        rvToDoItems.run {
            adapter = ToDoItemAdapter(::onToDoItemClick).apply {
                submitDataSet(toDoItems)
            }

            setEmptyView(requireView().findViewById(R.id.ll_empty))
            showEmptyView()
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            buildItemTouchHelper().attachToRecyclerView(this)
        }
        fabAddToDoItem.setOnClickListener {
            onAddToDoItem()
        }
    }

    private fun onToDoItemClick(toDoItem: ToDoItem) {
        val toDoItemJson = Json.encodeToString(toDoItem)

        val editToDoIntent = Intent(
            requireActivity(),
            AddToDoActivity::class.java
        ).putExtra(
            AddToDoActivity.TO_DO_ITEM_KEY,
            toDoItemJson
        )

        startActivity(editToDoIntent)
    }

    private fun onToDoItemMoved(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(toDoItems, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(toDoItems, i, i - 1)
            }
        }
        rvToDoItems.adapter?.notifyItemMoved(from, to)
    }

    private fun onToDoItemSwiped(position: Int) {
        val swipedToDoItem = toDoItems[position]
        toDoItems = toDoItems - swipedToDoItem
        val adapter = (rvToDoItems.adapter as ToDoItemAdapter)
        adapter.submitDataSet(toDoItems)

        Snackbar.make(
            requireView(),
            getString(R.string.deleted_todo),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                toDoItems = toDoItems.toMutableList().apply {
                    add(position, swipedToDoItem)
                }
                adapter.submitDataSet(toDoItems)
            }
            .setOnIgnoredCallback { _, _ ->
                tryCancelAlarm(swipedToDoItem)
            }
            .show()
    }

    private fun onAddToDoItem() {
        val createToDoIntent = Intent(
            requireActivity(),
            AddToDoActivity::class.java
        )
        startActivity(createToDoIntent)
    }

    private fun tryCancelAlarm(toDoItem: ToDoItem) {
        if (toDoItem.reminder == null) return

        val cancelAlarmIntent = Intent(
            requireContext(),
            ReminderService::class.java
        ).apply {
            action = ReminderService.ACTION_CANCEL
            putExtra(
                ReminderService.TO_DO_ITEM_KEY,
                Json.encodeToString(toDoItem)
            )
        }
        requireActivity().startService(cancelAlarmIntent)
    }

    private fun buildItemTouchHelper() = ItemTouchHelper(
        object : ItemTouchHelper.Callback() {

            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = true

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ): Int {
                val upFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(upFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                onToDoItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onToDoItemSwiped(viewHolder.adapterPosition)
            }
        }
    )

    override fun onPause() {
        super.onPause()
        StoreRetrieveData.save(requireContext(), toDoItems)
    }
}
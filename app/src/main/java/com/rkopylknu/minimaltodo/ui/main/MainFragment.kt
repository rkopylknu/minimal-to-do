package com.rkopylknu.minimaltodo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase
import com.rkopylknu.minimaltodo.domain.usecase.ReplaceItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.ui.add.AddToDoActivity
import com.rkopylknu.minimaltodo.ui.util.RecyclerViewEmptySupport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels {
        (requireActivity().application as App).run {
            MainViewModel.Factory(
                DisplayItemsUseCaseImpl(toDoItemRepository),
                ReplaceItemUseCaseImpl(toDoItemRepository),
                DeleteItemUseCaseImpl(
                    toDoItemRepository,
                    DeleteAlarmUseCaseImpl(applicationContext)
                ),
                RestoreItemUseCaseImpl(
                    toDoItemRepository,
                    CreateAlarmUseCaseImpl(applicationContext)
                ),
                appPreferencesManager
            )
        }
    }

    private lateinit var rvToDoItems: RecyclerViewEmptySupport
    private lateinit var fabAddToDoItem: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()

        val adapter = (rvToDoItems.adapter as ToDoItemAdapter)
        adapter.submitDataSet(viewModel.toDoItems)
    }

    private fun setupUI() {
        requireView().run {
            rvToDoItems = findViewById(R.id.rv_to_do_items)
            fabAddToDoItem = findViewById(R.id.fab_add_to_do_item)
        }

        rvToDoItems.run {
            adapter = ToDoItemAdapter(
                onItemClick = ::onToDoItemClick,
                theme = viewModel.theme
            )

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
        val editToDoIntent = Intent(
            requireActivity(),
            AddToDoActivity::class.java
        ).putExtra(
            AddToDoActivity.TO_DO_ITEM_KEY,
            Json.encodeToString(toDoItem)
        )
        startActivity(editToDoIntent)
    }

    private fun onToDoItemMoved(from: Int, to: Int) {
        viewModel.onReplaceItem(from, to)
        rvToDoItems.adapter?.notifyItemMoved(from, to)
    }

    private fun onToDoItemSwiped(position: Int) {
        val swipedToDoItem = viewModel.toDoItems[position]

        viewModel.onDeleteItem(swipedToDoItem, position)

        val adapter = (rvToDoItems.adapter as ToDoItemAdapter)
        adapter.submitDataSet(viewModel.toDoItems)

        Snackbar.make(
            requireView(),
            getString(R.string.deleted_todo),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                viewModel.onRestoreItem()
                adapter.submitDataSet(viewModel.toDoItems)
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
                onToDoItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                onToDoItemSwiped(viewHolder.adapterPosition)
            }
        }
    )
}
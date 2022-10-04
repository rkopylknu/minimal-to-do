package com.rkopylknu.minimaltodo.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.databinding.FragmentMainBinding
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.ui.about.AboutFragment
import com.rkopylknu.minimaltodo.ui.add.AddToDoFragment
import com.rkopylknu.minimaltodo.ui.settings.SettingsFragment
import com.rkopylknu.minimaltodo.ui.util.RecyclerViewEmptySupport
import com.rkopylknu.minimaltodo.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), MenuProvider {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        setupUI()
        setupObservers()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_about_me -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToAboutFragment()
                )
            }
            R.id.item_preferences -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSettingsFragment()
                )
            }
            else -> return false
        }
        return true
    }

    private fun setupUI() = binding.run {
        rvToDoItems.run {
            val theme = runBlocking {
                viewModel.theme.first()
            }
            adapter = ToDoItemAdapter(
                onItemClick = ::onToDoItemClick,
                theme = theme
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

    private fun setupObservers() {
        viewModel.toDoItems.collectOnLifecycle(viewLifecycleOwner) { items ->
            val adapter = (binding.rvToDoItems.adapter as ToDoItemAdapter)
            adapter.submitDataSet(items)
        }
    }

    private fun onToDoItemClick(toDoItem: ToDoItem) {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToAddToDoFragment(
                Json.encodeToString(toDoItem)
            )
        )
    }

    private fun onToDoItemMoved(from: Int, to: Int) {
        viewModel.onReplaceItem(from, to)
        binding.rvToDoItems.adapter?.notifyItemMoved(from, to)
    }

    private fun onToDoItemSwiped(position: Int) =
        viewLifecycleOwner.lifecycleScope.launch {
            val swipedToDoItem = viewModel.toDoItems.first()[position]

            viewModel.onDeleteItem(swipedToDoItem, position)

            Snackbar.make(
                requireView(),
                getString(R.string.deleted_todo),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.undo)) {
                    viewModel.onRestoreItem()
                }
                .show()
        }

    private fun onAddToDoItem() {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToAddToDoFragment(null)
        )
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
                direction: Int,
            ) {
                onToDoItemSwiped(viewHolder.adapterPosition)
            }
        }
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
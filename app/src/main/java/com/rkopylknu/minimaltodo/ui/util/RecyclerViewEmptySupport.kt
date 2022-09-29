package com.rkopylknu.minimaltodo.ui.util

import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.AttributeSet
import android.view.View

class RecyclerViewEmptySupport : RecyclerView {

    private var emptyView: View? = null

    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    constructor(context: Context?) : super(context!!) {}

    fun showEmptyView() {
        val adapter = adapter
        if (adapter != null && emptyView != null) {
            if (adapter.itemCount == 0) {
                emptyView!!.visibility = VISIBLE
                this@RecyclerViewEmptySupport.visibility = GONE
            } else {
                emptyView!!.visibility = GONE
                this@RecyclerViewEmptySupport.visibility = VISIBLE
            }
        }
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle) {
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    fun setEmptyView(v: View?) {
        emptyView = v
    }
}
package com.rkopylknu.minimaltodo.Main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rkopylknu.minimaltodo.About.AboutActivity
import com.rkopylknu.minimaltodo.AddToDo.AddToDoActivity
import com.rkopylknu.minimaltodo.AddToDo.AddToDoFragment
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultFragment
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Reminder.ReminderFragment
import com.rkopylknu.minimaltodo.Settings.SettingsActivity
import com.rkopylknu.minimaltodo.Utility.*
import org.json.JSONException
import java.io.IOException
import java.util.*

class MainFragment : AppDefaultFragment() {

    private lateinit var recyclerView: RecyclerViewEmptySupport
    private lateinit var addToDoItemFAB: FloatingActionButton
    private lateinit var coordLayout: CoordinatorLayout
    private lateinit var adapter: BasicListAdapter
    private lateinit var storeRetrieveData: StoreRetrieveData
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener

    private var toDoItemsArrayList: ArrayList<ToDoItem?>? = null
    private var indexOfDeletedToDoItem = 0
    private var theme = "name_of_the_theme"
    private val testStrings = arrayOf(
        "Clean my room",
        "Water the plants",
        "Get car washed",
        "Get my dry cleaning"
    )

    private lateinit var addToDoLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        addToDoLauncher = requireActivity().registerForActivityResult(
            StartActivityForResult(), ::onAddToDoItemActivityResult
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        theme = requireActivity()
            .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            .getString(THEME_SAVED, LIGHTTHEME)!!

        activity?.setTheme(
            if (theme == LIGHTTHEME) {
                R.style.CustomStyle_LightTheme
            } else {
                R.style.CustomStyle_DarkTheme
            }
        )

        requireActivity()
            .getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(CHANGE_OCCURED, false)
            .apply()


        storeRetrieveData = StoreRetrieveData(context, FILENAME)
        toDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
        adapter = BasicListAdapter(toDoItemsArrayList!!)

        setAlarms()

        coordLayout = view.findViewById(R.id.myCoordinatorLayout)
        addToDoItemFAB = view.findViewById<FloatingActionButton>(R.id.addToDoItemFAB).apply {
            setOnClickListener {
                val item = ToDoItem("", "", false, null).apply {
                    todoColor = ColorGenerator.MATERIAL.random()
                }

                val newTodo = Intent(context, AddToDoActivity::class.java)
                newTodo.putExtra(TODOITEM, item)

                addToDoLauncher.launch(newTodo)
            }
        }

        customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {

            override fun show() {
                addToDoItemFAB.animate().translationY(0f)
                    .setInterpolator(DecelerateInterpolator(2f)).start()
            }

            override fun hide() {
                val fabMargin =
                    (addToDoItemFAB.layoutParams as CoordinatorLayout.LayoutParams)
                        .bottomMargin

                addToDoItemFAB.animate()
                    .translationY((addToDoItemFAB.height + fabMargin).toFloat())
                    .setInterpolator(AccelerateInterpolator(2.0f)).start()
            }
        }

        recyclerView = view.findViewById(R.id.toDoRecyclerView) as RecyclerViewEmptySupport
        recyclerView.apply {
            adapter = this@MainFragment.adapter
            layoutManager = LinearLayoutManager(context)

            setEmptyView(view.findViewById(R.id.toDoEmptyView))
            setHasFixedSize(true)

            itemAnimator = DefaultItemAnimator()
            itemTouchHelper = ItemTouchHelper(
                ItemTouchHelperClass(this@MainFragment.adapter)
            )
            itemTouchHelper.attachToRecyclerView(this)
            addOnScrollListener(customRecyclerScrollViewListener)

            if (theme == LIGHTTHEME) {
                setBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.primary_lightest,
                        activity?.theme
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireActivity()
            .getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)

        val exit = sharedPreferences.getBoolean(ReminderFragment.EXIT, false)
        if (exit) {
            sharedPreferences.edit()
                .putBoolean(ReminderFragment.EXIT, false)
                .apply()

            activity?.finish()
        }

        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR
        */

        val recreateActivity = requireActivity()
            .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(RECREATE_ACTIVITY, false)

        if (recreateActivity) {
            requireActivity()
                .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(RECREATE_ACTIVITY, false)
                .apply()

            activity?.recreate()
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = requireActivity()
            .getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)

        val changeOccured = sharedPreferences.getBoolean(CHANGE_OCCURED, false)

        if (changeOccured) {
            toDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
            adapter = BasicListAdapter(toDoItemsArrayList!!)
            recyclerView.adapter = adapter

            setAlarms()

            sharedPreferences.edit()
                .putBoolean(CHANGE_OCCURED, false)
                .apply()
        }
    }

    private fun setAlarms() {
        toDoItemsArrayList?.forEach { item ->
            if (item == null) return@forEach

            if (item.hasReminder() && item.toDoDate != null) {
                if (item.toDoDate.before(Date())) {
                    item.toDoDate = null
                    return@forEach
                }

                val intent = Intent(context, TodoNotificationService::class.java)
                intent.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                intent.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)

                createAlarm(
                    intent,
                    item.identifier.hashCode(),
                    item.toDoDate.time
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val intent = Intent(context, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.preferences -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onAddToDoItemActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_CANCELED) return

        val item = if (Build.VERSION.SDK_INT >= 33) {
            result.data?.getSerializableExtra(TODOITEM, ToDoItem::class.java)
        } else {
            result.data?.getSerializableExtra(TODOITEM) as ToDoItem
        }
        if (item == null) return

        if (item.toDoText.isEmpty()) return

        var existed = false
        if (item.hasReminder() && item.toDoDate != null) {
            val intent = Intent(context, TodoNotificationService::class.java).apply {
                putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                putExtra(TodoNotificationService.TODOUUID, item.identifier)
            }
            createAlarm(
                intent,
                item.identifier.hashCode(),
                item.toDoDate.time
            )
        }

        toDoItemsArrayList?.withIndex()?.forEach { indexed ->
            val (index, curItem) = indexed
            if (curItem == null) return

            if (curItem.identifier == toDoItemsArrayList?.get(index)?.identifier) {
                toDoItemsArrayList?.set(index, curItem)
                existed = true
                adapter.notifyDataSetChanged()
                return@forEach
            }
        }
        if (!existed) {
            addToDataStore(item)
        }
    }

    private val alarmManager: AlarmManager
        get() = requireActivity()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pendingIntent = PendingIntent
            .getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pendingIntent != null
    }

    private fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am: AlarmManager = alarmManager
        val pi: PendingIntent = PendingIntent
            .getService(context, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT
            )
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (!doesPendingIntentExist(i, requestCode)) return

        val pendingIntent = PendingIntent
            .getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            .apply { cancel() }

        alarmManager.cancel(pendingIntent)
    }

    private fun addToDataStore(item: ToDoItem) {
        toDoItemsArrayList!!.add(item)
        adapter.notifyItemInserted(toDoItemsArrayList!!.size - 1)
    }

    fun makeUpItems(items: ArrayList<ToDoItem?>, len: Int) {
        for (testString in testStrings) {
            val item = ToDoItem(testString, testString, false, Date())

//            item.setTodoColor(getResources().getString(R.color.red_secondary));
            items.add(item)
        }
    }

    inner class BasicListAdapter(
        private val items: ArrayList<ToDoItem?>,
    ) : RecyclerView.Adapter<BasicListAdapter.ViewHolder?>(),
        ItemTouchHelperClass.ItemTouchHelperAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_circle_try, parent, false)
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position] ?: return

            val sharedPreferences = requireActivity()
                .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)

            val theme = sharedPreferences.getString(THEME_SAVED, LIGHTTHEME)

            // Background color for each to-do item. Necessary for night/day mode
            // Color of title text in our to-do item. White for night mode, dark gray for day mode
            val (bgColor, todoTextColor) = if (theme == LIGHTTHEME) {
                Color.WHITE to ResourcesCompat.getColor(
                    resources,
                    R.color.secondary_text,
                    activity?.theme
                )
            } else {
                Color.DKGRAY to Color.WHITE
            }

            holder.run {
                linearLayout.setBackgroundColor(bgColor)

                if (item.hasReminder() && item.toDoDate != null) {
                    mToDoTextview.maxLines = 1
                    mTimeTextView.visibility = View.VISIBLE
                } else {
                    mTimeTextView.visibility = View.GONE
                    mToDoTextview.maxLines = 2
                }

                mToDoTextview.text = item.toDoText
                mToDoTextview.setTextColor(todoTextColor)
            }

            val myDrawable: TextDrawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(item.toDoText.substring(0, 1), item.todoColor)

            holder.mColorImageView.setImageDrawable(myDrawable)
            if (item.toDoDate != null) {
                val timeToShow = AddToDoFragment
                    .formatDate(
                        if (DateFormat.is24HourFormat(context)) {
                            DATE_TIME_FORMAT_24_HOUR
                        } else {
                            DATE_TIME_FORMAT_12_HOUR
                        },
                        item.toDoDate
                    )

                holder.mTimeTextView.text = timeToShow
            }
        }

        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemRemoved(position: Int) {
            val justDeletedToDoItem = items.removeAt(position) ?: return
            indexOfDeletedToDoItem = position

            val i = Intent(context, TodoNotificationService::class.java)
            deleteAlarm(i, justDeletedToDoItem.identifier.hashCode())
            notifyItemRemoved(position)

            Snackbar
                .make(
                    coordLayout,
                    "Deleted Todo",
                    Snackbar.LENGTH_LONG
                )
                .setAction("UNDO") {
                    items.add(indexOfDeletedToDoItem, justDeletedToDoItem)

                    if (
                        justDeletedToDoItem.toDoDate != null &&
                        justDeletedToDoItem.hasReminder()
                    ) {
                        val intent = Intent(
                            context,
                            TodoNotificationService::class.java
                        ).apply {
                            putExtra(
                                TodoNotificationService.TODOTEXT,
                                justDeletedToDoItem.toDoText
                            )
                            putExtra(
                                TodoNotificationService.TODOUUID,
                                justDeletedToDoItem.identifier
                            )
                        }
                        createAlarm(
                            intent,
                            justDeletedToDoItem.identifier.hashCode(),
                            justDeletedToDoItem.toDoDate.time
                        )
                    }
                    notifyItemInserted(indexOfDeletedToDoItem)
                }
                .show()
        }

        inner class ViewHolder(
            view: View
        ) : RecyclerView.ViewHolder(view) {

            val linearLayout: LinearLayout
            val mToDoTextview: TextView
            val mColorImageView: ImageView
            val mTimeTextView: TextView

            init {
                view.setOnClickListener {
                    val item = items[this@ViewHolder.absoluteAdapterPosition]
                    val intent = Intent(context, AddToDoActivity::class.java).apply {
                        putExtra(TODOITEM, item)
                    }
                    addToDoLauncher.launch(intent)
                }
                mToDoTextview = view.findViewById(R.id.toDoListItemTextview)
                mTimeTextView = view.findViewById(R.id.todoListItemTimeTextView)
                mColorImageView = view.findViewById(R.id.toDoListItemColorImageView)
                linearLayout = view.findViewById(R.id.listItemLinearLayout)
            }
        }

        override fun getItemCount() = items.size
    }

    private fun saveDate() {
        try {
            storeRetrieveData.saveToFile(toDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        storeRetrieveData.saveToFile(toDoItemsArrayList)
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.removeOnScrollListener(customRecyclerScrollViewListener)
    }

    override fun layoutRes() = R.layout.fragment_main

    companion object {

        const val TODOITEM = "com.rkopylknu.minimaltodo.MainActivity"
        private const val REQUEST_ID_TODO_ITEM = 100
        const val DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a"
        const val DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm"
        const val FILENAME = "todoitems.json"
        const val SHARED_PREF_DATA_SET_CHANGED = "com.rkopylknu.datasetchanged"
        const val CHANGE_OCCURED = "com.rkopylknu.changeoccured"
        const val THEME_PREFERENCES = "com.rkopylknu.themepref"
        const val RECREATE_ACTIVITY = "com.rkopylknu.recreateactivity"
        const val THEME_SAVED = "com.rkopylknu.savedtheme"
        const val DARKTHEME = "com.rkopylknu.darktheme"
        const val LIGHTTHEME = "com.rkopylknu.lighttheme"

        fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData) =
            storeRetrieveData.loadFromFile() ?: arrayListOf()

        fun newInstance() = MainFragment()
    }
}
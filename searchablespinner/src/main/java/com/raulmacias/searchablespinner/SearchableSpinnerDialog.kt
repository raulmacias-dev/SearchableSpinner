package com.raulmacias.searchablespinner

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import com.raulmacias.searchablespinner.databinding.SearchDialogBinding


class SearchableSpinnerDialog() : DialogFragment(R.layout.search_dialog), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var _binding: SearchDialogBinding

    private var items: MutableList<Any?> = arrayListOf("")
    private var mDismissText: String? = null
    private var mDialogTitle: String? = null
    private var mDismissListener: DialogInterface.OnClickListener? = null
    lateinit var onSearchableItemClick: OnSearchableItemClick<Any?>
    private var listAdapter: ArrayAdapter<Any?>? = null


    companion object {
        @JvmStatic
        val CLICK_LISTENER = "click_listener"

        fun getInstance(items: MutableList<Any?>): SearchableSpinnerDialog {
            val dialog = SearchableSpinnerDialog()
            dialog.items = items
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val rootView = layoutInflater.inflate(R.layout.search_dialog, null)
        _binding = SearchDialogBinding.bind(rootView)

        listAdapter = CustomAdapter(requireActivity(), items as ArrayList<String>) as ArrayAdapter<Any?>

        _binding.listView.adapter = listAdapter
        _binding.listView.isTextFilterEnabled = true
        _binding.listView.setOnItemClickListener { parent, view, position, id ->
            onSearchableItemClick.onSearchableItemClicked(_binding.listView.adapter.getItem(position), position)
            dialog?.dismiss()
        }

        _binding.searchView.setOnQueryTextListener(this)
        _binding.searchView.setOnCloseListener(this)
        _binding.searchView.clearFocus()

        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(rootView)
        val title = if (mDialogTitle.isNullOrBlank()) getString(R.string.search_dialog_title) else mDialogTitle
        alertBuilder.setTitle(title)

        val dismiss = if(mDismissText.isNullOrBlank()) getString(R.string.search_dialog_close) else mDismissText
        alertBuilder.setPositiveButton(dismiss, mDismissListener)

        return alertBuilder.create()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        _binding.searchView.clearFocus()
        return true
    }

    @Synchronized
    override fun onQueryTextChange(query: String?): Boolean {

        if (query.isNullOrBlank()) {
            (_binding.listView.adapter as ArrayAdapter<*>).filter.filter(null)
        } else {
            (_binding.listView.adapter as ArrayAdapter<*>).filter.filter(query)
        }

        return true
    }

    fun showDialog(parentActivity: Activity, tag: String? = null) {

        val fm = (parentActivity as AppCompatActivity).supportFragmentManager
        show(fm, tag)

    }

    override fun onClose(): Boolean {
        return false
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }


    fun setDismissText(closeText: String?) {
        mDismissText = closeText
    }


    fun setDismissText(closeText: String?, listener: DialogInterface.OnClickListener) {
        mDismissText = closeText
        mDismissListener = listener
    }


    fun setTitle(dialogTitle: String?) {
        mDialogTitle = dialogTitle
    }

}

class CustomAdapter(private val activity: Activity, private var items: List<String>): ArrayAdapter<String?>(activity, R.layout.support_simple_spinner_dropdown_item, items){

    var filtered = listOf<String>()

    init {
        filtered = items
    }

    override fun getCount() = filtered.size

    override fun getItem(position: Int): String = filtered[position]

    override fun getFilter() = filter

    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = FilterResults()

            val query = if (constraint != null && constraint.isNotEmpty()) autocomplete(constraint.toString())
            else listOf<String>()

            results.values = query
            results.count = query.size

            return results
        }

        private fun autocomplete(input: String): ArrayList<String> {
            val results = arrayListOf<String>()

            for (item in items) {
                if (item.toLowerCase().contains(input.toLowerCase())) results.add(item)
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            filtered = results.values as List<String>
            if(constraint == null){
                filtered = items
            }
            notifyDataSetInvalidated()
        }

        override fun convertResultToString(result: Any) = (result as String)
    }

}
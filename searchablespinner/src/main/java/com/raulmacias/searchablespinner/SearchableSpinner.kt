package com.raulmacias.searchablespinner

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter


class SearchableSpinner : androidx.appcompat.widget.AppCompatSpinner, View.OnTouchListener,
    OnSearchableItemClick<Any?> {

    private lateinit var mSearchDialog: SearchableSpinnerDialog
    private val mContext: Context
    private var mDialogTitle: String? = null
    private var mCloseText: String? = null
    private var mItems: MutableList<Any?> = mutableListOf(null)

    constructor(context: Context) : super(context) {
        this.mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        setAttributes(context, attrs)
        init()
    }

    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        setAttributes(context, attrs)
        init()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (mSearchDialog.isAdded) return true

        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (adapter != null) {
                mItems.clear()
                for (i in 0 until (adapter.count )) {
                    mItems.add(adapter.getItem(i) )
                }

                mSearchDialog.showDialog(scanForActivity(mContext)!! ,"search")

            }
        }
        return true
    }

    override fun onSearchableItemClicked(item: Any?, position: Int) {
        setSelection(mItems.indexOf(item))
    }

    fun setDialogTitle(title: String?) {
        mDialogTitle = title
        mSearchDialog.setTitle(title)
    }

    fun setDismissText(dismiss: String?) {
        mCloseText = dismiss
        mSearchDialog.setDismissText(dismiss)
    }

    fun setDismissText(dismiss: String?, onDismissListener: DialogInterface.OnClickListener) {
        mCloseText = dismiss
        mSearchDialog.setDismissText(dismiss, onDismissListener)
    }

    private fun init() {
        mSearchDialog = SearchableSpinnerDialog.getInstance(items = mItems)
        mSearchDialog.setTitle(mDialogTitle)
        mSearchDialog.setDismissText(mCloseText)
        mSearchDialog.onSearchableItemClick = this

        setOnTouchListener(this)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner)

        for (i in 0 until attributes.indexCount) {
            val attr = attributes.getIndex(i)
            when (attr) {
                R.styleable.SearchableSpinner_closeText -> mCloseText = attributes.getString(attr)
                R.styleable.SearchableSpinner_dialogTitle -> mDialogTitle = attributes.getString(attr)
            }
        }
        attributes.recycle()
    }

    private fun scanForActivity(context: Context?): Activity? {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> scanForActivity(context.baseContext)
            else -> null
        }

    }

    override fun getSelectedItem(): Any {
        return super.getSelectedItem()
    }

    override fun setSelection(position: Int) {
        super.setSelection(position)
    }

    /**
     * Searchable spinner works only with ArrayAdapter. Spinner Adapter **lost state after rotate**
     *
     *  @param adapter ArrayAdapter<Any?>
     */
    fun setAdapter(adapter: ArrayAdapter<Any?>) {
        super.setAdapter(adapter)
    }


    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        super.setOnItemSelectedListener(listener)
    }

}
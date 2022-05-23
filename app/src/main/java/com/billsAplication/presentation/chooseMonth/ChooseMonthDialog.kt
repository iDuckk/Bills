package com.billsAplication.presentation.chooseCategory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.billsAplication.R

class ChooseMonthDialog: DialogFragment() {

    private val KEY_MONTH_LIST_FRAGMENT = "key_month_from_fragment"
    private val KEY_MONTH_ITEMS_DIALOG = "key_month_from_dialog"
    private val KEY_CHOSEN_MONTH_LIST_FRAGMENT = "key_CHOSEN_month_from_fragment"
    private val REQUESTKEY_MONTH_ITEM = "RequestKey_MONTH_item"
    private var checkedItems = booleanArrayOf()
    private var sentListItems = arrayOf<String>()
    private var getMonthList = arrayOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //get list of categories
        getMonthList = requireArguments().getStringArray(KEY_MONTH_LIST_FRAGMENT) as Array<String>
        //get boolean list of chosen categories
        checkedItems = requireArguments().getBooleanArray(KEY_CHOSEN_MONTH_LIST_FRAGMENT)!!
        //set items in getCategoryList if chosen them
        checkedItems.forEachIndexed { index, item ->
            if(item){
                sentListItems += getMonthList[index]
            }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.month))
                .setMultiChoiceItems(getMonthList, checkedItems) {
                        dialog, which, isChecked ->
                    checkedItems[which] = isChecked //if chosen item
                    val item = getMonthList.get(which) // Get the clicked item
                    if(isChecked)
                        sentListItems += item    //Add item
                    else
                        addItem(item)    //Remove item when Unchecked
                }
                .setPositiveButton(getString(R.string.add_apply)
                ) {
                        dialog, id ->
                    // User clicked OK, so save the selectedItems results somewhere
                    setFragmentResult(REQUESTKEY_MONTH_ITEM,
                        bundleOf(KEY_MONTH_ITEMS_DIALOG to sentListItems))
                }
                .setNegativeButton(getString(R.string.search_cancel)) {
                        dialog, _ ->  dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.ChooseCategoryDialog_error_null))
    }

    private fun addItem(item: String){
        var list = arrayOf<String>()
            sentListItems.forEach {
                if(it != item)
                   list += it
            }
            sentListItems = list
    }
}
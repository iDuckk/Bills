package com.billsAplication.presentation.chooseCategory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.billsAplication.R

class ChooseCategoryDialog: DialogFragment() {

    private val KEY_CATEGORY_LIST_FRAGMENT = "key_category_from_fragment"
    private val KEY_CATEGORY_ITEMS_DIALOG = "key_category_from_dialog"
    private val KEY_CHOSEN_CATEGORY_LIST_FRAGMENT = "key_CHOSEN_category_from_fragment"
    private val REQUESTKEY_CATEGORY_ITEM = "RequestKey_Category_item"
    private var checkedItems = booleanArrayOf()
    private var sentListItems = arrayOf<String>()
    private var getCategoryList = arrayOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // get list of categories
        getCategoryList = requireArguments().getStringArray(KEY_CATEGORY_LIST_FRAGMENT) as Array<String>
        //get boolean list of chosen categories
        checkedItems = requireArguments().getBooleanArray(KEY_CHOSEN_CATEGORY_LIST_FRAGMENT)!!
        //set items in getCategoryList if chosen them
        checkedItems.forEachIndexed { index, item ->
            if(item){
                sentListItems += getCategoryList[index]
            }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.search_choose_category))
                .setMultiChoiceItems(getCategoryList, checkedItems) {
                        dialog, which, isChecked ->
                    checkedItems[which] = isChecked //if chosen item
                    val item = getCategoryList.get(which) // Get the clicked item
                    if(isChecked)
                        sentListItems += item    //Add item
                    else
                        addItem(item)    //Remove item when Unchecked
                }
                .setPositiveButton(getString(R.string.add_apply)
                ) {
                        dialog, id ->
                    // User clicked OK, so save the selectedItems results somewhere
                    setFragmentResult(REQUESTKEY_CATEGORY_ITEM,
                        bundleOf(KEY_CATEGORY_ITEMS_DIALOG to sentListItems))
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
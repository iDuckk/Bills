package com.billsAplication.presentation.chooseCategory

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.billsAplication.R


class SetLanguageDialog: DialogFragment() {

    private val KEY_LANGUAGE_LIST_FRAGMENT = "key_language_from_fragment"
    private val KEY_CHOSEN_LANGUAGE_LIST_FRAGMENT = "key_SET_language_from_fragment"
    private val REQUEST_KEY_LANGUAGE_ITEM = "RequestKey_LANGUAGE_item"
    private val KEY_LANGUAGE_ITEMS_DIALOG = "key_language_from_dialog"
    private var checkedItems = 0
    private var sentItems = 0
    private var getLanguageList = arrayOf<String>()

    var myClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            val lv: ListView = (dialog as AlertDialog).listView
            if (which == Dialog.BUTTON_POSITIVE) // выводим в лог позицию выбранного элемента
                Log.d("TAG","pos = " + lv.getCheckedItemPosition())
            else  // выводим в лог позицию нажатого элемента
                sentItems = which
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //get list of categories
        getLanguageList = requireArguments().getStringArray(KEY_LANGUAGE_LIST_FRAGMENT) as Array<String>
        //get boolean list of chosen categories
        checkedItems = requireArguments().getInt(KEY_CHOSEN_LANGUAGE_LIST_FRAGMENT)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.language))
                .setSingleChoiceItems(getLanguageList, checkedItems, myClickListener)
                .setPositiveButton(getString(R.string.add_apply)
                ) {
                        dialog, id ->
                    // User clicked OK, so save the selectedItems results somewhere
                    setFragmentResult(REQUEST_KEY_LANGUAGE_ITEM,
                        bundleOf(KEY_LANGUAGE_ITEMS_DIALOG to sentItems))
                }
                .setNegativeButton(getString(R.string.search_cancel)) {
                        dialog, _ ->  dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.ChooseCategoryDialog_error_null))
    }
}
package com.billsAplication.presentation.adapter.dialogCategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.CategoryItemBinding
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class DialogCategoryAdapter  @Inject constructor(): ListAdapter<BillsItem, DialogCategoryViewHolder>(
    DialogCategoryCallback()
) {

    var onClickListenerDelete: ((item : BillsItem) -> Unit)? = null
    var onClickListenerGetItem: ((item : BillsItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogCategoryViewHolder {
        return DialogCategoryViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DialogCategoryViewHolder, position: Int) {
        val item = getItem(position)

        holder.tv_Category.text = item.category.toString()

        holder.tv_Category.setOnClickListener {
            onClickListenerGetItem?.invoke(item)
        }

        holder.tv_Category.setOnLongClickListener(){
            if(holder.im_Delete.isVisible)
                holder.im_Delete.visibility = View.GONE
            else
                holder.im_Delete.visibility = View.VISIBLE
            true
        }

        holder.im_Delete.setOnClickListener {
            holder.im_Delete.visibility = View.GONE
            onClickListenerDelete?.invoke(item)
        }

    }

}
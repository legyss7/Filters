package com.filters.presentation.adaptor

import androidx.recyclerview.widget.RecyclerView
import com.filters.domain.listfilter.Filter
import com.filters.databinding.FilterListFormBinding

class ListViewHolder(
    private val binding: FilterListFormBinding,
    private val onClick: (Filter) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Filter) {
        with(binding) {
            titleCard.text = item.titleFilter
            imgSchematic.setImageResource(item.imgSchema)

            root.setOnClickListener {
                onClick(item)
            }
        }
    }
}
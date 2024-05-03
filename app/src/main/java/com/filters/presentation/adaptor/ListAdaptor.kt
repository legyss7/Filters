package com.filters.presentation.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.filters.domain.listfilter.Filter
import com.filters.databinding.FilterListFormBinding

class ListAdaptor(private val onClick: (Filter) -> Unit) : RecyclerView.Adapter<ListViewHolder> () {

    private var listFilter: List<Filter> = emptyList()

    fun submitList(list: List<Filter>) {
        listFilter = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = FilterListFormBinding
            .inflate(LayoutInflater.from(parent.context))
        return ListViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int {
        return listFilter.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = listFilter[position]
        holder.bind(item)
    }

}
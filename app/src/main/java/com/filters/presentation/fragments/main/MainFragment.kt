package com.filters.presentation.fragments.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.filters.R
import com.filters.domain.listfilter.Filter
import com.filters.databinding.FragmentMainBinding
import com.filters.presentation.adaptor.ListAdaptor
import com.filters.presentation.fragments.calcuclation.CalculationsFragment
import com.filters.presentation.fragments.main.MainViewModel

import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val adapterFilter: ListAdaptor by lazy {
        ListAdaptor { clickFilter -> onClickFilter(clickFilter) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapterFilter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getListFilters(binding.root.context)
        }

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listFilters.collect { listFilters ->
                adapterFilter.submitList(listFilters)
            }
        }
    }

    private fun onClickFilter(item: Filter) {
        val fragment = CalculationsFragment.newInstance(item)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_tag, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
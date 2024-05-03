package com.filters.presentation.fragments.calcuclation

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.filters.App
import com.filters.R
import com.filters.domain.listfilter.Filter
import com.filters.databinding.FragmentCalculationsBinding
import com.filters.domain.CalculationResult
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.launch

class CalculationsFragment : Fragment() {

    private var _binding: FragmentCalculationsBinding? = null
    private val binding get() = _binding!!

    companion object {

        fun newInstance(item: Filter): CalculationsFragment {
            val fragment = CalculationsFragment()
            val args = Bundle()
            args.putParcelable("filter_item", item)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: CalculationsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClassL: Class<T>): T {
                val enteredDao = (binding.root.context.applicationContext as App)
                    .db.enteredDao()
                return CalculationsViewModel(enteredDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculationsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Filter>("filter_item")?.let { item ->
            fragmentInitialization(item)
        }

        configureUIListeners()

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                updateUIState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.calculationResult.collect { result ->
                if (result != null) {
                    updateInformation(result)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.enteredData.collect { enteredData ->
                enteredData?.let { data ->
                    binding.valueOneEdit.setText(data.valueOne.toString())
                    binding.valueTwoEdit.setText(data.valueTwo.toInt().toString())
                    binding.valueThreeEdit.setText(data.valueThree.toString())
                    binding.valueFourEdit.setText(data.valueFour.toString())
                    binding.valueFiveEdit.setText(data.valueFive.toString())
                    binding.valueSixEdit.setText(data.valueSix.toString())
                }
            }
        }
        viewModel.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveData()
        _binding = null
    }

    private fun fragmentInitialization(item: Filter) {

        with(binding) {
            textTitle.text = item.titleFilter
            imgSchematic.setImageResource(item.imgSchema)
            valueOneLayout.hint = item.label[0]
            valueTwoLayout.hint = item.label[1]
            valueThreeLayout.hint = item.label[2]
            valueFourLayout.hint = item.label[3]
            valueFiveLayout.hint = item.label[4]
            valueSixLayout.hint = item.label[5]
            viewModel.updateData(item.errors, item.typeFilter, item.fields)
        }
    }

    private fun configureUIListeners() {
        binding.valueOneEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueOne(text)
        }
        binding.valueTwoEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueTwo(text)
        }
        binding.valueThreeEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueThree(text)
        }
        binding.valueFourEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueFour(text)
        }
        binding.valueFiveEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueFive(text)
        }
        binding.valueSixEdit.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValueSix(text)
        }

        binding.buttonCalculate.setOnClickListener {
            // Спрятать клавиатуру
            val inputMethodManager = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            viewModel.calculate()
        }
    }

    private fun updateUIState(state: State) {
        binding.buttonCalculate.isEnabled = when (state) {
            is State.INPUT -> false
            is State.CALCULATION -> true
            is State.SUCCESS -> false
            is State.ERROR -> false
        }

        binding.apply {
            valueOneLayout.isErrorEnabled = state is State.ERROR
            valueTwoLayout.isErrorEnabled = state is State.ERROR
            valueThreeLayout.isErrorEnabled = state is State.ERROR
            valueFourLayout.isErrorEnabled = state is State.ERROR
            valueFiveLayout.isErrorEnabled = state is State.ERROR
            valueSixLayout.isErrorEnabled = state is State.ERROR

            if (state is State.ERROR) {
                valueOneLayout.error = state.errorValueOne
                valueTwoLayout.error = state.errorValueTwo
                valueThreeLayout.error = state.errorValueThree
                valueFourLayout.error = state.errorValueFour
                valueFiveLayout.error = state.errorValueFive
                valueSixLayout.error = state.errorValueSix
            }
        }
    }

    private fun updateInformation(result: CalculationResult) {
        binding.informationOutput.text = buildString {
            append(result.result)
            graph(result.graphData)
        }
    }

    private fun graph(dataPoint: Array<DataPoint>) {
        val graph = binding.graph
        val series = LineGraphSeries(dataPoint)
        val gridLabel = graph.gridLabelRenderer

        graph.title = "АЧХ  U, дБ"
        graph.titleColor = resources.getColor(R.color.green_b)
        series.color = resources.getColor(R.color.green_b)
        gridLabel.horizontalAxisTitleColor = resources.getColor(R.color.green_b)
        gridLabel.verticalAxisTitleColor = resources.getColor(R.color.green_b)
        gridLabel.horizontalAxisTitle = "f, кГц"
//        gridLabel.verticalAxisTitle = "U, дБ"

        graph.viewport.apply {
            setXAxisBoundsManual(true)
            setMinX(0.0)
            setMaxX(dataPoint.maxOf { it.x })
            setYAxisBoundsManual(true)
            setMinY(setMinY(dataPoint))
            setMaxY(dataPoint.maxOf { it.y } + 1.0)
        }

        graph.series.clear()
        graph.addSeries(series)

        series.setOnDataPointTapListener { _, dataPoint ->
            Toast.makeText(
                requireContext(),
                "Clicked at X: ${dataPoint.x}, Y: ${dataPoint.y}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setMinY(dataPoint: Array<DataPoint>): Double {
        return if (dataPoint.minOf { it.y } > -80.0) {
            dataPoint.minOf { it.y } + 1
        } else {
            -80.0
        }
    }
}
package com.example.graphite

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graphite.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // use mainActivity.appModel
    private val app = MainActivity.app

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        // set calculatorSelections to the allowedCalculators from appModel
        val calculatorSelections = app.getAllowedCalculators().toTypedArray()

        // update the spinner calculatorSpinner
        binding.calculatorSpinner.adapter = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, calculatorSelections)

        return binding.root

    }

    fun navigateToSecondFragment() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val allowedCalculators = app.getAllowedCalculators();

        val allowedCalculatorsNames = allowedCalculators.map { it["name"] }

        println("allowed calculators: $allowedCalculators")

        // update the spinner calculatorSpinner
        binding.calculatorSpinner.adapter = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, allowedCalculatorsNames.toTypedArray())

        binding.buttonFirst.setOnClickListener {
            // set appModel in mainactivity.kt to the new url from calculatorSpinner
            for (calculator in allowedCalculators) {
                if (calculator["name"] == binding.calculatorSpinner.selectedItem) {
                    app.setCalculatorURL(calculator["url"]!!)
                }
            }

            // navigate to the second fragment
            navigateToSecondFragment()
        }

        // if exit_exam button is clicked run leaveExam in examactivity
        binding.exitExam.setOnClickListener {
            (activity as ExamActivity).leaveExam()
        }

        // hide calculatorSpinner and button_first if isExamMode is true
        if (app.getIsExamMode()) {
            binding.exitExam.visibility = View.VISIBLE
            // if calculators is empty
            if (app.getAllowedCalculators().isEmpty()) { // if not allowed any calculators
                binding.textviewFirst.text = "Class Mode:\n\n No Calculators Allowed"
                binding.calculatorSpinner.visibility = View.GONE
                binding.buttonFirst.visibility = View.GONE
            } else {
                binding.textviewFirst.text = "Class Mode"
                binding.calculatorSpinner.visibility = View.VISIBLE
                binding.buttonFirst.visibility = View.VISIBLE
            }
        } else {
            binding.exitExam.visibility = View.GONE
            binding.calculatorSpinner.visibility = View.VISIBLE
            binding.buttonFirst.visibility = View.VISIBLE
            binding.textviewFirst.text = "Welcome! Select the desired Resource below."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
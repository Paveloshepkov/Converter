package com.example.converter.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.converter.ConverterApplication
import com.example.converter.R
import com.example.converter.databinding.FragmentConverterBinding
import com.example.converter.viewmodel.ConverterViewModel
import com.example.converter.viewmodel.ConverterViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConverterFragment : Fragment() {

    private val viewModel: ConverterViewModel by activityViewModels {
        ConverterViewModelFactory(
            (activity?.application as ConverterApplication)
        )
    }

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    var left = false
    var right = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            lifecycleOwner = viewLifecycleOwner
            converterViewModel = viewModel
            rightNumber.isFocused

            leftNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (left) {
                        left = false
                    } else {
                        right = true
                        viewModel.setLeftNumber(binding.leftNumber.text.toString())
                        binding.rightNumber.setText(viewModel.rightNumber.toString())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })

            rightNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (right) {
                        right = false
                    } else {
                        left = true
                        viewModel.setRightNumber(binding.rightNumber.text.toString())
                        binding.leftNumber.setText(viewModel.leftNumber.toString())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })
        }

        binding.leftEdit.setOnClickListener()
        {
            val action =
                ConverterFragmentDirections
                    .actionConverterFragmentToValuteListFragment(true)
            this.findNavController().navigate(action)
        }

        binding.rightEdit.setOnClickListener()
        {
            val action =
                ConverterFragmentDirections
                    .actionConverterFragmentToValuteListFragment(false)
            this.findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.converter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> showInformationDialog()
            R.id.date -> {
                if(viewModel.dateInString!=""){
                    Toast.makeText(activity, viewModel.dateInString, Toast.LENGTH_LONG).show()
                }

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInformationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.instructions))
            .setCancelable(true)
            .setNeutralButton(getString(R.string.ok)) { _, _ -> }
            .show()
    }
}

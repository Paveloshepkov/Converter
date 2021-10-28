package com.example.converter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.converter.ConverterApplication
import com.example.converter.R
import com.example.converter.databinding.FragmentValuteListBinding
import com.example.converter.databinding.ValuteItemBinding
import com.example.converter.domain.Valute
import com.example.converter.viewmodel.ConverterViewModel
import com.example.converter.viewmodel.ConverterViewModelFactory

class ValuteListFragment : Fragment() {

    private val navigationArgs: ValuteListFragmentArgs by navArgs()

    private val viewModel: ConverterViewModel by activityViewModels {
        ConverterViewModelFactory(
            (activity?.application as ConverterApplication)
        )
    }
    private var viewModelAdapter: ValuteListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentValuteListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_valute_list,
            container,
            false
        )
        val myArg = navigationArgs.trueOrFalse

        binding.lifecycleOwner = viewLifecycleOwner
        binding.converterViewModel = viewModel

        viewModelAdapter = if (myArg) {
            ValuteListAdapter(ValuteClick {
                viewModel.getLeftValute(it)
                this.findNavController().navigate(R.id.converterFragment)
            })
        } else {
            ValuteListAdapter(ValuteClick {
                viewModel.getRightValute(it)
                this.findNavController().navigate(R.id.converterFragment)
            })
        }

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.valuteList.observe(viewLifecycleOwner, { item ->
            item?.apply {
                viewModelAdapter?.valute = item
            }
        })

    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Ошибка сети", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.valute_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update -> {
                viewModel.refreshDataFromRepository()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class ValuteListAdapter(private val callBack: ValuteClick) :
    RecyclerView.Adapter<ValuteViewHolder>() {

    var valute: List<Valute> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValuteViewHolder {
        val withDataBinding: ValuteItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ValuteViewHolder.LAYOUT,
            parent,
            false
        )
        return ValuteViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ValuteViewHolder, position: Int) {
        holder.binding.also {
            it.valute = valute[position]
            it.valuteCallback = callBack
        }
    }

    override fun getItemCount() = valute.size
}

class ValuteViewHolder(val binding: ValuteItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.valute_item
    }
}

class ValuteClick(val block: (Valute) -> Unit) {
    fun onClick(valute: Valute) = block(valute)
}


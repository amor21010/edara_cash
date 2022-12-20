package net.edara.edaracash.presentation.search_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import net.edara.edaracash.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    private val viewModel: ResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        val adapter = ResultAdapter { item ->
            findNavController().navigate(
                ResultFragmentDirections.actionResultFragmentToEditWorkOrderFragment(
                    item
                )
            )

        }

        binding.resultRecycler.adapter = adapter
        viewModel.resultOrder.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(listOf(it))
            }
        }

        return binding.root
    }


}
package com.billsAplication.presentation.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.databinding.FragmentSearchBinding
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    private var imageRoll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .visibility = View.GONE

        titleBar()

        searchViews()
    }

    private fun searchViews(){
        imageRoll()

    }

    private fun imageRoll(){
        binding.imRollViews.setOnClickListener {
            if(imageRoll) {
                binding.imRollViews.setImageResource(R.drawable.ic_arrow_up)
                imageRoll = false
                binding.cardViewSearch.visibility = View.VISIBLE
            }
            else {
                binding.imRollViews.setImageResource(R.drawable.ic_arrow_down)
                imageRoll = true
                binding.cardViewSearch.visibility = View.GONE
            }
        }
    }

    private fun titleBar() {
        binding.imSearchBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_billsListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
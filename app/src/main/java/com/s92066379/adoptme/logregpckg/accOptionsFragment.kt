package com.s92066379.adoptme.logregpckg


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.s92066379.adoptme.R
import com.s92066379.adoptme.databinding.FragmentAccOptionsBinding

class accOptionsFragment: Fragment(R.layout.fragment_acc_options) {
    private lateinit var binding: FragmentAccOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnoptionLogin.setOnClickListener{
            findNavController().navigate(R.id.action_accOptionsFragment_to_loginFragment)
        }
        binding.btnoptionRegister.setOnClickListener{
            findNavController().navigate(R.id.action_accOptionsFragment_to_registerFragment2)
        }
    }
}
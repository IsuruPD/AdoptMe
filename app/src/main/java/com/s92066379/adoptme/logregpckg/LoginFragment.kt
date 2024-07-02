package com.s92066379.adoptme.logregpckg


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.s92066379.adoptme.viewmodel.LoginVM
import com.s92066379.adoptme.R
import com.s92066379.adoptme.util.Resource
import com.s92066379.adoptme.activities.OptionsActivity
import com.s92066379.adoptme.databinding.FragmentLoginBinding
import com.s92066379.adoptme.dialogBoxes.setupBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginVM>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginSubTitle.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment2)
        }

        binding.apply{
            btnLogin.setOnClickListener{
                val email=loginEmailEdt.text.toString()
                val password=loginPasswordEdt.text.toString()

                viewModel.login(email, password)
            }
        }

        binding.loginForgotPaswordTxt.setOnClickListener{
            setupBottomSheetDialog { email->
                viewModel.resetPassword(email)

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        Snackbar.make(requireView(), "Check your email for the reset link", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(), "Error! ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), "Logged In!",Toast.LENGTH_LONG).show()
                        Intent(requireActivity(), OptionsActivity:: class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                        binding.btnLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }
}
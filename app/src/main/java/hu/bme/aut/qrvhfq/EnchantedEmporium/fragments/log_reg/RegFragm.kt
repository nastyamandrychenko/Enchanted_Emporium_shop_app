package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.User
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.RegisterViewModel
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentRegisterBinding

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegFragm: Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            RegisterBut.setOnClickListener{
                val user = User(
                    firstNameLable.text.toString().trim(),
                    LastNameLable.text.toString().trim(),
                    EmailLabel.text.toString().trim()


                )
                val password = PasswordLable.text.toString()
                viewModel.createAccountwithEmailandPassword(user,password)
                lifecycleScope.launchWhenStarted {
                 viewModel.register.collect{
                     when(it){
                         is Resource.Loading -> {

                         binding.RegisterBut.startAnimation()
                         }
                         is Resource.Success -> {
                         Log.d("test", it.data.toString())
                             binding.RegisterBut.revertAnimation()
                         }
                         is Resource.Error ->{
                             Log.d(TAG, it.message.toString())
                             binding.RegisterBut.revertAnimation()


                         }
                     }
                 }
                }
            }
        }
    }
}
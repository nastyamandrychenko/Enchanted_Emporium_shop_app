package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragm : Fragment(R.layout.fragment_account_options){
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.LogInBut.setOnClickListener{
            findNavController().navigate(R.id.action_accountOptionsFragm_to_loginFragm)

        }

        binding.RegisterBut.setOnClickListener{
            findNavController().navigate(R.id.action_accountOptionsFragm_to_regFragm)

        }

    }

    }
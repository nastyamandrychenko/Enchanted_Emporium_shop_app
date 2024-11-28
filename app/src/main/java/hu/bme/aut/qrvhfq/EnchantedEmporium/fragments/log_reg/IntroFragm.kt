package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.ShoppingActivity
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.SharedPrefsUtil
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentIntroBinding

class IntroFragm : Fragment(R.layout.fragment_intro) {
private lateinit var binding: FragmentIntroBinding
    private lateinit var sharedPrefsUtil: SharedPrefsUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefsUtil = SharedPrefsUtil(requireContext())
        if (sharedPrefsUtil.shouldAutoLogin(requireContext())) {

            val intent = Intent(requireActivity(), ShoppingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        binding.startBut.setOnClickListener {
       findNavController().navigate(R.id.action_introFragm_to_accountOptionsFragm)

        }
    }

}
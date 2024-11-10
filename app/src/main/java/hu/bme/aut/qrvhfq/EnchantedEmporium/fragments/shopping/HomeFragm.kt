package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentHomeBinding

class HomeFragm : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val categoriesFragments = arrayListOf<Fragment>(

        )
    }
}
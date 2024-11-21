package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.HomeViewpagerAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories.home_categories.AutumnPotionsCategoryFragment
import hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories.home_categories.HalloweenCategoryFragment
import hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories.home_categories.MoonPhasesCategoryFragment
import hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories.home_categories.TrendingCategoryFragment
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
            TrendingCategoryFragment(),
            HalloweenCategoryFragment(),
            AutumnPotionsCategoryFragment(),
            MoonPhasesCategoryFragment()

        )

        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Trending now"
                1 -> tab.text = "Halloween"
                2 -> tab.text = "Autumn potions"
                3 -> tab.text = "Moon Phases Collection"

            }
        }.attach()


        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            tab.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selector)

        }


        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)

            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(20, 0, 20, 0)
            tab.layoutParams = layoutParams

        }
    }
}
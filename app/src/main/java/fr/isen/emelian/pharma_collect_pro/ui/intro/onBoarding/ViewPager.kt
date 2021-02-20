package fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding

import androidx.fragment.app.Fragment
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding.screens.FirstScreenFragment
import fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding.screens.SecondScreenFragment
import fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding.screens.ThirdScreenFragment

class ViewPager(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    @NonNull
    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return FirstScreenFragment()
            1 -> return SecondScreenFragment()
            2 -> return ThirdScreenFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return 3
    }

}
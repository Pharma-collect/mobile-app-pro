package fr.isen.emelian.pharma_collect_pro.ui.intro.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cuberto.liquid_swipe.LiquidPager
import fr.isen.emelian.pharma_collect_pro.R

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)
        val pager = view.findViewById(R.id.viewPager) as LiquidPager
        val viewPager = ViewPager(parentFragmentManager, 1)
        pager.adapter = viewPager
        return view
    }

}
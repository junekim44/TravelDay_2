package com.example.travelday_2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newteamproj.CheckListFragment

class ViewPagerAdapter(fm:FragmentManager, lifecycle:Lifecycle, var tabCount: Int):FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->CheckListFragment()
            1->TravelListParentFragment()
            2->CommunityParentFragment()
            else->TravelListParentFragment()
        }
    }
}
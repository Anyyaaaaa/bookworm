package com.example.bookworm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

import paterns.adapters.adapters.LibraryPagerAdapter;

public class LibraryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LibraryPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        pagerAdapter = new LibraryPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Читаю");
                            break;
                        case 1:
                            tab.setText("Буду читати");
                            break;
                        case 2:
                            tab.setText("Прочитано");
                            break;
                        case 3:
                            tab.setText("Улюблене");
                            break;
                    }
                }
        ).attach();

        return view;
    }
}

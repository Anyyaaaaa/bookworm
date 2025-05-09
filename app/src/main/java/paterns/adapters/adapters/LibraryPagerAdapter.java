package paterns.adapters.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookworm.fragment.ContentFragment;
import com.example.bookworm.fragment.FavoriteFragment;
import com.example.bookworm.fragment.ReadFragment;
import com.example.bookworm.fragment.ReadingFragment;
import com.example.bookworm.fragment.WantToReadFragment;

public class LibraryPagerAdapter extends FragmentStateAdapter {
    public LibraryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return ContentFragment.newInstance("Читаю");
            case 1: return ContentFragment.newInstance("Буду читати");
            case 2: return ContentFragment.newInstance("Прочитано");
            case 3: return ContentFragment.newInstance("Улюблене");
            default: return ContentFragment.newInstance("Читаю");
        }
    }


    @Override
    public int getItemCount() {
        return 4;
    }
}



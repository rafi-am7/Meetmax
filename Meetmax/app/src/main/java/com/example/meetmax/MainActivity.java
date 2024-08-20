package com.example.meetmax;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.meetmax.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    ///ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    FeedFragment feedFragment;
    NotificationsFragment notificationsFragment;
    MyCommunityFragment myCommunityFragment;
    SettingsFragment settingsFragment;
    ExploreFragment exploreFragment;
    PostAddFragment postAddFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EdgeToEdge.enable(this);
        //binding=ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        exploreFragment = new ExploreFragment();
        feedFragment = new FeedFragment();
        notificationsFragment = new NotificationsFragment();
        myCommunityFragment = new MyCommunityFragment();
        settingsFragment = new SettingsFragment();
        postAddFragment = new PostAddFragment();
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        replaceFragment(feedFragment);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_menu_feed)
                {
                    replaceFragment(feedFragment);
                }
                else if(item.getItemId()==R.id.nav_menu_community)
                {
                    replaceFragment(myCommunityFragment);
                }
                else if(item.getItemId()==R.id.nav_menu_explore)
                {
                    replaceFragment(exploreFragment);
                }
                else if(item.getItemId()==R.id.nav_menu_notifications)
                {
                    replaceFragment(notificationsFragment);
                }
                else if(item.getItemId()==R.id.nav_menu_settings)
                {
                    replaceFragment(settingsFragment);
                }
                return true;
            }
        });

    }
    void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_bottom_nav,fragment)
                .commit();

    }
}
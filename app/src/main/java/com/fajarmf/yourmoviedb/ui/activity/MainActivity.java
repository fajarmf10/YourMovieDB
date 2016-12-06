package com.fajarmf.yourmoviedb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fajarmf.yourmoviedb.R;
import com.fajarmf.yourmoviedb.ui.fragment.MainFragment;
import com.fajarmf.yourmoviedb.ui.settings.SettingsActivity;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class MainActivity  extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MAIN_FRAGMENT_TAG = "MFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        } else {
            addMainFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.aboutme) {
            Intent aboutMeIntent = new Intent(this, AboutMe.class);
            startActivity(aboutMeIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addMainFragment() {
        if (!isFinishing()) {
            MainFragment mainFragment = MainFragment.newInstance();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.main_grid_container, mainFragment, MAIN_FRAGMENT_TAG).commit();
        }
    }
}

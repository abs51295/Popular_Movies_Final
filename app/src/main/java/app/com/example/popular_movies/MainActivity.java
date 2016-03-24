package app.com.example.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import app.com.example.popular_movies.sync.MovieSyncAdapter;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class MainActivity extends AppCompatActivity implements DetailActivityFragment.Callback {
    private Boolean isTwoPane = false;


    @Override
    public void onItemSelected(long MovieId) {
        if (isTwoPane) {
            Bundle mBundle = new Bundle();
            mBundle.putLong(DetailActivityFragment.PAR_KEY, MovieId);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setText("Info"));
            tabLayout.addTab(tabLayout.newTab().setText("Reviews"));
            tabLayout.addTab(tabLayout.newTab().setText("Trailers"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final PagerAdapter adapter = new app.com.example.popular_movies.model.PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), mBundle);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putLong(DetailActivityFragment.PAR_KEY, MovieId);
            intent.putExtras(mBundle);

            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
        isTwoPane = findViewById(R.id.pager) != null;
        MovieSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

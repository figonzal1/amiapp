package techwork.ami;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import techwork.ami.Category.FragmentCategory;
import techwork.ami.Offers.OffersReservations.OffersReservationsList.OffersReservationsActivity;
import techwork.ami.Offers.OrdersList.FragmentOrder;
import techwork.ami.Promotion.MyPromotions.MyPromotionsActivity;
import techwork.ami.Promotion.PromotionsList.FragmentHome;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_REQUEST_CODE = 101;

    public static boolean notificate = true;
    public static Date now = new Date();

    private String name;
    private String lastnames;
    private String email;
    private String id;
    private String gender;
    private String firstLogin;

    private ViewPager viewPager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Get the profile data from the session (sharedPreferences) and showed on the nav bar
        updateProfileView();

        // Check if the user is logged in
        if (id.equals("-1")) {
            // if not, show the login activity
            Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(iLogin);
        }

        // Check if the user already made the first login
        else if (firstLogin.equals("1")) {
            // if not, show the after login activity
            Intent iAfterLogin = new Intent(MainActivity.this, AfterLoginActivity.class);
            finish();
            startActivity(iAfterLogin);
        }

        fragment = null;

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_REQUEST_CODE);

        //Assign pageViewer and the Tab Handler
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), MainActivity.this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.toolbarBottom);
        tabLayout.setupWithViewPager(viewPager);

        //Assign the ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);

    }

    // Get the profile's data from the shared preferences
    private void getProfile() {
        // TODO: revisar seguridad de sharedPreferences, onda si puede cambiar el id a la mala
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        id = sharedPref.getString(Config.KEY_SP_ID, "-1");
        name = sharedPref.getString(Config.KEY_SP_NAME, " ");
        lastnames = sharedPref.getString(Config.KEY_SP_LASTNAMES, "");
        email = sharedPref.getString(Config.KEY_SP_EMAIL, "");
        gender = sharedPref.getString(Config.KEY_SP_GENDER, "0");
        firstLogin = sharedPref.getString(Config.KEY_SP_FIRST_LOGIN, "1");
    }

    // Update the profile data (from the SharedPreferences) showed on the nav bar
    private void updateProfileView() {
        getProfile();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        ImageView imageViewProfileIcon = (ImageView) header.findViewById(R.id.navHeaderImageView);
        TextView textViewNavHeaderName = (TextView) header.findViewById(R.id.navHeaderTextName);
        TextView textViewNavHeaderEmail = (TextView) header.findViewById(R.id.navHeaderTextEmail);

        String name_capitalize = name.substring(0, 1).toUpperCase() + name.substring(1);

        textViewNavHeaderName.setText(name_capitalize + " " + lastnames);
        textViewNavHeaderEmail.setText(email);

        switch (gender) {
            case "1":
                imageViewProfileIcon.setImageResource(R.drawable.profile_icon_woman);
                break;
            case "2":
                imageViewProfileIcon.setImageResource(R.drawable.profile_icon_man);
                break;
            default:
                imageViewProfileIcon.setImageResource(R.drawable.profile_icon_other);
                break;
        }
    }

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    // logout the current session
    private void logout(){
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Config.KEY_SP_ID, "-1");
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_top, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FragmentHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_maps:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                return true;

            case R.id.action_logout:
                logout();
                Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intentLogout);
                return true;

            /*case R.id.action_config:
                Toast.makeText(getApplicationContext(),"Configuraci??n",Toast.LENGTH_LONG).show();
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume MainActivity");

        updateProfileView();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);

        TextView textViewNavHeaderName = (TextView) header.findViewById(R.id.navHeaderTextName);
        TextView textViewNavHeaderEmail = (TextView) header.findViewById(R.id.navHeaderTextEmail);

        textViewNavHeaderName.setText(name + " " + lastnames);
        textViewNavHeaderEmail.setText(email);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reserve_needs) {
            Intent intent = new Intent(MainActivity.this, OffersReservationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reservations) {
            Intent iMyReservations= new Intent(MainActivity.this, MyPromotionsActivity.class);
            startActivity(iMyReservations);
        } else if (id == R.id.nav_edit_profile) {
            Intent iMyProfile = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(iMyProfile);
        } else if (id == R.id.nav_contact_us) {
            Intent iContactUs = new Intent(MainActivity.this, ContactUsActivity.class);
            startActivity(iContactUs);
        } else if (id == R.id.nav_logout) {
            logout();
            Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(iLogin);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

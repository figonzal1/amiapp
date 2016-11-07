package techwork.ami.Need.OffersLocalDetails;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import techwork.ami.Config;
import techwork.ami.R;

public class StreetViewPanoramaFragment extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_panorama_activity);


        Bundle bundle = getIntent().getExtras();
        double lat = Double.parseDouble(bundle.getString(Config.TAG_GET_LOCAL_LAT));
        double lon = Double.parseDouble(bundle.getString(Config.TAG_GET_LOCAL_LONG));
        final LatLng location = new LatLng(lat,lon);



        final SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment)getSupportFragmentManager().findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

                if (savedInstanceState==null){
                    streetViewPanorama.setPosition(location);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


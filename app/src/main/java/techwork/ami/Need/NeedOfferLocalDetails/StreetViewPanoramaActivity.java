package techwork.ami.Need.NeedOfferLocalDetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import techwork.ami.Config;
import techwork.ami.R;

public class StreetViewPanoramaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_panorama_activity);

        Bundle bundle = getIntent().getExtras();
        double lat = Double.parseDouble(bundle.getString(Config.TAG_GL_LAT));
        double lon = Double.parseDouble(bundle.getString(Config.TAG_GL_LONG));
        final LatLng location = new LatLng(lat,lon);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment)getSupportFragmentManager().findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                if (savedInstanceState==null){
                    streetViewPanorama.setPosition(location);
                }
            }
        });
    }
}

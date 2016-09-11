package techwork.ami.Need;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import techwork.ami.R;


public class FragmentNeed extends Fragment {


    public FragmentNeed() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentNeed newInstance() {
        FragmentNeed fragment = new FragmentNeed();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Listo
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.need_fragment, container, false);
    }

}

package techwork.ami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@ link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMap extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String[] datos =
            new String[]{"Persona 1","Persona 2","Persona 3","Persona 4"};

    private ViewPager pager;

    public FragmentMap() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentMap newInstance() {
        FragmentMap fragment = new FragmentMap();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    //Listo
    //@Override
    //public void onActivityCreated(@Nullable Bundle savedInstanceState) {
       // this.pager = (ViewPager) pager.findViewById(R.id.main_viewpager);
       // super.onActivityCreated(savedInstanceState);
    //}
}

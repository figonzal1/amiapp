package techwork.ami.ReservationsOffers;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import techwork.ami.R;

/**
 * Created by Daniel on 21-09-2016.
 */

public class MyReservationOfferValidateDialog extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;

    public interface MyReservationOfferValidateListener {
        void onFinishMyReservationOfferValidateDialog(String inputText);
    }

    // Empty constructor required for DialogFragment
    public MyReservationOfferValidateDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_reservation_offer_validate,container);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().setTitle(getResources().getString(R.string.myReservationOfferDialog));

        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            MyReservationOfferValidateListener activity = (MyReservationOfferValidateListener) getActivity();
            activity.onFinishMyReservationOfferValidateDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
}

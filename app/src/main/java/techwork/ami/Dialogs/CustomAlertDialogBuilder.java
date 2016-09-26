package techwork.ami.Dialogs;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by Daniel on 23-09-2016.
 */

// Following to https://goo.gl/6AAnXP (oficial doc)
/*
        // Create a custom dialog to this context
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        // Create a EditText
        final EditText edittext = new EditText(context);
        // Type no visible password
        edittext.setInputType(Config.inputPasswordType);
        // Set message and title
        alert.setMessage(getResources().getString(R.string.my_reservations_offers_validate_message));
        alert.setTitle(getResources().getString(R.string.my_reservations_offers_validate_title));
        // Set into the view
        alert.setView(edittext);

        alert.setNegativeButton(getResources().getString(R.string.my_reservations_offers_validate_negativeText),
        new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {

        }
        });

        // If want to no close the dialog when click on the button, otherwise only define method like above
        // Define the behavior
        alert.setPositiveButton(getResources().getString(R.string.my_reservations_offers_validate_positiveText),null);
        final AlertDialog d = alert.create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
        });
        // Show the dialog (implicit create)
        //alert.show();*/

public class CustomAlertDialogBuilder extends AlertDialog.Builder {
    /**
     * Click listeners
     */
    private DialogInterface.OnClickListener mPositiveButtonListener = null;
    private DialogInterface.OnClickListener mNegativeButtonListener = null;
    private DialogInterface.OnClickListener mNeutralButtonListener = null;

    /**
     * Buttons text
     */
    private CharSequence mPositiveButtonText = null;
    private CharSequence mNegativeButtonText = null;
    private CharSequence mNeutralButtonText = null;

    private DialogInterface.OnDismissListener mOnDismissListener = null;

    private Boolean mCancelOnTouchOutside = null;

    public CustomAlertDialogBuilder(Context context) {
        super(context);
    }

    public CustomAlertDialogBuilder setOnDismissListener (DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
        return this;
    }

    @Override
    public CustomAlertDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mNegativeButtonListener = listener;
        mNegativeButtonText = text;
        return this;
    }

    @Override
    public CustomAlertDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mNeutralButtonListener = listener;
        mNeutralButtonText = text;
        return this;
    }

    // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
    @Override
    public CustomAlertDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mPositiveButtonListener = listener;
        mPositiveButtonText = text;
        return this;
    }

    // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
    @Override
    public CustomAlertDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        setNegativeButton(getContext().getString(textId), listener);
        return this;
    }

    @Override
    public CustomAlertDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
        setNeutralButton(getContext().getString(textId), listener);
        return this;
    }

    @Override
    public CustomAlertDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        setPositiveButton(getContext().getString(textId), listener);
        return this;
    }

    public CustomAlertDialogBuilder setCanceledOnTouchOutside (boolean cancelOnTouchOutside) {
        mCancelOnTouchOutside = cancelOnTouchOutside;
        return this;
    }

    @Override
    public AlertDialog create() {
        throw new UnsupportedOperationException("CustomAlertDialogBuilder.create(): use show() instead..");
    }

    @Override
    public AlertDialog show() {
        final AlertDialog alertDialog = super.create();

        DialogInterface.OnClickListener emptyOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        };

        // Enable buttons (needed for Android 1.6) - otherwise later getButton() returns null
        if (mPositiveButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mPositiveButtonText, emptyOnClickListener);
        }

        if (mNegativeButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mNegativeButtonText, emptyOnClickListener);
        }

        if (mNeutralButtonText != null) {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mNeutralButtonText, emptyOnClickListener);
        }

        // Set OnDismissListener if available
        if (mOnDismissListener != null) {
            alertDialog.setOnDismissListener(mOnDismissListener);
        }

        if (mCancelOnTouchOutside != null) {
            alertDialog.setCanceledOnTouchOutside(mCancelOnTouchOutside);
        }

        alertDialog.show();

        // Set the OnClickListener directly on the Button object, avoiding the auto-dismiss feature
        // IMPORTANT: this must be after alert.show(), otherwise the button doesn't exist..
        // If the listeners are null don't do anything so that they will still dismiss the dialog when clicked
        if (mPositiveButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mPositiveButtonListener.onClick(alertDialog, AlertDialog.BUTTON_POSITIVE);
                }
            });
        }

        if (mNegativeButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mNegativeButtonListener.onClick(alertDialog, AlertDialog.BUTTON_NEGATIVE);
                }
            });
        }

        if (mNeutralButtonListener != null) {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mNeutralButtonListener.onClick(alertDialog, AlertDialog.BUTTON_NEUTRAL);
                }
            });
        }

        return alertDialog;
    }
}

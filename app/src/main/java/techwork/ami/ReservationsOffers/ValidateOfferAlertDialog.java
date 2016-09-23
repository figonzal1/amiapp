package techwork.ami.ReservationsOffers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import techwork.ami.Config;
import techwork.ami.Interfaces.SimpleDialogPasswordEditText;

/**
 * Created by Daniel on 22-09-2016.
 */

public class ValidateOfferAlertDialog {
    private Context context;

    ValidateOfferAlertDialog (Context context){
        this.context = context;
    }

    void showAlert(String message, String title, String positiveText, String negativeText, final SimpleDialogPasswordEditText toDo) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        edittext.setInputType(Config.inputPasswordType);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setView(edittext);
        alert.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                String YouEditTextValue = edittext.getText().toString();
                toDo.doPositive();
            }
        });
        alert.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                toDo.doNegative();
            }
        });
        alert.show();
    }
}

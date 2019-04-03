package org.trac.app;

import android.os.Bundle;
import android.app.Dialog;
import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;

public class TracDialog extends DialogFragment {
    //private EditText mEditText;
    private static String tracname;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_trac, null);
        final EditText mEditText = (EditText) dialogView.findViewById(R.id.tracname);
        Log.v(Trac.TAG, mEditText.getHint().toString());
        builder.setView(dialogView);
        builder.setMessage(R.string.dialog_show)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //listener.onDialogPositiveClick(TracDialog.this);
                listener.onDialogPositiveClick(mEditText.getText().toString());
                Log.v(Trac.TAG,"TracDialog Ok Clicked");

            }
        })
       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //listener.onDialogNegativeClick(TracDialog.this);
                listener.onDialogNegativeClick(mEditText.getText().toString());
                Log.v(Trac.TAG,"TracDialog Cancel Clicked");
                if(dialog != null /*&& dialog.isShowing()*/) {
                    dialog.dismiss();
                }
            }
        });
        return builder.create();
    }
    public interface TracDialogListener {
        public void onDialogPositiveClick(String tracname);
        public void onDialogNegativeClick(String tracname);
        //public void onDialogPositiveClick(DialogFragment dialog);
        //public void onDialogNegativeClick(DialogFragment dialog);
    }

    TracDialogListener listener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (TracDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TracDialogListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mEditText = (EditText) view.findViewById(R.id.tracname);
    }

}

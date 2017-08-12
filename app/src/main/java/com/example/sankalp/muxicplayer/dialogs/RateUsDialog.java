package com.example.sankalp.muxicplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.R;

/**
 * Created by sankalp on 3/4/2017.
 */
public class RateUsDialog extends DialogFragment {
    RatingBar ratingBar;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rate_dialog,null,false);
        ratingBar= (RatingBar) view.findViewById(R.id.rating_bar);
//        final float rating=ratingBar.getRating();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(view);
        dialogBuilder
                .setCancelable(true)
                .setNegativeButton("SUBMIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
//                                Toast.makeText(getContext(),rating + " Star Thankyou!!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(),"Thankyou!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
        return dialogBuilder.create();
    }
}

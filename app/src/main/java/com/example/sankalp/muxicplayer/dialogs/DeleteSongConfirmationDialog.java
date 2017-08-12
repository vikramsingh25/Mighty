package com.example.sankalp.muxicplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.TracksListAdapter;
import com.example.sankalp.muxicplayer.fragments.TracksFragment;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;

/**
 * Created by sankalp on 2/28/2017.
 */
public class DeleteSongConfirmationDialog extends DialogFragment {

    TextView songName;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.dialog_confirmation,null,false);
        songName= (TextView) view.findViewById(R.id.confirm_name);
        Bundle bundle=getArguments();
        final String title=bundle.getString("name");
        final int pos=bundle.getInt("position",-1);
        final long id=bundle.getLong("id");
        String setTitle=title+R.string.extension;
        songName.setText(setTitle);
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setCancelable(true)
                .setTitle("Delete")
                .setMessage("Following song will be deleted.")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        int songsDeleted = new TracksFragment().deleteSong(title);
                        int songsDeleted = new MightySongProvider().deleteSong(getContext(),title,id);
                        if (songsDeleted > 0) {
//                            Log.d("TracksFragment",String.valueOf(pos));
                                            TracksFragment.adapter.removeSingleSong(pos);
                            Toast.makeText(getContext(), title + ".mp3 deleted successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "no song deleted", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return dialog.create();
    }
}

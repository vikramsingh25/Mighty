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

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;

/**
 * Created by sankalp on 5/17/2017.
 */
public class ClearCacheDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setCancelable(true)
                .setMessage("This will delete all temporary data including playlists")
                .setTitle("Clear Cache")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MightySongProvider().delete(MightyContract.SongEntry.CONTENT_URI,null,null);
                        new MightySongProvider().delete(MightyContract.PlaylistEntry.CONTENT_URI,null,null);
                        new MightySongProvider().delete(MightyContract.PlaylistSongEntry.CONTENT_URI,null,null);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
        return super.onCreateDialog(savedInstanceState);
    }
}

package com.example.sankalp.muxicplayer.dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.adapters.AddToPlaylistAdaper;
import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.loaders.PlaylistLoader;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;

import java.util.List;

/**
 * Created by sankalp on 2/23/2017.
 */
public class AddToPlaylistDialog extends DialogFragment {

    AddToPlaylistAdaper addToPlaylistAdaper;
    List<PlaylistInfo> list;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int songPosition = getArguments().getInt("songPosition");
        list=new PlaylistLoader(getContext()).loadInBackground();
//        Log.d("abcd",String.valueOf(list.size()));
        addToPlaylistAdaper=new AddToPlaylistAdaper(getContext(),list);
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setAdapter(addToPlaylistAdaper, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MightySongProvider().addSelectedSongToPlaylist(getContext(),songPosition,which);
            }
        });
        dialog.setCancelable(true)
                .setTitle("Select a Playlist")
                .setPositiveButton("Create new", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        CreatePlaylistDialog createPlaylist=new CreatePlaylistDialog();
                        Bundle bundle=new Bundle();
                        bundle.putInt("songPosition",songPosition);
                        createPlaylist.setArguments(bundle);
                        createPlaylist.show(ft,"CreatePlaylistDialog");

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return dialog.create();
    }

}

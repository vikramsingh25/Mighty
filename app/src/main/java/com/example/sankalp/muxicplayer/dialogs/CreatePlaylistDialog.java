package com.example.sankalp.muxicplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.mighty_async_tasks.PlaylistAsyncTasks;

/**
 * Created by sankalp on 3/4/2017.
 */
public class CreatePlaylistDialog extends DialogFragment {

    EditText playlistTitle,playlistDescription;
    TextView plalistError;
    View view;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle=getArguments();

        LayoutInflater layoutInflater= (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view=layoutInflater.inflate(R.layout.playlist_dialog,null,false);

        playlistTitle= (EditText) view.findViewById(R.id.play_list_title);
        playlistDescription= (EditText) view.findViewById(R.id.play_list_description);
        plalistError= (TextView) view.findViewById(R.id.playlist_error);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(view);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String[] playlistAttr={playlistTitle.getText().toString()
                                        ,playlistDescription.getText().toString()};

//                                        Log.d(LOG_TAG,playlistAttr[0] + "   :   " + playlistAttr[1]);

                                if (playlistAttr[0].length() == 0) {
                                    Toast.makeText(getContext(),"Playlist name couldn't be empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    new PlaylistAsyncTasks(getContext()).execute(playlistAttr);
                                    if (bundle != null) {
                                        int pos = bundle.getInt("songPosition");
//                                        String[] playlistSongAttr = {playlistTitle.getText().toString()
//                                                , playlistDescription.getText().toString(),String.valueOf(pos)};
//                                        new PlaylistAsyncTasks(getContext()).execute(playlistAttr);

                                        int playlistCount = new MightySongProvider().getPlaylistCount(getContext());
//                                        Log.d("add",String.valueOf(playlistCount)+":"+String.valueOf(pos));
                                        new MightySongProvider().addSelectedSongToPlaylist(getContext(), pos, playlistCount);
                                    }
//                                            onCreateView(inflater,container,savedInstanceState);
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        return alertDialogBuilder.create();
    }
}

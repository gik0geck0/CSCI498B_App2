package edu.mines.alterego;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.mines.alterego.CharacterDBHelper;

class NotesFragment extends Fragment {
    int mCharId;
    ArrayAdapter<NotesData> mNotesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: The default should be -1, so that errors are generated. BUT for testing, this was set to 0, for game 0
        mCharId = getArguments().getInt((String) getResources().getText(R.string.gameid), 0);

        if (mCharId == -1) {
            // Yes, this is annoying, but it'll make an error VERY obvious. In testing, I have never seen this toast/error message. But ya never know
            Toast.makeText(getActivity(), "GameID not valid", 400).show();
            Log.e("QuidditchScoring:ScoringScreen", "GAME ID IS NOT VALID!!!!!");
        }

        // Inflate the layout for this fragment
        View notes_view = inflater.inflate(R.layout.notes_view, container, false);
        ListView nView = (ListView) notes_view.findViewById(R.id.notes_list);

        CharacterDBHelper dbhelper = new CharacterDBHelper(getActivity());
        ArrayList<NotesData> nItems = dbhelper.getNotesData(mCharId);

        mNotesAdapter = new ArrayAdapter<NotesData>(getActivity(), android.R.layout.simple_list_item_1, nItems);
        nView.setAdapter(mNotesAdapter);


        return notes_view;
    }
    
}
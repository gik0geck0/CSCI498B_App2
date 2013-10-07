package edu.mines.alterego;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import edu.mines.alterego.CharacterDBHelper;
import edu.mines.alterego.GameData;
import edu.mines.alterego.GameActivity;

/**
 *  Alter Ego
 *  @author: Matt Buland, Maria Deslis, Eric Young
 *
 * ----------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Matt Buland, Maria Deslis, Eric Young
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ----------------------------------------------------------------------------
 *
 * @version: 0.1
 *
 * Release Notes:
 *
 * 0.1:
 *      The basic functionality is *basically* there. Games and characters can
 *      be created. The remaining components will follow the same flow: have a
 *      display; click button to add things to "display" (from database); use
 *      a dialog to get input-parameters.
 */
public class MainActivity extends Activity implements View.OnClickListener, ListView.OnItemClickListener {

    ArrayAdapter<GameData> mGameDbAdapter;
    CharacterDBHelper mDbHelper;
    Button newGameB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//ArrayList<String> gameList = new ArrayList<String>();
		mDbHelper = new CharacterDBHelper(this);
		ArrayList<GameData> gamePairList = mDbHelper.getGames();
        /*
		for( Pair<Integer, String> game : gamePairList) {
			gameList.add(game.second);
		}
        */

		mGameDbAdapter = new ArrayAdapter<GameData>( this, android.R.layout.simple_list_item_1, gamePairList);
		ListView gameListView = (ListView) findViewById(R.id.main_game_list_view);
		gameListView.setAdapter(mGameDbAdapter);
        gameListView.setOnItemClickListener(this);
        
        // Create New Game Button
        newGameB = (Button) findViewById(R.id.main_new_game);
        
        // Set On Click Listener for Create New Game Button
        newGameB.setOnClickListener(this);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Main Menu Items
		switch (item.getItemId()) {
			case R.id.action_new_game:
				newGameDialogue();
                break;
			case R.id.action_settings:
				startActivity(new Intent(this, Settings.class));
                break;
			default:
				return super.onOptionsItemSelected(item);
		}
        return true;
	}

    @Override
    public void onClick(View v) {
    	if (v == newGameB) {
    		newGameDialogue();
    	}
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameData selectedGame = mGameDbAdapter.getItem(position);

        Log.i("AlterEgos::MainAct::SelectGame", "The game with an id " + selectedGame.first + " and a name of " + selectedGame.second + " was selected.");

        Intent launchGame = new Intent(view.getContext(), GameActivity.class);
        launchGame.putExtra((String) getResources().getText(R.string.gameid), selectedGame.first);

        MainActivity.this.startActivity(launchGame);
    }
    
    
    // Opens up dialogue for user to input new game 
    public void newGameDialogue() {
        AlertDialog.Builder newGameDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
 
        // Inflate the view
        newGameDialog.setView(inflater.inflate(R.layout.new_game_dialog, null))
            .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Perceive this dialog as an AlertDialog
                    AlertDialog thisDialog = (AlertDialog) dialog;

                    EditText nameInput = (EditText) thisDialog.findViewById(R.id.game_name);
                    String gameName = nameInput.getText().toString();
                    // Create a new game
                    Log.i("AlterEgos::MainAct::NewGame", "Creating a game with the name " + gameName);
                    //CharacterDBHelper mDbHelper = new CharacterDBHelper(this);
                    GameData newGame = mDbHelper.addGame(gameName);
                    mGameDbAdapter.add(newGame);
                    hideCreateNewGameButton();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Cancel: Just close the dialog
                    dialog.dismiss();
                }
            });

        newGameDialog.create().show();
    }
    
    public void hideCreateNewGameButton() {
    		newGameB.setVisibility(View.GONE);
    }
}
package edu.mines.alterego;

import android.content.Context;
import android.content.ContentValues;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import java.util.Date;
import java.util.ArrayList;

/**
 *  <h1>SQLite Database Adapter (helper as Google/Android calls it)</h1>
 *
 *  Offers many static functions that can be used to update or view game-statistics in the database
 *  The API follows the general rule of first aquiring the database via 'getWritable/ReadableDatabase',
 *  then using the static functions defined in this class to interact with the database.
 *
 *  @author: Matt Buland
 */
public class CharacterDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "alterego";
    private static final int DB_VERSION = 1;

    public CharacterDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * For an SQLiteOpenHelper, the onCreate method is called if and only if
     * the database-name in question does not already exist. Theoretically,
     * this should only happen once ever, and after the one time, updates
     * will be applied for schema updates.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {

        /*
         * Game table: Base unifying game_id construct
         * The game is used to reference 
         */
        database.execSQL("CREATE TABLE IF NOT EXISTS game ( " +
                "game_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS character ( character_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, game_id INTEGER ,FOREIGN KEY(game_id) REFERENCES game(game_id) )");

        database.execSQL("CREATE TABLE IF NOT EXISTS inventory_item ( "+
                "inventory_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "character_id INTEGER," +
                "FOREIGN KEY(character_id) REFERENCES character(character_id)" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS character_stat ( " +
                "character_stat_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "character_id INTEGER," +
                "stat_value INTEGER," +
                "stat_name INTEGER," +
                "description_usage_etc INTEGER," +
                "category_id INTEGER," +
                "FOREIGN KEY(character_id) REFERENCES character(character_id)" +
                "FOREIGN KEY(category_id) REFERENCES category(category_id)" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS item_stat ( " +
                "item_stat_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "inventory_item_id INTEGER," +
                "stat_value INTEGER," +
                "stat_name INTEGER," +
                "description_usage_etc INTEGER," +
                "category_id INTEGER," +
                "FOREIGN KEY(category_id) REFERENCES category(category_id)" +
                "FOREIGN KEY(inventory_item_id) REFERENCES inventory_item(inventory_item_id)" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS category ( " +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS note ( " +
                "note_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "game_id INTEGER," +
                "note TEXT," +
                "FOREIGN KEY(game_id) REFERENCES game(game_id)" +
                ")");
        /* Example DDL from Matt's Quidditch scoring app
        database.execSQL("CREATE TABLE IF NOT EXISTS score ( " +
                "score_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "score_datetime INTEGER, " +
                "team_id INTEGER, " +   // team_id is a number identifying the team. In this first revision, it will be 0 or 1 for left and right
                "amount INTEGER, " +
                "snitch INTEGER, " +
                "game_id INTEGER, " +
                "FOREIGN KEY(game_id) REFERENCES game(game_id) )");
         */
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Do nothing.
    }

    public ArrayList< Pair <Integer, String> > getGames() {
        Cursor dbGames = getWritableDatabase().rawQuery("SELECT * from game", null);
        dbGames.moveToFirst();
        ArrayList<Pair<Integer, String>> games = new ArrayList<Pair<Integer, String>>();
        while( !dbGames.isAfterLast()) {
            games.add(new Pair<Integer, String> (dbGames.getInt(0), dbGames.getString(1) ) );
            dbGames.moveToNext();
        }
        dbGames.close();
        return games;
    }

    public Pair<Integer, String> addGame(String name) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues gamevals = new ContentValues();
        gamevals.put("name", name);

        long rowid = database.insert("game", null, gamevals);
        String[] args = new String[]{ ""+rowid };

        Cursor c = database.rawQuery("SELECT * FROM game WHERE game.ROWID =?", args);
        c.moveToFirst();

        return new Pair<Integer, String>(c.getInt(c.getColumnIndex("game_id")), c.getString(c.getColumnIndex("name")));
    }

    public ArrayList<String> getCharacters(int gameID) {
        ArrayList<String> characters = new ArrayList<String>();
        return characters;
    }

    public ArrayList<InventoryItem> getInventoryItems(int characterId) {
        Cursor invCursor = getReadableDatabase().rawQuery(
                "SELECT "+
                    "character_id," +
                    "inventory_item_id," +
                    "inventory_item.name AS 'item_name'," +
                    "inventory_item.description AS 'item_description'" +
                "FROM character " +
                    "INNER JOIN inventory_item ON inventory_item.character_id = character.character_id " +
                "WHERE character.character_id = ?",
                new String[]{""+characterId});
        ArrayList<InventoryItem> invList = new ArrayList<InventoryItem>();
        invCursor.moveToFirst();

        int iidCol = invCursor.getColumnIndex("inventory_item_id");
        int iNameCol = invCursor.getColumnIndex("item_name");
        int iDescCol = invCursor.getColumnIndex("item_description");
        while (!invCursor.isAfterLast()) {
            invList.add(new InventoryItem(
                        invCursor.getInt(iidCol),
                        invCursor.getString(iNameCol),
                        invCursor.getString(iDescCol)));
            invCursor.moveToNext();
        }
        return invList;
    }
}

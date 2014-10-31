package com.minicat.germanwhist;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class MainActivity extends ActionBarActivity {

    /* preferences */
    public static final String PREFS_NAME = "GermanWhistPrefs";
    public static final String PREFS_GAMES = "games";
    public static final String PREFS_WINS = "wins";


    String TAG = "MainActivity";

    HandView mHandView;

    GameState mGameState;

    WhistBot mWhistBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Retrieve win/games count etc*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // If no games yet - set it up
        if (!settings.contains(PREFS_GAMES)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(PREFS_GAMES, 0).putInt(PREFS_WINS, 0).commit();
            Log.e(TAG, PREFS_NAME + " JUST INITIALISED: " + settings.getInt(PREFS_WINS, -1) + " " + settings.getInt(PREFS_GAMES, -1));
        } else {
            Log.e(TAG, PREFS_NAME + " JUST RELOADED: " + settings.getInt(PREFS_WINS, -1) + " " + settings.getInt(PREFS_GAMES, -1));
        }

        setContentView(R.layout.activity_main);

        // Make game state / attempt to load old
        if (savedInstanceState != null) {
            // TODO these should probably be constants, sigh
            // Get the card piles.
            Hand playerHand = new Hand(savedInstanceState.getString("playerHand"));
            Hand botHand = new Hand(savedInstanceState.getString("botHand"));
            LinkedList<Card> shownPile = Card.listFromString(savedInstanceState.getString("shownPile"));
            LinkedList<Card> hiddenPile = Card.listFromString(savedInstanceState.getString("hiddenPile"));
            mGameState = new GameState(playerHand, botHand, shownPile, hiddenPile, true);
            // Set round and tricks
            mGameState.mRound = savedInstanceState.getInt("round");
            mGameState.mPlayerTricks = savedInstanceState.getInt("playerTricks");
            mGameState.mPreviousTrick = new GameState.Trick(savedInstanceState.getString("previousTrick"));
        } else {
            mGameState = new GameState(true);
        }

        mWhistBot = new CatBot(mGameState);

        mHandView = new HandView(mGameState, this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.everything);
        layout.addView(mHandView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //mHandView = (HandView) findViewById(R.id.hand_view);

        // Set up listener
        mHandView.setEventListener(new HandView.HandListener() {
            @Override
            public void onCardPlayed(Card card) {
                // delegate.
                playCard(card);
            }
        });

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        } */
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save cards
        savedInstanceState.putString("playerHand", mGameState.mPlayerHand.toString());
        savedInstanceState.putString("botHand", mGameState.mBotHand.toString());
        savedInstanceState.putString("shownPile", Card.listToString(mGameState.mShownPile));
        savedInstanceState.putString("hiddenPile", Card.listToString(mGameState.mHiddenPile));
        // Save round and tricks
        savedInstanceState.putInt("round", mGameState.mRound);
        savedInstanceState.putInt("playerTricks", mGameState.mPlayerTricks);
        savedInstanceState.putString("previousTrick", mGameState.mPreviousTrick.toString());
    }

    /**
     * Called when a card is played by the player.
     *
     * @param card card that has been played
     */
    void playCard(Card card) {
        Log.e(TAG, card.toString());
        // TODO Refactor into gameState.playCard or something
        // Is this an invalid play?

        if (!mGameState.mPlayerTurn && !mGameState.isValidPlay(card)) {
            Log.e(TAG, "Invalid play");
            // TODO: some kind of indicator
        } else {
            // Play the card
            if (mGameState.mPlayerTurn) {
                mGameState.playFirst(card);
                // Have bot play their card
                mGameState.playSecond(mWhistBot.playSecond());
            } else mGameState.playSecond(card);

            // Log the bot hand
            String s = "";
            for (Card c : mGameState.mBotHand.mergeCards()) {
                s = s + c.toString() + " ";
            }
            Log.e("BOT HAND: ", s);

            // Resolve the round
            mGameState.resolveRound();

            // Have bot play their card, if its their turn next
            if (!mGameState.mPlayerTurn && mGameState.mBotHand.size() != 0)
                mGameState.playFirst(mWhistBot.playFirst());
        }


        // If game won, have the hand view announce the winner!
        if (mGameState.mRound > 26) {
            boolean playerWonGame = mGameState.mPlayerTricks > 6;
            mHandView.gameWon(true, playerWonGame);

            // Update count of games
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            int games = settings.getInt(PREFS_GAMES, 0) + 1;
            int wins = settings.getInt(PREFS_WINS, 0);
            // increment wins if necessary
            if (playerWonGame) wins++;
            editor.remove(PREFS_GAMES).remove(PREFS_WINS).commit();
            editor.putInt(PREFS_GAMES, games).putInt(PREFS_WINS, wins).commit();

            Log.e(TAG, PREFS_NAME + " JUST UPDATED ON GAME OVER: " + settings.getInt(PREFS_WINS, -1) + " " + settings.getInt(PREFS_GAMES, -1));
        }

        // Redraw
        mHandView.invalidate();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.show_wins) {
            // TODO: Make this not a toast, with pretty interface and option to clear history, basically just better UI
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            int wins = settings.getInt(PREFS_WINS, -1);
            int games = settings.getInt(PREFS_GAMES, -1);
            double percentage = wins / (double) games * 100;
            DecimalFormat df = new DecimalFormat("#.##");
            Toast toast = Toast.makeText(this, "Wins: " + wins + ", Losses: " + (games - wins) + ", Total: " + games +
                    "\nWin Percentage: " + df.format(percentage) + "%", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        }
        if (id == R.id.action_settings) {
            // TODO: Change card order, for one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}

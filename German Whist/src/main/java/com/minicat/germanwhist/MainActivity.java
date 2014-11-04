package com.minicat.germanwhist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class MainActivity extends ActionBarActivity {

    String TAG = "MainActivity";

    HandView mHandView;

    GameState mGameState;

    WhistBot mWhistBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Retrieve win/games count etc*/
        SharedPreferences settings = getSharedPreferences(StatsHelper.PREFS_NAME, 0);
        // If no games yet - set it up
        if (!settings.contains(StatsHelper.PREFS_GAMES)) {
            StatsHelper.resetStats(settings);
        }

        setContentView(R.layout.activity_main);

        // Make game state / attempt to load old
        mGameState = new GameState(true);
        if (savedInstanceState != null) {
            // check.
            if (savedInstanceState.containsKey("playerHand")) {
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
                // If its right at the start, it might not have a previous trick.
                if (savedInstanceState.containsKey("previousTrick")) {
                    mGameState.mPreviousTrick = new GameState.Trick(savedInstanceState.getString("previousTrick"));
                }
                // Load turn, what computer played
                mGameState.mPlayerTurn = savedInstanceState.getBoolean("playerTurn");
                if (!mGameState.mPlayerTurn) {
                    mGameState.mFirstPlayed = new Card(savedInstanceState.getString("firstPlayed"));
                }
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        // set card order
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String suitOrder = defaultPrefs.getString(getString(R.string.pref_order),
                getString(R.string.default_suit_order));

        mHandView.mSuitOrder = suitOrder;
        mHandView.invalidate();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Only bother saving if game not over
        if (mGameState.mRound <= 26) {
            // Save cards
            savedInstanceState.putString("playerHand", mGameState.mPlayerHand.toString());
            savedInstanceState.putString("botHand", mGameState.mBotHand.toString());
            savedInstanceState.putString("shownPile", Card.listToString(mGameState.mShownPile));
            savedInstanceState.putString("hiddenPile", Card.listToString(mGameState.mHiddenPile));
            // Save round and tricks
            savedInstanceState.putInt("round", mGameState.mRound);
            savedInstanceState.putInt("playerTricks", mGameState.mPlayerTricks);
            if (mGameState.mPreviousTrick != null) {
                savedInstanceState.putString("previousTrick", mGameState.mPreviousTrick.toString());
            }
            // If it isnt the players turn, save what card the bot played
            savedInstanceState.putBoolean("playerTurn", mGameState.mPlayerTurn);
            if (!mGameState.mPlayerTurn) {
                savedInstanceState.putString("firstPlayed", mGameState.mFirstPlayed.toString());
            }
        }
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
            SharedPreferences settings = getSharedPreferences(StatsHelper.PREFS_NAME, 0);
            // increment wins if necessary
            if (playerWonGame) StatsHelper.incrementCount(settings, StatsHelper.PREFS_WINS);
            // increment games
            StatsHelper.incrementCount(settings, StatsHelper.PREFS_GAMES);
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
        if (id == R.id.menu_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_show_wins) {
            showStats();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_new_game) {
            // Delegate.
            newGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStats() {
        SharedPreferences settings = getSharedPreferences(StatsHelper.PREFS_NAME, 0);
        int wins = settings.getInt(StatsHelper.PREFS_WINS, -1);
        int games = settings.getInt(StatsHelper.PREFS_GAMES, -1);
        int forfeit = settings.getInt(StatsHelper.PREFS_FORFEIT, -1);
        double percentage = wins / (double) games * 100;
        if (games == 0) percentage = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        String text = "You have " + wins + " wins and " + (games - wins) + " losses." +
                "\nWin Percentage: " + df.format(percentage) +
                "%\nGames Completed: " + games + "\nGames Forfeited: " + forfeit;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text).setTitle(R.string.wins_losses);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
                // TODO: can you just remove the onclicks?
            }
        });
        builder.show();
    }

    private void newGame() {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? \nConsidered a forfeit if past round 13.").setTitle("New Game");
        // Add the buttons
        String positiveText = "Abandon";
        if (mGameState.mRound > 13) positiveText = "Forfeit";
        builder.setPositiveButton("Ok - " + positiveText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // stats if in 2nd phase
                if (mGameState.mRound > 13) {
                    SharedPreferences settings = getSharedPreferences(StatsHelper.PREFS_NAME, 0);
                    StatsHelper.incrementCount(settings, StatsHelper.PREFS_FORFEIT);
                }
                // TODO Closing the app and reopening restarts without forfeit.

                // new game state
                mGameState = new GameState(true);
                mHandView.mGameState = mGameState;
                mHandView.invalidate();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

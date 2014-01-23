package com.minicat.germanwhist;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends ActionBarActivity {

    static Hand mPlayerHand;
    static Hand mCompHand;

    // apparently this is a queue
    static LinkedList<Card> mShownPile;
    static LinkedList<Card> mHiddenPile;

    static int mRound = 1;
    static int mPlayerTricks = 0;

    String TAG = "MainActivity";

    HandView mHandView;

    GameState mGameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandView = (HandView) findViewById(R.id.hand_view);

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

        ArrayList<Card> playerCards = new ArrayList<Card>();
        ArrayList<Card> compCards = new ArrayList<Card>();
        mShownPile = new LinkedList<Card>();
        mHiddenPile = new LinkedList<Card>();

        Deck deck = new Deck();
        deck.shuffle();
        deck.deal(playerCards, compCards, mShownPile, mHiddenPile);

        mPlayerHand = new Hand(playerCards);
        mCompHand = new Hand(compCards);

        // Make game state
        mGameState = new GameState(mShownPile.getFirst().mSuit, true, mPlayerHand, mCompHand);

        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        } */
    }

    /**
     * Called when a card is played by the player.
     *
     * @param card card that has been played
     */
    void playCard(Card card) {
        Log.e(TAG, card.toString());

        // Remove it from the player's hand
        mPlayerHand.remove(card);


        // Redraw
        mHandView.invalidate();

        mRound++;

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
        if (id == R.id.action_settings) {
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

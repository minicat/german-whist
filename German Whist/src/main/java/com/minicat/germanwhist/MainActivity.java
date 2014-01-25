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
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends ActionBarActivity {

    String TAG = "MainActivity";

    HandView mHandView;

    GameState mGameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Card> playerCards = new ArrayList<Card>();
        ArrayList<Card> compCards = new ArrayList<Card>();
        LinkedList<Card> shownPile = new LinkedList<Card>();
        LinkedList<Card> hiddenPile = new LinkedList<Card>();

        Deck deck = new Deck();
        deck.shuffle();
        deck.deal(playerCards, compCards, shownPile, hiddenPile);

        Hand playerHand = new Hand(playerCards);
        Hand compHand = new Hand(compCards);

        // Make game state
        mGameState = new GameState(playerHand, compHand, shownPile, hiddenPile, true);


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

    /**
     * Called when a card is played by the player.
     *
     * @param card card that has been played
     */
    void playCard(Card card) {
        Log.e(TAG, card.toString());

        // Is this an invalid play?

        if (!mGameState.mPlayerTurn && !mGameState.isValidPlay(card)) {
            Log.e(TAG, "Invalid play");
            // TODO: some kind of indicator
        } else {
            // Play the card
            if (mGameState.mPlayerTurn) {
                mGameState.playFirst(card);
                // Have computer play their card
                // TODO: This doesnt follow the rules atm. Fix!
                mGameState.playSecond(mGameState.mCompHand.mergeCards().get(0));
            } else mGameState.playSecond(card);

            // Resolve the round
            mGameState.resolveRound();

            // Have computer play their card, if its their turn next
            if (!mGameState.mPlayerTurn && mGameState.mCompHand.size() != 0)
                mGameState.playFirst(mGameState.mCompHand.mergeCards().get(0));
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

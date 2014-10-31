package com.minicat.germanwhist;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Describes state of the game.
 * Created by Emma on 23/01/14.
 */
public class GameState {

    // whether it's the players turn to play first or not
    boolean mPlayerTurn;

    final Card.Suit mTrumps;

    Hand mPlayerHand;
    Hand mBotHand;

    Queue mShownPile;
    Queue mHiddenPile;

    Card mFirstPlayed;
    Card mSecondPlayed;

    Trick mPreviousTrick;

    int mRound = 1;
    int mPlayerTricks = 0;

    String TAG = "GameState";

    /**
     * This constructor is for a blank gamestate/new game.
     *
     * @param playerTurnFirst whose turn is it first?
     */
    GameState(boolean playerTurnFirst) {

        ArrayList<Card> playerCards = new ArrayList<Card>();
        ArrayList<Card> botCards = new ArrayList<Card>();
        mShownPile = new LinkedList<Card>();
        mHiddenPile = new LinkedList<Card>();

        Deck deck = new Deck();
        deck.shuffle();
        deck.deal(playerCards, botCards, mShownPile, mHiddenPile);

        mPlayerHand = new Hand(playerCards);
        mBotHand = new Hand(botCards);
        mTrumps = ((Card) mShownPile.peek()).mSuit; // Requires cast.
        mPlayerTurn = playerTurnFirst;
    }

    GameState(Hand playerHand, Hand botHand, Queue<Card> shownPile, Queue<Card> hiddenPile,
              boolean playerTurnFirst) {
        mPlayerHand = playerHand;
        mBotHand = botHand;
        mShownPile = shownPile;
        mHiddenPile = hiddenPile;
        mTrumps = shownPile.peek().mSuit;
        mPlayerTurn = playerTurnFirst;
    }

    void playFirst(Card card) {
        mFirstPlayed = card;
        Log.e(TAG, "playFirst " + mPlayerTurn + " " + card.toString());
    }

    void playSecond(Card card) {
        mSecondPlayed = card;
        Log.e(TAG, "playSecond " + mPlayerTurn + " " + card.toString());
    }

    /**
     * For second plays, checks if it is a valid play
     *
     * @param card card they want to play
     * @return whether its valid or not
     */
    boolean isValidPlay(Card card) {
        // Valid if same suit...
        if (card.mSuit == mFirstPlayed.mSuit) return true;
        // Or if void in that suit
        // TODO: this assumes its just checking the player hahahaha fix this later
        if (mPlayerHand.isVoid(mFirstPlayed.mSuit)) return true;
        return false;
    }

    // Determines winner appropriately, and deals adding/removing cards
    // TODO: Might want to deal adding/removing cards elsewhere
    void resolveRound() {
        // Remove cards from hands
        if (mPlayerTurn) {
            mPlayerHand.remove(mFirstPlayed);
            mBotHand.remove(mSecondPlayed);
        } else {
            mBotHand.remove(mFirstPlayed);
            mPlayerHand.remove(mSecondPlayed);
        }

        boolean firstWon;
        // If cards are the same suit, higher card wins.
        if (mFirstPlayed.mSuit == mSecondPlayed.mSuit) {
            if (mFirstPlayed.mRank.getVal() > mSecondPlayed.mRank.getVal()) {
                firstWon = true;
            } else {
                firstWon = false;
            }
        } else {
            // Otherwise, unless second player played a trump card, first player wins
            if (mSecondPlayed.mSuit == mTrumps) {
                firstWon = false;
            } else {
                firstWon = true;
            }
        }


        boolean playerWon = ((mPlayerTurn && firstWon) || (!mPlayerTurn && !firstWon));

        // make last trick
        mPreviousTrick = new Trick(mFirstPlayed, mSecondPlayed, mPlayerTurn, playerWon);


        // Deal if required
        if (!mShownPile.isEmpty()) {
            if (playerWon) {
                // Player gets shown card
                Log.e(TAG, "Player gets: " + (mShownPile.peek()).toString());
                Log.e(TAG, "Bot gets: " + (mHiddenPile.peek()).toString());
                mPreviousTrick.setPlayerDrew((Card) mShownPile.peek());
                mPlayerHand.add((Card) mShownPile.poll());
                mBotHand.add((Card) mHiddenPile.poll());

            } else {
                // Player gets hidden card
                Log.e(TAG, "Player gets: " + (mHiddenPile.peek()).toString());
                Log.e(TAG, "Bot gets: " + (mShownPile.peek()).toString());
                mPreviousTrick.setPlayerDrew((Card) mHiddenPile.peek());
                mPlayerHand.add((Card) mHiddenPile.poll());
                mBotHand.add((Card) mShownPile.poll());
            }
        } else {
            // Increment score if this is a counted trick
            if (playerWon) {
                mPlayerTricks++;
            }
        }

        // Fix turns
        if (playerWon) mPlayerTurn = true;
        else mPlayerTurn = false;

        // Fix played
        mFirstPlayed = null;
        mSecondPlayed = null;

        // increment round
        mRound++;

    }

    public static class Trick {
        Card first;
        Card second;
        boolean playerFirst;
        boolean playerWon;
        Card playerDrew;

        /**
         * Opposite of toString.
         *
         * @param saved
         */
        Trick(String saved) {
            // todo needs tests for this and toString
            String[] parts = saved.split(" ");
            this.first = new Card(parts[0]);
            this.second = new Card(parts[1]);
            this.playerFirst = Boolean.parseBoolean(parts[2]);
            this.playerWon = Boolean.parseBoolean(parts[3]);
            this.playerDrew = new Card(parts[4]);
        }

        Trick(Card first, Card second, boolean playerFirst, boolean playerWon) {
            this.first = first;
            this.second = second;
            this.playerFirst = playerFirst;
            this.playerWon = playerWon;
        }

        public void setPlayerDrew(Card playerDrew) {
            this.playerDrew = playerDrew;
        }

        @Override
        public String toString() {
            String str = "";
            String space = " ";
            // TODO make this less terrible
            str += first.toString() + space;
            str += second.toString() + space;
            str += playerFirst + space;
            str += playerWon + space;
            str += playerDrew.toString();
            return str;
        }

    }
}

package com.minicat.germanwhist;

import java.util.Queue;

/**
 * Describes state of the game.
 * Created by Emma on 23/01/14.
 */
public class GameState {

    // whether it's the players turn or not
    boolean mPlayerTurn;

    final Card.Suit mTrumps;

    Hand mPlayerHand;
    Hand mCompHand;

    Queue mShownPile;
    Queue mHiddenPile;

    Card mFirstPlayed;
    Card mSecondPlayed;

    static int mRound = 1;
    static int mPlayerTricks = 0;

    GameState(Hand playerHand, Hand compHand, Queue<Card> shownPile, Queue<Card> hiddenPile,
              boolean playerTurnFirst) {
        mPlayerHand = playerHand;
        mCompHand = compHand;
        mShownPile = shownPile;
        mHiddenPile = hiddenPile;
        mTrumps = shownPile.peek().mSuit;
        mPlayerTurn = playerTurnFirst;
    }

    void playFirst(Card card) {
        mFirstPlayed = card;
    }

    void playSecond(Card card) {
        mSecondPlayed = card;
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
        if (mPlayerHand.isVoid(mTrumps)) return true;
        return false;
    }

    // Determines winner appropriately, and deals adding/removing cards
    // TODO: Might want to deal adding/removing cards elsewhere
    void resolveRound() {
        // Remove cards from hands
        if (mPlayerTurn) {
            mPlayerHand.remove(mFirstPlayed);
            mCompHand.remove(mSecondPlayed);
        } else {
            mCompHand.remove(mFirstPlayed);
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

        // Deal appropriately
        if ((mPlayerTurn && firstWon) || (!mPlayerTurn && !firstWon)) {
            // Player gets shown card
            mPlayerHand.add((Card) mShownPile.poll());
            mCompHand.add((Card) mHiddenPile.poll());
        } else {
            // Player gets hidden card
            mPlayerHand.add((Card) mHiddenPile.poll());
            mCompHand.add((Card) mShownPile.poll());
        }


        // increment whose turn it is
        mPlayerTurn = !mPlayerTurn;

        // increment round
        mRound++;
    }
}

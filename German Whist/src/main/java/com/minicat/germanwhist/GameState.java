package com.minicat.germanwhist;

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

    Card mFirstPlayed;

    GameState(Card.Suit trumps, boolean playerTurnFirst, Hand playerHand, Hand compHand) {
        mTrumps = trumps;
        mPlayerTurn = playerTurnFirst;
        mPlayerHand = playerHand;
        mCompHand = compHand;
    }

    void playFirst(Card card) {
        mFirstPlayed = card;
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
        // Or if trumps
        if (card.mSuit == mTrumps) return true;
        return false;
    }

    void incrementTurn() {
        mPlayerTurn = !mPlayerTurn;
    }

}

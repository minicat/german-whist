package com.minicat.germanwhist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Emma's german whist player! Meow.
 * Created by Emma on 26/01/14.
 */
public class CatBot extends WhistBot {

    // For second stage: suit currently playing top down from
    Card.Suit mCurr;

    CatBot(GameState gameState) {
        super(gameState);
    }

    public Card playFirst() {
        // If first stage, check card on top of shown pile:
        if (g.mRound < 14) {
            if (wantToWin((Card) g.mShownPile.peek())) {
                // Play highest non trump
                return playHighestNotTrumps();
            } else {
                // Play lowest non trump
                return playLowestNotTrumps();
            }
        } else {
            // Second stage
            // Play highest from longest non trumps suit top down.
            if (mCurr == null) mCurr = getLongestSuit();
            ArrayList<Card> ofSuit = g.mBotHand.mCards.get(mCurr);
            // Need to move on to the next suit
            if (ofSuit.size() == 0) mCurr = getLongestSuit();
            return ofSuit.get(ofSuit.size() - 1);
        }
    }

    // Prioritize non trumps
    private Card.Suit getLongestSuit() {
        // TODO: well this is kind of ugly
        int size = 0;
        Card.Suit curr = g.mTrumps;
        for (Map.Entry<Card.Suit, ArrayList<Card>> entry : g.mBotHand.mCards.entrySet()) {
            if (entry.getValue().size() > size && entry.getKey() != g.mTrumps) {
                size = entry.getValue().size();
                curr = entry.getKey();
            }
        }
        return curr;
    }


    public Card playSecond() {

        Card fP = g.mFirstPlayed;

        // If first stage, check card on top of shown pile:
        if (g.mRound < 14) {
            if (wantToWin((Card) g.mShownPile.peek())) {
                // if void, play lowest other card
                if (g.mBotHand.isVoid(fP.mSuit)) {
                    return playLowestNotTrumps();
                } else {
                    // Otherwise, want to win it.
                    return wantToWinFollowSuit(fP);
                }
            } else {
                return playLowestNotTrumpsFollowSuit(fP.mSuit);
            }
        } else {
            // Second round
            // If not void
            if (!g.mBotHand.isVoid(fP.mSuit)) {
                // You're not void
                return wantToWinFollowSuit(fP);
            } else {
                // You're void
                // Play lowest trumps, if can
                if (!g.mBotHand.isVoid(g.mTrumps)) {
                    return g.mBotHand.mCards.get(g.mTrumps).get(0);
                }
                // Otherwise just play lowest
                return playLowestNotTrumps();
            }

        }
    }

    /**
     * Called when playing second, and we want to win (and have to try to follow suit.)
     *
     * @param fP card played first
     * @return card to play
     */
    private Card wantToWinFollowSuit(Card fP) {
        ArrayList<Card> suitCards = g.mBotHand.mCards.get(fP.mSuit);
        // If we have a card that can win it, play it (minimise)
        int val = fP.mRank.getVal();
        if (suitCards.get(suitCards.size() - 1).mRank.getVal() > fP.mRank.getVal()) {
            // Iterate through to find smallest card that wins
            // TODO: It should be sorted, so this should work right?
            for (Card c : suitCards) {
                if (c.mRank.getVal() > val) return c;
            }
        }
        // Can't win, so play lowest
        return playLowestNotTrumpsFollowSuit(fP.mSuit);
    }

    /**
     * Determines whether the bot wants to win the card or not.
     *
     * @param card Card on top of the shown pile
     * @return wants to win or not
     */
    boolean wantToWin(Card card) {
        // Simple logic: want to win if trumps or AKQ
        return (card.mSuit == g.mTrumps || card.mRank.getVal() > Card.Rank.JACK.getVal());
    }

    /**
     * Finds the lowest non trumps card to play.
     * if tie, returns from longest suit.
     * Does play trumps if no other choice :(
     *
     * @return Lowest non trumps card
     */
    Card playLowestNotTrumps() {
        ArrayList<Card> cards = g.mBotHand.mergeCards();
        Collections.sort(cards, new lowestComparator());
        return cards.get(0);
        // TODO To Write
    }


    /**
     * Finds the lowest non trumps card to play, attempting to follow suit.
     * if tie, returns from longest suit.
     * Does play trumps if no other choice :(
     *
     * @return Lowest non trumps card
     */
    Card playLowestNotTrumpsFollowSuit(Card.Suit suit) {
        ArrayList<Card> cards = g.mBotHand.mergeCards();
        Collections.sort(cards, new lowestComparator());
        return cards.get(0);
        // TODO To Write
    }

    Card playHighestNotTrumps() {
        return
        // TODO to Write
    }
}

/**
 * Helps get the lowest non trumps card.
 */
class lowestComparator implements Comparator<Card> {
    @Override
    public int compare(Card a, Card b) {
        // Return negative if a < b, positive if a > b, etc
        // First, sort on suit. Trumps are highest.
        if
        return a.mRank.getVal() - b.mRank.getVal();

        // TODO to write
    }
}

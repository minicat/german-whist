package com.minicat.germanwhist;

import android.util.Log;

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
            Card top = (Card) g.mShownPile.peek();
            if (wantToWin(top)) {
                // Play highest non trump, lower than card
                return playHighestNotTrumps(top);
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
            if (ofSuit.size() == 0) {
                mCurr = getLongestSuit();
                ofSuit = g.mBotHand.mCards.get(mCurr);
            }
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
     * TODO: if tie, returns from longest suit.
     * Does play trumps if no other choice :(
     *
     * @return Lowest non trumps card
     */
    Card playLowestNotTrumps() {
        ArrayList<Card> cards = g.mBotHand.mergeCards();
        Collections.sort(cards, new LowestComparator(g));

        String temp = "";
        for (Card c : cards) {
            temp = temp + c.toString() + " ";
        }
        Log.e("testing lowestnottrumps", temp);
        Log.e("testing lowestnottrumps", "trumps are " + g.mTrumps);
        return cards.get(0);
    }


    /**
     * Finds the lowest non trumps card to play, attempting to follow suit.
     * if tie, returns from longest suit.
     *
     * @return Lowest non trumps card
     */
    Card playLowestNotTrumpsFollowSuit(Card.Suit suit) {
        // If can follow suit, play lowest
        ArrayList<Card> suitCards = g.mBotHand.mCards.get(suit);
        if (!suitCards.isEmpty()) {
            return suitCards.get(0);
        }
        // otherwise, play lowest without following suit
        return playLowestNotTrumps();
    }

    Card playHighestNotTrumps(Card top) {
        ArrayList<Card> cards = g.mBotHand.mergeCards();
        Collections.sort(cards, new LowestComparator(g));
        // TODO: This is absolutely terrible.
        Card temp = cards.get(0);
        for (Card c : cards) {
            if (c.mSuit != g.mTrumps) {
                // Don't play a card worth more than it, unless its a trump and within 3 values.
                if ((top.mSuit == g.mTrumps && c.mRank.getVal() <= top.mRank.getVal() + 3)
                        || c.mRank.getVal() <= top.mRank.getVal()) {
                    temp = c;
                }
            }
            else break;
        }
        return temp;
    }
}

/**
 * Helps get the lowest non trumps card.
 */
class LowestComparator implements Comparator<Card> {

    GameState g;

    LowestComparator(GameState g) {
        this.g = g;
    }

    // TODO: Test this
    @Override
    public int compare(Card a, Card b) {
        // Return negative if a < b, positive if a > b, etc
        // First, sort trumps out to the end.
        if (a.mSuit != b.mSuit) {
            if (a.mSuit == g.mTrumps) return 1;
            else if (b.mSuit == g.mTrumps) return -1;
        }
        // otherwise sort on value
        return a.mRank.getVal() - b.mRank.getVal();
    }
}

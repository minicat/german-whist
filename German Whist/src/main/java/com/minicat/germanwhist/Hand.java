package com.minicat.germanwhist;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a hand of cards. Contains card constants also.
 * Created by Emma on 18/01/14.
 */
public class Hand {
    Map<Card.Suit, ArrayList<Card>> mCards;


    Hand(ArrayList<Card> cards) {
        // Initialise all the suit arraylists
        mCards = new HashMap<Card.Suit, ArrayList<Card>>();
        mCards.put(Card.Suit.CLUBS, new ArrayList<Card>());
        mCards.put(Card.Suit.HEARTS, new ArrayList<Card>());
        mCards.put(Card.Suit.SPADES, new ArrayList<Card>());
        mCards.put(Card.Suit.DIAMONDS, new ArrayList<Card>());

        // Assign cards based on suit
        for (Card c : cards) {
            mCards.get(c.mSuit).add(c);
        }

        // Sort
        sort();
    }

    // Sorts each of the suits
    private void sort() {
        for (Map.Entry<Card.Suit, ArrayList<Card>> entry : mCards.entrySet()) {
            Collections.sort(entry.getValue(), new CardComparator());
        }
    }

    class CardComparator implements Comparator<Card> {
        @Override
        public int compare(Card a, Card b) {
            // Return negative if a < b, positive if a > b, etc
            return a.mRank.getVal() - b.mRank.getVal();
        }
    }

    /**
     * Removes card from the hand
     *
     * @param card card being played
     */
    void remove(Card card) {
        Log.e("Hand", card.toString());
        mCards.get(card.mSuit).remove(card);
    }

    /**
     * Adds card to hand
     *
     * @param card card being drawn
     */
    void add(Card card) {
        mCards.get(card.mSuit).add(card);
        Collections.sort(mCards.get(card.mSuit), new CardComparator());
    }

    @Override
    public String toString() {
        String s = "";
        for (Map.Entry<Card.Suit, ArrayList<Card>> entry : mCards.entrySet()) {
            s = s + entry.getKey() + ": ";
            for (Card c : entry.getValue()) {
                s = s + c.toString() + " ";
            }
            s = s + "\n";
        }
        return s;
    }

    /**
     * Gets hand size
     *
     * @return int hand size
     */
    int size() {
        int sum = 0;
        for (Map.Entry<Card.Suit, ArrayList<Card>> entry : mCards.entrySet()) {
            sum += entry.getValue().size();
        }
        return sum;
    }

    /**
     * Checks if hand is void of given suit
     *
     * @param suit Suit checking void
     * @return True if void, false if not
     */
    boolean isVoid(Card.Suit suit) {
        return mCards.get(suit).isEmpty();
    }

    ArrayList<Card> mergeCards() {
        ArrayList<Card> all = new ArrayList<Card>();
        all.addAll(mCards.get(Card.Suit.CLUBS));
        all.addAll(mCards.get(Card.Suit.DIAMONDS));
        all.addAll(mCards.get(Card.Suit.HEARTS));
        all.addAll(mCards.get(Card.Suit.SPADES));
        return all;
    }
}

package com.minicat.germanwhist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;

/**
 * This is a deck of cards.
 * Created by Emma on 17/01/14.
 */
public class Deck {

    private ArrayList<Card> mCards;

    // TODO: when we rewrite Cards as not buttons, can remove context
    Deck() {
        // Create all cards and add them to array list
        mCards = new ArrayList<Card>();

        //TODO: rank and suit in Deck or Card?
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                mCards.add(new Card(suit, rank));
            }
        }
    }

    void shuffle() {
        // Collections can shuffle, yay!
        long seed = System.nanoTime();
        Collections.shuffle(mCards, new Random(seed));
    }

    /**
     * this is German whist dealing :3 so 4 piles of 13
     */
    void deal(ArrayList<Card> playerHand, ArrayList<Card> compHand, Queue<Card> shownPile,
              Queue<Card> hiddenPile) {
        for (int i = 0; i < 13; i++) {
            playerHand.add(mCards.get(i * 4));
            compHand.add(mCards.get(i * 4 + 1));
            shownPile.add(mCards.get(i * 4 + 2));
            hiddenPile.add(mCards.get(i * 4 + 3));
        }
    }

}

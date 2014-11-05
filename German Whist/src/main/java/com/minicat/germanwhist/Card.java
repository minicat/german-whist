package com.minicat.germanwhist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is a card.
 * <p/>
 * Created by Emma on 15/01/14.
 */
public final class Card {

    // used to be 87
    public static final int HEIGHT = 85 * 2;

    // used to be 56
    public static final int WIDTH = 60 * 2;

    /**
     * I hate myself so much ;___; fucking enums
     */
    static enum CardColor {
        RED(Color.parseColor("#DB2121")),
        BLACK(Color.BLACK);

        private int color;

        private CardColor(int color) {
            this.color = color;
        }

        private int getColor() {
            return color;
        }
    }


    /*
    Represents the suit of the card.
     */
    static enum Suit {
        HEARTS("♥", CardColor.RED),
        CLUBS("♣", CardColor.BLACK),
        SPADES("♠", CardColor.BLACK),
        DIAMONDS("♦", CardColor.RED);

        //TODO: make this an image or something later?
        private String rep;
        private CardColor color;

        private Suit(String rep, CardColor color) {
            this.rep = rep;
            this.color = color;
        }

        //TODO: should this be private? HAVE NEVER USED ENUMS BEFORE
        String getRep() {
            return rep;
        }

        private CardColor getCardColor() {
            return color;
        }


        // Reverse lookup. http://stackoverflow.com/questions/5316311/java-enum-reverse-look-up-best-practice

        /**private static final HashMap<Character, Suit> lookup = new HashMap<Character, Suit>();

        static {
            for (Suit s : values())
                lookup.put(s.getRep().charAt(0), s);
         } */

        public static Suit get(char rep) {
            for (Suit s : values()) {
                if (s.getRep().charAt(0) == rep) {
                    return s;
                }
            }
            // couldnt find it
            return null;
        }
    }

    /*
    Represents the rank of the card.
    Apparently this is called the rank?
     */
    static enum Rank {
        ACE("A", 14),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13);

        private String rep;
        private int val;

        private Rank(String rep, int val) {
            this.rep = rep;
            this.val = val;
        }

        String getRep() {
            return rep;
        }

        int getVal() {
            return val;
        }

        // Reverse lookup.
        private static final HashMap<Character, Rank> lookup = new HashMap<Character, Rank>();

        static {
            for (Rank r : values())
                lookup.put(r.getRep().charAt(0), r);
        }

        public static Rank get(char rep) {
            return lookup.get(rep);
        }
    }


    Suit mSuit;

    Rank mRank;

    /**
     * Reverses toString. For saving stuff.
     */
    public Card(String string) {
        this.mSuit = Suit.get(string.charAt(0));
        // check that its 2 character string
        if (string.length() == 2) {
            this.mRank = Rank.get(string.charAt(1));
        } else {
            // its a 10 TODO MAKE THIS BETTER ARGH
            this.mRank = Rank.TEN;
        }
    }


    public Card(Suit suit, Rank rank) {
        this.mSuit = suit;
        this.mRank = rank;
    }

    @Override
    public String toString() {
        return mSuit.getRep() + mRank.getRep();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card c = (Card) o;
            if ((c.mSuit == this.mSuit) && (c.mRank == this.mRank)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO: If theres problems with hash tabling cards, this is probably why.
        int hash = 37;
        hash += 17 * mRank.getVal();
        hash += 19 * mSuit.getRep().hashCode();
        return hash;
    }

    public Drawable makeDrawable(Context context) {
        Bitmap b = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw card
        Paint pCard = new Paint();
        pCard.setColor(Color.parseColor("#A8A8A8"));
        c.drawRect(0, 0, WIDTH, HEIGHT, pCard);

        // Draw card border
        pCard.setColor(Color.DKGRAY);
        pCard.setStyle(Paint.Style.STROKE);
        pCard.setStrokeWidth(2);
        c.drawRect(0, 0, WIDTH, HEIGHT, pCard);

        // Create paint depending on color
        Paint p = new Paint();
        // this is so stupid
        p.setColor(mSuit.getCardColor().getColor());
        p.setTextSize(60);
        p.setTextAlign(Paint.Align.CENTER);
        p.setAntiAlias(true);
        String text = mRank.getRep() + "" + mSuit.getRep();

        c.drawText(text, WIDTH / 2, HEIGHT / 2 + 20, p);
        p.setTextSize(25);
        c.drawText(mRank.getRep(), 14, 22, p);
        c.drawText(mSuit.getRep(), 14, 44, p);

        return new BitmapDrawable(context.getResources(), b);
    }

    public static Drawable cardBackDrawable(Context context) {
        Bitmap b = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Draw card
        Paint pCard = new Paint();
        pCard.setColor(Color.GRAY);
        c.drawRect(0, 0, WIDTH, HEIGHT, pCard);

        return new BitmapDrawable(context.getResources(), b);
    }

    // TODO TESTS
    public static LinkedList<Card> listFromString(String saved) {
        LinkedList<Card> list = new LinkedList<Card>();
        String[] cards = saved.split(" ");
        Card card;
        for (String c : cards) {
            card = new Card(c);
            list.add(card);
        }
        return list;
    }

    public static String listToString(Queue<Card> cards) {
        String str = "";
        for (Card c : cards) {
            str += c.toString() + " ";
        }
        return str.trim();
    }
}

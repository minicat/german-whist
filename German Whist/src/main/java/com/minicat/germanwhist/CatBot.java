package com.minicat.germanwhist;

/**
 * Emma's german whist player! Meow.
 * Created by Emma on 26/01/14.
 */
public class CatBot extends WhistBot {

    CatBot(GameState gameState) {
        super(gameState);
    }

    public Card playFirst() {
        return g.mBotHand.mergeCards().get(0);
    }

    public Card playSecond() {
        return g.mBotHand.mergeCards().get(0);
    }

    private void wantToWin() {

    }
}

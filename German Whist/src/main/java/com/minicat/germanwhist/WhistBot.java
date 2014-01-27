package com.minicat.germanwhist;

/**
 * Interface for German Whist AIs.
 * Created by Emma on 26/01/14.
 */
public abstract class WhistBot {

    // TODO: Passing game state means that the AI can cheat and look at the player's hand...fix later

    protected GameState g;

    public WhistBot(GameState gameState) {
        g = gameState;
    }

    public abstract Card playFirst();

    public abstract Card playSecond();

}

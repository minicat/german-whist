package com.minicat.germanwhist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is called HandView, but has the other game stuff too...
 * Created by Emma on 19/01/14.
 */
public class HandView extends View implements Runnable {

    static final String TAG = "HandView";

    /**
     * How many dp cards should be offset by when active.
     */
    static final int CARD_OFFSET = 10;

    GameState mGameState;

    //Context mContext;
    Paint mTextPaint;
    Paint mArrowPaint;
    int mWidth;
    int mHeight;

    int mScreenPadding = 10;

    int mCardsTop;
    int mCardsBottom;

    boolean gameWon = false;
    boolean playerWon;

    Card mHover;

    boolean mFailedPlay;

    boolean mDrawAnim;
    int mDrawAnimFrames;

    String mSuitOrder;

    // This is how the activity communicates with the hand view. Observer pattern!
    private HandListener mHandListener;


    // Maps x coordinates from the line of cards in hand to card
    Map<Pair<Integer, Integer>, Card> mBoundsToMap;

    public HandView(GameState gameState, Context context) {
        this(gameState, context, null);
    }

    public HandView(GameState gameState, Context context, AttributeSet attrs) {
        this(gameState, context, attrs, 0);
    }

    public HandView(GameState gameState, Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mGameState = gameState;

        mBoundsToMap = new HashMap<Pair<Integer, Integer>, Card>();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);


        mArrowPaint = new Paint();
        mArrowPaint.setTextSize(25);
        mArrowPaint.setColor(Color.DKGRAY);
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setFakeBoldText(true);
        //mContext = context;

        // Get screen dimensions - doesnt include action bar etc
        //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Display display = wm.getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size); apparently requires API level 13
        //mScreenWidth = display.getWidth();
        //mScreenHeight = display.getHeight();

        // Set listener for drawing card.
        mGameState.setRoundListener(new GameState.RoundListener() {
            @Override
            public void playerDrew(Card card) {
                // Mark drawing card animation as active.
                mDrawAnim = true;
                mDrawAnimFrames = CARD_OFFSET;
            }
        });

    }

    @Override
    public void onDraw(Canvas canvas) {
        //Log.e(TAG, "drawing");

        mHeight = this.getHeight();
        mWidth = this.getWidth();

        //canvas.drawText("This is the hand view", canvas.getWidth()/2, canvas.getHeight()/2, mTextPaint);

        // Clear map (cards may have changed)
        mBoundsToMap.clear();

        // TODO: are you sure Card.HEIGHT always equals cardDrawable.getIntrinsicHeight() ?
        mCardsTop = mHeight - mScreenPadding - Card.HEIGHT;
        mCardsBottom = mHeight - mScreenPadding;

        // Determine order to draw cards in.
        if (mSuitOrder == null) {
            // this generally shouldn't be an  issue, but just in case
            mSuitOrder = "♦♣♥♠"; //accessing string resources from here is meh
        }
        ArrayList<Card.Suit> order = new ArrayList<Card.Suit>();
        for (int i = 0; i < mSuitOrder.length(); i++) {
            order.add(Card.Suit.get(mSuitOrder.charAt(i)));
        }


        int i = 0, xLeft, xRight;
        for (int j = 0; j < order.size(); j++) {
            ArrayList<Card> entry = mGameState.mPlayerHand.mCards.get(order.get(j));
            for (Card c : entry) {
                //Drawable cardDrawable = c.makeDrawable(getContext());
                // x = (int)(i * (cardDrawable.getIntrinsicWidth()) * 2/5);
                //Log.e(TAG, "getIntrinsicHeight = "+ cardDrawable.getIntrinsicHeight() + " " + cardDrawable.getIntrinsicWidth());
                //Log.e(TAG, "actual= "+ Card.HEIGHT + " " + Card.WIDTH);
                // dunno why /12 vs /13....hmm
                //xLeft = mScreenPadding + (int)(i * (mWidth - mScreenPadding*2 - cardDrawable.getIntrinsicWidth()) / 12);
                xLeft = mScreenPadding + (int) (i * (mWidth - mScreenPadding * 2 - Card.WIDTH) / 12);

                // TODO: Move this into drawCard?
                if (c != mHover) {
                    drawCard(c, xLeft, mCardsTop, canvas);
                } else {
                    // Hovering over this card, boost it up.
                    // TODO: This may break hitbox detection?
                    drawCard(c, xLeft, mCardsTop - CARD_OFFSET, canvas);
                }

                // IF not last card, then width is less
                if (i < mGameState.mPlayerHand.size() - 1)
                    xRight = xLeft + (int) ((mWidth - mScreenPadding * 2 - Card.WIDTH) / 12);
                    //else xRight = xLeft + cardDrawable.getIntrinsicWidth();
                    //if (i < MainActivity.mPlayerHand.mCards.size() - 1) xRight = xLeft + (int)((mWidth - mScreenPadding*2 - Card.WIDTH) / 12);
                    // last card has no card on top = should be bigger
                else xRight = xLeft + Card.WIDTH;
                // Put in map
                mBoundsToMap.put(new Pair<Integer, Integer>(xLeft, xRight), c);
                //Log.e(TAG, "i = " + i + " " + xLeft + "," + xRight);
                i++;

            }
        }

        if (!mGameState.mShownPile.isEmpty()) {

            canvas.drawText("Top Card:", 10, 30, mTextPaint);
            drawCard((Card) mGameState.mShownPile.peek(), 10, 40, canvas);
        }

        canvas.drawText("Trumps: " + mGameState.mTrumps.getRep(), 10, 240, mTextPaint);

        canvas.drawText("Round Number: " + mGameState.mRound, 200, 30, mTextPaint);
        canvas.drawText("Cards Left: " + mGameState.mShownPile.size(), 200, 60, mTextPaint);
        canvas.drawText("Player Tricks Won: " + mGameState.mPlayerTricks, 200, 90, mTextPaint);
        //TODO ?? -14 not 13 : rounds played
        int compTricksMaybe = mGameState.mRound - 14 - mGameState.mPlayerTricks;
        int compTricks = (compTricksMaybe > 0) ? compTricksMaybe : 0;
        canvas.drawText("Computer Tricks Won: " + compTricks, 200, 120, mTextPaint);

        // Draw first turn card if possible
        if (mGameState.mFirstPlayed != null) {
            canvas.drawText("Computer played:", 10, 300, mTextPaint);
            drawCard(mGameState.mFirstPlayed, 10, 310, canvas);
        }

        // Draw previous trick if there was one
        if (mGameState.mPreviousTrick != null) {
            GameState.Trick t = mGameState.mPreviousTrick;
            canvas.drawText("Previous trick:", 200, 200, mTextPaint);
            canvas.drawText("Player first: " + t.playerFirst + " Player won: " + t.playerWon, 200, 230, mTextPaint);
            drawCard(t.first, 200, 240, canvas);
            drawCard(t.second, 330, 240, canvas);
            if (t.playerDrew != null) {
                canvas.drawText("You drew: " + t.playerDrew.toString(), 400, 200, mTextPaint);
            }
        }

        // If game over, show winner
        if (gameWon) {
            String message = "Game over! Winner: ";
            if (playerWon) message = message + "you! :D";
            else message = message + "Catbot! :3";
            canvas.drawText(message, 10, 500, mTextPaint);
        }

        // Loop at 60fps.
        postDelayed(this, 16);
    }

    /**
     * Makes it a runnable, for game loop.
     */
    @Override
    public void run() {
        // Update state of what we draw
        //updateState();
        // update animation for drawing a card
        // TODO: Looks kind of weird right now, since other cards just instantly move over.
        if (mDrawAnim) {
            if (mDrawAnimFrames > 0) mDrawAnimFrames--;
            else mDrawAnim = false;
        }

        // Request a redraw of this view
        // onDraw(Canvas) will be called
        invalidate();
    }


    public void gameWon(boolean gameWon, boolean playerWon) {
        this.gameWon = gameWon;
        this.playerWon = playerWon;
    }

    /**
     * Draws provided card at coordinates provided on canvas provided
     *
     * @param card   card to draw (x, y) = top left corner
     * @param x      x coord in pixels
     * @param y      y coord in pixels
     * @param canvas canvas to draw on
     */
    void drawCard(Card card, int x, int y, Canvas canvas) {
        Drawable d = card.makeDrawable(getContext());
        int ymod = 0;
        // TODO: Tidy this u.
        // If this card was the last card drawn, have indicator
        if (mGameState.mPreviousTrick != null) {
            if (mGameState.mPreviousTrick.playerDrew != null) {
                if (card.equals(mGameState.mPreviousTrick.playerDrew)) {
                    // if you need to animate it, do so
                    if (mDrawAnimFrames > 0) {
                        ymod = CARD_OFFSET + mDrawAnimFrames;
                    } else {
                        // otherwise have it lifted up UNLESS another card has been hovered.
                        // dont need to deal with it itself being hovered here (null)
                        // Also don't hover it, if you've tried to play another card
                        if (mHover == null && !mFailedPlay) {
                            ymod = CARD_OFFSET;
                        }
                    }
                }
            }
        }
        // can use card height/width instead
        d.setBounds(x, y - ymod, x + d.getIntrinsicWidth(), y + d.getIntrinsicHeight() - ymod);
        d.draw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        // Want the latest coordinates -> getPointerCount() - 1
        event.getPointerCoords(event.getPointerCount() - 1, coords);

        //Log.e(TAG, coords.x +" " + coords.y + " " + mCardsTop + " " + mCardsBottom);

        Card selected = null;

        // surely there is a better way to do this
        //Log.e(TAG, "mBoundsToMap size: " + mBoundsToMap.size());
        //checking y coords
        if (mCardsTop <= coords.y && coords.y <= mCardsBottom) {
            for (Pair<Integer, Integer> pair : mBoundsToMap.keySet()) {
                // checking x coords
                if (pair.first < coords.x && coords.x <= pair.second) {
                    selected = mBoundsToMap.get(pair);
                }
            }
        }

        if (selected != null) {
            Log.e(TAG, "Card: " + selected.toString() + " action: " + action);
            switch (action) {
                case (MotionEvent.ACTION_UP):
                    //Log.e(TAG, "ACTION UP");
                    if (mHandListener != null) {
                        mHandListener.onCardPlayed(selected);
                        // remember to stop hovering
                        mHover = null;
                    }
                    return true;
                case (MotionEvent.ACTION_DOWN):
                    //Log.e(TAG, "Action was DOWN");
                    // save which card you're hovering over
                    mHover = selected;
                    this.invalidate();
                    return true;
                // TODO: MAKE THIS REPEAT LESS SHITTY
                case (MotionEvent.ACTION_MOVE):
                    //Log.e(TAG, "Action was DOWN");
                    // save which card you're hovering over
                    mHover = selected;
                    this.invalidate();
                    return true;
            /*case (MotionEvent.ACTION_MOVE) :
                Log.e(TAG,"Action was MOVE");
                return true; */
                case (MotionEvent.ACTION_CANCEL):
                    //Log.e(TAG, "Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE):
                    //Log.e(TAG, "Movement occurred outside bounds " +
                    //"of current screen element");
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }
        // TODO: this is hacky. fix it later
        return true;

    }


    // Listener stuff.
    public void setEventListener(HandListener l) {
        this.mHandListener = l;
    }

    public interface HandListener {
        public void onCardPlayed(Card card);
    }

}

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
public class HandView extends View {

    static final String TAG = "HandView";

    //Context mContext;
    Paint mTextPaint;
    int mWidth;
    int mHeight;

    int mScreenPadding = 10;

    int mCardsTop;
    int mCardsBottom;


    // This is how the activity communicates with the hand view. Observer pattern!
    private HandListener mHandListener;


    // Maps x coordinates from the line of cards in hand to card
    Map<Pair<Integer, Integer>, Card> mBoundsToMap;

    public HandView(Context context) {
        this(context, null);
    }

    public HandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBoundsToMap = new HashMap<Pair<Integer, Integer>, Card>();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        //mContext = context;

        // Get screen dimensions - doesnt include action bar etc
        //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Display display = wm.getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size); apparently requires API level 13
        //mScreenWidth = display.getWidth();
        //mScreenHeight = display.getHeight();

    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.e(TAG, "drawing");

        mHeight = this.getHeight();
        mWidth = this.getWidth();

        //canvas.drawText("This is the hand view", canvas.getWidth()/2, canvas.getHeight()/2, mTextPaint);

        // Clear map (cards may have changed)
        mBoundsToMap.clear();

        // TODO: are you sure Card.HEIGHT always equals cardDrawable.getIntrinsicHeight() ?
        mCardsTop = mHeight - mScreenPadding - Card.HEIGHT;
        mCardsBottom = mHeight - mScreenPadding;

        int i = 0, xLeft, xRight;
        for (Map.Entry<Card.Suit, ArrayList<Card>> entry : MainActivity.mPlayerHand.mCards.entrySet()) {
            for (Card c : entry.getValue()) {
                Drawable cardDrawable = c.makeDrawable(getContext());
                // x = (int)(i * (cardDrawable.getIntrinsicWidth()) * 2/5);
                //Log.e(TAG, "getIntrinsicHeight = "+ cardDrawable.getIntrinsicHeight() + " " + cardDrawable.getIntrinsicWidth());
                //Log.e(TAG, "actual= "+ Card.HEIGHT + " " + Card.WIDTH);
                // dunno why /12 vs /13....hmm
                //xLeft = mScreenPadding + (int)(i * (mWidth - mScreenPadding*2 - cardDrawable.getIntrinsicWidth()) / 12);
                xLeft = mScreenPadding + (int) (i * (mWidth - mScreenPadding * 2 - Card.WIDTH) / 12);
                //cardDrawable.setBounds(xLeft, mHeight - mScreenPadding - cardDrawable.getIntrinsicHeight() , xLeft + cardDrawable.getIntrinsicWidth(),
                //mHeight - mScreenPadding);
                cardDrawable.setBounds(xLeft, mCardsTop, xLeft + Card.WIDTH, mCardsBottom);
                cardDrawable.draw(canvas);

                // IF not last card, then width is less
                if (i < MainActivity.mPlayerHand.size() - 1)
                    xRight = xLeft + (int) ((mWidth - mScreenPadding * 2 - Card.WIDTH) / 12);
                    //else xRight = xLeft + cardDrawable.getIntrinsicWidth();
                    //if (i < MainActivity.mPlayerHand.mCards.size() - 1) xRight = xLeft + (int)((mWidth - mScreenPadding*2 - Card.WIDTH) / 12);
                    // last card has no card on top = should be bigger
                else xRight = xLeft + Card.WIDTH;
                // Put in map
                mBoundsToMap.put(new Pair<Integer, Integer>(xLeft, xRight), c);
                Log.e(TAG, "i = " + i + " " + xLeft + "," + xRight);
                i++;

            }
        }

        Card topCard = MainActivity.mShownPile.getFirst();
        Drawable topCardDrawable = topCard.makeDrawable(getContext());
        topCardDrawable.setBounds(10, 40, 10 + topCardDrawable.getIntrinsicWidth(), 40 + topCardDrawable.getIntrinsicHeight());
        topCardDrawable.draw(canvas);
        canvas.drawText("Top Card:", 10, 30, mTextPaint);
        canvas.drawText("Round Number: " + MainActivity.mRound, 200, 30, mTextPaint);
        canvas.drawText("Cards Left: " + MainActivity.mShownPile.size(), 200, 60, mTextPaint);
        canvas.drawText("Player Tricks Won: " + MainActivity.mPlayerTricks, 200, 90, mTextPaint);
        //TODO ?? -14 not 13 : rounds played
        int compTricksMaybe = MainActivity.mRound - 14 - MainActivity.mPlayerTricks;
        int compTricks = (compTricksMaybe > 0) ? compTricksMaybe : 0;
        canvas.drawText("Computer Tricks Won: " + compTricks, 200, 120, mTextPaint);

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
            Log.e(TAG, "Card: " + selected.toString());
            switch (action) {
                case (MotionEvent.ACTION_UP):
                    Log.e(TAG, "ACTION UP");
                    if (mHandListener != null) {
                        mHandListener.onCardPlayed(selected);
                    }
                    return true;
                case (MotionEvent.ACTION_DOWN):
                    Log.e(TAG, "Action was DOWN");
                    // TODO: Highlight current card somehow
                    return true;
            /*case (MotionEvent.ACTION_MOVE) :
                Log.e(TAG,"Action was MOVE");
                return true; */
                case (MotionEvent.ACTION_CANCEL):
                    Log.e(TAG, "Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE):
                    Log.e(TAG, "Movement occurred outside bounds " +
                            "of current screen element");
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

package com.legion.quickdraw;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;


/**
 * @author impaler
 *
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
public class MainThread extends Thread {
	
        /*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        
        private float x;
        private float y;
        
        private static final int SPEED = 100;
        private boolean dRight;
        private boolean dLeft;
        private boolean dUp;
        private boolean dDown;
        
        private int mCanvasWidth;
        private int mCanvasHeight;

        private long mLastTime;
        private Bitmap mSnowflake;
        private Bitmap mBG;
        
        public Context mContext;
        
         /** Message handler used by thread to post stuff back to the GameView */
        private Handler mHandler;

         /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        public MainThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
        	
            x = 10;
            y = 10;
            
            mBG = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.reg_bg);
            mBG.getScaledWidth(mCanvasWidth);
        	mSnowflake = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
            	// Initialize game here!
            	
                x = 10;
                y = 10;
            	
                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) 
                	setState(STATE_PAUSE);
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) 
                        	updateGame();
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         * 
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            synchronized (mSurfaceHolder) {
                mMode = mode;
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }

        /**
         * Handles a key-down event.
         * 
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true
         */
        boolean doKeyDown(int keyCode, KeyEvent msg) {
        	boolean handled = false;
            synchronized (mSurfaceHolder) {
            	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            		dRight = true;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            		dLeft = true;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
            		dUp = true;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            		dDown = true;
            		handled = true;
            	}
                return handled;
            }
        }

        /**
         * Handles a key-up event.
         * 
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true if the key was handled and consumed, or else false
         */
        boolean doKeyUp(int keyCode, KeyEvent msg) {
        	boolean handled = false;
            synchronized (mSurfaceHolder) {
            	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            		dRight = false;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            		dLeft = false;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
            		dUp = false;
            		handled = true;
            	}
            	if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            		dDown = false;
            		handled = true;
            	}
                return handled;
            }
        }

        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
        	// empty canvas
        	canvas.drawARGB(255, 0, 0, 0);
        	
        	canvas.drawBitmap(mBG, 0, 0, new Paint());
        	
        	canvas.drawBitmap(mSnowflake, x, y, new Paint());
        }

        /**
         * Updates the game.
         */
        private void updateGame() {
        	//// <DoNotRemove>
            long now = System.currentTimeMillis();
            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) 
            	return;
            double elapsed = (now - mLastTime) / 1000.0;
            mLastTime = now;
            //// </DoNotRemove>
            
            /*
             * Why use mLastTime, now and elapsed?
             * Well, because the frame rate isn't always constant, it could happen your normal frame rate is 25fps
             * then your char will walk at a steady pace, but when your frame rate drops to say 12fps, without elapsed
             * your character will only walk half as fast as at the 25fps frame rate. Elapsed lets you manage the slowdowns
             * and speedups!
             */

            if (dUp)
            	y -= elapsed * SPEED;
            if (dDown)
            	y += elapsed * SPEED;
            if (y < 0)
            	y = 0;
            else if (y >= mCanvasHeight - mSnowflake.getHeight())
            	y = mCanvasHeight - mSnowflake.getHeight();
            if (dLeft)
            	x -= elapsed * SPEED;
            if (dRight)
            	x += elapsed * SPEED;
            if (x < 0)
            	x = 0;
            else if (x >= mCanvasWidth - mSnowflake.getWidth())
            	x = mCanvasWidth - mSnowflake.getWidth();
        }
    }
	
	
	
//	private static final String TAG = MainThread.class.getSimpleName();
//	
//	private boolean isTouching;
//	private boolean didClick;
//	
//	private float x;
//    private float y;
//    
//    private int mCanvasWidth;
//    private int mCanvasHeight;
//	
//	private long mLastTime;
//    private Bitmap mSnowflake;
//    
//    /** Message handler used by thread to post stuff back to the GameView */
//    private Handler mHandler;
//        
//    /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
//	public enum State {
//	    BEGIN, NEXTROUND, HIGHNOON, LISTEN, 
//	    KILL, DIE, WIN, LOSE, END
//	}
//	public State myState;
//	public boolean isPaused = false;
//	
//	public void setState(State newState)
//	{
//		this.myState = newState;
//	}
//	
//
//	// Surface holder that can access the physical surface
//	private SurfaceHolder surfaceHolder;
//	// The actual view that handles inputs
//	// and draws to the surface
//	private MainGamePanel gamePanel;
//
//	// flag to hold game state 
//	private boolean running;
//	public void setRunning(boolean running) {
//		this.running = running;
//	}
//
//	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel, Context context ) {
//		super();
//		this.surfaceHolder = surfaceHolder;
//		this.gamePanel = gamePanel;
//		setState(State.BEGIN);
//		setRunning(true);
//		
//		mSnowflake = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//    }
//	
//	/**
//     * Starts the game, setting parameters for the current difficulty.
//     */
//	public void doStart() {
//        synchronized (surfaceHolder) {
//        	// Initialize game here!
//        	
//            x = 10;
//            y = 10;
//        	
//            mLastTime = System.currentTimeMillis() + 100;
//            setState(State.NEXTROUND);
//        }
//    }
//	
//	 /**
//     * Pauses the physics update & animation.
//     */
//    public void pause() {
//        synchronized (surfaceHolder) {
//            if (!isPaused) 
//            	isPaused = true;
//        }
//    }
//	
//
//	@Override
//	public void run() {
//		long tickCount = 0L;
//		Log.d(TAG, "Starting game loop");
//		while (running) {
//			tickCount++;
//			// update game state 
//			// render state to the screen
//			Canvas c = null;
//
//             try {
//                   c = surfaceHolder.lockCanvas(null);
//                   synchronized (surfaceHolder) {
//                		if (!isPaused) 
//                			updateGame();
//                		gamePanel.onDraw(c);
//                 }
//             } finally {
//
//                 if (c != null) {
//                	 surfaceHolder.unlockCanvasAndPost(c);
//                 }
//             }
//		}
//		Log.d(TAG, "Game loop executed " + tickCount + " times");
//	}
//	
//	/* Callback invoked when the surface dimensions change. */
//    public void setSurfaceSize(int width, int height) {
//        // synchronized to make sure these all change atomically
//        synchronized (surfaceHolder) {
//            mCanvasWidth = width;
//            mCanvasHeight = height;
//        }
//    }
//
//    /**
//     * Resumes from a pause.
//     */
//    public void unpause() {
//        // Move the real time clock up to now
//        synchronized (surfaceHolder) {
//            mLastTime = System.currentTimeMillis() + 100;
//        }
//        isPaused = false;
//    }
//    
//    /**
//     * Draws the ship, fuel/speed bars, and background to the provided
//     * Canvas.
//     */
//    private void doDraw(Canvas canvas) {
//    	// empty canvas
//    	canvas.drawARGB(255, 0, 0, 0);
//    	
//    	canvas.drawBitmap(mSnowflake, x, y, new Paint());
//    }
//    
//    boolean doKeyDown(int keyCode, KeyEvent msg) {
//    	boolean handled = false;
//    	
//    	 synchronized (surfaceHolder) {
//         	handled = true;
//         	isTouching = true;
//         	didClick = true;
//            return handled;
//         }
//    }
//    
//    boolean doKeyUp(int keyCode, KeyEvent msg) {
//    	boolean handled = false;
//    	
//   	 synchronized (surfaceHolder) {
//        	handled = true;
//        	isTouching = false;
//        	didClick = false;
//           return handled;
//        }
//   }
//    
//	/**
//     * Updates the game.
//     */
//    private void updateGame() {
//    	//// <DoNotRemove>
//        long now = System.currentTimeMillis();
//        // Do nothing if mLastTime is in the future.
//        // This allows the game-start to delay the start of the physics
//        // by 100ms or whatever.
//        if (mLastTime > now) 
//        	return;
//        double elapsed = (now - mLastTime) / 1000.0;
//        mLastTime = now;
//        //// </DoNotRemove>
//        
//        /*
//         * Why use mLastTime, now and elapsed?
//         * Well, because the frame rate isn't always constant, it could happen your normal frame rate is 25fps
//         * then your char will walk at a steady pace, but when your frame rate drops to say 12fps, without elapsed
//         * your character will only walk half as fast as at the 25fps frame rate. Elapsed lets you manage the slowdowns
//         * and speedups!
//         */
//
//
//        if (y < 0)
//        	y = 0;
//        else if (y >= mCanvasHeight - mSnowflake.getHeight())
//        	y = mCanvasHeight - mSnowflake.getHeight();
//
//        if (x < 0)
//        	x = 0;
//        else if (x >= mCanvasWidth - mSnowflake.getWidth())
//        	x = mCanvasWidth - mSnowflake.getWidth();
//    }
	

package com.legion.quickdraw;

//import eu.MrSnowflake.android.gametemplate.AttributeSet;
//import eu.MrSnowflake.android.gametemplate.Handler;
//import eu.MrSnowflake.android.gametemplate.InterruptedException;
//import eu.MrSnowflake.android.gametemplate.Message;
//import eu.MrSnowflake.android.gametemplate.Override;
import com.legion.quickdraw.MainThread;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author impaler
 * This is the main surface that handles the ontouch events and draws
 * the image to the screen.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {
	
	 /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** The thread that actually draws the animation */
    private MainThread thread;

    public MainGamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new MainThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            	// Use for pushing back messages.
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     * 
     * @return the animation thread
     */
    public MainThread getThread() {
        return thread;
    }

    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus)
        	thread.pause();
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private Bitmap b = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
//
//	private static final String TAG = MainGamePanel.class.getSimpleName();
//	
//	public MainThread thread;
//	
//	/** Handle to the application context, used to e.g. fetch Drawables. */
//    private Context mContext;
//
//	public MainGamePanel(Context context) {
//		super(context);
//		// adding the callback (this) to the surface holder to intercept events
//		getHolder().addCallback(this);
//		
//		// create the game loop thread
//		thread = new MainThread(getHolder(), this, context);
//		
//		// make the GamePanel focusable so it can handle events
//		setFocusable(true);
//	}
//
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//	}
//	
//	/**
//     * Fetches the animation thread corresponding to this LunarView.
//     * 
//     * @return the animation thread
//     */
//    public MainThread getThread() {
//        return thread;
//    }
//	
//	/**
//     * Standard override to get key-press events.
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent msg) {
//        return thread.doKeyDown(keyCode, msg);
//    }
//
//    /**
//     * Standard override for key-up. We actually care about these, so we can
//     * turn off the engine or stop rotating.
//     */
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent msg) {
//        return thread.doKeyUp(keyCode, msg);
//    }
//	
//	/**
//     * Standard window-focus override. Notice focus lost so we can pause on
//     * focus lost. e.g. user switches to take a call.
//     */
//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        if (!hasWindowFocus)
//        	thread.pause();
//    }
//
//	public void surfaceCreated(SurfaceHolder holder) {
//		// at this point the surface is created and
//		// we can safely start the game loop
//		thread.setRunning(true);
//		thread.start();
//	}
//
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		Log.d(TAG, "Surface is being destroyed");
//		// tell the thread to shut down and wait for it to finish
//		// this is a clean shutdown
//		boolean retry = true;
//		thread.setRunning(false);
//		while (retry) {
//			try {
//				thread.join();
//				retry = false;
//			} catch (InterruptedException e) {
//				// try again shutting down the thread
//			}
//		}
//		Log.d(TAG, "Thread was shut down cleanly");
//	}
//	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			
//			
//			
//			if (event.getY() > getHeight() - 50) {
//				thread.setRunning(false);
//				((Activity)getContext()).finish();
//			} else {
//				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
//			}
//		}
//		return super.onTouchEvent(event);
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		
//	}

}

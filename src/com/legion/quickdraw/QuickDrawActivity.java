package com.legion.quickdraw;

//import com.legion.quickdraw.QuickDrawView.QuickDrawThread; // lol

import com.legion.quickdraw.MainGamePanel;
import com.legion.quickdraw.MainThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class QuickDrawActivity extends Activity{// implements View.OnClickListener {
    /** Called when the activity is first created. */
	
	
	private static final int MENU_PAUSE = Menu.FIRST;

    private static final int MENU_RESUME = Menu.FIRST + 1;

    private static final int MENU_START = Menu.FIRST + 2;

    private static final int MENU_STOP = Menu.FIRST + 3;

    /** A handle to the thr	ead that's actually running the animation. */
    private MainThread mGameThread;

    /** A handle to the View in which the game is running. */
    private MainGamePanel mGameView;

    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     * 
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                mGameThread.doStart();
                return true;
            case MENU_STOP:
                mGameThread.setState(MainThread.STATE_LOSE);
                return true;
            case MENU_PAUSE:
                mGameThread.pause();
                return true;
            case MENU_RESUME:
                mGameThread.unpause();
                return true;
        }

        return false;
    }

    /**
     * Invoked when the Activity is created.
     * 
     * @param savedInstanceState a Bundle containing state saved from a previous
     *        execution, or null if this is a new execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.game);

        // get handles to the LunarView from XML, and its LunarThread
        mGameView = (MainGamePanel) findViewById(R.id.game);
        mGameThread = mGameView.getThread();

        // set up a new game
        mGameThread.setState(MainThread.STATE_READY);
        Log.w(this.getClass().getName(), "SIS is null");
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGameView.getThread().pause(); // pause game when Activity pauses
    }

	
	
	
	
	
	
	
	
	
//	private static final String TAG = QuickDrawActivity.class.getSimpleName();
//
//	private MainGamePanel mgp;
//	
//	private MainThread mThread;
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.main);
//
//    }
//     
//
//    //here's a method that you must have when your activity implements the
//
//    //View.OnClickListener interface...
//
//    public void onClick(View v) {
//
//        showToastMessage("you clicked on a btn3, which uses this Activity as the listener");
//
//    }
//    
//
//    //here's the handler for btn4 (declared in the xml layout file)...
//
//    //note: this method only works with android 2.1 (api level 7), it must be public and
//
//    //must take a single parameter which is a View
//
//    public void buttonPlayListener(View v) {
//        setContentView(R.layout.mainmenu);
//    }
//    
//    public void btn1Listener(View v) {
//    	//setContentView(new AndroidTutorialPanel(this));
//    	
//         // making it full screen
//         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//         // set our MainGamePanel as the View
//         //mgp = new MainGamePanel(this);
//         //setContentView(mgp);
//         mgp = (MainGamePanel) findViewById(R.id.game);
//         mThread = mgp.getThread(); // IT BROKE HERE - WAT DO????
//         
//         mThread.setState(MainThread.BEGIN);
//         //mThread.doStart();
//         Log.d(TAG, "View added");
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if (keyCode == KeyEvent.KEYCODE_BACK) {
//		    mgp.thread.setRunning(false);
//		    this.finish();
//		    return super.onKeyDown(keyCode, event);
//	    } else {
//	    	return super.onKeyDown(keyCode, event);
//	    }
//    }
//    
//    
//    public void btn3Listener(View v) {
//        showToastMessage("You clicked btn3 - listener was set up in the XML layout");
//
//    }
//    
//    public void btn4Listener(View v) {
//            showToastMessage("You clicked btn4 - listener was set up in the XML layout");
//
//    }
//
//    private void showToastMessage(String msg){
//
//        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
//
//        toast.show();
//
//    }
    
//    @Override
//	protected void onDestroy() {
//		Log.d(TAG, "Destroying...");
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onStop() {
//		Log.d(TAG, "Stopping...");
//		super.onStop();
//	}
}
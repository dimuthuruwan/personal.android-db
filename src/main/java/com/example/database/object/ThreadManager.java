package com.example.database.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationHandler is a singleton; only one of this instance should be
 * instantiated for the lifetime of the app.
 * This class contains a Handler (mHandler) which runs on the UI thread.
 * Messages sent to this handler to make updates to the UI thread, or they can
 * be delegated to a background, worker thread.
 *
 * Created by Eric Tsang on 19/10/2014.
 */
public class ThreadManager {

    /////////////////////////////
    // DEFINE STATIC CONSTANTS //
    /////////////////////////////

    // ThreadPoolExecutor constructor parameters
    /**
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static final int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    /** Sets amount of time an idle thread waits before terminating (seconds) */
    private static final int KEEP_ALIVE_TIME = 1;

    /** Sets the Time Unit to seconds */
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // CustomHandler message types
    /**
     * Message type describing a message that is a runnable, that must be run
     * on some sort of worker thread. Such message types will be assigned to a
     * worker thread of this class to do its thing.
     */
    public static final int START_RUNNABLE_TASK = 0;

    /**
     * Message type describing a message that needs to run on the UI thread;
     * executed on the same thread as this class. Such message types must
     * implement the UpdateUITask interface. The updateUI method will be run on
     * the UI thread.
     */
    public static final int UPDATE_UI_TASK = 1;

    ////////////////////////////////////////
    // INSTANTIATE STATIC SUPPORT OBJECTS //
    ////////////////////////////////////////

    /** A queue of Runnables to be passed to the threadPool for execution */
    private static final BlockingQueue<Runnable> workQueue =
            new LinkedBlockingQueue<Runnable>();

    /** Handler object that's attached to the UI thread */
    private final Handler mHandler;

    /** Reference to the singleton ApplicationHandler */
    private static final ThreadManager mInstance;

    /** Creates a thread pool manager & instantiate one */
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            NUMBER_OF_CORES,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue);

    /** Instantiates a single ApplicationHandler */
    static { mInstance = new ThreadManager(); }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    /**
     * Constructs the work queues and thread pools used to download
     * and decode images. Because the constructor is marked private,
     * it's unavailable to other classes, even in the same package.
     */
    private ThreadManager() {

        // Defines a Handler object that's attached to the UI thread
        mHandler = new CustomHandler(Looper.getMainLooper());
    }

    ///////////////////////
    // INTERFACE METHODS //
    ///////////////////////

    /**
     * runs the run method of the passed Runnable instance (runnable) on a
     * worker thread when one becomes available.
     *
     * @param runnable a Runnable instance who's run method will be executed on a
     * background thread when one becomes available.
     */
    public static void runOnWorkerThread(Runnable runnable) {
        mInstance.mHandler.obtainMessage(
                START_RUNNABLE_TASK, runnable).sendToTarget();
    }

    /**
     * runs the updateUI method of the passed UpdateUITask instance
     * (updateUITask) on the UI thread.
     *
     * @param updateUITask an UpdateUITask instance who's updateUI method will
     * be run on the main application thread; it can access UI components.
     */
    public static void runOnMainThread(Runnable updateUITask) {
        mInstance.mHandler.obtainMessage(
                UPDATE_UI_TASK, updateUITask).sendToTarget();
    }

    ///////////////////
    // INNER CLASSES //
    ///////////////////

    /**
     * message Handler class of the ThreadManager; when it has a message to
     * handle, it computes what kind of message it is, and runs it on the
     * appropriate thread.
     */
    public class CustomHandler extends Handler {

        private CustomHandler(Looper looper) {
            super(looper);
        }

        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        public void handleMessage(Message messageIn) {

            switch (messageIn.what) {

                /*
                 * Assumes that messageIn.obj implements Runnable. Executes
                 * the Runnable's run method on a background worker thread.
                 */
                case ThreadManager.START_RUNNABLE_TASK:

                    // Adds task to the thread pool for execution on a
                    // worker thread
                    threadPool.execute((Runnable) messageIn.obj);
                    break;

                /*
                 * Assumes that messageIn.obj implements UpdateUITask.
                 * Executes the UpdateUITask's updateUI method on the UI
                 * thread.
                 */
                case ThreadManager.UPDATE_UI_TASK:

                    // Runs task on the UI thread (this thread).
                    ((Runnable) messageIn.obj).run();
                    break;

                /* Pass along other messages from the UI */
                default:
                    super.handleMessage(messageIn);
                    break;
            }
        }
    }
}

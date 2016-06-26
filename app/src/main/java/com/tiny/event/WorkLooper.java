package com.tiny.event;

import android.os.Looper;
import android.util.Log;

/**
 * Created Tiny
 * 实现主要功能：
 * 创建时间： on 2016/6/26.
 * 修改者： 修改日期： 修改内容：
 */
public class WorkLooper implements Runnable {

    private final Object mLock = new Object();
    private Looper mLooper;


    /**
     * Creates a worker thread with the given name. The thread then runs a {@link android.os.Looper}
     *
     * @param name
     */
    public WorkLooper(String name) {
        Thread thread = new Thread(null, this, name);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        Log.d("tiny", "WorkLooper -- WorkLooper(String name)");

        synchronized (mLock) {
            while (mLooper == null) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return current Looper, maybe return null
     */
    public Looper getLooper() {
        return mLooper;
    }

    @Override
    public void run() {
        synchronized (mLock) {
            Looper.prepare();
            Log.d("tiny", "WorkLooper -- run");
            mLooper = Looper.myLooper();
            mLock.notifyAll();
        }
        Looper.loop();
    }

    /**
     * Quits the looper.
     */
    public void quit() {
        mLooper.quit();
    }
}

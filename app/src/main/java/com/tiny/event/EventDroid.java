package com.tiny.event;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created JackLuo
 * 实现主要功能：
 * <p/>
 * 1、创建对象 （单例模式）
 * 2、注册监听
 * 3、发送事件
 * <p/>
 * <p/>
 * 创建时间： on 2016/6/16.
 * 修改者： 修改日期： 修改内容：
 */
public class EventDroid {

    private static EventDroid ed = null;
    private static Hashtable<Integer, Vector<WeakReference<IEvtReceiver>>> receiverList = new Hashtable<>();
    //对象锁
    private Object mLockReceiver = new Object();
    //内部使用  消息发送者队列
    private BlockingDeque<ISender> senderList = new LinkedBlockingDeque<>();

    //内部事件发送 异步线程
    private InnerSendHandler mSendHandler = new InnerSendHandler(new WorkLooper("SendThread").getLooper());
    //内部回调 异步线程
    private InnerEventHandler mEventHandler = new InnerEventHandler(new WorkLooper("EventThread").getLooper());

    private EventMonitor mEventMonitor = null;

    private boolean flag = true;

    private static class InnerSendHandler extends Handler {
        public InnerSendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InnerSendStructure is = (InnerSendStructure) msg.obj;

            DroidSender sender = new DroidSender(msg.what);
            sender.bindData(is.sendData);
            is.ed.senderList.offer(sender);
            is.ed.notifyThread();
            msg.obj = null;
        }
    }

    private static class InnerEventHandler extends Handler {
        public InnerEventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (msg.obj != null) {
                    final InnerStructure is = (InnerStructure) msg.obj;
                    if (is.er != null) {
                        is.er.onReceiver(is.tag, is.er, is.mData);
                    }
                }
            }

        }
    }

    /**
     * private constructor
     */
    private EventDroid() {
        mEventMonitor = new EventMonitor();
        mEventMonitor.start();
    }

    /**
     * create a singleton
     *
     * @return
     */
    public static EventDroid ins() {
        if (ed == null) {
            synchronized (EventDroid.class) {
                ed = new EventDroid();
            }
        }
        return ed;
    }

    /**
     * 注册监听者
     *
     * @param tag      消息标示
     * @param receiver 新注册的监听器
     */
    public void register(int tag, IEvtReceiver receiver) {
        if (receiver == null)
            throw new RuntimeException("IEvtReceiver is null");
        Vector<WeakReference<IEvtReceiver>> old = receiverList.get(tag);
        if (old != null) {
            if (!contains(old.iterator(), receiver))
                old.add(new WeakReference<>(receiver));
            else return;
        } else {
            old = new Vector<>();
            old.add(new WeakReference(receiver));
        }
        synchronized (mLockReceiver) {
            receiverList.put(tag, old);
        }
    }


    /**
     * 发送数据
     *
     * @param tag
     * @param sendData
     */
    public void send(int tag, Object sendData) {
        send(tag, sendData, 0);
    }

    /**
     * 带延时的发送数据
     *
     * @param tag
     * @param sendData
     * @param waitMiSecond
     */
    public void send(int tag, Object sendData, int waitMiSecond) {
        InnerSendStructure is = new InnerSendStructure();
        is.ed = this;
        is.sendData = sendData;
        is.waitMiSecond = waitMiSecond;

        mSendHandler.sendMessageDelayed(mSendHandler.obtainMessage(tag, is), waitMiSecond);
    }

    private void notifyThread() {
        synchronized (mEventMonitor) {
            if (mEventMonitor != null) {
                mEventMonitor.notifyAll();
            }
        }
    }

    /**
     * 判断新传入的监听器是否已经存在
     *
     * @param iterator
     * @param receiver 新传入的监听器
     * @return 如果存在返回false，否则true
     */
    private boolean contains(Iterator<WeakReference<IEvtReceiver>> iterator, IEvtReceiver receiver) {
        if (iterator == null || receiver == null)
            return false;
        WeakReference<IEvtReceiver> reference;
        while (iterator.hasNext()) {
            reference = iterator.next();
            if (reference != null) {
                if (receiver.equals(reference)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 事件总线
     */
    private class EventMonitor extends Thread {

        public EventMonitor() {
            super("EventMonitor");
        }

        @Override
        public void run() {
            int tag = 0;
            ISender is = null;
            WeakReference<IEvtReceiver> ier = null;

            while (flag) {
                try {


                    if (senderList != null && senderList.size() > 0) {
                        while (!senderList.isEmpty()) {
                            is = senderList.poll();
                            tag = is.getTag();

                            Vector<WeakReference<IEvtReceiver>> iers = receiverList.get(tag);
                            if (iers != null) {
                                synchronized (mLockReceiver) {
                                    Iterator<WeakReference<IEvtReceiver>> iterator = iers.iterator();
                                    while (iterator.hasNext()) {
                                        ier = iterator.next();
                                        mEventHandler.sendMessage(mEventHandler.obtainMessage(0, new InnerStructure(ier.get(),
                                                is.getMessageData(), is.getTag())));
                                        if (ier.get() == null) {
                                            iterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class InnerSendStructure {
        public EventDroid ed;
        public Object sendData;
        public int waitMiSecond;
    }

    private static class InnerStructure {

        IEvtReceiver er;
        Object mData;
        int tag;

        InnerStructure(IEvtReceiver er, Object mData, int tag) {
            this.er = er;
            this.mData = mData;
            this.tag = tag;
        }
    }

    static class DroidSender implements ISender {

        protected Object obj;
        protected int tag;

        public DroidSender(int tag) {
            this.tag = tag;
        }

        public void bindData(Object obj) {
            this.obj = obj;
        }

        @Override
        public Object getMessageData() {
            return obj;
        }

        @Override
        public int getTag() {
            return tag;
        }
    }

}

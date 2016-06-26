package com.tiny.event;

/**
 * Created JackLuo
 * 实现主要功能：
 * 创建时间： on 2016/6/16.
 * 修改者： 修改日期： 修改内容：
 */
public interface IEvtReceiver<T> {

    /**
     *
     * @param tag
     * @param receiver
     * @param result
     * @return
     */
    int onReceiver(int tag, IEvtReceiver<T> receiver, T result);
}

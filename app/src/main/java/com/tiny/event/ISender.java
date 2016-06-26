package com.tiny.event;

/**
 * Created JackLuo
 * 实现主要功能：
 * 创建时间： on 2016/6/26.
 * 修改者： 修改日期： 修改内容：
 */
public interface ISender {

    /**
     * 获取消息数据
     *
     * @return
     */
    public Object getMessageData();

    /**
     * 获取消息标识
     *
     * @return
     */
    public int getTag();
}

package com.tiny.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.tiny.event.EventDroid;
import com.tiny.event.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created JackLuo
 * 实现主要功能：
 * 创建时间： on 2016/6/26.
 * 修改者： 修改日期： 修改内容：
 */
public class SendMessageActivity extends AppCompatActivity {

    @BindView(R.id.btn_send)
    Button send;

    @OnClick(R.id.btn_send)
    void onSend() {
        EventDroid.ins().send(EventTag.TEST_SEND_INTERGER_TAG, 5);
        EventDroid.ins().send(EventTag.TEST_SEND_STRING_TAG, "10");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);
    }
}

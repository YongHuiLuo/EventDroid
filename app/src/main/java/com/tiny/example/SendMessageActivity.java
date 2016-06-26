package com.tiny.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tiny.event.EventDroid;
import com.tiny.event.R;

/**
 * Created JackLuo
 * 实现主要功能：
 * 创建时间： on 2016/6/26.
 * 修改者： 修改日期： 修改内容：
 */
public class SendMessageActivity extends Activity {

    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        btnSend = (Button) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDroid.ins().send(EventTag.TEST_SEND_TAG, "5");
            }
        });
    }
}

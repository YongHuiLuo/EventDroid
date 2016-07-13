package com.tiny.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tiny.event.EventDroid;
import com.tiny.event.IEvtReceiver;
import com.tiny.event.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created JackLuo
 * 实现主要功能：
 * 创建时间： on 2016/7/13.
 * 修改者： 修改日期： 修改内容：
 */
public class SecondActivity extends Activity {

    @BindView(R.id.txt_message)
    TextView mTxtMessage;

    @OnClick(R.id.btn_goto) void skip() {
        Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);

        EventDroid.ins().register(EventTag.TEST_SEND_STRING_TAG, strRecv);
    }

    IEvtReceiver strRecv = new IEvtReceiver() {
        @Override
        public int onReceiver(int tag, IEvtReceiver receiver, final Object result) {
            if (tag == EventTag.TEST_SEND_STRING_TAG) {
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTxtMessage.setText("接收消息--" + result.toString());
                    }
                });
            }
            return 0;
        }
    };

}

package com.tiny.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tiny.event.EventDroid;
import com.tiny.event.IEvtReceiver;
import com.tiny.event.R;

public class MainActivity extends AppCompatActivity {

    private Button btnGoto;
    private TextView txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnGoto = (Button) findViewById(R.id.btn_goto);
        txtContent = (TextView) findViewById(R.id.txt_content);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SendMessageActivity.class);
                startActivity(intent);
            }
        });

        EventDroid.ins().register(EventTag.TEST_SEND_TAG, receiver);
    }


    IEvtReceiver receiver = new IEvtReceiver() {
        @Override
        public int onReceiver(int tag, IEvtReceiver receiver, Object result) {
            if (tag == EventTag.TEST_SEND_TAG) {
                Log.d("tiny", "result --- " + result);
                Toast.makeText(getBaseContext(), result.toString(), Toast.LENGTH_LONG).show();
            }
            return 0;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

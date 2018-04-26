package com.example.hp.testwannianli.userdefinedwidget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hp.testwannianli.R;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_activity_test);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Main2Activity.this,DatePickerActivity.class),0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 0){
            Log.i(TAG,"---Main---");
            if (data == null)
                return;
            Bundle bundle = data.getExtras();
            Log.i(TAG,"---"+"\n"+bundle.getString("year")+"\n"
                    +bundle.getString("month")+"\n"
                    +bundle.getString("day")+"\n"
                    +bundle.getString("hour")+"\n"
                    +bundle.getString("minute")+"\n"
                    +bundle.getString("second")+"\n"
                    +"---");
        }
    }
}

package com.example.hp.testwannianli.dayManager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.userdefinedwidget.Main2Activity;

public class MenuActivity extends AppCompatActivity {

    private Button add_bt;
    private Button manage_bt;

    private Button datePicker_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initView();
        //监听增加按钮
        addOnClick();
        //监听管理按钮
        manageOnClick();

        adatePickerOnClick();
    }

    public void initView(){
        add_bt = (Button)findViewById(R.id.add_bt);
        manage_bt=(Button)findViewById(R.id.manage_bt);

        datePicker_test=(Button)findViewById(R.id.datePicker_test);

    }
    public void addOnClick(){
        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,AddDayManagerActivity.class);
                startActivity(intent);
            }
        });
    }
    public void manageOnClick(){
        manage_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,ManageAllDaysActivity.class);
                startActivity(intent);
            }
        });
    }

    public void adatePickerOnClick(){
        datePicker_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });
    }
}

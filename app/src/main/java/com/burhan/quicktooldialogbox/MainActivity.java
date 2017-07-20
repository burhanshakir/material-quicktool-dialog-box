package com.burhan.quicktooldialogbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import burhan.com.material_quicktooldialogbox.QuickToolDialog;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        QuickToolDialog quickToolDialog = new QuickToolDialog(this, QuickToolDialog.HORIZONTAL, QuickToolDialog.LIGHT_THEME);

        quickToolDialog.setButtons(R.xml.quicktool_dialog_items);
    }
}

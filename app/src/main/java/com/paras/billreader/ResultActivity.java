package com.paras.billreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView amount;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        amount = findViewById(R.id.amount);
        bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            if(bundle.containsKey("amount"))
            {
                amount.setText("Bill Amount : "+bundle.getString("amount"));
            }
        }
    }
}

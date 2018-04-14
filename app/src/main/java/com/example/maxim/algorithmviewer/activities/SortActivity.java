package com.example.maxim.algorithmviewer.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.R;

import org.w3c.dom.Text;

import java.util.Random;

public class SortActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        int[] sourceMassive = getIntent().getIntArrayExtra("sourceMassive");
        int[] firstCountMassives = getIntent().getIntArrayExtra("firstCountMassive");
        int[] secondCountMassive = getIntent().getIntArrayExtra("secondCountMassive");
        Random r = new Random();
        for (int i = 0; i < sourceMassive.length; i++) {
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 10, 0, 10);
            tr.setLayoutParams(lp);
            tr.setGravity(Gravity.CENTER);

            int color = Color.argb(255, r.nextInt(255), r.nextInt(255), r.nextInt(255));

            TextView elementView = new TextView(this);
            elementView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            elementView.setGravity(Gravity.CENTER);
            elementView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            Spannable sp = new SpannableString("" + sourceMassive[i]);
            sp.setSpan(new ForegroundColorSpan(color), 0, sp.toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            elementView.setText(sp);


            Spannable sp2 = new SpannableString("" + firstCountMassives[i]);
            sp2.setSpan(new ForegroundColorSpan(color), 0, sp2.toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            TextView firstSwapsCountView = new TextView(this);
            firstSwapsCountView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            firstSwapsCountView.setGravity(Gravity.CENTER);
            firstSwapsCountView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            firstSwapsCountView.setText(sp2);

            Spannable sp3 = new SpannableString("" + secondCountMassive[i]);
            sp3.setSpan(new ForegroundColorSpan(color), 0, sp3.toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            TextView secondSwapsCountView = new TextView(this);
            secondSwapsCountView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            secondSwapsCountView.setGravity(Gravity.CENTER);
            secondSwapsCountView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            secondSwapsCountView.setText(sp3);


            tr.addView(elementView);
            tr.addView(firstSwapsCountView);
            tr.addView(secondSwapsCountView);


            ((TableLayout) findViewById(R.id.elementsTable)).addView(tr);
        }
    }


    public Color getRandomColor(Random r) {
        Color c = new Color();
        c.red(r.nextInt(255) / 255);
        c.green(r.nextInt(255) / 255);
        c.blue(r.nextInt(255) / 255);
        return c;
    }
}

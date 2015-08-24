package com.geekfan.collapsablelinearlayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by xufan on 2015/7/31.
 */
public class MainActivity extends Activity {
TagLayout tableLayout;
ImageView moreIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tableLayout=(TagLayout)findViewById(R.id.collapsLayout);
        moreIv=(ImageView)findViewById(R.id.more);
        tableLayout.setOnCollapseObserver(new TagLayout.onCollapseObserver() {
            @Override
            public void isCollaps(boolean flag) {
                if (flag) {
                    moreIv.setImageResource(R.drawable.ic_arrow_down_screening);
                } else {
                    moreIv.setImageResource(R.drawable.ic_arrow_up_screening);
                }
            }
        });
        moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableLayout.toggle();
            }
        });
        tableLayout.setTagClickListener(new TagLayout.OnTagClick() {
            @Override
            public void onClick(String tag) {
                Toast.makeText(MainActivity.this,tag,Toast.LENGTH_SHORT).show();
            }
        });
        tableLayout.appendTag("i am text1",true);
        tableLayout.appendTag("i am long text2",true);
        tableLayout.appendTag("text3",true);
        tableLayout.appendTag("i am long text4",true);
        tableLayout.appendTag("text5",true);
        tableLayout.appendTag("i am long text6",true);
        tableLayout.appendTag("i am text7",true);
        tableLayout.appendTag("i am long text8",true);
        tableLayout.appendTag("text3",true);
        tableLayout.appendTag("i am long text9",true);
        tableLayout.appendTag("text10",true);
        tableLayout.appendTag("i am long text11",true);
        tableLayout.appendTag("i am text12",true);
        tableLayout.appendTag("i am long text13",true);
        tableLayout.appendTag("text14",true);
        tableLayout.appendTag("i am long text15",true);
        tableLayout.appendTag("i am long text16",true);
        tableLayout.appendTag("text17",true);
        tableLayout.appendTag("text18",true);
        tableLayout.appendTag("text19",true);
        tableLayout.appendTag("i am long text20",true);
        tableLayout.appendTag("i am long text21",true);
        tableLayout.appendTag("text22",true);
    }
}

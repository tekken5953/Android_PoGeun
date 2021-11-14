package com.example.pogeun;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


public class SideBarView extends RelativeLayout implements View.OnClickListener {

    // 메뉴버튼 클릭 이벤트 리스너
    public EventListener listener;

    public void setEventListener(EventListener l) {
        listener = l;
    }

    // 메뉴버튼 클릭 이벤트 리스너 인터페이스
    public interface EventListener {
        // 닫기 버튼 클릭 이벤트
        void btnCancel();

        void btnChild1();

        void btnChild2();

        void btnChild3();

        void share();

        void option();

        void my_page();
    }

    public SideBarView(Context context) {
        this(context, null);
        init();
    }

    public SideBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.sidemenu, this, true);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_side_level_1).setOnClickListener(this);
        findViewById(R.id.btn_side_level_2).setOnClickListener(this);
        findViewById(R.id.btn_side_level_3).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.option_img).setOnClickListener(this);
        findViewById(R.id.user_icon).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_cancel:
                listener.btnCancel();
                break;
            case R.id.btn_side_level_1:
                listener.btnChild1();
                break;
            case R.id.btn_side_level_2:
                listener.btnChild2();
                break;
            case R.id.btn_side_level_3:
                listener.btnChild3();
                break;
            case R.id.share:
                listener.share();
                break;
            case R.id.option_img:
                listener.option();
                break;
            case R.id.user_icon:
                listener.my_page();
            default:
                break;
        }
    }
}


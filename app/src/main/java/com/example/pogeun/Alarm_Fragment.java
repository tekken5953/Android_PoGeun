package com.example.pogeun;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Objects;

public class Alarm_Fragment extends Fragment {
    TimePicker timePicker;
    Calendar calendar = Calendar.getInstance();
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DATE);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    TextView setting_time;
    ImageView delete;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timePicker = Objects.requireNonNull(getActivity()).findViewById(R.id.time_picker);
        setting_time = getActivity().findViewById(R.id.alarm_time);
        delete = getActivity().findViewById(R.id.alarm_delete);

        getActivity().findViewById(R.id.add_alarm).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //TODO
                toastMsg("알람을 설정하였습니다.");
                delete.setVisibility(View.VISIBLE);
                if (timePicker.getHour() < hour) {
                    setting_time.setText(month + "월 " + (day + 1) + "일  " + timePicker.getHour() + "시 " + timePicker.getMinute() + "분");
                } else if (timePicker.getHour() == hour) {
                    if (timePicker.getMinute() > min) {
                        setting_time.setText(month + "월 " + day + "일  " + timePicker.getHour() + "시 " + timePicker.getMinute() + "분");
                    } else {
                        setting_time.setText(month + "월 " + (day + 1) + "일  " + timePicker.getHour() + "시 " + timePicker.getMinute() + "분");
                    }
                } else {
                    setting_time.setText(month + "월 " + day + "일  " + timePicker.getHour() + "시 " + timePicker.getMinute() + "분");
                }
            }
        });
        getActivity().findViewById(R.id.current_time).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                toastMsg("시계를 현재시간으로 설정하였습니다.");
                timePicker.setHour(hour);
                timePicker.setMinute(min);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMsg("알람을 삭제했습니다.");
                delete.setVisibility(View.GONE);
                setting_time.setText("알람 없음");
            }
        });
    }

    ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.alarm_fragment, container, false);
        return viewGroup;
    }

    public void toastMsg(String s) {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) getActivity().findViewById(R.id.toast_layout));
        final TextView text = layout.findViewById(R.id.text);
        Toast toast = new Toast(getContext());
        text.setTextSize(13);
        text.setTextColor(Color.BLACK);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(layout);
        text.setText(s);
        toast.show();
    }
}

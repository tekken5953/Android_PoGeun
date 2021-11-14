package com.example.pogeun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class UserInfo_Fragment extends Fragment {

    ViewGroup viewGroup;
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE2 = 1;
    Bitmap resizedBmp, resizedBmp2;
    ImageView user_info_ring, user_info_back;
    String back_main = "user_info";
    TextView user_name_info, user_id_info, user_email_info;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("User Login");
    String path = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        path = getActivity().getIntent().getExtras().getString("user_id");
        user_info_ring = viewGroup.findViewById(R.id.user_icon_ring);
        user_info_back = viewGroup.findViewById(R.id.user_back_ring);
        user_name_info = getActivity().findViewById(R.id.user_name_info);
        user_id_info = getActivity().findViewById(R.id.user_id_info);
        user_email_info = getActivity().findViewById(R.id.user_email_info);
        user_id_info.setText(path);
        getUserValue("이메일", user_email_info);
        getUserValue("닉네임", user_name_info);
        final TextView text = getActivity().findViewById(R.id.back_main);
        final ImageButton edit_btn = getActivity().findViewById(R.id.edit_user_info);
        final Animation fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        final Animation fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        user_info_ring.setBackground(new ShapeDrawable(new OvalShape()));
        user_info_ring.setClipToOutline(true);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_user_info, null, false);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                final Button edit_finish_btn = view.findViewById(R.id.edit_finish_btn);
                final Button edit_cancel_btn = view.findViewById(R.id.edit_cancel_btn);
                final ImageView edit_name = view.findViewById(R.id.edit_name_btn);
                final ImageView edit_email = view.findViewById(R.id.edit_email_btn);
                final ImageView edit_profile = view.findViewById(R.id.edit_profile_btn);
                final ImageView edit_back = view.findViewById(R.id.edit_back_btn);
                final EditText edit_name_edit = view.findViewById(R.id.edit_name);
                final EditText edit_email_edit = view.findViewById(R.id.edit_email);
                final ImageView name_ok = view.findViewById(R.id.name_ok);
                final ImageView name_no = view.findViewById(R.id.name_no);
                final ImageView email_ok = view.findViewById(R.id.email_ok);
                final ImageView email_no = view.findViewById(R.id.email_no);
                final TextView edited_name = view.findViewById(R.id.edited_name);
                final TextView edited_email = view.findViewById(R.id.edited_email);
                final ImageView edited_profile = view.findViewById(R.id.edited_profile);
                final ImageView edited_back = view.findViewById(R.id.edited_back);
                getUserValue("닉네임", edited_name);
                getUserValue("이메일", edited_email);

                edited_name.setText(user_name_info.getText().toString());
                edited_email.setText(user_email_info.getText().toString());

                if (resizedBmp != null) {
                    edited_profile.setBackground(new ShapeDrawable(new OvalShape()));
                    edited_profile.setClipToOutline(true);
                    edited_profile.setImageBitmap(resizedBmp);
                }
                if (resizedBmp2 != null) {
                    edited_back.setBackground(new ShapeDrawable(new OvalShape()));
                    edited_back.setClipToOutline(true);
                    edited_back.setImageBitmap(resizedBmp2);
                }
                edit_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                edit_finish_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createThreadAndDialog();
                        alertDialog.dismiss();
                    }
                });
                fadeAnimaion("닉네임", edit_name, name_ok, name_no, edit_name_edit, edited_name, fade_in, fade_out, user_name_info);
                fadeAnimaion("이메일", edit_email, email_ok, email_no, edit_email_edit, edited_email, fade_in, fade_out, user_email_info);

                edit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType
                                (MediaStore.Images.Media.CONTENT_TYPE);
                        edited_profile.setColorFilter(null);
                        startActivityForResult(intent, REQUEST_CODE);
                        text.setText(back_main);
                        alertDialog.dismiss();
                    }
                });
                edit_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType
                                (MediaStore.Images.Media.CONTENT_TYPE);
                        edited_back.setColorFilter(null);
                        startActivityForResult(intent, REQUEST_CODE2);
                        text.setText(back_main);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.userinfo_fragment, container, false);
        return viewGroup;
    }

    public void fadeAnimaion(final String s, final ImageView edit, final ImageView ok, final ImageView no, final EditText editText, final TextView text,
                             final Animation in, final Animation out, final TextView textView) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getVisibility() == View.VISIBLE) {
                    edit.startAnimation(out);
                    text.startAnimation(out);
                    edit.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.startAnimation(in);
                            editText.setVisibility(View.VISIBLE);
                            ok.startAnimation(in);
                            ok.setVisibility(View.VISIBLE);
                            no.startAnimation(in);
                            no.setVisibility(View.VISIBLE);
                        }
                    }, 700);
                }

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        text.setText(editText.getText().toString());
                        editText.setVisibility(View.GONE);
                        no.setVisibility(View.GONE);
                        ok.setVisibility(View.GONE);
                        textView.setText(editText.getText().toString());
                        myRef.child(getActivity().getIntent().getExtras().getString("user_id"))
                                .child(s).setValue(editText.getText().toString());
                        //키보드 내리기
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.GONE);
                        no.setVisibility(View.GONE);
                        ok.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    assert data != null;
                    Uri img_uri = data.getData();
                    assert img_uri != null;
                    String img_string = img_uri.toString();
                    ImageView img1 = getActivity().findViewById(R.id.user_icon);
                    img1.setBackground(new ShapeDrawable(new OvalShape()));
                    img1.setClipToOutline(true);
                    InputStream in = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    //selected Image`s size is bigger than imginfo`s one
                    if (img.getWidth() > 200 || img.getHeight() > 200) {
                        resizedBmp = Bitmap.createScaledBitmap(img, (int) 200, (int) 200, true);
                    }
                    assert in != null;
                    in.close();
                    img1.setImageBitmap(resizedBmp);
                    user_info_ring.setImageBitmap(resizedBmp);
                    createThreadAndDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    assert data != null;
                    Uri img_uri = data.getData();
                    assert img_uri != null;
                    String img_string = img_uri.toString();
                    ImageView img3 = getActivity().findViewById(R.id.user_back);
                    img3.setBackground(new ShapeDrawable(new OvalShape()));
                    img3.setClipToOutline(true);
                    InputStream in = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    //selected Image`s size is bigger than imginfo`s one
                    resizedBmp2 = Bitmap.createScaledBitmap(img, (int) 500, (int) 300, true);
                    assert in != null;
                    in.close();
                    img3.setImageBitmap(resizedBmp2);
                    user_info_back.setImageBitmap(resizedBmp2);
                    createThreadAndDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ProgressDialog loagind_Dialog; // Loading Dialog

    void createThreadAndDialog() {
        /* ProgressDialog */
        loagind_Dialog = ProgressDialog.show(getContext(), null,
                "변경사항을 저장 중 입니다..", true, false);


        Thread thread = new Thread(new Runnable() {
            public void run() {
                // 시간걸리는 처리
                handler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // View갱신
                    toastMsg("계정정보 변경 완료");
                    loagind_Dialog.dismiss();
                }
            }, 1500);
        }
    };

    public void toastMsg(String s) {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) getActivity().findViewById(R.id.toast_layout));
        final TextView text = layout.findViewById(R.id.text);
        Toast toast = new Toast(getContext());
        text.setTextSize(13);
        text.setTextColor(Color.BLACK);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 600);
        toast.setView(layout);
        text.setText(s);
        toast.show();
    }

    public void getUserValue(final String s, final TextView textView) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    textView.setText(snapshot.child(path).child(s).getValue().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
package com.togastudios.android.toga;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewUserNameFragment extends Fragment {

    private nameContinueButton mInterface;
    private LinearLayout mLinearLayout;
    private Button mContinueButton;
    private EditText mEmailEditText;
    private String mName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mName = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (nameContinueButton)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_user_name, container, false);

        mLinearLayout = (LinearLayout)v.findViewById(R.id.new_user_name_layout);

        mEmailEditText = (EditText)v.findViewById(R.id.new_user_name_edittext);
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContinueButton = (Button)v.findViewById(R.id.new_user_name_continue);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyName(mName)) {
                    mInterface.nameButtonClicked(mName);
                }
            }
        });

        return v;
    }

    private boolean verifyName(String name) {
        if (name == null || name.length() == 0) {
            Toast.makeText(getActivity(), "Please enter your name", Toast.LENGTH_LONG).show();
        }
        else if (name.length() > 32) {
            Toast.makeText(getActivity(), "Names must be under 32 characters", Toast.LENGTH_LONG).show();
        }
        else {
            return true;
        }

        return false;
    }

    public interface nameContinueButton {
        public void nameButtonClicked(String name);
    }

}

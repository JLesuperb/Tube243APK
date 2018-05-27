package com.tube243.tube243.ui.dialogs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.tube243.tube243.R;

import java.io.IOException;

/**
 * Created by JonathanLesuperb on 2018/04/27.
 */

public class ProfileDialog extends BaseDialog implements View.OnClickListener
{

    public interface ProfileEditor
    {
        void onEdit();
    }

    private ProfileEditor profileEditor;

    public void setProfileEditor(ProfileEditor profileEditor)
    {
        this.profileEditor = profileEditor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dialog_profile, container, false);
        getDialog().setTitle(getString(R.string.dialog_profile_title));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        AppCompatEditText first_name_edit = view.findViewById(R.id.first_name_edit);
        first_name_edit.setText(("firstName"));
        AppCompatEditText last_name_edit = view.findViewById(R.id.last_name_edit);
        last_name_edit.setText(("lastName"));
        AppCompatEditText phone_number_edit = view.findViewById(R.id.phone_number_edit);
        phone_number_edit.setText(("phoneNumber"));
        AppCompatButton editBtn = view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(this);
        AppCompatButton cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);

        AppCompatImageView image_profile = view.findViewById(R.id.image_profile);
        {
            try
            {
                Drawable drawable = Drawable.createFromStream(getActivity().getAssets().open("images/user_default.png"),null);
                image_profile.setImageDrawable(drawable);
            }
            catch (IOException ignored){}
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.editBtn:
                if(profileEditor!=null)
                    profileEditor.onEdit();
                dismiss();
                break;
            case R.id.cancelBtn:
                dismiss();
                break;
        }
    }

    @Override
    public int getTheme()
    {
        return super.getTheme();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if(window!=null)
        {
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }
}

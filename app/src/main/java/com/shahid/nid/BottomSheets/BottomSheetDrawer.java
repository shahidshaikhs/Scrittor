package com.shahid.nid.BottomSheets;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shahid.nid.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class BottomSheetDrawer extends BottomSheetDialogFragment {

    private BottomSheetDrawer.BottomSheetListener bottomSheetListener;
    private TextView userName;
    private LinearLayout rootLayoutDrawer;
    private SharedPreferences.Editor editorTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_drawer, container, false);

        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        LinearLayout userNameClickEvent = view.findViewById(R.id.userNameClick);
        LinearLayout rootLayout = view.findViewById(R.id.rootLayout);
        RelativeLayout settingsLayout = view.findViewById(R.id.settings);
        RelativeLayout rateLayout = view.findViewById(R.id.nav_rate);
        RelativeLayout aboutLayout = view.findViewById(R.id.nav_about);
        RelativeLayout donateLayout = view.findViewById(R.id.nav_donate);
        RelativeLayout translateLayout = view.findViewById(R.id.nav_translate);
        RelativeLayout themeLayout = view.findViewById(R.id.themeToggle);
        RelativeLayout accentLayout = view.findViewById(R.id.accentToggle);
        RelativeLayout displayTint = view.findViewById(R.id.displayTint);

        userName = view.findViewById(R.id.userName);
        rootLayoutDrawer = view.findViewById(R.id.rootLayout);

        SharedPreferences prefUserImage = getActivity().getSharedPreferences(getString(R.string.MY_PREFS_USER_IMAGE), MODE_PRIVATE);
        String image = prefUserImage.getString("user_image", "User Image");//"No name defined" is the default value.
        setImage(image, profileImage);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.MY_PREFS_USER_NAME), MODE_PRIVATE);
        String name = prefs.getString("user_name", "User Name");//"No name defined" is the default value.
        userName.setText(name);

        SharedPreferences prefsTheme = getActivity().getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);
        String theme = prefsTheme.getString("theme", "not_defined");
        if(theme.equals("dark")){
            rootLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
            displayTint.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.displayTint1), PorterDuff.Mode.SRC_OVER);
        }else if(theme.equals("light")){
            rootLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary2), PorterDuff.Mode.SRC_OVER);
            displayTint.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.displayTint2), PorterDuff.Mode.SRC_OVER);
        }else if(theme.equals("amoled")){
            rootLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.drawerColor), PorterDuff.Mode.SRC_OVER);
            displayTint.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.displayTint3), PorterDuff.Mode.SRC_OVER);
        }else{
            rootLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_OVER);
            displayTint.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.displayTint1), PorterDuff.Mode.SRC_OVER);
        }

        userNameClickEvent.setOnClickListener(view114 -> getUserName(userName.getText().toString()));

        themeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create custom dialog object
                Dialog themeDialog = new Dialog(getContext(), R.style.Theme_Dialog);
                // Include dialog.xml file
                themeDialog.setContentView(R.layout.dialog_theme_selector);
                themeDialog.setTitle(null);

                TextView dark, light, amoled;

                dark = themeDialog.findViewById(R.id.dark);
                light = themeDialog.findViewById(R.id.light);
                amoled = themeDialog.findViewById(R.id.amoled);


                SharedPreferences prefsTheme;
                String theme;
                prefsTheme = getActivity().getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);

                theme = prefsTheme.getString("theme", "not_defined");
                if (theme.equals("dark")) {
                    dark.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else if (theme.equals("light")) {
                    light.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else if (theme.equals("amoled")) {
                    amoled.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else {
                    dark.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }

                themeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                themeDialog.show();

                dark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editorTheme = getActivity().getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE).edit();
                        editorTheme.putString("theme", "dark");
                        editorTheme.apply();
                        getActivity().getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
                        rootLayoutDrawer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                        getActivity().recreate();
                        Toast.makeText(getContext(), "Theme has been changed", Toast.LENGTH_SHORT).show();
                    }
                });

                light.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editorTheme = getActivity().getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE).edit();
                        editorTheme.putString("theme", "light");
                        editorTheme.apply();
                        getActivity().getTheme().applyStyle(R.style.OverlayPrimaryColorLight, true);
                        rootLayoutDrawer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary2));

                        getActivity().recreate();
                        Toast.makeText(getContext(), "Theme has been changed", Toast.LENGTH_SHORT).show();
                    }
                });

                amoled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editorTheme = getActivity().getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE).edit();
                        editorTheme.putString("theme", "amoled");
                        editorTheme.apply();
                        getActivity().getTheme().applyStyle(R.style.OverlayPrimaryColorAmoled, true);
                        rootLayoutDrawer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary3));

                        getActivity().recreate();
                        Toast.makeText(getContext(), "Theme has been changed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("settings");
            }
        });

        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("rate");
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("about");
            }
        });

        donateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("donate");
            }
        });

        translateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("translate");
            }
        });

        accentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClickDrawer("accent");
            }
        });

        profileImage.setOnClickListener(view113 -> {

            Dialog imageDialog = new Dialog(getContext(), R.style.Theme_Dialog);
            // Include dialog.xml file
            imageDialog.setContentView(R.layout.custom_image_picker);
            imageDialog.setTitle(null);

            imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            imageDialog.show();

            final View face1 = imageDialog.findViewById(R.id.face1);
            View face2 = imageDialog.findViewById(R.id.face2);
            View face3 = imageDialog.findViewById(R.id.face3);
            View face4 = imageDialog.findViewById(R.id.face4);
            View face5 = imageDialog.findViewById(R.id.face5);
            View face6 = imageDialog.findViewById(R.id.face6);
            View face7 = imageDialog.findViewById(R.id.face7);
            View face8 = imageDialog.findViewById(R.id.face8);
            View face9 = imageDialog.findViewById(R.id.face9);
            View face10 = imageDialog.findViewById(R.id.face10);
            View face11 = imageDialog.findViewById(R.id.face11);
            View face12 = imageDialog.findViewById(R.id.face12);

            face1.setOnClickListener(view112 -> {
                changeUserImage(view112.getContext(), "face1", profileImage);
                imageDialog.dismiss();
            });

            face2.setOnClickListener(view111 -> {
                changeUserImage(view111.getContext(), "face2", profileImage);
                imageDialog.dismiss();
            });

            face3.setOnClickListener(view110 -> {
                changeUserImage(view110.getContext(), "face3", profileImage);
                imageDialog.dismiss();
            });

            face4.setOnClickListener(view19 -> {
                changeUserImage(view113.getContext(), "face4", profileImage);
                imageDialog.dismiss();
            });

            face5.setOnClickListener(view18 -> {
                changeUserImage(view113.getContext(), "face5", profileImage);
                imageDialog.dismiss();
            });

            face6.setOnClickListener(view17 -> {
                changeUserImage(view113.getContext(), "face6", profileImage);
                imageDialog.dismiss();
            });

            face7.setOnClickListener(view16 -> {
                changeUserImage(view113.getContext(), "face7", profileImage);
                imageDialog.dismiss();
            });

            face8.setOnClickListener(view15 -> {
                changeUserImage(view113.getContext(), "face8", profileImage);
                imageDialog.dismiss();
            });


            face9.setOnClickListener(view14 -> {
                changeUserImage(view113.getContext(), "face9", profileImage);
                imageDialog.dismiss();
            });


            face10.setOnClickListener(view13 -> {
                changeUserImage(view113.getContext(), "face10", profileImage);
                imageDialog.dismiss();
            });


            face11.setOnClickListener(view12 -> {
                changeUserImage(view113.getContext(), "face11", profileImage);
                imageDialog.dismiss();
            });


            face12.setOnClickListener(view1 -> {
                changeUserImage(view113.getContext(), "face12", profileImage);
                imageDialog.dismiss();
            });
        });

        return view;
    }


    public interface BottomSheetListener{
        void onButtonClickDrawer(String buttonClick);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            bottomSheetListener = (BottomSheetDrawer.BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " " + "must implement BottomSheetListener");
        }
    }

    public void setImage(String value, CircleImageView imageView) {
        switch (value) {
            case "face1": {
                imageView.setImageResource(R.drawable.face1);
                break;
            }
            case "face2":
                imageView.setImageResource(R.drawable.face2);
                break;
            case "face3": {
                imageView.setImageResource(R.drawable.face3);
                break;
            }
            case "face4": {
                imageView.setImageResource(R.drawable.face4);
                break;
            }
            case "face5": {
                imageView.setImageResource(R.drawable.face5);
                break;
            }
            case "face7": {
                imageView.setImageResource(R.drawable.face7);
                break;
            }
            case "face8": {
                imageView.setImageResource(R.drawable.face8);
                break;
            }
            case "face9": {
                imageView.setImageResource(R.drawable.face9);
                break;
            }
            case "face10": {
                imageView.setImageResource(R.drawable.face10);
                break;
            }
            case "face11": {
                imageView.setImageResource(R.drawable.face11);
                break;
            }
            case "face12": {
                imageView.setImageResource(R.drawable.face12);
                break;
            }

            default: {
                imageView.setImageResource(R.drawable.face6);
                break;
            }
        }
    }

    public void changeUserImage(Context context, String value, CircleImageView circleImageView) {

        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.MY_PREFS_USER_IMAGE), MODE_PRIVATE).edit();

        switch (value) {
            case "face1": {
                circleImageView.setImageResource(R.drawable.face1);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
            case "face2":
                circleImageView.setImageResource(R.drawable.face2);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            case "face3": {
                circleImageView.setImageResource(R.drawable.face3);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
            case "face4": {
                circleImageView.setImageResource(R.drawable.face4);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
            case "face5": {
                circleImageView.setImageResource(R.drawable.face5);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
            case "face7": {
                circleImageView.setImageResource(R.drawable.face7);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
            case "face8": {
                circleImageView.setImageResource(R.drawable.face8);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }

            case "face9": {
                circleImageView.setImageResource(R.drawable.face9);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }

            case "face10": {
                circleImageView.setImageResource(R.drawable.face10);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }

            case "face11": {
                circleImageView.setImageResource(R.drawable.face11);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }

            case "face12": {
                circleImageView.setImageResource(R.drawable.face12);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }

            default: {
                circleImageView.setImageResource(R.drawable.face6);
                editor.putString("user_image", String.valueOf(value));
                editor.apply();
                break;
            }
        }
    }

    public void getUserName(String present) {
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.MY_PREFS_USER_NAME), MODE_PRIVATE).edit();

        Dialog userNameDialog = new Dialog(getContext(),  R.style.Theme_Dialog);
        // Include dialog.xml file
        userNameDialog.setContentView(R.layout.dialog_username);
        userNameDialog.setTitle(null);

        TextView agree, disagree;
        EditText userNameText;
        agree = userNameDialog.findViewById(R.id.agree);
        disagree = userNameDialog.findViewById(R.id.disagree);
        userNameText = userNameDialog.findViewById(R.id.userNameText);

        userNameText.setText(present);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user_name", String.valueOf(userNameText.getText().toString()).replaceAll("\\s+$", ""));
                editor.apply();
                userName.setText(String.valueOf(userNameText.getText().toString()).replaceAll("\\s+$", ""));
                userNameDialog.dismiss();
            }
        });

        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameDialog.dismiss();
            }
        });

        userNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userNameDialog.show();
    }
}

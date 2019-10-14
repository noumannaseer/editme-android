package com.example.editme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import com.example.editme.R;
import com.example.editme.fragments.IntroFragment;
import com.example.editme.utils.AndroidUtil;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroSliderActivity
        extends AppIntro
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState);
        setSlider();
        setBarColor(AndroidUtil.getColor(R.color.toolbar_text_color_transparent));
        setSeparatorColor(Color.BLACK);
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setDoneText(AndroidUtil.getString(R.string.done));
    }

    private void setSlider()
    {

        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_1, 0));
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_2, 1));
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_3, 2));



//         setBarColor(AndroidUtil.getColor(R.color.toolbar_text_color_transparent));
        setIndicatorColor(AndroidUtil.getColor(R.color.light_grey),
                          AndroidUtil.getColor(R.color.toolbar_text_color));
        setNextArrowColor(AndroidUtil.getColor(R.color.toolbar_text_color_transparent));
        setColorDoneText(AndroidUtil.getColor(R.color.white));

    }

    //******************************************************************
    @Override
    public void onSkipPressed(Fragment currentFragment)
    //******************************************************************
    {
        super.onSkipPressed(currentFragment);
        if (currentFragment instanceof IntroFragment)
        {
            IntroFragment aboutFragment = (IntroFragment)currentFragment;
            for (int i = aboutFragment.getPosition(); i < 4; i++)
            {
                nextButton.callOnClick();
            }
        }

    }
    //******************************************************************
    @Override
    public void onDonePressed(Fragment currentFragment)
    //******************************************************************
    {
        super.onDonePressed(currentFragment);
        finish();
    }

    //******************************************************************
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment)
    //******************************************************************
    {
        super.onSlideChanged(oldFragment, newFragment);
    }

}

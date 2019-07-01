package com.example.editme.utils;

import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextUtils;

import com.example.editme.databinding.LoginDialogBinding;

public class LoginInputHandler
{
    LoginDialogBinding binding;

    public void setBinding(LoginDialogBinding binding)
    {
        this.binding = binding;
    }

    public void passwordValidator(Editable editable)
    {
        if (binding.password == null) return;
        int minimumLength = 8;
        if (!TextUtils.isEmpty(editable.toString()) && editable.length() < minimumLength)
        {
            binding.password.setError("Password must be minimum " + minimumLength + " length");
        }
        else
        {
            binding.password.setError(null);
        }
    }
}

package com.careons.app.Shared.Components.CustomFields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.careons.app.Shared.Utils.Utils;

public class NormalEditText extends EditText {

    public NormalEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NormalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(Utils.getTypeFaceNormal(getContext()));
        }
    }
}


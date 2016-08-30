package com.careons.app.Shared.Components.CustomFields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.careons.app.Shared.Utils.Utils;

public class NormalTextView extends TextView {

    public NormalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(Utils.getTypeFaceNormal(getContext()));
        }
    }
}


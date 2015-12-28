package com.mmga.mmgahottweet.ui.transformer;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mmga.mmgahottweet.R;

public class InsetViewTransformer extends BaseViewTransformer {

    @Override
    public void transformView(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view) {

        float fraction = Math.max((translation - peekedTranslation) / (maxTranslation - peekedTranslation), 0);
        ImageView header = (ImageView) parent.findViewById(R.id.header);
        LinearLayout resumeContent = (LinearLayout) parent.findViewById(R.id.resume_content);
        TextView callMe = (TextView) parent.findViewById(R.id.my_connection);
        TextView followMe = (TextView) parent.findViewById(R.id.my_github);

        header.setRotation(fraction * 180);
        int distance = (int) - view.getResources().getDimension(R.dimen.sheet_parallax_vertical);
        resumeContent.setTranslationY(fraction * distance);
        callMe.setTranslationY(fraction * distance);
        followMe.setTranslationY(fraction * distance * 2);

//        if (translation == 0 || translation == parent.getHeight()) {
//            parent.setBackgroundColor(0);
//            ensureLayout(view, View.LAYER_TYPE_NONE);
//        } else {
//            parent.setBackgroundColor(view.getResources().getColor(R.color.colorBackground));
//            ensureLayout(view, View.LAYER_TYPE_HARDWARE);
//        }


    }

    private void ensureLayout(View view, int layerType) {
        if (view.getLayerType() != layerType) {
            view.setLayerType(layerType, null);
        }
    }
}

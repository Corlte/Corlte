package com.lzyp.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.glide.ImgLoader;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.mall.R;

/**
 * 资质证明
 */
public class ShopCertActivity extends AbsActivity {

    public static void forward(Context context, String certText, String certImgUrl) {
        Intent intent = new Intent(context, ShopCertActivity.class);
        intent.putExtra(Constants.MALL_CERT_TEXT, certText);
        intent.putExtra(Constants.MALL_CERT_IMG, certImgUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cert;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_141));
        Intent intent = getIntent();
        String text = intent.getStringExtra(Constants.MALL_CERT_TEXT);
        String imgUrl = intent.getStringExtra(Constants.MALL_CERT_IMG);
        TextView textView = findViewById(R.id.text);
        textView.setText(text);
        ImageView imageView = findViewById(R.id.img);
        ImgLoader.display(mContext, imgUrl, imageView);
    }
}

package com.vns.webstore.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vns.webstore.ui.activity.OpenArticleActivity;
import com.webstore.webstore.R;

/**
 * Created by root on 18/02/2017.
 */

public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView ownerAvatar;
    TextView ownerName;
    private TextView type;
    ImageView articleImage;
    TextView articleTitle;
    TextView articleDesc;
    private String url;
    private String articleHtml;
    private int index;
    public ArticleViewHolder(View view, boolean wideImage) {
        super(view);
        ownerAvatar = (ImageView) view.findViewById(R.id.owner_avatar);
        ownerName = (TextView) view.findViewById(R.id.owner_name);
        type = (TextView) view.findViewById(R.id.type);
        articleImage = (ImageView) view.findViewById(R.id.article_image);
        articleTitle = (TextView) view.findViewById(R.id.article_title);
        articleImage.setOnClickListener(this);
        articleTitle.setOnClickListener(this);
        if(wideImage) {
            Display display = ((Activity) view.getContext()).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            float scWidth = outMetrics.widthPixels;
            articleImage.getLayoutParams().width = (int) scWidth;
            articleImage.getLayoutParams().height = (int) (scWidth * 0.6f);
            articleDesc = (TextView) view.findViewById(R.id.article_desc);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), OpenArticleActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("article",true);
        intent.putExtra("articleHtml",getArticleHtml());
        view.getContext().startActivity(intent);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArticleHtml() {
        return articleHtml;
    }

    public void setArticleHtml(String articleHtml) {
        this.articleHtml = articleHtml;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TextView getType() {
        return type;
    }

    public void setType(TextView type) {
        this.type = type;
    }
}

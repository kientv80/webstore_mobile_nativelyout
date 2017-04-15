package com.vns.webstore.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vns.webstore.middleware.entity.CateItem;
import com.webstore.webstore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 18/01/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private List<CateItem> cateItems;
    private Context context;
    ClickListener clickListener;
    public CategoryAdapter(Context context, List<CateItem> cateItems, ClickListener clickListener) {
        this.context = context;
        this.setCateItems(cateItems);
        this.clickListener = clickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_layout, viewGroup, false);
        return new CategoryViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder cateViewHolder,final int i) {
        Picasso.with(context).load(getCateItems().get(i).getIcon().trim()).placeholder(R.drawable.photos_icon).resize(60, 60).into(cateViewHolder.cateImage);
        cateViewHolder.cateLabel.setText(getCateItems().get(i).getLabel());
        cateViewHolder.setUrl(getCateItems().get(i).getUrl());
        cateViewHolder.cateName=getCateItems().get(i).getName();
        cateViewHolder.openLink = getCateItems().get(i).isOpenLink();

    }

    @Override
    public int getItemCount() {
        return getCateItems().size();
    }


    public List<CateItem> getCateItems() {
        return cateItems;
    }

    public void setCateItems(List<CateItem> cateItems) {
        this.cateItems = cateItems;
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView cateImage;
        TextView cateLabel;
        String cateName;
        String url;
        boolean openLink;
        ClickListener clickListener;

        public CategoryViewHolder(View view, ClickListener clickListener) {
            super(view);
            cateImage = (ImageView) view.findViewById(R.id.category_image);
            cateLabel = (TextView) view.findViewById(R.id.category_label);
            cateImage.setOnClickListener(this);
            cateLabel.setOnClickListener(this);
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            JSONObject data = new JSONObject();
            try {
                data.put("cateName",cateName);
                data.put("cateLabel",cateLabel.getText());
                data.put("openLink",openLink);
                data.put("url",url);
                clickListener.onClick(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}

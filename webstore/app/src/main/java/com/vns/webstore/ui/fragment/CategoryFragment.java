package com.vns.webstore.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vns.webstore.middleware.entity.CateItem;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.ui.adapter.CategoryAdapter;
import com.vns.webstore.ui.adapter.ClickListener;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 08/02/2017.
 */

public class CategoryFragment extends Fragment implements HttpRequestListener{
    RecyclerView categoriesListingView;
    private ClickListener onClickListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.recyclerview_listing,container,false);
        categoriesListingView = (RecyclerView)viewGroup.findViewById(R.id.recyclerview);
        categoriesListingView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext());
        categoriesListingView.setLayoutManager(layoutManager);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) categoriesListingView.getLayoutManager();
        try {
            loadCategories(container);
        } catch (JSONException e) {
            e.printStackTrace();
            ActivityLogService.getInstance().logUserActivity(new UserActivity(getClass().getSimpleName()+".loadCategories","ERROR",e.getMessage()));
        }
        return viewGroup;
    }

    private void loadCategories(ViewGroup container) throws JSONException {
        String type = "";
        if(worldNews)
            type = "worldnews";

        String categories = LocalStorageHelper.getFromFile("categories"+type);
        if(categories != null && !categories.isEmpty()){
            JSONObject cates = new JSONObject(categories);
            if(cates.has("data")) {
                JSONObject cateData = new JSONObject(cates.getString("data"));
                if(cateData.has("categories"))
                    renderCategories(cateData.getString("categories"));
                if (((System.currentTimeMillis() - cates.getLong("cachedTime")) > 30 * 60 * 1000)) {
                    HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/category?type=" + type, null, "categories"+type);
                }
            }
        } else {
            HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/category?type="+ type, this,"categories"+type);
        }

    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        System.out.println("CategoryFragment onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        categoriesListingView = null;
    }

    @Override
    public void onRecievedData(Object data, ErrorCode errorCode) {
        if (data != null && !data.toString().isEmpty()) {
            try{
                renderCategories(new JSONObject(data.toString()).getString("categories"));
            }catch (Exception ex){
                ex.printStackTrace();
                ActivityLogService.getInstance().logUserActivity(new UserActivity(getClass().getSimpleName()+".onRecievedData","ERROR",ex.getMessage()));
            }
        }
    }

    private void renderCategories(Object data) throws JSONException{
        final List<CateItem> categories = JSONHelper.toObjects(data.toString(), CateItem.class);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    RecyclerView.Adapter adapter = categoriesListingView.getAdapter();
                    if(adapter == null) {
                        adapter = new CategoryAdapter(getContext(), categories, onClickListener);
                        categoriesListingView.setAdapter(adapter);
                    }else{
                        List<CateItem> cateItems = ((CategoryAdapter) adapter).getCateItems();
                        int length = cateItems.size();
                        cateItems.clear();
                        cateItems.addAll(categories);
                        categoriesListingView.notifyAll();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ActivityLogService.getInstance().logUserActivity(new UserActivity(getClass().getSimpleName()+".renderCategories","ERROR",ex.getMessage()));
                }
            }
        });

    }

    String title;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private boolean worldNews;

    public void setWorldNews(boolean worldNews) {
        this.worldNews = worldNews;
    }
}

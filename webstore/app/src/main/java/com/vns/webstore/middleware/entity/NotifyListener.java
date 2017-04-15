package com.vns.webstore.middleware.entity;

import com.vns.webstore.middleware.entity.NotifyInfo;

import java.util.List;

/**
 * Created by LAP10572-local on 9/11/2016.
 */
public interface NotifyListener {
    public void haveNewUpdate(List<NotifyInfo> notifyInfoList, int newNotifyCount);
}

package com.lbs.cheng.lbscampus.util.Permission;

import java.util.List;

/**
 * Created by LT on 2019/3/16.
 */


public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermission);

    void onShouldShowRationale(List<String> deniedPermission);
}

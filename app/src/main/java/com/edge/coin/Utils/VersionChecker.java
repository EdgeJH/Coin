package com.edge.coin.Utils;

import android.content.pm.PackageManager;
import android.os.AsyncTask;

public class VersionChecker extends AsyncTask<String, Void, Boolean> {

    PackageManager packageManager;
    VersionCallback versionCallback;


    public VersionChecker(PackageManager packageManager, VersionCallback versionCallback) {
        this.packageManager = packageManager;
        this.versionCallback = versionCallback;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        boolean update;
        String store_version = MarketVersionChecker.getMarketVersion(params[0]);
        try {
            String device_version = packageManager.getPackageInfo(params[0], 0).versionName;
            if (store_version != null) {
                if (store_version.compareTo(device_version) > 0) {
                    // 업데이트 필요
                    update = true;
                } else {
                    update = false;
                }
            } else {
                update = false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            update = false;
        }
        return update;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        versionCallback.isUpdate(aVoid);
    }
}
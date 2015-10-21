package com.groep11.eva_app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EvaSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static EvaSyncAdapter sEvaSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sEvaSyncAdapter == null) {
                sEvaSyncAdapter = new EvaSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sEvaSyncAdapter.getSyncAdapterBinder();
    }
}

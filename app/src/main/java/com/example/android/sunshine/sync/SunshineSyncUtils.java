package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sagar on 25/6/17.
 */

public class SunshineSyncUtils {

    public static void startImmediateSync(final Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}

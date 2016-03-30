package com.alexqueudotrafel.retrofitrealmsample.util;

import android.os.CountDownTimer;

import java.util.concurrent.Callable;

/**
 * Created by alexqueudotrafel on 24/03/16.
 */
public class Utils {

    public static CountDownTimer setTimeout(final Callable func, final long timeout) {
        return new CountDownTimer(timeout, 0) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                try {
                    func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

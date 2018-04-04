package com.fingerth.barcodelib.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ======================================================
 * Created by Administrator acg_fingerth on 2018/4/4.
 * <p/>
 * 版权所有，违者必究！
 * <详情描述/>
 */
public class BarcodeUtils {
    private static BarcodeUtils instances;

    private BarcodeUtils() {
    }

    public static BarcodeUtils getInstances() {
        if (instances == null) {
            instances = new BarcodeUtils();
        }
        return instances;
    }




}

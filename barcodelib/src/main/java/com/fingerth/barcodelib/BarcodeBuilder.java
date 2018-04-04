package com.fingerth.barcodelib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

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
public class BarcodeBuilder {

    private static BarcodeBuilder builder;

    private BarcodeBuilder() {
    }

    public static BarcodeBuilder builder() {
        if (builder == null) {
            builder = new BarcodeBuilder();
        }
        return builder;
    }


    /**
     * 在子线程 ， 生成条形码
     *
     * @param context       context
     * @param qrContent     需要生成的内容
     * @param desiredWidth  生成条形码的宽带
     * @param desiredHeight 生成条形码的高度
     * @param displayCode   是否在条形码下方显示内容
     * @param callback      回调得到条形码的bitmap（注意：在子线程中）
     */
    public void createBarcodeInThreadPool(final Context context, final String qrContent, final int desiredWidth, final int desiredHeight, final boolean displayCode, final BarcodeCallback callback) {

        getThreadPool(new NewThreadRunnable() {
            @Override
            public void run() {
                Bitmap resultBitmap = createBarcode(context, qrContent, desiredWidth, desiredHeight, displayCode);
                if (callback != null) {
                    callback.completion(resultBitmap);
                }
            }
        });
    }

    /**
     * 在当前线程 ， 生成条形码
     *
     * @param context       context
     * @param qrContent     需要生成的内容
     * @param desiredWidth  生成条形码的宽带
     * @param desiredHeight 生成条形码的高度
     * @param displayCode   是否在条形码下方显示内容
     * @return Bitmap
     */
    public Bitmap createBarcodeInCurrentThread(Context context, String qrContent, int desiredWidth, int desiredHeight, boolean displayCode) {
        return createBarcode(context, qrContent, desiredWidth, desiredHeight, displayCode);

    }


    private void getThreadPool(final NewThreadRunnable newThreadRunnable) {
        ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                new Thread() {
                    @Override
                    public void run() {
                        newThreadRunnable.run();
                    }
                }.start();
            }
        });
        //执行任务
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                newThreadRunnable.run();
            }
        });
    }

    public interface NewThreadRunnable {
        void run();
    }

    public interface BarcodeCallback {
        void completion(Bitmap resultBitmap);
    }


    /**
     * 生成条形码
     */
    private Bitmap createBarcode(Context context, String contents, int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap resultBitmap;
        /**
         * 图片两端所保留的空白的宽度
         */
        int marginW = 30;
        /**
         * 条形码的编码类型
         */
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
            Bitmap codeBitmap = createCodeBitmap(contents, desiredWidth + 2 * marginW, desiredHeight, context);
            resultBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
        } else {
            resultBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
        }

        return resultBitmap;
    }

    /**
     * 生成条形码的Bitmap
     *
     * @param contents 需要生成的内容
     * @param format   编码格式
     */
    private Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成显示编码的Bitmap
     */
    private Bitmap createCodeBitmap(String contents, int width, int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        return tv.getDrawingCache();
    }

    /**
     * 将两个Bitmap合并成一个
     */
    private Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 30;
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth() + second.getWidth() + marginW, first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }
}

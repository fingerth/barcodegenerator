package com.fingerth.barcodegenerator;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingerth.barcodelib.BarcodeBuilder;

public class MainActivity extends AppCompatActivity {

    private ImageView iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv1 = findViewById(R.id.iv1);
        TextView tv1 = findViewById(R.id.tv1);
        iv2 = findViewById(R.id.iv2);
        TextView tv2 = findViewById(R.id.tv2);

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
        Bitmap bitmap1 = BarcodeBuilder.builder().createBarcodeInCurrentThread(this, "A0002009", 600, 220, false);
        iv1.setImageBitmap(bitmap1);//当前是UI线程


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
        BarcodeBuilder.builder().createBarcodeInThreadPool(this, "A0002009", 600, 220, true, new BarcodeBuilder.BarcodeCallback() {
            @Override
            public void completion(final Bitmap resultBitmap) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv2.setImageBitmap(resultBitmap);
                    }
                });

            }
        });
    }
}

# barcodegenerator

### 条形码生成器工具类  Barcode generator

> 需要依赖的zxing

> 就一个工具类，代码直接拷出来用。


- 使用代码

```
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
```
![image](https://github.com/fingerth/barcodegenerator/blob/master/pic/Barcode.png)



### end


### Proguard Decompile

- Rename apk file to zip
- Then unzip this file
- Use [dex2jar] (https://code.google.com/p/dex2jar/) to convert classes.dex to checkout.jar


    ./d2j-dex2jar.sh ~/path/of/classes.dex -o ~/path/destination/checkout.jar

- Open checkout.jar with [JD-GUI] (http://jd.benow.ca/)

### Unzip apk

Use [apktool] (https://code.google.com/p/android-apktool/) to unpack apk file

    apktool d checkout-debug.apk

### Copy and Install proguard apk to device

    adb push -p checkout-debug.apk /data/local/tmp/com.receiptofi.checkout
    adb shell pm install -r "/data/local/tmp/com.receiptofi.checkout"


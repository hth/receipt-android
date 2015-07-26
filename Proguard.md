
## Proguard Decompile And Signing APK

### Proguard Decompile

- Rename apk file to zip
- Then unzip this file
- Use [dex2jar] (https://code.google.com/p/dex2jar/) to convert classes.dex to receipts.jar


    ./d2j-dex2jar.sh ~/path/of/classes.dex -o ~/path/destination/receipts.jar

- Open receipts.jar with [JD-GUI] (http://jd.benow.ca/)

### Unzip apk

Use [apktool] (https://code.google.com/p/android-apktool/) to unpack apk file

    apktool d receipts-debug.apk

### Copy and Install proguard apk to device

    adb push -p receipts-debug.apk /data/local/tmp/com.receiptofi.receipts &&
    adb shell pm install -r "/data/local/tmp/com.receiptofi.receipts"


### Signing APK (Do not use this, instead use ApkSigning.md)

    keytool -genkey -v -keystore checkout-staging.keystore -alias staging.checkout -keyalg RSA -keysize 2048 -validity 90

    Enter keystore password:
    Re-enter new password:
    What is your first and last name?
      [Unknown]:  receiptofi.com
    What is the name of your organizational unit?
      [Unknown]:  Checkout
    What is the name of your organization?
      [Unknown]:  Receiptofi Inc
    What is the name of your City or Locality?
      [Unknown]:  Sunnyvale
    What is the name of your State or Province?
      [Unknown]:  California
    What is the two-letter country code for this unit?
      [Unknown]:  US
    Is CN=receiptofi.com, OU=Checkout, O=Receiptofi Inc, L=Sunnyvale, ST=California, C=US correct?
      [no]:  yes

    Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 90 days
    	for: CN=receiptofi.com, OU=Checkout, O=Receiptofi Inc, L=Sunnyvale, ST=California, C=US
    Enter key password for <checkout.staging>
    	(RETURN if same as keystore password):
    Re-enter new password:
    [Storing checkout-staging.keystore]

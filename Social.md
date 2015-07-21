#Social Setup

## Facebook

Register Key Hashes for the app with facebook. And enter "Enter keystore password:"
that was used when generating keystore thats being passed.

### Find alias
    keytool -list -v -keystore checkout-release.keystore

### Key Hashes for Android
    keytool -exportcert -alias release.checkout -keystore checkout-release.keystore | openssl sha1 -binary | openssl base64




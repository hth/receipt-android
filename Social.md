#Social Setup

## Facebook
---

Register Key Hashes for the app with facebook. And enter "Enter keystore password:"
that was used when generating keystore thats being passed.

### Find alias
    keytool -list -v -keystore checkout-release.keystore

### Key Hashes for Android
    keytool -exportcert -alias release.checkout -keystore checkout-release.keystore | openssl sha1 -binary | openssl base64


## Google+ Android App
---

### Client ID for Android application

In a terminal, run the the [`Keytool`] (https://developers.google.com/+/mobile/android/getting-started)
utility to get the `SHA-1` fingerprint of the certificate.

For the `debug.keystore`, the password is `android`.

    keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v

Create placeholder for `Debug`, `Staging`, `Live` environment

### Steps to sign APK

    keytool -genkey -v -keystore receipts-release.keystore -alias receipts.release -keyalg RSA -keysize 2048 -validity 8500

- Add Key store password
- And add another password
- Keep the name and origination name as Receiptofi Inc
- Add this information in the properties files

    Enter keystore password:
    Re-enter new password:
    What is your first and last name?
      [Unknown]:  Receiptofi Inc
    What is the name of your organizational unit?
      [Unknown]:  Receiptofi Inc
    What is the name of your organization?
      [Unknown]:  Receiptofi Inc
    What is the name of your City or Locality?
      [Unknown]:  Sunnyvale
    What is the name of your State or Province?
      [Unknown]:  California
    What is the two-letter country code for this unit?
      [Unknown]:  US
    Is CN=Receiptofi Inc, OU=Receiptofi Inc, O=Receiptofi Inc, L=Sunnyvale, ST=CA, C=US correct?
      [no]:  yes

### For Debug signing

[Debug] (https://developers.google.com/+/mobile/android/getting-started)

      keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v
      Enter keystore password: Type "android" if using debug.keystore

### For SHA1

     keytool -list -v -keystore receipts-release.keystore

### For Facebook Key Hash

     keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
     keytool -exportcert -alias receipts.release -keystore receipts-release.keystore | openssl sha1 -binary | openssl base64

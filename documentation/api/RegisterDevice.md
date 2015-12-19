###Register device 

As soon as user logs in for the first time and when APP has noticed there is no Device ID registered/saved internally, APP should make a call to register the device

API call <code>POST</code> path 

	curl -ik -X POST 
    -H "X-R-MAIL: test@receiptofi.com" 
    -H "X-R-AUTH: %242a%" 
    -H "X-R-DID: Unique-Device-Id" 
    -H "X-R-DT: I or A"
    https://receiptofi.com:9443/receipt-mobile/api/register.json

***Success***

This call should always return the same response below. 

- If the device is not registered, it will register the device and then return the response below. 
- If device is registered then it still returns the same response because device is registered.

	{"registered":true}
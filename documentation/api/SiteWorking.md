### Check if site is up and running ###
---------------------------------------

Following call will make sure if site is up and running

    curl -ik -X GET https://test.receiptofi.com/receipt-mobile/healthCheck.json

HTTP success response

    HTTP/1.1 200 OK
    Server: nginx/1.6.1
    ......

HTTP Body

    {"working":true}

If there is no response then site is not working. This call should return a response very quickly.

Complete HTTP success response

    HTTP/1.1 200 OK
    Server: nginx/1.6.1
    Date: Fri, 03 Apr 2015 20:21:43 GMT
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Connection: keep-alive
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    Strict-Transport-Security: max-age=31536000; includeSubdomains
    X-Frame-Options: DENY
    
    {"working":true}
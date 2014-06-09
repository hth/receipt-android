receipt-android
===============

Receipt Android App

There two ways to test through command line
- curl on mac
- httpie https://gist.github.com/BlakeGardner/5586954

Below there are couple of examples using curl and httpie. For encoding password please use http://www.url-encode-decode.com/

**Authenticate**
____________

Use following curl or httpie with your username and password. 
Note: In test and prod environment only call made over **secure protocol** will be supported

Local

    curl -i -X POST  -d mail=vijay@receiptofi.com -d password=vijay http://localhost:9090/receipt-mobile/j_spring_security_check

QA secure

    curl -ik -X POST -d mail=vijay@receiptofi.com -d password=vijay https://67.148.60.37:9443/receipt-mobile/j_spring_security_check

QA

    curl -i -X POST  -d mail=vijay@receiptofi.com -d password=vijay http://67.148.60.37:9090/receipt-mobile/j_spring_security_check

Example of response

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    X-R-MAIL: vijay@receiptofi.com
    X-R-AUTH: $2a$15$zbXzZ0mMXLWi3ACh3Ekl7.bz18ULpnkZ.9bhdhfZH9754cz8os3wS
    Set-Cookie: id="vijay@receiptofi.com|$2a$15$zbXzZ0mMXLWi3ACh3Ekl7.bz18ULpnkZ.9bhdhfZH9754cz8os3wS"; Version=1; Domain=localhost; Max-Age=1814400; Expires=Mon, 30-Jun-2014 03:59:38 GMT; Path=/receipt-mobile
    Content-Length: 0
    Date: Mon, 09 Jun 2014 03:59:38 GMT

X-R-MAIL and X-R-AUTH needs to be saved locally and has to be supplied with http header in every call that gets invoked from app


**API Call**
________

All API call should have the MAIL and AUTH in http header.
To query use following curl or http (replace XXX with valid user id and auth key)

    curl -i -X GET -H "X-R-MAIL: XXX" -H "X-R-AUTH: XXX" http://localhost:9090/receipt-mobile/api/haveAccess.json
    curl -i -X GET -H "X-R-MAIL: vijay@receiptofi.com" -H "X-R-AUTH: %242a%2415%24zbXzZ0mMXLWi3ACh3Ekl7.bz18ULpnkZ.9bhdhfZH9754cz8os3wS"  http://localhost:9090/receipt-mobile/api/haveAccess.json
    http GET http://localhost:9090/receipt-mobile/api/haveAccess.json X-R-MAIL:vijay@receiptofi.com X-R-AUTH:%242a%2415%24zbXzZ0mMXLWi3ACh3Ekl7.bz18ULpnkZ.9bhdhfZH9754cz8os3wS

Note: X-R-AUTH code needs to be encoded by going to site http://www.url-encode-decode.com/;
Decoded auth code is    $2a$15$LOIOMLJUu5S5yXGvFqAl3udDB/mTd3tSHPRyml41EHWi7QIARSrwS

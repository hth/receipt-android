### Check if user has access###

All API call should have the <code>X-R-MAIL</code> and <code>X-R-AUTH</code> in http header.<br>
To query use following <code>curl</code> or <code>httpie</code>. (replace XXX with valid User Id and AUTH code)<br>

Check if user has access using <code>X-R-AUTH</code> code

    curl -i -X GET -H "X-R-MAIL: XXX" -H "X-R-AUTH: XXX" http://localhost:9090/receipt-mobile/api/hasAccess.json

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://test.receiptofi.com/receipt-mobile/api/hasAccess.json

    http GET http://localhost:9090/receipt-mobile/api/hasAccess.json X-R-MAIL:test@receiptofi.com X-R-AUTH:%242a%241
    
HTTP Header response when success

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    ......
    
HTTP Body response when success
    
    {"access":"granted"}

HTTP Header response when access denied **HTTP/1.1 401 Unauthorized**

    HTTP/1.1 401 Unauthorized
    Server: Apache-Coyote/1.1
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    Strict-Transport-Security: max-age=31536000 ; includeSubDomains
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    Content-Type: text/html;charset=UTF-8
    Content-Language: en
    Content-Length: 975
    Date: Sun, 15 Jun 2014 04:29:39 GMT
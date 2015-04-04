### Update Email/UserId

To update user email/userId, invoke API call as below.

API call <code>/receipt-mobile/api/updateMail.json</code>

    curl -i -X POST
    -H "Content-Type: application/json"
    -H "X-R-MAIL: email@receiptofi.com"
    -H "X-R-AUTH: %242a%241"
    -d '{"UID":"change-email@receiptofi.com"}'
    http://localhost:9090/receipt-mobile/api/updateMail.json

- HTTP header response upon success

    ##### Application submission success

    Updated data returned in header. Similar to login success. Using the same method update
    X-R-AUTH and X-R-MAIL when response status code is 200.

    - HTTP/1.1 200 OK

            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            X-R-MAIL: change-email@receiptofi.com
            X-R-AUTH: %242a%2415%2
            Content-Length: 0
            Date: Fri, 26 Dec 2014 12:11:54 GMT
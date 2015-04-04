### Update Password

To update user password, invoke API call as below.

API call <code>/receipt-mobile/api/updatePassword.json</code>

    curl -i -X POST
    -H "Content-Type: application/json"
    -H "X-R-MAIL: email@receiptofi.com"
    -H "X-R-AUTH: %242"
    -d '{"PA":"somepassword"}'
    http://localhost:9090/receipt-mobile/api/updatePassword.json

HTTP header response when there is an exception

- List of possible errors: (Note these are mostly common errors across all API calls)

    ##### Application submission error

    - HTTP/1.1 500 Internal Server Error

            HTTP/1.1 500 Internal Server Error
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:26:36 GMT
            Connection: close

    - HTTP/1.1 401 Unauthorized

            HTTP/1.1 401 Unauthorized
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:23:48 GMT

    #### Development Errors

    - HTTP/1.1 415 Unsupported Media Type

            HTTP/1.1 415 Unsupported Media Type
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:18:29 GMT

    - HTTP/1.1 405 Method Not Allowed

            HTTP/1.1 405 Method Not Allowed
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Allow: POST
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:18:18 GMT

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
            X-R-AUTH: %242a%2415%24aiQz
            X-R-MAIL: email@receiptofi.com
            Content-Type: application/json;charset=UTF-8
            Content-Length: 101
            Date: Fri, 26 Dec 2014 10:29:16 GMT
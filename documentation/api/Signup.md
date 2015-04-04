## User Signup ##
_________________

API call <code>POST</code> path <code>/receipt-mobile/registration.json</code> to signup.

JSON body should contain

- FN - First Name     - Two characters length minimum. "John Doe would be treated as FN John and LN Doe"
- EM - Email          - Valid email (example t@t.c) and at least five characters length.
- PW - Password       - Six characters length
- BD - Birth day      - Optional       - Number (Range 18 - 99+)

Below are responses for various input with respective Error Code. On success, response header with be same as Social
login with X-R-AUTH and X-R-MAIL code.

System Error code 410

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "test2@receiptofi.com", "FN": "first", "PW":"pass"}' http://localhost:9090/receipt-mobile/registration.json
    HTTP/1.1 200 OK
    {
      "error": {
        "systemErrorCode": "410",
        "systemError": "EXISTING_USER",
        "EM": "test2@receiptofi.com",
        "reason": "user already exists"
      }
    }

System Error Code 100

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "test2@receiptofi.com", "FN": "first", "PC":"pass"}' http://localhost:9090/receipt-mobile/registration.json
    HTTP/1.1 200 OK
    {
      "error": {
        "systemErrorCode": "100",
        "systemError": "USER_INPUT",
        "PW": "Empty",
        "reason": "failed data validation"
      }
    }

System Error Code 500

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "t@receiptofi.com", "FN": "first", "PW":"pass"}' http://localhost:9090/receipt-mobile/registration.json
    HTTP/1.1 200 OK

    {
      "error": {
        "systemErrorCode": "500",
        "systemError": "SEVERE",
        "EM": "t@receiptofi.com",
        "reason": "failed creating account"
      }
    }

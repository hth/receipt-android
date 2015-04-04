## User Password recover ##
___________________________

API call <code>POST</code> path <code>/receipt-mobile/recover.json</code> to recover password.

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "test@receiptofi.com"}' http://localhost:9090/receipt-mobile/recover.json

When bad request

    curl -i  -X POST -H "Content-Type: application/json" -d '' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 400 Bad Request

Empty Json data

    curl -i  -X POST -H "Content-Type: application/json" -d '{}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK
    {"error":{"systemErrorCode":"100","systemError":"USER_INPUT","EM":"Empty","reason":"failed data validation"}}

Bad Json format

    curl -i  -X POST -H "Content-Type: application/json" -d '{123}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK
    {"error":{"systemErrorCode":"210","systemError":"MOBILE_JSON","reason":"could not parse JSON"}}

Invalid Key with valid email or empty value

    curl -i  -X POST -H "Content-Type: application/json" -d '{"XX": ""}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK
    {
      "error": {
        "systemErrorCode": "210",
        "systemError": "MOBILE_JSON",
        "reason": "could not parse [XX]"
      }
    }

Value empty

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": ""}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK
    {
      "error": {
        "systemErrorCode": "100",
        "systemError": "USER_INPUT",
        "EM": "Empty",
        "reason": "failed data validation"
      }
    }

When user does not exists:
    Note: In this scenario: Still show success response and not the error below.

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "some@receiptofi.com"}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK
    {
      "error": {
        "systemErrorCode": "412",
        "systemError": "USER_NOT_FOUND",
        "EM": "some@receiptofi.com",
        "reason": "user does not exists"
      }
    }

When success, empty body

    curl -i  -X POST -H "Content-Type: application/json" -d '{"EM": "test@receiptofi.com"}' http://localhost:9090/receipt-mobile/recover.json
    HTTP/1.1 200 OK


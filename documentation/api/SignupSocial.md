## Social Authentication and Signup ##
______________________

API call <code>POST</code> path <code>/receipt-mobile/authenticate.json</code> to signup or login through social

	http https://test.receiptofi.com/receipt-mobile/authenticate.json < ~/Downloads/pid.json

Curl command gives connection refusal, prefer to use above <code>http</code> command

	curl -ik -X POST -H "Content-Type: application/json" -d '{"pid": "GOOGLE","at": "ya29"}' https://test.receiptofi.com/receipt-mobile/authenticate.json

	curl -i  -X POST -H "Content-Type: application/json" -d '{"pid": "GOOGLE","at": "ya29"}' http://localhost:9090/receipt-mobile/authenticate.json

Sample <code>pid.json</code> file

	{
      "pid": "FACEBOOK",
      "at": "XXXX-SOME-ACCESS-TOKEN-XXXX"
    }

When login or signup fails for invalid token, which probably results in <code>401</code> HTTP error in message

	HTTP/1.1 200 OK
	...............

    {
        "error": {
            "httpStatus": "UNAUTHORIZED",
            "httpStatusCode": 401,
            "reason": "denied by provider GOOGLE",
            "systemError": "AUTHENTICATION",
            "systemErrorCode": "400"
        }
    }

When there is some system issue. <code>Error</code> would be reported as below. Though these messages are not to be displayed to user, it could be used as reference by the Mobile App.

	HTTP/1.1 200 OK
	................
	{
        "error": {
            "reason": "could not connect to server",
            "systemError": "SEVERE",
            "systemErrorCode": "500"
        }
    }


Successful response when credentials are validated. <code>X-R-AUTH</code> is encoded string.

	HTTP/1.1 200 OK
	...........
    X-R-AUTH: %242fsdfsdfa%LtFxE8jVIijuHbm5r2b2m.fgdfgdfgdfg%2FiDvy
    X-R-MAIL: realemailaddress@youknowho.com
    X-XSS-Protection: 1; mode=block

    {
        "X-R-AUTH": "%242a%2415%24y%CCCCCC-XXXXXXXXXXXXXXX",
        "X-R-MAIL": "100007981713206"
    }

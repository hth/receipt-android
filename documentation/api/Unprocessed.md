###Get unprocessed file count

API call <code>/receipt-mobile/api/unprocessed.json</code>

	curl -i -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241"  http://localhost:9090/receipt-mobile/api/unprocessed.json

	{
      "unprocessedCount": 20
    }
    
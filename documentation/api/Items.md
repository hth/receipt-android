### Items

To get details of receipt, invoke API call with <code>receiptId</code>.

API call <code>/receipt-mobile/api/receiptDetail/53e714b303646c5236d45c84.json</code>

	curl -i -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242" http://site/receipt-mobile/api/receiptDetail/53e714b303646c5236d45c84.json
	
HTTP body response when there is data

Field explanation
- <code>quant</code>, Quantity
- <code>id</code>, record id
- <code>seq</code>, position of the item in receipt

		[
		  {
		    "quant": "1.0",
		    "id": "53e714b303646c5236d45c85",
		    "name": "Organic Fuji",
		    "price": "9.99",
		    "receiptId": "53e714b303646c5236d45c84",
		    "seq": "1",
		    "tax": "0.0",
		    "cs": "US"
		  },
		  {
		    "quant": "1.0",
		    "id": "53e714b303646c5236d45c86",
		    "name": "Homogen Milk",
		    "price": "6.59",
		    "receiptId": "53e714b303646c5236d45c84",
		    "seq": "2",
		    "tax": "0.0",
		    "cs": "US"
		  }
		]

HTTP Header response

Possible header response from the call

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1

When **No** receipt exists or when user provides incorrect receipt id	
	
	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	
When **Authorization** fails	
	
	HTTP/1.1 401 Unauthorized
	Server: Apache-Coyote/1.1
### Updates available 

API below will get all the updates available. Currently it just gets "Receipt" updates, but in future it will get "Profile", "Mileage", "Uploaded Document" updates. When device is not registred, this API will register the device too. It would be better not to use this API for registering the device.

API call <code>GET</code> path 

    curl -ik -X GET 
    -H "X-R-MAIL: test@receiptofi.com" 
    -H "X-R-AUTH: %242a%" 
    -H "X-R-DID: Unique-Device-Id" 
    -H "X-R-DT: I or A"
    https://receiptofi.com:9443/receipt-mobile/api/update.json

Different types of updates supported are:

	RECEIPT,
	ITEM,
	MILEAGE,
	PROFILE,
	UPLOAD_DOCUMENT

***Success***

When there are no updates available, response will contain empty receipts list

	{
	  "profile": null,
	  "receipts": []
	}
	
When there are updates available, response will contain receipts list ordered by Receipt Date

	{
    "profile": {
      "firstName": "Test",
      "lastName": "Test",
      "mail": "test@receiptofi.com",
      "name": "Test Test",
      "rid": "10000000002",
      "cs": "US"
    },
    "receipts": [
      {
        "bizName": {
          "name": "Any Resturant"
        },
        "bizStore": {
          "address": "Sunnyvale, CA, USA",
          "phone": "(696) 969-6969"
        },
        "date": "2014-08-17T11:11:00.000-07:00",
        "expenseReport": null,
        "files": [
          {
            "blobId": "53f0dbf40364600e14bd9e30",
            "orientation": 0,
            "sequence": 0
          }
        ],
        "id": "53f0e42b0364e0a0a79efc68",
        "notes": {
          "text": "Notes goes here"
        },
        "ptax": "0.0000",
        "rid": "10000000002",
        "total": 1969.0.
        "cs": "US",
        "cd": "1234"
      },
      {
        "date": "2014-08-17T00:00:00.000-07:00",
        "expenseReport": null,
        "notes": {
          "text": null
        },
        ..
        ..
      }
    ]
  }

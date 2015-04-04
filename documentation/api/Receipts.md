### Get receipts

**To get all receipts** 

API call <code>/receipt-mobile/api/allReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://test.receiptofi.com/receipt-mobile/api/allReceipts.json
    
**To get Receipts from start of the year** 

API call <code>/receipt-mobile/api/ytdReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://test.receiptofi.com/receipt-mobile/api/ytdReceipts.json

**To get Receipts for this month**

API call <code>/receipt-mobile/api/thisMonthReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://test.receiptofi.com/receipt-mobile/api/thisMonthReceipts.json
    
HTTP Header response

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    
HTTP Body when there is data. Note: Date format is ISO8601 format

    [
      {
        "id": "539d0c3a8de23882a69b94ad",
        "total": 116.0,
        "bizName": {
          "name": "Costco"
        },
        "bizStore": {
          "address": "1000 North Rengstorff Avenue, Mountain View, CA 94043, USA",
          "phone": "(650) 988-1841"
        },
        "notes": null,
        "files": [
          {
            "blobId": "539d09a10364b2452f8e744d",
            "sequence": 0,
            "orientation": 90
          }
        ],
        "date": "2014-07-03T18:00:00.000+0000",
        "ptax": "0.0000",
        "rid": "10000000002",
        "expenseReport": "vhnyqRVKW0tTUiq6"
      },
      {
        "id": "539ceb490364da3e933db72b",
        "total": 5.96,
        "bizName": {
          "name": "Target"
        },
        "bizStore": {
          "address": "298 West McKinley Avenue, Sunnyvale, CA 94086, USA",
          "phone": "(408) 702-1012"
        },
        "notes": {
          "text": "Bought kiwi for shoe"
        },
        "files": [
          {
            "blobId": "539ce78f0364ab6cb1bacbbf",
            "sequence": 0,
            "orientation": 90
          }
        ],
          "date": "2014-06-28T04:00:00.000+0000",
        "ptax": "0.085610",
        "rid": "10000000002",
        "expenseReport": null
      }
    ]
    
HTTP Body when there is **No** data

    []
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

New fields 
 
 - splitCount, splitTax, splitTotal, referReceiptId, ptax --> :)   
 - If splitCount > 1, then the receipt is being shared
 - If referReceiptId is null and splitCount > 1, then you are the original owner
 - If referReceiptId is not null and splitCount > 1, then receipts is shared with you
 - Items are saved with original receipt id. If the receipt is shared, then the items with have receipt id from referReceiptId
    

    [
      {
        "a": true,
        "bizName": {
          "name": "Appu Ghar Express"
        },
        "bizStore": {
          "address": "Building, Sector 38, Noida, Uttar Pradesh 201301, India",
          "lat": "28.5627755",
          "lng": "77.33619689999999",
          "phone": "01204247560"
        },
        "bs": "P",
        "d": false,
        "expenseReport": null,
        "expenseTagId": "",
        "files": [
          {
            "blobId": "2015-10/01/560cce78f32026eb6cd2dc00.jpg",
            "orientation": 0,
            "sequence": 0
          }
        ],
        "id": "56163e20f32026103793694d",
        "notes": {
          "text": null
        },
        "ptax": "0.0000",
        "receiptDate": "2015-09-20T12:59:00.000+00:00",
        "referReceiptId": "560cd915f32026eb6cd2dc1f",
        "rid": "10000000004",
        "splitCount": 2,
        "splitTax": 92.63,
        "splitTotal": 320.0,
        "tax": 185.26,
        "total": 640.0
      },
      {
        "a": true,
        "bizName": {
          "name": "Costco"
        },
        "bizStore": {
          "address": "Costco Wholesale, 150 Lawrence Station Road, Sunnyvale, CA 94086, USA",
          "lat": "37.3720256",
          "lng": "-121.9946682",
          "phone": "(408) 730-1892"
        },
        "bs": "P",
        "d": false,
        "expenseReport": null,
        "expenseTagId": "",
        "files": [
          {
            "blobId": "2015-07/17/55a8a527f320262152e84dc0.png",
            "orientation": 0,
            "sequence": 0
          }
        ],
        "id": "55a8cafef32026215232664d",
        "notes": {
          "text": null
        },
        "ptax": "0.0000",
        "receiptDate": "2015-07-17T14:56:00.000+00:00",
        "referReceiptId": null,
        "rid": "10000000004",
        "splitCount": 3,
        "splitTax": 0.0,
        "splitTotal": 13.98,
        "tax": 0.0,
        "total": 41.93
      }
    ]
    
HTTP Body when there is **No** data

    []
### Delete receipt

API call <code>POST</code>. API path <code>/api/receipt/delete.json</code>

    curl -i -X POST
    -H "Content-Type: application/json"
    -H "X-R-MAIL: email@receiptofi.com"
    -H "X-R-AUTH: %242"
    -d '{"receiptId":"some1d23"}'
    http://localhost:9090/receipt-mobile/api/receipt/delete.json
    
    
HTTP Header response when success

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    ......
        
Response         
    
    {
      "awaitingFriends": [],
      "billing": null,
      "expenseTags": [],
      "friends": [],
      "items": [
        {
          "expenseTagId": "",
          "id": "56cd26df263a1f2a2e9ccabe",
          "name": "Milk",
          "price": "1.0",
          "quant": "1.0",
          "receiptId": "56cd26df263a1f2a2e9ccabd",
          "seq": "1",
          "tax": "0.0"
        }
      ],
      "notifications": [],
      "owes": [],
      "owesOther": [],
      "pendingFriends": [],
      "profile": null,
      "receiptSplits": [],
      "receipts": [
        {
          "a": true,
          "bizName": {
            "name": "Costco"
          },
          "bizStore": {
            "address": "Sunnyvale, CA, USA",
            "lat": "37.36883",
            "lng": "-122.0363496",
            "phone": "(408) 000-0001",
            "rating": 0.0,
            "type": "[locality, political]"
          },
          "bs": "P",
          "d": true,
          "expenseReport": null,
          "expenseTagId": "",
          "files": [
            {
              "blobId": "2016-02/24/56cd24ed263a1f28735288a4.png",
              "orientation": 0,
              "sequence": 0
            }
          ],
          "id": "56cd26df263a1f2a2e9ccabd",
          "notes": {
            "text": null
          },
          "ptax": "0.0000",
          "receiptDate": "2016-02-22T08:00:00.000+00:00",
          "referReceiptId": "",
          "rid": "10000000001",
          "splitCount": 1,
          "splitTax": 0.0,
          "splitTotal": 1.0,
          "tax": 0.0,
          "total": 1.0
        }
      ],
      "unprocessedDocuments": {
        "unprocessedCount": 0
      }
    }

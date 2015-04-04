#### All available data

API will get **all** data for registered account. This API is mostly used when device has not been registered or when
 new user logs in. 

<code>GET</code> API call <code>/receipt-mobile/api/all.json</code>

    curl -ik -X GET 
    -H "X-R-MAIL: test@receiptofi.com" 
    -H "X-R-AUTH: %242a%2415%24e2" 
    -H "X-R-DID: 12345" 
    https://test.receiptofi.com/receipt-mobile/api/all.json

All different kind of data available in this API

    EXPENSE TAGS
    ITEMS
    NOTIFICATIONS
    PROFILE
    RECEIPTS
    UNPROCESSED COUNT
    
JSON HTTP Response

    HTTP/1.1 200 OK
    Server: nginx/1.6.1
    Date: Thu, 02 Apr 2015 18:26:44 GMT
    Content-Type: application/json;charset=UTF-8
    Content-Length: 320
    Connection: keep-alive
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    Strict-Transport-Security: max-age=31536000; includeSubdomains
    X-Frame-Options: DENY

JSON Response data

    {
      "expenseTags": [
        {
          "color": "#0D060D",
          "id": "54cc7fdbd4c6bde31a31c978",
          "tag": "BUSINESS"
        },
        {
          "color": "#E625C9",
          "id": "54cd6896d4c6e568298c9dc1",
          "tag": "HOME"
        },
        {
          "color": "#463BE6",
          "id": "550cd248036487b4351ab869",
          "tag": "QWE"
        }
      ],
      "items": [
        {
          "expenseTagId": "",
          "id": "551cd1f5036401b6df58bcd8",
          "name": "Fuel",
          "price": "3.299",
          "quant": "4.167",
          "receiptId": "551cd1f5036401b6df58bcd7",
          "seq": "1",
          "tax": "0.0"
        },
        {
          "expenseTagId": "",
          "id": "547c1808036405eb90adac76",
          "name": "Sale",
          "price": "9.23",
          "quant": "1.0",
          "receiptId": "547c1808036405eb90adac75",
          "seq": "1",
          "tax": "0.0"
        }
      ],
      "notifications": [
        {
          "c": "2015-04-01T23:23:11.523-07:00",
          "id": "551ce04f0364146df5ebe506",
          "m": "E4145F2D-A494-4018-939D-CFD917037EE0.png upload successful",
          "n": true,
          "nt": "DOCUMENT_UPLOADED",
          "ri": "551ce04f0364146df5ebe504",
          "u": "2015-04-01T23:23:11.523-07:00"
        },
        {
          "c": "2015-04-01T22:21:57.009-07:00",
          "id": "551cd1f5036401b6df58bcd9",
          "m": "$13.75 'Chevron' receipt processed",
          "n": true,
          "nt": "RECEIPT",
          "ri": "551cd1f5036401b6df58bcd7",
          "u": "2015-04-01T22:21:57.009-07:00"
        }
      ],
      "profile": {
        "firstName": "Test",
        "lastName": "Test",
        "mail": "test@receiptofi.com",
        "name": "Test Test",
        "rid": "10000000004"
      },
      "receipts": [
        {
          "bizName": {
            "name": "Chevron"
          },
          "bizStore": {
            "address": "296 North Fair Oaks Avenue, Sunnyvale, CA 94085, USA",
            "phone": "(408) 245-9676"
          },
          "bs": "P",
          "expenseReport": null,
          "expenseTagId": "",
          "files": [
            {
              "blobId": "551cce7e0364146df5ebe4a1",
              "orientation": 0,
              "sequence": 0
            }
          ],
          "id": "551cd1f5036401b6df58bcd7",
          "notes": {
            "text": null
          },
          "ptax": "0.0000",
          "receiptDate": "2015-04-02T10:06:00.000-07:00",
          "rid": "10000000004",
          "tax": 0.0,
          "total": 13.75
        },
        {
          "bizName": {
            "name": "Madras Groceries"
          },
          "bizStore": {
            "address": "1187 El Camino Real, Sunnyvale, CA 94087, USA",
            "phone": "(408) 746-0808"
          },
          "bs": "P",
          "expenseReport": null,
          "expenseTagId": "",
          "files": [
            {
              "blobId": "551cd02a0364146df5ebe4d5",
              "orientation": 0,
              "sequence": 0
            }
          ],
          "id": "551cd18b036401b6df58bcd4",
          "notes": {
            "text": null
          },
          "ptax": "0.0000",
          "receiptDate": "2015-04-02T10:06:00.000-07:00",
          "rid": "10000000004",
          "tax": 0.0,
          "total": 7.99
        },
        {
          "bizName": {
            "name": "LITTLE INDIA CAFE"
          },
          "bizStore": {
            "address": "415 North Mary Avenue #101, Sunnyvale, CA 94085, USA",
            "phone": "00003563652"
          },
          "bs": "P",
          "expenseReport": null,
          "expenseTagId": "",
          "files": [
            {
              "blobId": "547c0f63036405eb90adac1b",
              "orientation": -270,
              "sequence": 0
            }
          ],
          "id": "547c1788036405eb90adac72",
          "notes": {
            "text": null
          },
          "ptax": "0.0000",
          "receiptDate": "2014-10-18T21:00:00.000-07:00",
          "rid": "10000000004",
          "tax": 0.0,
          "total": 9.23
        },
        {
          "bizName": {
            "name": "LITTLE INDIA CAFE"
          },
          "bizStore": {
            "address": "415 North Mary Avenue #101, Sunnyvale, CA 94085, USA",
            "phone": "00003563652"
          },
          "bs": "P",
          "expenseReport": null,
          "expenseTagId": "",
          "files": [
            {
              "blobId": "547c0f63036405eb90adac29",
              "orientation": -270,
              "sequence": 0
            }
          ],
          "id": "547c1808036405eb90adac75",
          "notes": {
            "text": null
          },
          "ptax": "0.0000",
          "receiptDate": "2014-09-17T20:59:00.000-07:00",
          "rid": "10000000004",
          "tax": 0.0,
          "total": 9.23
        }
      ],
      "unprocessedDocuments": {
        "unprocessedCount": 1
      }
    }
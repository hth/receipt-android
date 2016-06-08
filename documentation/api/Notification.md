## Notifications

Below is the various groups notification (ng) can be clubbed into. 
By default all messages when has missing group should be marked as `N` 

### Notification Group (ng)

    /** Friendship request, connection, invitation send) */
    S("S", "Social"),

    /** File/Document delete, File/Document upload, ... */
    F("F", "File"),

    /** Receipt notifications(receipt processed) */
    R("R", "Receipt"),

    /** For welcome message or other random un-associated messages. */
    N("N", "Normal Message");
    
### Json Structure
    
    "notifications": [
        {
          "a": false,   (missing)
          "c": "2015-04-01T23:23:11.523-07:00",
          "id": "551ce04f0364146df5ebe506",
          "m": "E4145F2D-A494-4018-939D-CFD917037EE0.png upload successful",
          "mr": true,   (new)
          "n": true,
          "ng" = "F",
          "nt": "DOCUMENT_UPLOADED",
          "ri": "551ce04f0364146df5ebe504",
          "u": "2015-04-01T23:23:11.523-07:00"
        },
        {
          "a": false,   (missing)
          "c": "2015-04-01T22:21:57.009-07:00",
          "id": "551cd1f5036401b6df58bcd9",
          "m": "$13.75 'Chevron' receipt processed",
          "mr": false,  (new)
          "n": true,
          "ng" = "R",
          "nt": "RECEIPT",
          "ri": "551cd1f5036401b6df58bcd7",
          "u": "2015-04-01T22:21:57.009-07:00"
        }
    ],
    
    
### Mark notification as read

API call <code>POST</code> path <code>/receipt-mobile/api/notification/read.json</code> to mark list of 
notification as read. Call this API when user navigates away from Notification screen. Can club all the 
ids (comma separated) in one call.


    curl -X "POST" "http://localhost:9090/receipt-mobile/api/notification/read.json" 
    	-H "X-R-AUTH: $2a94duSio7DVCINL." 
    	-H "Content-Type: application/json; charset=utf-8" 
    	-H "X-R-DID: E9Emyemail@receiptofi.com.com" 
    	-H "X-R-MAIL: myemail@receiptofi.com" 
    	-d "{"notificationIds":"56ll61db66d2c3114, 56e22b561db66d2c11151"}"

#### Response

Complete response similar to `Update` API with update Notification to be marked as read.
    
    {
      "awaitingFriends": [],
      "billing": {
        "billingHistories": [],
        "bt": "Promotion"
      },
      "coupons": [],
      "expenseTags": [],
      "friends": [],
      "items": [],
      "notifications": [],
      "owes": [],
      "pendingFriends": [],
      "profile": null,
      "receiptSplits": [],
      "receipts": [],
      "type": "UPDATE",
      "unprocessedDocuments": {
        "unprocessedCount": 0
      }
    }
    

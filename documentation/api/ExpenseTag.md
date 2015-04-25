## Expense Tag

There are three actions 

- Add Expense Tag
- Update Expense Tag
- Delete Expense Tag

### Add Expense Tag

API call `POST`. API path `/api/addExpenseTag.json`

Required `tagName` and `tagColor`

    curl -X "POST" "https://test.receiptofi.com/receipt-mobile/api/addExpenseTag.json" \
    	-H "X-R-AUTH: x0wvlBm" \
    	-H "X-R-MAIL: test@receiptofi.com" \
    	-d "{\"tagName\":\"DADA\",\"tagColor\":\"#CECECE\"}"
    	
Failure response when incorrect data is sent

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1   

    {
      "error": {
        "reason": "Either Expense Tag or Color received as empty.",
        "systemErrorCode": "100",
        "systemError": "USER_INPUT"
      }
    }
    
Success Response   

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
 
    {
      "billing": null,
      "expenseTags": [
        {
          "color": "#0D060D",
          "id": "54cc7fdbd4c6bde31a31c978",
          "tag": "BUSINESS"
        },
        {
          "color": "#CECECE",
          "id": "553b68d1bd2898546e65ede4",
          "tag": "DADA"
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
      "items": [],
      "notifications": [],
      "profile": null,
      "receipts": [],
      "unprocessedDocuments": {
        "unprocessedCount": 1
      }
    }

Besides this there is regular authentication passed `X-R-MAIL` and `X-R-AUTH`
    
### Update Expense Tag

API call `POST`. API path `/api/updateExpenseTag.json`

Required `tagName` and `tagColor` and `tagId`

    curl -X "POST" "https://test.receiptofi.com/receipt-mobile/api/updateExpenseTag.json" \
    	-H "X-R-AUTH: $2a$15$e2kRPwglBm" \
    	-H "X-R-MAIL: test@receiptofi.com" \
    	-d "{\"tagName\":\"DADA\",\"tagColor\":\"CCC\",\"tagId\":\"553b7196bd289854eb3dd8fc\"}"
    	
Failure Response 

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    	
    {
      "error": {
        "reason": "Expense Tag does not exists.",
        "systemErrorCode": "500",
        "systemError": "SEVERE"
      }
    }
    
Failure Response when missing required field

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    
    {
      "error": {
        "reason": "Either Expense Tag or Color or Id received as empty.",
        "systemErrorCode": "500",
        "systemError": "SEVERE"
      }
    }       

Success Response 

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1

    {
      "billing": null,
      "expenseTags": [
        {
          "color": "#0D060D",
          "id": "54cc7fdbd4c6bde31a31c978",
          "tag": "BUSINESS"
        },
        {
          "color": "CCC",
          "id": "553b7196bd289854eb3dd8fc",
          "tag": "DADA"
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
      "items": [],
      "notifications": [],
      "profile": null,
      "receipts": [],
      "unprocessedDocuments": {
        "unprocessedCount": 1
      }
    }
 
Besides this there is regular authentication passed `X-R-MAIL` and `X-R-AUTH`

### Delete Expense Tag

API call `POST`. API path `/api/deleteExpenseTag.json`

Required `tagName` and `tagId`

    curl -X "POST" "https://test.receiptofi.com/receipt-mobile/api/deleteExpenseTag.json" \
    	-H "X-R-AUTH: $2a$15$e2kRPwgvlBm" \
    	-H "X-R-MAIL: test@receiptofi.com" \
    	-d "{\"tagName\":\"DADAB\",\"tagId\":\"553b68d1bd2898546e65ede4\"}"

Failure response when incorrect data is sent

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1 
    
    {
      "error": {
        "reason": "Expense Tag does not exists.",
        "systemErrorCode": "500",
        "systemError": "SEVERE"
      }
    }
    
Success Response  
 
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1

    {
      "billing": null,
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
      "items": [],
      "notifications": [],
      "profile": null,
      "receipts": [],
      "unprocessedDocuments": {
        "unprocessedCount": 1
      }
    }

Besides this there is regular authentication passed `X-R-MAIL` and `X-R-AUTH` 
 
### Do receipt split

Perform split

API call `POST`. API call `/receipt-mobile/api/split.json`
    
    curl -X "POST" "http://localhost:9090/receipt-mobile/api/split.json"
    	-H "X-R-AUTH: $2a$15$e2kRPwg04Ld6W9u4WWwvTuYZdbUhf5PSz8BLtQCRzDRwP5x0wvlBm" 
    	-H "X-R-DID: 12347" 
    	-H "X-R-MAIL: test@receiptofi.com"

And send jsonBody

    requestBodyJson 
        - fidAdd    (comma separated fid [Friend Rid])
        - receiptId
        
Successful response will contain complete update response. Of which receipt split details would be displayed as below. 
                                                                                                                          
    "receiptSplits": [
        {
          "receiptId": "561639d1f320261037936939",
          "splits": [
            {
              "initials": "PS",
              "name": "Pratap Singh",
              "rid": "10000000102"
            },
            {
              "initials": "SP",
              "name": "Suresh Poonia",
              "rid": "10000000009"
            }
          ]
        },
        {
          "receiptId": "56163e20f32026103793694d",
          "splits": [
            {
              "initials": "RG",
              "name": "Ravi Gadgil",
              "rid": "10000000116"
            }
          ]
        }
    ]
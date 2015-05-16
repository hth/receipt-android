### Overview

   [https://developers.braintreepayments.com/android+java/start/overview] (https://developers.braintreepayments.com/android+java/start/overview)

### Mobile APP reference

   [https://developers.braintreepayments.com/android+java/start/hello-client] (https://developers.braintreepayments.com/android+java/start/hello-client)

### Client Token

Get client token from server to initializing Braintree Payment SDK.

API call `POST`. API call `/receipt-mobile/api/token.json`

    curl -X "POST" "http://localhost:9090/receipt-mobile/api/token.json"
    	-H "X-R-AUTH: $2a$15$e2kRPwg04Ld6W9u4WWwvTuYZdbUhf5PSz8BLtQCRzDRwP5x0wvlBm"
    	-H "X-R-DID: 12347"
    	-H "X-R-MAIL: test@receiptofi.com"

Response

    {"token":"eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIy"}

### Plans

Gets all available plans.

API call `GET`. API call `/receipt-mobile/api/plans.json`

    curl -X "GET" "http://localhost:9090/receipt-mobile/api/plans.json"
    	-H "X-R-AUTH: $2a$15$e2k"
    	-H "X-R-MAIL: test@receiptofi.com"

Response

    [
      {
        "accountBillingType": "M10",
        "billingDayOfMonth": 25,
        "billingFrequency": 1,
        "description": "Process 10 receipts every month",
        "id": "M10",
        "name": "Monthly 10",
        "paymentGateway": "BT",
        "price": 2.00
      },
      {
        "accountBillingType": "M30",
        "billingDayOfMonth": 25,
        "billingFrequency": 1,
        "description": "Process 30 receipts every month",
        "id": "M30",
        "name": "Monthly 30",
        "paymentGateway": "BT",
        "price": 4.00
      }
    ]

### Payments

Submit payment

API call `POST`. API call `/receipt-mobile/api/payment.json`

    curl -X "POST" "http://localhost:9090/receipt-mobile/api/payment.json"
    	-H "X-R-AUTH: $2a$15$e2k"
    	-H "X-R-DID: 12347"
    	-H "X-R-MAIL: test@receiptofi.com"
    	-d "{
    	"planId":"M10",
    	"firstName":"Jenna",
    	"lastName":"Smith",
    	"company":"Some Company",
    	"cardNumber":"4111111111111111",
    	"month":"05",
    	"year":"2016",
    	"cvv":"100",
    	"postal":"60622"
    	}"

Response

    {"status":true}

### More API to come

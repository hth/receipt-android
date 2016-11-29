### Payment card

Json format for Payment cards

    "paymentCards": [
        {
          "id" : "1233wrdsgfdsgsdfg",
          "nm" : "Some name",
          "cn": "M",
          "cd": "9069",
          "a": true                      
        }
      ],   
      
      @JsonProperty ("id")
      private String id;
  
      @JsonProperty ("cn")
      private String cardNetwork;
  
      @JsonProperty ("cd")
      private String cardDigit;
  
      @JsonProperty ("a")
      private boolean active;
      
### Card Network

Four kinds of network
      
    A - American Express
    D - Discover
    M - MasterCard
    V - Visa      

### Actions that can be performed on receipt

#### Add New Card

When adding new card, `id` is blank or missing. Preferred to keep it blank.

    curl -X "POST" "http://localhost:9090/receipt-mobile/api/paymentCard/update.json"
         -H "X-R-AUTH: $2a$15$B/MOag/xLb/BTn9lduzCf.xvRcIjjfnFcACEi2TZpawzHCZQQju8G" 
         -H "Content-Type: application/json; charset=utf-8" 
         -H "X-R-DID: 12347" 
         -H "X-R-MAIL: abc@r.com" 
         -d "{"nm":"Hello","a":"1","cn":"M","cd":"1234"}"

#### Inactive Card, Change name, Change number, Change card network

- To remove a card, set `a="0"` to inactive card with `"id":"abc1233980keh"`
- To update a card,
        set `nm="new_name"` to change card name with `"id":"abc1233980keh"`
        set `cn="V"` to change to card network with `"id":"abc1233980keh"`
        set `cd="8900"` to card number with `"id":"abc1233980keh"`

Same URL for all activities 

    curl -X "POST" "http://localhost:9090/receipt-mobile/api/paymentCard/update.json"
         -H "X-R-AUTH: $2a$15$B/MOag/xLb/BTn9lduzCf.xvRcIjjfnFcACEi2TZpawzHCZQQju8G" 
         -H "Content-Type: application/json; charset=utf-8" 
         -H "X-R-DID: 12347" 
         -H "X-R-MAIL: abc@r.com" 
         -d "{"id":"abc1233980keh","nm":"Hello","a":"0","cn":"M","cd":"1234"}"

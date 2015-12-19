# Friends

Supported Apis' [Invite](#invite-api), [Friend](#friend-api) and [UnFriend](#unfriend-api)
 
Friends and Awaiting or Pending Friends (same data response)  would look as below.  

    {       
      "friends": [
        {
          "in": "BB",
          "na": "BR BR",
          "rid": "10000000002"
        }
      ],      
      "pendingFriends": [
        {
          "pa": true,
          "au": "xtk3d0qwjrpkt7ag2f2175xmx414aoty",
          "c": 1445075325745,
          "em": "jkl@r.com",
          "id": "5622197dad116ed79ee94985",
          "in": "vb",
          "na": "vbxcvbcv bxcvbc",
          "pr": ""
        }
      ]
      "awaitingFriends": [],
    }
    
## Invite API

To invite a user, invoke API call as below. You will receive the whole object with just `pendingFriends` populated. 
Delete all the pending friends and re-create data associated with Pending Friends. 

API call <code>/receipt-mobile/api/invite.json</code>

    curl -i -X POST
    -H "Content-Type: application/json"
    -H "X-R-MAIL: email@receiptofi.com"
    -H "X-R-AUTH: %242"
    -d '{"EM":"a@r.com"}'
    http://localhost:9090/receipt-mobile/api/invite.json

### HTTP header response when to different requests

- List of possible errors: 

    #### Application submission error

     - HTTP/1.1 500 Internal Server Error
     
            HTTP/1.1 500 Internal Server Error
            Server: Apache-Coyote/1.1

    - HTTP/1.1 401 Unauthorized
                    
            HTTP/1.1 401 Unauthorized
            Server: Apache-Coyote/1.1
    
    #### Development Errors
    
    - HTTP/1.1 415 Unsupported Media Type
    
            HTTP/1.1 415 Unsupported Media Type
            Server: Apache-Coyote/1.1
    
    - HTTP/1.1 405 Method Not Allowed
    
            HTTP/1.1 405 Method Not Allowed
            Server: Apache-Coyote/1.1
    
- Invalid data like email submitted by User    
    
        curl -X "POST" "http://localhost:9090/receipt-mobile/api/invite.json"
            -H "X-R-AUTH: $2a$15$"
            -H "Content-Type: application/json"
            -H "X-R-MAIL: a@r.com"
            -d "{"EM":"z@r"}"
            
    - Response 
        
            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
                
            {
              "error": {
                "reason": "Failed data validation.",
                "EM": "z@r",
                "systemErrorCode": "100",
                "systemError": "USER_INPUT"
              }
            }    	
            

- Valid Email submission

    #### Application submission success    

        curl -X "POST" "http://localhost:9090/receipt-mobile/api/invite.json"
            -H "X-R-AUTH: $2a$15$0zzqwJomm"
            -H "Content-Type: application/json"
            -H "X-R-MAIL: a@r.com"
            -d "{"EM":"z@r.com"}"
            
    
    - Response                    
     
            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
            
            {
              "awaitingFriends": [],
              "billing": null,
              "expenseTags": [],
              "friends": [],
              "items": [],
              "notifications": [],
              "pendingFriends": [
                {
                  "pa": false,
                  "au": "124i8nnfm11wa4cemo4nvjvz53m3z18m",
                  "c": 1445501020201,
                  "em": "z@r.com",
                  "id": "5628985c191035550d80b802",
                  "initials": "z@",
                  "name": null,
                  "pr": ""
                },
                {
                  "pa": false,
                  "au": "3v10d1tdd81max3xrsboz2iympykt1fu",
                  "c": 1445629856296,
                  "em": "m@r.com",
                  "id": "562a8fa01910357467763f23",
                  "initials": "m@",
                  "name": null,
                  "pr": ""
                }
              ],
              "profile": null,
              "receiptSplits": [],
              "receipts": [],
              "unprocessedDocuments": null
            }            
        
## Friend API

When Friend API is invoked you would get all the `friends`, `pendingFriends`, `awaitingFriends` 
            
Connection Type `ct` that are valid
            
    - 'A' Accept invitation
    - 'C' Cancel invite. When you have sent a request to add as a friend. 
    - 'D' Decline invitation. Happens when you have received invitation.

API call <code>/receipt-mobile/api/friend.json</code>

    curl -X "POST" "http://localhost:9090/receipt-mobile/api/friend.json" 
    	-H "X-R-AUTH: $2a$15$0zzqwJommKS" 
    	-H "Content-Type: application/json" 
    	-H "X-R-MAIL: a@r.com" 
    	-d "{"id":"562a8fa01910357467763f23","au":"3v10d1tdd81max3xrsboz2iympykt1fu","ct":"A"}"
                                                                          
### HTTP header response when to different requests  
                  
- List of possible errors: 

    #### Application submission error

     - HTTP/1.1 500 Internal Server Error
     
            HTTP/1.1 500 Internal Server Error
            Server: Apache-Coyote/1.1

    - HTTP/1.1 401 Unauthorized
                    
            HTTP/1.1 401 Unauthorized
            Server: Apache-Coyote/1.1
    
    #### Development Errors
    
    - HTTP/1.1 415 Unsupported Media Type
    
            HTTP/1.1 415 Unsupported Media Type
            Server: Apache-Coyote/1.1
    
    - HTTP/1.1 405 Method Not Allowed
    
            HTTP/1.1 405 Method Not Allowed
            Server: Apache-Coyote/1.1                  
                      
- Invalid Connection Type `ct` submitted                         

        curl -X "POST" "http://localhost:9090/receipt-mobile/api/friend.json" 
            -H "X-R-AUTH: $2a$15$0zzqwJommKS" 
            -H "Content-Type: application/json" 
            -H "X-R-MAIL: a@r.com" 
            -d "{"id":"562a8fa01910357467763f23","au":"3v10d1tdd81max3xrsboz2iympykt1fu","ct":"R"}"
            
    - Response        

        HTTP/1.1 200 OK
        Server: Apache-Coyote/1.1
    
        {
          "error": {
            "reason": "Something went wrong. Engineers are looking into this.",
            "systemErrorCode": "500",
            "systemError": "SEVERE"
          }
        }
        
- Valid Connection Type `ct` submitted but not correct Connection Type sent.
                           
    Since its a pending request and the submitted connection type is `Accept`. Which is an invalid
    connection type for this action. Response for this action is `false`.
                       
    #### Accept request (incorrect connection type)                         

        curl -X "POST" "http://localhost:9090/receipt-mobile/api/friend.json" 
            -H "X-R-AUTH: $2a$15$0zzqwJommKS" 
            -H "Content-Type: application/json" 
            -H "X-R-MAIL: a@r.com" 
            -d "{"id":"562a8fa01910357467763f23","au":"3v10d1tdd81max3xrsboz2iympykt1fu","ct":"A"}"
            
    - Response               

        HTTP/1.1 200 OK
        Server: Apache-Coyote/1.1
        
        {"success":false}      

- Valid Response with correct `Connection Type` for Cancelling friend request

    #### Cancel request    

        curl -X "POST" "http://localhost:9090/receipt-mobile/api/friend.json" 
            -H "X-R-AUTH: $2a$15$0zzqwJomm"
            -H "Content-Type: application/json"
            -H "X-R-MAIL: a@r.com"
            -d "{"id":"562a8fa01910357467763f23","au":"3v10d1tdd81max3xrsboz2iympykt1fu","ct":"C"}"

    - Response       

            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
            
            {
              "awaitingFriends": [
                {
                  "pa": true,
                  "au": "cu8bi5s38mmrh2cg9pp21dm54ir78but",
                  "c": 1445637977654,
                  "em": "d@r.com",
                  "id": "562aaf591910357467763f32",
                  "initials": "DD",
                  "name": "DD DD",
                  "pr": ""
                }
              ],
              "billing": null,
              "expenseTags": [],
              "friends": [
                {
                  "initials": "BB",
                  "name": "BB BB",
                  "rid": "10000000003"
                }
              ],
              "items": [],
              "notifications": [],
              "pendingFriends": [
                {
                  "pa": false,
                  "au": "124i8nnfm11wa4cemo4nvjvz53m3z18m",
                  "c": 1445501020201,
                  "em": "z@r.com",
                  "id": "5628985c191035550d80b802",
                  "initials": "z@",
                  "name": null,
                  "pr": ""
                }
              ],
              "profile": null,
              "receiptSplits": [],
              "receipts": [],
              "unprocessedDocuments": null
            }

## UnFriend API
                        
When UnFriend API is invoked you would get just the `friends`. 
`fid` is Friend Id

API call <code>/receipt-mobile/api/unfriend.json</code> 
                         
    curl -X "POST" "http://localhost:9090/receipt-mobile/api/unfriend.json" 
        -H "X-R-AUTH: $2a$15$0zzqwJomm" 
        -H "Content-Type: application/json" 
        -H "X-R-MAIL: a@r.com" 
        -d "{"fid":"10000000003"}"    
                     
                                 
### HTTP header response when to different requests
                                 
- Invalid `fid` value    
                                 
        curl -X "POST" "http://localhost:9090/receipt-mobile/api/unfriend.json" 
             -H "X-R-AUTH: $2a$15$0zzqwJomm" 
             -H "Content-Type: application/json" 
             -H "X-R-MAIL: a@r.com" 
             -d "{"fid":""}" 
             
    - Response
    
        {
          "error": {
            "reason": "Missing required data.",
            "systemErrorCode": "100",
            "systemError": "USER_INPUT"
          }
        }
        
- Provided correct `fid` and DOES NOT `connection`
        
        curl -X "POST" "http://localhost:9090/receipt-mobile/api/unfriend.json" 
                        -H "X-R-AUTH: $2a$15$0zzqwJomm" 
                        -H "Content-Type: application/json" 
                        -H "X-R-MAIL: a@r.com" 
                        -d "{"fid":"10000000003"}"    
                        
    - Response  
          
          HTTP/1.1 200 OK
          Server: Apache-Coyote/1.1
          
          {"success":false}
                         
- Provided correct `fid` and has `connection`  

        curl -X "POST" "http://localhost:9090/receipt-mobile/api/unfriend.json" 
                -H "X-R-AUTH: $2a$15$0zzqwJomm" 
                -H "Content-Type: application/json" 
                -H "X-R-MAIL: a@r.com" 
                -d "{"fid":"10000000003"}"    
                
    - Response

            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
                         
            {
            "awaitingFriends": [],
            "billing": null,
            "expenseTags": [],
            "friends": [
             {
               "initials": "DD",
               "name": "DD DD",
               "rid": "10000000006"
             }
            ],
            "items": [],
            "notifications": [],
            "pendingFriends": [],
            "profile": null,
            "receiptSplits": [],
            "receipts": [],
            "unprocessedDocuments": null
            }
### Friends
 
Friends and Awaiting or Pending Friends would look as below.  

    {
      "awaitingFriends": [],
      "friends": [
        {
          "in": "BB",
          "na": "BR BR",
          "rid": "10000000002"
        }
      ],
      "pendingFriends": [
        {
          "a": true,
          "au": "xtk3d0qwjrpkt7ag2f2175xmx414aoty",
          "c": 1445075325745,
          "em": "jkl@r.com",
          "id": "5622197dad116ed79ee94985",
          "in": "vb",
          "na": "vbxcvbcv bxcvbc",
          "pr": ""
        }
      ]
    }
    
#### Invite Friend API

To invite a user, invoke API call as below.

API call <code>/receipt-mobile/api/invite.json</code>

    curl -i -X POST
    -H "Content-Type: application/json"
    -H "X-R-MAIL: email@receiptofi.com"
    -H "X-R-AUTH: %242"
    -d '{"EM":"a@r.com"}'
    http://localhost:9090/receipt-mobile/api/invite.json

HTTP header response when there is an exception

- List of possible errors: 

    ##### Application submission error

     - HTTP/1.1 500 Internal Server Error
     
            HTTP/1.1 500 Internal Server Error
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Thu, 22 Oct 2015 08:44:41 GMT
            Connection: close

    - HTTP/1.1 401 Unauthorized
                    
            HTTP/1.1 401 Unauthorized
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Thu, 22 Oct 2015 08:40:02 GMT
            Connection: close
    
    #### Development Errors
    
    - HTTP/1.1 415 Unsupported Media Type
    
            HTTP/1.1 415 Unsupported Media Type
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:18:29 GMT
    
    - HTTP/1.1 405 Method Not Allowed
    
            HTTP/1.1 405 Method Not Allowed
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Allow: POST
            Content-Type: text/html
            Content-Length: 0
            Date: Fri, 26 Dec 2014 09:18:18 GMT        
    
    
- Invalid data like email submitted by User    
    
        curl -X "POST" "http://localhost:9090/receipt-mobile/api/invite.json" \
            -H "X-R-AUTH: $2a$15$" \
            -H "Content-Type: application/json" \
            -H "X-R-MAIL: a@r.com" \
            -d "{\"EM\":\"z@r\"}"
            
        Response 
        
            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Type: application/json;charset=UTF-8
            Content-Length: 108
            Date: Thu, 22 Oct 2015 08:50:23 GMT
            Connection: close
                
            {
              "error": {
                "reason": "Failed data validation.",
                "EM": "z@r",
                "systemErrorCode": "100",
                "systemError": "USER_INPUT"
              }
            }    	
            

- Valid Email submission

    ##### Application submission success    

            curl -X "POST" "http://localhost:9090/receipt-mobile/api/invite.json" \
                -H "X-R-AUTH: $2a$15$0zzqwJomm" \
                -H "Content-Type: application/json" \
                -H "X-R-MAIL: a@r.com" \
                -d "{\"EM\":\"z@r.com\"}"
            
    
    - HTTP/1.1 200 OK                      
     
            HTTP/1.1 200 OK
            Server: Apache-Coyote/1.1
            Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            Pragma: no-cache
            Expires: 0
            X-XSS-Protection: 1; mode=block
            X-Frame-Options: DENY
            X-Content-Type-Options: nosniff
            Content-Length: 0
            Date: Thu, 22 Oct 2015 08:41:55 GMT
            Connection: close
            

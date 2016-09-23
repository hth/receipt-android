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
    FRIENDS
    PENDING & AWAITING FRIENDS
    
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
          "tax": "0.0",
          "cs": "US"
        },
        {
          "expenseTagId": "",
          "id": "547c1808036405eb90adac76",
          "name": "Sale",
          "price": "9.23",
          "quant": "1.0",
          "receiptId": "547c1808036405eb90adac75",
          "seq": "1",
          "tax": "0.0",
          "cs": "US"
        }
      ],
      "notifications": [
        {
          "c": "2015-04-01T23:23:11.523-07:00",
          "id": "551ce04f0364146df5ebe506",
          "m": "E4145F2D-A494-4018-939D-CFD917037EE0.png upload successful",
          "n": true,
          "ng" = "F",
          "nt": "DOCUMENT_UPLOADED",
          "ri": "551ce04f0364146df5ebe504",
          "u": "2015-04-01T23:23:11.523-07:00"
        },
        {
          "c": "2015-04-01T22:21:57.009-07:00",
          "id": "551cd1f5036401b6df58bcd9",
          "m": "$13.75 'Chevron' receipt processed",
          "n": true,
          "ng" = "R",
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
        "rid": "10000000004",
        "cs": "US"
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
          "total": 13.75,
          "cs": "US",
          "cd": "1234"          
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
          "total": 7.99,
          "cs": "US",
          "cd": "1234"
        }
      ],
      "unprocessedDocuments": {
        "unprocessedCount": 1
      },
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
      ],
      "owes": [
        {
          "fid": "10000000005",
          "name": "He Man",
          "splitTotal": 40.61
        },
        {
          "fid": "10000000004",
          "name": "Big Man",
          "splitTotal": 197.58000000000004
        },
        {
          "fid": "10000000002",
          "name": "AA AA",
          "splitTotal": 139.99
        }
      ],
      "owesOther": [
        {
          "name": "Big Man",
          "rid": "10000000004",
          "splitTotal": 149.56
        }
      ],
    }
    
### Field References    
    
Expense Tag field reference
    
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("tag")
    private String tag;

    @JsonProperty ("color")
    private String color;
    
Items field reference
    
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("seq")
    private String seq;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("quant")
    private String quantity;

    @JsonProperty ("price")
    private String price;

    @JsonProperty ("tax")
    private String tax;

    @JsonProperty ("receiptId")
    private String receiptId;

    @JsonProperty ("expenseTagId")
    private String expenseTagId;
    
Notification field names
    
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("m")
    private String message;

    @JsonProperty ("n")
    private boolean notify;

    @JsonProperty ("nt")
    private String notificationType;
    
    /** @since 1.0.3 */
    @JsonProperty ("ng")
    private String notificationGroup;

    /**
     * Could be a receipt id or Document id
     */
    @JsonProperty ("ri")
    private String referenceId;
    
    @JsonProperty("mr")
    private boolean markedRead;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("u")
    private String updated;
    
    @JsonProperty ("a")
    private boolean active;
    
Receipt field name
        
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("total")
    private Double total;

    @JsonProperty ("bizName")
    private JsonBizName jsonBizName;

    @JsonProperty ("bizStore")
    private JsonBizStore jsonBizStore;

    @JsonProperty ("notes")
    private JsonComment jsonNotes;

    @JsonProperty ("files")
    private Collection<JsonFileSystem> jsonFileSystems = new LinkedList<>();

    @JsonProperty ("receiptDate")
    private String receiptDate;

    @JsonProperty ("ptax")
    private String percentTax;

    @JsonProperty ("tax")
    private Double tax;

    @JsonProperty ("rid")
    private String receiptUserId;

    @JsonProperty ("expenseReport")
    private String expenseReportInFS;

    @JsonProperty ("bs")
    private String billedStatus = BilledStatusEnum.NB.getName();

    @JsonProperty ("expenseTagId")
    private String expenseTagId;    
        
Friends

    @JsonProperty ("rid")
    private String rid;
    
    @JsonProperty ("initials")
    private String initials;
    
    @JsonProperty ("name")
    private String name;

Awaiting and Pending Friends 

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("au")
    private String authKey;

    @JsonProperty ("c")
    private Date created;

    @JsonProperty ("initials")
    private String initials;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("em")
    private String email;

    @JsonProperty ("pr")
    private String provider = "";      // FACEBOOK or GOOGLE

    @JsonProperty ("a")
    private boolean active;
    
Owe and Owe Others
    
    /** When object used for OwesOther then RID is not null and FID is null. */
    @Null
    @JsonProperty ("rid")
    private String receiptUserId;

    /** When object used for OweMe then RID is not and FID is not null. */
    @Null
    @JsonProperty ("fid")
    private String friendUserId;

    @JsonProperty ("splitTotal")
    private Double splitTotal;

    @JsonProperty ("name")
    private String name;
    
Coupons
    
    @JsonProperty ("id")
    private String id;

    @JsonProperty ("rid")
    private String rid;

    @JsonProperty ("lid")
    private String lid;

    @JsonProperty ("bn")
    private String businessName;

    @JsonProperty ("ft")
    private String freeText;

    @JsonProperty ("av")
    private String available;

    @JsonProperty ("ex")
    private String expire;

    @JsonProperty ("ct")
    private String couponType;

    @JsonProperty ("c")
    private String created;

    @JsonProperty ("u")
    private String updated;

    @JsonProperty ("rm")
    private boolean reminder;

    @JsonProperty ("ip")
    private String imagePath;

    @JsonProperty ("sh")
    private List<String> sharedWithRids = new ArrayList<>();

    @JsonProperty ("oi")
    private String originId;

    @JsonProperty ("uc")
    private boolean usedCoupon;

    @JsonProperty ("a")
    private boolean active;
        
        
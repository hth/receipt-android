receipt-mobile
==============

There two ways to test through command line
- <code>curl</code> using mac
- <code>httpie</code> on windows or mac. https://github.com/jakubroztocil/httpie and helpful command can be found  https://gist.github.com/BlakeGardner/5586954


![Mobile Api Architecture](/architecture/Mobile-Architecture.png)

###Check if site is up and running###

Following call will make sure if site is up and running

    curl -ik -X GET https://67.148.60.37:9443/receipt-mobile/healthCheck.json

HTTP Response success

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    ......

HTTP Body

    {"working":true}

If there is no response then site is not working. This call should return a response very quickly.

##User Authentication##
____________

Use following <code>curl</code> or <code>httpie</code> with your <code>username</code> and <code>password</code>.<br>
**Note**: Application should make secure <code>HTTPS</code> calls, only <code>HTTPS</code> calls will be supported and responded. Any other call will get exception.

Curl command options used 

    -i include header
    -k insecure call over ssl
    -X request type
    -H header
    -d Data

QA Secure login for getting <code>X-R-AUTH</code> code from user's account

    curl -ik -X POST -d mail=test@receiptofi.com -d password=test https://67.148.60.37:9443/receipt-mobile/j_spring_security_check

HTTP Response Header

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    Strict-Transport-Security: max-age=31536000 ; includeSubDomains
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    X-R-MAIL: test@receiptofi.com
    X-R-AUTH: $2a$15$x9M5cc3mR24Ns4wgL47gaut/3.pM2tW9J.0SWeLroGbi2q8OU2k4C
    Content-Length: 0
    Date: Sun, 15 Jun 2014 04:09:38 GMT

Values from <code>X-R-MAIL</code> and <code>X-R-AUTH</code> has to be supplied in http header with every API call that gets invoked through mobile application.<br>
**Note**: <code>X-R-AUTH</code> code has to be encoded before sending in with the header. Any Java encoding API should help. For testing go to the site http://www.url-encode-decode.com/ for encoding <code>X-R-AUTH</code> string

**Note**: <code>X-R-AUTH</code> code needs to be encoded by going to site http://www.url-encode-decode.com/;

    Decoded X-R-AUTH code:  $2a$15$x9M5cc3mR24Ns4wgL47gaut/3.pM2tW9J.0SWeLroGbi2q8OU2k4C
    Encoded X-R-AUTH code:  %242a%2415%24x9M5cc3mR24Ns4wgL47gaut%2F3.pM2tW9J.0SWeLroGbi2q8OU2k4C

##Social Authentication and Signup##
______________________

API call <code>POST</code> path <code>/receipt-mobile/authenticate.json</code> to signup or login through social

	http https://67.148.60.37:9443/receipt-mobile/authenticate.json < ~/Downloads/pid.json

Curl command gives connection refusal, prefer to use above <code>http</code> command

	curl -ik -X POST -H "Content-Type: application/json" -d '{"pid": "GOOGLE","at": "ya29"}' https://67.148.60.37:9443/receipt-mobile/authenticate.json

	curl -i  -X POST -H "Content-Type: application/json" -d '{"pid": "GOOGLE","at": "ya29"}' http://localhost:9090/receipt-mobile/authenticate.json

Sample <code>pid.json</code> file

	{
      "pid": "FACEBOOK",
      "at": "XXXX-SOME-ACCESS-TOKEN-XXXX"
    }

When login or signup fails for invalid token, which probably results in <code>401</code> HTTP error in message

	HTTP/1.1 200 OK
	...............

    {
        "error": {
            "httpStatus": "UNAUTHORIZED",
            "httpStatusCode": 401,
            "reason": "denied by provider GOOGLE",
            "systemError": "AUTHENTICATION",
            "systemErrorCode": "400"
        }
    }

When there is some system issue. <code>Error</code> would be reported as below. Though these messages are not to be displayed to user, it could be used as reference by the Mobile App.

	HTTP/1.1 200 OK
	................
	{
        "error": {
            "reason": "could not connect to server",
            "systemError": "SEVERE",
            "systemErrorCode": "500"
        }
    }


Successful response when credentials are validated. <code>X-R-AUTH</code> is encoded string.

	HTTP/1.1 200 OK
	...........
    X-R-AUTH: %242fsdfsdfa%LtFxE8jVIijuHbm5r2b2m.fgdfgdfgdfg%2FiDvy
    X-R-MAIL: realemailaddress@youknowho.com
    X-XSS-Protection: 1; mode=block

    {
        "X-R-AUTH": "%242a%2415%24y%CCCCCC-XXXXXXXXXXXXXXX",
        "X-R-MAIL": "100007981713206"
    }


##API Calls##
________

###Logout user and login again###

Upon log out, <code>X-R-AUTH</code> should be deleted and <code>X-R-MAIL</code> remains intact. Logout action complete by re-directing user to login page.

Different scenarios when user tries to login again

- If user enters correct credentials then <code>X-R-AUTH</code> should be restored and everything is available as it was before logout. Account remains as is with all the receipts information in the Mobile App.

- If user enters different credentials than existing <code>X-R-MAIL</code>, Mobile App should drop all the tables and recreate as if the user is login for first time. Start with saving new <code>X-R-MAIL</code> and <code><X-R-AUTH</code>

###Check if user has access###

All API call should have the <code>X-R-MAIL</code> and <code>X-R-AUTH</code> in http header.<br>
To query use following <code>curl</code> or <code>httpie</code>. (replace XXX with valid User Id and AUTH code)<br>

Check if user has access using <code>X-R-AUTH</code> code

    curl -i -X GET -H "X-R-MAIL: XXX" -H "X-R-AUTH: XXX" http://localhost:9090/receipt-mobile/api/hasAccess.json

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://67.148.60.37:9443/receipt-mobile/api/hasAccess.json

    http GET http://localhost:9090/receipt-mobile/api/hasAccess.json X-R-MAIL:test@receiptofi.com X-R-AUTH:%242a%241
    
HTTP Header response when success

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    ......
    
HTTP Body response when success
    
    {"access":"granted"}

HTTP Header response when access denied **HTTP/1.1 401 Unauthorized**

    HTTP/1.1 401 Unauthorized
    Server: Apache-Coyote/1.1
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    Strict-Transport-Security: max-age=31536000 ; includeSubDomains
    X-XSS-Protection: 1; mode=block
    X-Frame-Options: DENY
    X-Content-Type-Options: nosniff
    Content-Type: text/html;charset=UTF-8
    Content-Language: en
    Content-Length: 975
    Date: Sun, 15 Jun 2014 04:29:39 GMT
    
###Register device 

As soon as user logs in for the first time and when APP has noticed there is no Device ID registered/saved internally, APP should make a call to register the device

API call <code>POST</code> path <code>curl -i -X POST -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%" -H "X-R-DID: Unique-Device-Id" http://localhost:9090/receipt-mobile/api/register.json</code>

***Success***

This call should always return the same response below. 
- If the device is not registered, it will register the device and then return the response below. 
- If device is registered then it still returns the same response because device is registered.

	{"registered":true}

###Updates avaliable 

API below will get all the updates available. Currently it just gets "Receipt" updates, but in future it will get "Profile", "Mileage", "Uploaded Document" updates. When device is not registred, this API will register the device too. It would be better not to use this API for registering the device.

API call <code>GET</code> path <code>curl -i -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%" -H "X-R-DID: Unique-Device-Id" http://localhost:9090/receipt-mobile/api/update.json</code>

Different types of updates supported are:

	RECEIPT,
	MILEAGE,
	PROFILE,
	UPLOAD_DOCUMENT

***Success***

When there are no updates avaliable, response will contain empty receipts list

	{"receipts":[]}
	
When there are updates avaliable, response will contain receipts list ordered by Receipt Date

	{
	  "receipts": [
	    {
	      "id": "53edc24030040bbe580c7566",
	      "total": 1.0,
	      "bizName": {
	        "name": "Costco"
	      },
	      "bizStore": {
	        "address": "Sunnyvale, CA, USA",
	        "phone": "(408) 000-0001"
	      },
	      "notes": null,
	      "files": [
	        {
	          "blobId": "53edc21530040bbe580c755d",
	          "sequence": 0,
	          "orientation": 90
	        }
	      ],
	      "date": 1407517201000,
	      "ptax": "0.0000",
	      "rid": "10000000011",
	      "expenseReport": null
	    }
	  ]
	}

###Upload Document

API call <code>POST</code> path <code>/receipt-mobile/api/upload.json</code>

Note: Max file upload size - 10 MB

    curl -i  -X POST -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" -F "qqfile=@/Absolute/Location/File.jpg" http://localhost:9090/receipt-mobile/api/upload.json

    curl -ik -X POST -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" -F "qqfile=@/Absolute/Location/File.jpg" https://67.148.60.37:9443/receipt-mobile/api/upload.json

**Success**

When document <code>original.jpg</code> is uploaded successfully, response returned with the name of the document uploaded and number of unprocessed documents

    {
      "blobId": "53e439597830a10417794f64",
      "unprocessedDocuments": {
        "unprocessedCount": 21
      },
      "uploadedDocumentName": "original.jpg"
    }

**Map image blobId with filename in App Gallery**

* First scenario

    Take photo. Save photo to gallery. Use iOS/Android naming convention. Stack new image in Wi-Fi queue. Upload image to server.
    On success response, above JSON is returned.

    Local App DB

    Make a entry and map blobId with Filenames in APP DB

        blobId | Filename in gallery
        -------|---------------------
        53e439597830a10417794f64 | IMG_1425.JPG
        53e439597830a10417794f55 | 20140808_89897.PNG

* Second scenario

    For new device

    APP DB does not contain map entry for new device. when entry not found, download the file using blobId. Save the file to gallery with iOS/Andorid naming convention.
    Then map the saved file to blobId as above


* Third scenario

    When user deletes the file from gallery.

    For receipt detailed review. BlobId is returned. Find the corresponding file in gallery. When file size is '0', then load the image from server.
    When images is loaded in the APP, add the same file with iOS/Android naming convention in gallery. When file is added to gallery, write the
    filename and blobId to local APP DB.

**Error**

If <code>qqfile</code> missing pr file is empty

	{
      "error": {
        "systemErrorCode": "300",
        "systemError": "DOCUMENT_UPLOAD",
        "reason": "qqfile name missing in request or no file uploaded"
      }
    }

If failed to upload document will include which document failed to upload with error message

	{
	  "error": {
		"systemErrorCode": "300",
		"systemError": "DOCUMENT_UPLOAD",
		"reason": "failed document upload",
		"document": "File.jpg"
	  }
	}

Other errors could be

	{
	  "error": {
		"systemErrorCode": "300",
		"systemError": "DOCUMENT_UPLOAD",
		"reason": "multipart failure for document upload"
	  }
	}

###Get unprocessed file count

API call <code>/receipt-mobile/api/unprocessed.json</code>

	curl -i -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241"  http://localhost:9090/receipt-mobile/api/unprocessed.json

	{
      "unprocessedCount": 20
    }

###Get receipts

**To get all receipts** 

API call <code>/receipt-mobile/api/allReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://67.148.60.37:9443/receipt-mobile/api/allReceipts.json
    
**To get Receipts from start of the year** 

API call <code>/receipt-mobile/api/ytdReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://67.148.60.37:9443/receipt-mobile/api/ytdReceipts.json

**To get Receipts for this month**

API call <code>/receipt-mobile/api/thisMonthReceipts.json</code>

    curl -ik -X GET -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" https://67.148.60.37:9443/receipt-mobile/api/thisMonthReceipts.json
    
HTTP Header response

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    
HTTP Body when there is data. Note: Date format is ISO8601 format

    [
      {
        "id": "539d0c3a8de23882a69b94ad",
        "total": 116.0,
        "bizName": {
          "name": "Costco"
        },
        "bizStore": {
          "address": "1000 North Rengstorff Avenue, Mountain View, CA 94043, USA",
          "phone": "(650) 988-1841"
        },
        "notes": null,
        "files": [
          {
            "blobId": "539d09a10364b2452f8e744d",
            "sequence": 0,
            "orientation": 90
          }
        ],
        "date": "2014-07-03T18:00:00.000+0000",
        "ptax": "0.0000",
        "rid": "10000000002",
        "expenseReport": "vhnyqRVKW0tTUiq6"
      },
      {
        "id": "539ceb490364da3e933db72b",
        "total": 5.96,
        "bizName": {
          "name": "Target"
        },
        "bizStore": {
          "address": "298 West McKinley Avenue, Sunnyvale, CA 94086, USA",
          "phone": "(408) 702-1012"
        },
        "notes": {
          "text": "Bought kiwi for shoe"
        },
        "files": [
          {
            "blobId": "539ce78f0364ab6cb1bacbbf",
            "sequence": 0,
            "orientation": 90
          }
        ],
          "date": "2014-06-28T04:00:00.000+0000",
        "ptax": "0.085610",
        "rid": "10000000002",
        "expenseReport": null
      }
    ]
    
HTTP Body when there is **No** data

    []

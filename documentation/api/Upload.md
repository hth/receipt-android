### Upload Document

API call <code>POST</code> path <code>/receipt-mobile/api/upload.json</code>

Note: Max file upload size - 10 MB

    curl -i  -X POST -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" -F "qqfile=@/Absolute/Location/File.jpg" http://localhost:9090/receipt-mobile/api/upload.json

    curl -ik -X POST -H "X-R-MAIL: test@receiptofi.com" -H "X-R-AUTH: %242a%241" -F "qqfile=@/Absolute/Location/File.jpg" https://test.receiptofi.com/receipt-mobile/api/upload.json

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

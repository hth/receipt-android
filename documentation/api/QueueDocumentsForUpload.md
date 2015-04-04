Image Upload Process - Android
===================

1. Whenever user clicks photo or choose to upload picture , it goes into image upload process.
2. Image upload is done by queuing which keeps the all necessary information related to image object.
3. Queue gets initialized at the start . Means if there are any items that are in database and app comes into memory stack , queue gets initialized with unuploaded images.
4. User cant upload same gallary image at the same time until previous one gets uploaded successfully. Else user gets a message saying "image already in a queue".
5. There is a maximum number of connections at any point of time during image upload.
6. When image upload process starts,it create number of thread equals to max thread configured and start upload which put lock on it with isTriedForUpload flag set.Threads been tracked for number of connections.
7. When any one of image upload gets the response it checks whether number of current connections are less than max connection configured.
 1. If its less then it picks next element from queue.
 2. In the same way all list get iterates.
8. If image has been uploaded successfully (I.e. it gets blobid from server) it get deleted from image queue and database.
9. Image upload happens only if user setting are set accordingly.
10. If user has wifi preference and when he came in wifi zone . Broadcast recievers receives an intent and start image upload for pending items left. User settings check has been put for every image upload case.

Image Upload Process - Android
===================

1) whenever user clicks photo or choose to upload picture , it goes into image upload process. <br><br>
2) image upload is done by queuing which keeps the all necessary information related to image object . <br><br>
3) queue gets initialized at the start . Means if there are any items that are in database and app comes into memory stack , queue gets initialized with unuploaded images. <br><br>
4) user cant upload same gallary image at the same time until previous one gets uploaded successfully. Else
User gets a message saying "image already in a queue". <br><br>
5)there is a maximum number of connections at any point of time during image upload .<br><br>
6)when image upload process starts,it create number of thread equals to max thread configured and start upload which put lock on it with isTriedForUpload flag set.Threads been tracked for number of connections .<br><br>
7)when any one of image upload gets the response it checks whether number of current connections are less than max connection configured.
If its less then it picks next element from queue .
In the same way all list get iterates.<br><br>
8)if image has been uploaded successfully (I.e. it gets blobid from server) it get deleted from image queue and database.<br><br>
9)image upload happens only if user setting are set accordingly.<br><br>
10)if user has wifi preference and when he came in wifi zone . Broadcast recievers receives an intent and start image upload for pending items left. User settings check has been put for every image upload case.<br><br>



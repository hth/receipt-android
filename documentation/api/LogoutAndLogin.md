### Logout user and login again ###

Upon log out, <code>X-R-AUTH</code> should be deleted and <code>X-R-MAIL</code> remains intact. Logout action complete by re-directing user to login page.

Different scenarios when user tries to login again

- If user enters correct credentials then <code>X-R-AUTH</code> should be restored and everything is available as it was before logout. Account remains as is with all the receipts information in the Mobile App.

- If user enters different credentials than existing <code>X-R-MAIL</code>, Mobile App should drop all the tables and recreate as if the user is login for first time. Start with saving new <code>X-R-MAIL</code> and <code>X-R-AUTH</code>

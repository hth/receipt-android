### Actions that can be performed on receipt

API call <code>POST</code>. API path <code>/api/receiptAction.json</code>

There are three different actions that can be performed on a receipt

1. Re-check Receipt
2. Set Expense Tag
3. Add Notes

Re-check receipts is a checkbox and defaults to false condition. When clicked its set to true.

Expense Tags, are radio buttons. Only one expense tag can be selected at one time. 
  
Notes is text entered by user. 

#### Expected data
 
    if (reCheck || null != tagId || null != notes) {
        JSONObject postData = new JSONObject();
        postData.put("expenseTagId", tagId);
        postData.put("notes", notes);
        postData.put("recheck", reCheck ? "RECHECK" : "");     //Notice boolean is checked and a text value is set as RECHECK or Empty String
        postData.put("receiptId", rModel.getId());
        
1. expenseTagId - Text
2. notes - Text
3. recheck - Text
4. receiptId - Text
        
Besides this there is regular authentication passed <code>X-R-MAIL</code> and <code>X-R-AUTH</code>        
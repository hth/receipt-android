## Billing

This is mostly read only operation. It reads the historical data and displays it. All the data comes from
either `all.json` api or `updates.json` api.

Definition for acronyms in data

    bd - Billed Date (This is transaction date)
    bm - Billed for Month
    bs - Billed Status {NB (Not Billed), P(Promotion), B(Billed), R(Refund)}
    bt - Billing Plan {Annual or Monthly}
    ts - Transaction Status {N (Not yet billed), P(Pending), V(Void), S(Successful), R(Refund)}

Format of the data for the billing screen below

    {
      "billing": {
        "billingHistories": [
          {
            "bd": "2015-07-24T00:55:23.023+00:00",
            "bm": "2015-08",
            "bs": "P",
            "bt": "Monthly 10",
            "id": "55b18cfbe32fc64645c29c59",
            "ts": "N"
          },
          {
            "bd": "2015-07-24T00:55:23.023+00:00",
            "bm": "2015-07",
            "bs": "P",
            "bt": "Annual 120",
            "id": "55b18cfbe32fc64645c29c58",
            "ts": "N"
          }
        ],
        "bt": "Monthly 10"
      },
      "expenseTags": [],
      "items": [],
      "notifications": [],
      "profile": {
        "firstName": "Li",
        "lastName": null,
        "mail": "li@receiptofi.com",
        "name": "Li",
        "rid": "10000000003",
        "cs": "US"
      },
      "receipts": [],
      "unprocessedDocuments": {
        "unprocessedCount": 0
      }
    }

## Notifications

Below is the various groups notification (ng) can be clubbed into.
By default all messages when has missing group should be marked as `N`

### Notification Group (ng)

    /** Friendship request, connection, invitation send) */
    S("S", "Social"),

    /** File/Document delete, File/Document upload, ... */
    F("F", "File"),

    /** Receipt notifications(receipt processed) */
    R("R", "Receipt"),

    /** For welcome message or other random un-associated messages. */
    N("N", "Normal Message");

### Json Structure

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
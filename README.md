# kafka
Playing with kafka

This simulates a bank that performs book transfers. These are the following micro-services:
- Checker: performs sanity check on the transfer instruction
- AML: performs anti-money laundry check
- Processor: performs the actual book transfer
- Mailer: sends notifications to customers
- Auditor: monitors and reports activities and status of transfers
- Accountant: computes aggregations to estimate current credit for each account
- Reporter: continuously report the credit for each account

The following topics are used:
  - transfer-request: the initial topic for new transfer requests, used by the simple client application, and consumed by the checker
  - tr-after-check: used by the checker to store valid requests, consumed by AML
  - tr-after-aml: used by the AML to store AML-compliant requests, consumed by the processor
  - tr-completed: used by the processor to store completed requests
  - tr-failed: any request is routed there in case of issue
  - account-updated: the processor will issue specific account update events in here
  - account-aggregated: the accoutant posts aggregated account values from account-updated in here
  

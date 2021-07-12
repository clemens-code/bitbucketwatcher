# Bitbucketwacther 

Der BitbucketWatcher dient zur überwachung eines Repositories. Dabei benachrichtigt er Nutzer, wenn es neue Pull-Requests, 
Statusänderungen bei der Codereview oder gemergerte Pull-Requests gibt. 
Außerdem werden Branches nach dem Mergen gelöscht, damit diese nicht unnötig herumliegen. 
Sollte ein Branch mindesten eine Woche nicht mehr aktualisiert worden sein, werden Benachrichtigungen gesendet, 
dass es einen veralteten und ungenutzen Branch gibt.

## Unterstützte Technologien

- Bitbucket mit der API-Version 1.0
- Teams 
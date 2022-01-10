# BitbucketWatcher 

The BitbucketWatcher is a helpful tool to keep up to date with updates on your repo. 
The Watcher tracks new PullRequests, status changes on code reviews, merged branches and forgotten branches. 

- New opened PR’s
- Review status change (per user) 
- Merged pull requests 
- Forgotten PR’s 
- delete merged branches

## Supported Technology

### Repositories

At the moment only Bitbucket is supported.

### Message Channels 

As message channels you can use Teams via the Webhook API or Telegram via the Bot API. You can use both or just one 
of them. If no teams-url is provided Teams won't be used. If there is no token, Telegram won't be used.
Make sure to configure at least one of them. The Watcher can also run without any message channel, but this doesn't make
a lot of sense. 

## How to Start 

The Docker Repository can be found [here](https://hub.docker.com/repository/docker/clemenscode/bitbucketwatcher)

The Helm Charts can be checkout from [here](https://charts.mayope.net)

### Environment Variables 

For the communication with Bitbucket and Teams some variables are needed. 
In Kubernetes the easiest way to provide them is with a config-map. 

#### Example for Config

*Bitbucket Config* 

```
apiVersion: v1
kind: ConfigMap
metadata:
  name: bitbucketwatchter-credentials-example
  namespace: default
data:
  base-url: https://your.bitbucket.instance
  project-key: yourkey
  repo-slug: theRepo
```

*Teams Config*

```
apiVersion: v1
kind: ConfigMap
metadata:
  name: teams-credentials
  namespace: default
data:
  teams-url: https://og2gether.webhook.office.com/webhookb2/your-really-long-webhook
  
```
  
*Telegram Config*

```
apiVersion: v1
kind: ConfigMap
metadata:
  name: telegram-credentials
  namespace: default
data:
  telegram-url: telegram
  bot: name of your bot
  token: bot auth token
  chat-id: id of the wanted chat 
```

To apply them use `kubectl apply -f .\dataName.yaml`

*Bitbucket-User*

For security reasons the BitbucketWatcher expects the user credentials in Secrets. 

```
kind: Secret
apiVersion: v1
metadata:
  name: bitbucket-user
  namespace: default
data:
  password: your-password
  username: Tyour-username
type: Opaque
```

### Deployment with Helm Charts 

First you need to add the Helm repo. 

To achieve this you can easily enter this to the command line `helm repo add {bitbucketwatcher} https://charts.mayope.net`
--> for {bitbucketwatcher} you can add whatever you want. I would recommend the name mayope because inside this repo there are 
more helm charts than just the BitbucketWatcher

If you want to change the credentials names or anything else you can do this with overwriting the values.yaml 
You need to add a Chart.yaml for example like this: 

```
apiVersion: v2
name: bitbucketwacther-some-cool-name
description: A Helm chart for the bitbucketwatcher

type: application

version: 0.1.0
```

And a values.yaml like this: 

```
replicaCount: 1

image:
  repository: clemenscode/bitbucketwatcher
  pullPolicy: IfNotPresent
  version: 0.0.1

serviceAccount:
  # Specifies whether a service account should be created
  create: false

service:
  type: ClusterIP
  port: 8080
  healthCheckPath: /

ingress:
  enabled: false

bitbucketSecret: your-user-credentials
teamsCredentials: your-teams-credentials
bitbucketCredentials: your-bitbucket-credentials
```

At last, you need to deploy the Watcher with helm. 
Use `helm upgrade --install bitbucketwatcher-dyps bitbucketwatcher/bitbucketwatcher -f .\values.yaml`


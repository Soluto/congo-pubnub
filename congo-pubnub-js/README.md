#Congo PubNub - JavaScript
PubNub implemenation of [Congo](https://github.com/Soluto/congo-core)

### Installation
```npm install congo-pubnub-js --save```

### Prerequisite
You'll need to setup a free account in PubNub and create an app. For more info see: https://www.pubnub.com/docs/getting-started-guides/pubnub-publish-subscribe

### Usage
####Invoker
First, create the invoker:
```javascript
var CongoPubNubInvoker = require('congo-pubnub-js').CongoPubNubInvoker;

var pubnubConfiguration = {
    publishKey: "<YOUR_PUBNUB_APP_PUBLISH_KEY>",  
    subscribeKey: "<YOUR_PUBNUB_APP_SUBSCRIBE_KEY>"
};

var invoker = new CongoPubNubInvoker(
    pubnubConfiguration, 
    "<REQUEST_CHANNEL_ID>", 
    "<RESPONSE_CHANNEL_ID>");
```
Then, use it directly by invoking remote call:
```javascript
var remoteCall = {
    correlationId: "34c99d2e-5d61-48a7-812f-59dcb6628433",
    service: "someService",
    method: "someMethod"
};

pubnubInvoker.invoke(remoteCall)
    .doOnNext(function(item) {
        console.log(item);
    })
    .doOnCompleted(function() {
        console.log("completed");
    })
    .doOnError(function(error) {
        console.log(error);
    })
    .subscribe();
```
or use [Congo Proxy](https://github.com/Soluto/congo-proxy)

####Listener
Not implemented yet

####Responder
Not implemened yet

### Example
See real app example in: https://github.com/Soluto/congo-examples/tree/master/pubNubExample



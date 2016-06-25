
#Congo PubNub - Java
PubNub implemenation of [Congo](https://github.com/Soluto/congo-core)

### Installation
In your build.gradle file add:
```
repositories {
   ...
    maven {
        url "https://dl.bintray.com/soluto/soluto-jars"
    }
    ...

}
...
dependencies {
    ...
    compile 'soluto:congo-core-java:0.0.4'
    compile 'soluto:congo-pubnub-java:0.0.2'
    compile 'com.pubnub:pubnub:4.0.1'
    ...
}
```

### Prerequisite
You'll need to setup a free account in PubNub and create an app. For more info see: https://www.pubnub.com/docs/getting-started-guides/pubnub-publish-subscribe.

Also you'll need to define PubNub request and response channel for message transportation 

### Usage
####Invoker
Not implemented yet

####Listener, Responder & Router
```java
PNConfiguration pnConfiguration = new PNConfiguration();
pnConfiguration.setPublishKey("<YOUR_PUBNUB_APP_PUBLISH_KEY>");
pnConfiguration.setSubscribeKey("<YOUR_PUBNUB_APP_SUBSCRIBE_KEY>");
  
RemoteCallListener listener = new PubNubRemoteCallListener(pnConfiguration, "<YOUR_REQUEST_CHANNEL>");
RemoteCallResponder responder = new PubNubRemoteCallResponder(pnConfiguration,"YOUR_RESPONSE_CHANNEL");

router = new Router(listener, responder);
router.use(new ControllerHandler("someService", new SomeService()));
router.listen();
````

### Example
See real app example in: https://github.com/Soluto/congo-examples/tree/master/pubNubExample



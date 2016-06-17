var Observable = require('rx').Observable;
var Notification = require('rx').Notification;

var PUBNUB = require('pubnub');

module.exports = function(pnConfiguration, requestChannel, responseChannel) {
    return {
        invoke: function(remoteCall) {
            return Observable.create(function (observer)  {
                var pubnub = PUBNUB.init({
                    publish_key: pnConfiguration.publishKey,
                    subscribe_key: pnConfiguration.subscribeKey,
                    error: function (pubnubError) {
                        console.log(pubnubError)
                        observer.onError(pubnubError);
                    }
                });

                pubnub.subscribe({
                    channel : responseChannel,
                    message : function(remoteCallResult) {
                        observer.onNext(JSON.parse(remoteCallResult));
                    },
                    error: function (pubnubSubscribeError) {
                        console.log(pubnubSubscribeError);
                        observer.onError(pubnubSubscribeError);
                    }
                });

                console.log("publish " + JSON.stringify(remoteCall));
                pubnub.publish({
                    channel : requestChannel,
                    message : remoteCall,
                    error : function(pubnubPublishError) {
                        observer.onError(pubnubPublishError);
                    }
                });

                return function() {
                    var cancelRemoteCall = Object.assign({}, remoteCall, {isCancelled: true})
                    pubnub.publish({
                        channel : requestChannel,
                        message : cancelRemoteCall,
                        error : function(pubnubPublishError) {
                            console.log("publish error!")
                            console.log(pubnubPublishError)
                            observer.onError(pubnubPublishError);
                        }
                    });
                };
            })
            .filter(function(remoteCallResult) {
                return remoteCallResult.correlationId === remoteCall.correlationId;
            })
            .flatMap(function(remoteCallResult) {
                if (remoteCallResult.notification.kind === "OnNext") {
                    return Observable.return(Notification.createOnNext(remoteCallResult.notification.value));
                }
                if (remoteCallResult.notification.kind === "OnCompleted") {
                    return Observable.return(Notification.createOnCompleted());
                }
                if (remoteCallResult.notification.kind === "OnError") {
                    return Observable.return(Notification.createOnNext(remoteCallResult.notification.error));
                }
            })
            .dematerialize();
        }
    }
}

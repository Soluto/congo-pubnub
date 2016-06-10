var Observable = require('rx').Observable;
var PUBNUB = require('pubnub');

module.exports = function(pnConfiguration, remoteCallsChannel) {
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
                    channel : remoteCallsChannel + "_" + remoteCall.correlationId,
                    message : function(message) {
                        var m = JSON.parse(message)
                        if (m.type === "onNext") {
                            observer.onNext(m);
                        }
                        else if (m.type === "onCompleted") {
                            observer.onCompleted();
                            pubnub.unsubscribe({channel : remoteCallsChannel + "_" + remoteCall.correlationId});
                        }
                        else if (m.type === "onError") {
                            observer.onError(m.errorItem);
                            pubnub.unsubscribe({channel : remoteCallsChannel + "_" + remoteCall.correlationId});
                        }
                    },
                    error: function (pubnubError) {
                        console.log(pubnubError);
                        observer.onError(pubnubError);
                    }
                });
                console.log("publish " + JSON.stringify(remoteCall));
                pubnub.publish({
                    channel : remoteCallsChannel,
                    message : remoteCall,
                    error : function(publishError) {
                        observer.onError(publishError);
                    }
                });

                return function() {
                    var cancelRemoteCall = Object.assign({}, remoteCall, {isCancelled: true})
                    pubnub.publish({
                        channel : remoteCallsChannel,
                        message : cancelRemoteCall,
                        error : function(publishError) {
                            observer.onError(publishError);
                        }
                    });
                };
            });
        }
    }
}

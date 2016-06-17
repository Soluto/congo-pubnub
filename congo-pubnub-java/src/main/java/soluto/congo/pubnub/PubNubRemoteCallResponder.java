package soluto.congo.pubnub;

import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import soluto.congo.core.RemoteCallResponder;
import soluto.congo.core.RemoteCallResult;

public class PubNubRemoteCallResponder implements RemoteCallResponder {
    private PubNub pubNub;
    private String responseChannel;

    public PubNubRemoteCallResponder(PNConfiguration pnConfiguration, String responseChannel) {
        this.pubNub = new PubNub(pnConfiguration);
        this.responseChannel = responseChannel;
    }

    @Override
    public Completable respond(final RemoteCallResult remoteCallResult) {
        return Observable
                .create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(final Subscriber<? super Object> subscriber) {
                        pubNub.publish()
                                .message(new Gson().toJson(remoteCallResult))
                                .channel(responseChannel)
                                .async(new PNCallback<PNPublishResult>() {
                                    @Override
                                    public void onResponse(PNPublishResult result, PNStatus status) {
                                        if (!status.isError()) {
                                            subscriber.onCompleted();
                                        }
                                        else {
                                            subscriber.onError(new Exception("failed to publish remote call result"));
                                        }
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }
}

package soluto.congo.pubnub;

import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

import rx.Observable;
import rx.subjects.PublishSubject;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallListener;

public class PubNubRemoteCallListener implements RemoteCallListener {
    private PublishSubject<RemoteCall> remoteCalls = PublishSubject.create();
    private PubNub pubNub;
    private String requestChannel;

    public PubNubRemoteCallListener(PNConfiguration pnConfiguration, String requestChannel) {
        this.requestChannel = requestChannel;
        pubNub = new PubNub(pnConfiguration);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult pubnubMessage) {
                String serializedRemoteCall = pubnubMessage.getMessage().toString();
                RemoteCall remoteCall = new Gson().fromJson(serializedRemoteCall, RemoteCall.class);
                remoteCalls.onNext(remoteCall);
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    @Override
    public Observable<RemoteCall> getRemoteCalls() {
        pubNub.subscribe().channels(Arrays.asList(requestChannel)).execute();
        return remoteCalls;
    }
}

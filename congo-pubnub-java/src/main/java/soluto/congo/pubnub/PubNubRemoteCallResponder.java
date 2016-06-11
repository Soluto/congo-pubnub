package soluto.congo.pubnub;

import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import soluto.congo.core.BridgeErrorMessage;
import soluto.congo.core.RemoteCall;
import soluto.congo.core.RemoteCallResponder;

import java.util.HashMap;
import java.util.Map;

public class PubNubRemoteCallResponder implements RemoteCallResponder {
    private PubNub pubNub;
    private String remoteCallsChannel;
    private int startTimestamp;

    public PubNubRemoteCallResponder(PNConfiguration pnConfiguration, String remoteCallsChannel) {
        pubNub = new PubNub(pnConfiguration);
        startTimestamp = pubNub.getTimestamp();
        this.remoteCallsChannel = remoteCallsChannel;
    }

    @Override
    public void onNext(RemoteCall remoteCall, Object nextItem) {
        try {
            long timetoken = pubNub.time().sync().getTimetoken() / 10000;
            int delay = pubNub.getTimestamp() - startTimestamp;
            long orderId = timetoken + delay;

            Map<String,Object> message = new HashMap<>();
            message.put("type", "onNext");
            message.put("nextItem", nextItem);
            message.put("orderId", orderId);

            pubNub.publish()
                    .message(new Gson().toJson(message))
                    .channel(remoteCallsChannel + "_" + remoteCall.correlationId)
                    .sync();
        }
        catch (PubNubException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted(RemoteCall remoteCall) {
        try {
            long timetoken = pubNub.time().sync().getTimetoken() / 10000;
            int delay = pubNub.getTimestamp() - startTimestamp;
            long orderId = timetoken + delay;

            Map<String,Object> message = new HashMap<>();
            message.put("type", "onCompleted");
            message.put("orderId", orderId);

            pubNub.publish()
                    .message(new Gson().toJson(message))
                    .channel(remoteCallsChannel + "_" + remoteCall.correlationId)
                    .sync();
        }
        catch (PubNubException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(RemoteCall remoteCall, Throwable exception) {
        Map<String,Object> message = new HashMap<>();
        message.put("type", "onError");
        message.put("errorItem", new BridgeErrorMessage(exception.getMessage()));
        pubNub.publish()
                .message(new Gson().toJson(message))
                .channel(remoteCallsChannel + "_" + remoteCall.correlationId)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                    }
                });
    }
}

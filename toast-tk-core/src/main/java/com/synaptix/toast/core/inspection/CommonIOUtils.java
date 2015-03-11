package com.synaptix.toast.core.inspection;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.ExistsResponse;
import com.synaptix.toast.automation.net.HighLightRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.automation.net.InitInspectionRequest;
import com.synaptix.toast.automation.net.InitResponse;
import com.synaptix.toast.automation.net.PoisonPill;
import com.synaptix.toast.automation.net.RecordRequest;
import com.synaptix.toast.automation.net.RecordResponse;
import com.synaptix.toast.automation.net.ScanRequest;
import com.synaptix.toast.automation.net.ScanResponse;
import com.synaptix.toast.automation.net.TableCommandRequest;
import com.synaptix.toast.automation.net.TableCommandRequestQuery;
import com.synaptix.toast.automation.net.ValueResponse;
import com.synaptix.toast.automation.net.CommandRequest.COMMAND_TYPE;
import com.synaptix.toast.core.interpret.EventCapturedObject;
import com.synaptix.toast.core.interpret.IEventInterpreter.EventType;

/**
 * Created by Sallah Kokaina on 12/11/2014.
 */
public class CommonIOUtils {
    public static final int TCP_PORT = 1470;

    public static void initSerialization(Kryo kryo) {
        kryo.register(ArrayList.class);
        kryo.register(COMMAND_TYPE.class);
        kryo.register(InitInspectionRequest.class);
        kryo.register(CommandRequest.class);
        kryo.register(TableCommandRequestQuery.class);
        kryo.register(TableCommandRequest.class);

        kryo.register(EventType.class);
        kryo.register(EventCapturedObject.class);
        kryo.register(IIdRequest.class);
        kryo.register(ScanRequest.class);
        kryo.register(RecordRequest.class);
        kryo.register(HighLightRequest.class);
        
        kryo.register(ExistsResponse.class);
        kryo.register(ValueResponse.class);
        kryo.register(InitResponse.class);
        kryo.register(ScanResponse.class);
        kryo.register(RecordResponse.class);
        
        kryo.register(PoisonPill.class);

    }
}

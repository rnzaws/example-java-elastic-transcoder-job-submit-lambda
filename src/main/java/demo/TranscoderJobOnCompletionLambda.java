
package demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;

public class TranscoderJobOnCompletionLambda {

  public void handleRequest(final SNSEvent pSnsEvent, final Context pContext) {
    try {
        for (final SNSRecord record : pSnsEvent.getRecords()) {
          System.out.println(record.getSNS().getMessage());
        }
    } catch (final Exception e) { throw new IllegalStateException(e); }

  }
}



package demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.elastictranscoder.model.CreateJobPlaylist;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

public class TranscoderJobLambda {

  private static final String HLS_64K_AUDIO_PRESET_ID = "1351620000001-200071";
  private static final String HLS_0400K_PRESET_ID     = "1351620000001-200050";
  private static final String HLS_0600K_PRESET_ID     = "1351620000001-200040";

  private static final String SEGMENT_DURATION = "2";

  public void handleRequest(final S3EventNotification pEvent, final Context pContext) {

    try {
      final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

      // Create the new elastic transcoder job(s)
      final AmazonElasticTranscoder client = AmazonElasticTranscoderClientBuilder.defaultClient();
      for (S3EventNotificationRecord record : pEvent.getRecords()) {

        final JobInput input = new JobInput().withKey(record.getS3().getObject().getKey());
        final Collection<CreateJobOutput> outputs = createJobOutputs(record.getS3().getObject().getKey());

        final Collection<String> outputKeys = new ArrayList<>();
        outputs.forEach((output) -> outputKeys.add(output.getKey()) );

        final CreateJobPlaylist playlist
        = new CreateJobPlaylist().withName("hls_" + record.getS3().getObject().getKey()).withFormat("HLSv3").withOutputKeys(outputKeys);

        final CreateJobResult result = client.createJob(
          new CreateJobRequest()
          .withPipelineId(System.getenv("PIPELINE_ID"))
          .withInput(input)
          .withOutputs(outputs)
          .withPlaylists(playlist)
        );
      }

    } catch (final Exception e) { throw new IllegalStateException(e); }
  }

  private Collection<CreateJobOutput> createJobOutputs(final String pOutputKey) throws Exception {
    return Arrays.asList(
      new CreateJobOutput().withKey("hlsAudio/" + pOutputKey).withPresetId(HLS_64K_AUDIO_PRESET_ID).withSegmentDuration(SEGMENT_DURATION),
      new CreateJobOutput().withKey("hls0400k/" + pOutputKey).withPresetId(HLS_0400K_PRESET_ID).withSegmentDuration(SEGMENT_DURATION),
      new CreateJobOutput().withKey("hls0600k/" + pOutputKey).withPresetId(HLS_0600K_PRESET_ID).withSegmentDuration(SEGMENT_DURATION)
    );
  }
}


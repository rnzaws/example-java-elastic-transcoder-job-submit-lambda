#!/bin/bash

gradle build

aws lambda update-function-code --function-name TranscoderJobLambda --zip-file fileb://build/distributions/example-java-elastic-transcoder-job-submit-lambda.zip

aws lambda update-function-code --function-name TranscoderJobOnCompletionLambda --zip-file fileb://build/distributions/example-java-elastic-transcoder-job-submit-lambda.zip

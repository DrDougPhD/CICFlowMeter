#!/usr/bin/env bash

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
mkdir ./data/
./aws/dist/aws s3 sync --no-sign-request --region us-west-2 "s3://cse-cic-ids2018/" ./data/

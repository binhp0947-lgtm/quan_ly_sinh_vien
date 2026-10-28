#!/bin/bash
set -e

echo "=========================================================="
echo "   TRIEN KHAI COURSE MANAGEMENT SYSTEM LEN CLOUD RUN      "
echo "=========================================================="

echo "[1/3] Bat cac Google Cloud APIs can thiet..."
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com

echo "[2/3] Tien hanh build va deploy len Cloud Run..."
gcloud run deploy course-management \
  --source . \
  --region asia-southeast1 \
  --allow-unauthenticated \
  --port 8080 \
  --memory 512Mi \
  --cpu 1

echo ""
echo "=========================================================="
echo "   DEPLOY THANH CONG! VUI LONG LAY LINK SERVICE O TREN    "
echo "=========================================================="

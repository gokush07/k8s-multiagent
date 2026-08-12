#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=== SRE Multi-Agent K8s Lab Bootstrap ==="
echo ""

# 1. Check prerequisites
echo "[1/6] Checking prerequisites..."
if ! docker info >/dev/null 2>&1; then
    echo "ERROR: Docker Desktop is not running. Please start Docker Desktop and re-run this script."
    exit 1
fi

for cmd in kind kubectl helm mvn; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        echo "ERROR: '$cmd' is not installed or not in PATH."
        exit 1
    fi
done

# 2. Create kind cluster & ensure context
echo "[2/6] Setting up kind cluster (kind-sre-lab)..."
if ! kind get clusters 2>/dev/null | grep -q "^sre-lab$"; then
    kind create cluster --name sre-lab --config kind-config.yaml
else
    echo "Cluster 'sre-lab' already exists."
fi
kubectl config use-context kind-sre-lab >/dev/null

# 3. Deploy Observability & Core Stack
echo "[3/6] Deploying Prometheus, Grafana, Kafka, ZooKeeper, and PostgreSQL..."
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts >/dev/null 2>&1 || true
helm repo update prometheus-community >/dev/null 2>&1 || true
helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
    -n monitoring --create-namespace -f k8s/observability/values/kube-prometheus-stack.yaml >/dev/null

kubectl apply -f k8s/app/namespace.yaml >/dev/null
kubectl apply -f k8s/app/postgres-secret.yaml >/dev/null
kubectl apply -f k8s/app/configmap.yaml >/dev/null
kubectl apply -f k8s/postgres.yaml >/dev/null
kubectl apply -f k8s/postgres-exporter.yaml >/dev/null
kubectl apply -f k8s/kafka-simple.yaml >/dev/null
kubectl apply -f k8s/kafka-exporter.yaml >/dev/null
kubectl apply -f k8s/app/service.yaml >/dev/null
kubectl apply -f k8s/app/servicemonitor.yaml >/dev/null
kubectl apply -f k8s/app/deployment.yaml >/dev/null
kubectl apply -f k8s/app/ingress.yaml >/dev/null
kubectl apply -f k8s/sharepoint-grafana-dashboard.yaml >/dev/null

# 4. Build and load Java Application image
echo "[4/6] Building Java app and loading into cluster..."
mvn -q -f app/pom.xml package -DskipTests >/dev/null
docker build -t lab-app:latest -f app/Dockerfile app >/dev/null
kind load docker-image lab-app:latest --name sre-lab >/dev/null

kubectl rollout restart deployment/lab-app -n lab >/dev/null
echo "Waiting for workloads to be Ready..."
kubectl rollout status deployment/postgres -n lab --timeout=240s >/dev/null
kubectl rollout status deployment/postgres-exporter -n lab --timeout=240s >/dev/null
kubectl rollout status deployment/zookeeper -n lab --timeout=240s >/dev/null
kubectl rollout status deployment/kafka -n lab --timeout=240s >/dev/null
kubectl rollout status deployment/kafka-exporter -n lab --timeout=240s >/dev/null
kubectl rollout status deployment/lab-app -n lab --timeout=240s >/dev/null

# Restore latest DB state snapshot if present
if [ -f "$ROOT_DIR/backups/labdb_snapshot_latest.sql" ]; then
    echo "Restoring state from latest DB snapshot..."
    "$ROOT_DIR/scripts/restore-db.sh" "$ROOT_DIR/backups/labdb_snapshot_latest.sql" >/dev/null 2>&1 || true
fi

# 5. Automatically setup Port Forwarding in background
echo "[5/6] Starting background port-forwarding..."
# Kill any previous port-forward processes for 8080, 9090, 3000
pkill -f "port-forward.*(8080|9090|3000)" 2>/dev/null || true
sleep 1

kubectl --context kind-sre-lab port-forward -n lab svc/lab-app 8080:80 >/dev/null 2>&1 &
kubectl --context kind-sre-lab port-forward -n monitoring svc/kube-prometheus-stack-prometheus 9090:9090 >/dev/null 2>&1 &
kubectl --context kind-sre-lab port-forward -n monitoring svc/kube-prometheus-stack-grafana 3000:80 >/dev/null 2>&1 &

# 6. Verify local endpoints
echo "[6/6] Verifying local endpoints..."
for i in {1..15}; do
    if curl -s http://127.0.0.1:8080/health | grep -q "ok" && \
       curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:9090/ | grep -qE "200|302" && \
       curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:3000/ | grep -qE "200|302"; then
        break
    fi
    sleep 1
done

echo ""
echo "=================================================="
echo " SUCCESS! SRE Lab environment is fully up & ready!"
echo "=================================================="
echo ""
echo "Access points:"
echo "  • App UI:          http://127.0.0.1:8080/"
echo "  • Health Endpoint: http://127.0.0.1:8080/health"
echo "  • Prometheus UI:   http://127.0.0.1:9090/"
echo "  • Grafana UI:      http://127.0.0.1:3000/ (admin/admin)"
echo ""


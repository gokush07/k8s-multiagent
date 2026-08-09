# SRE Multi-Agent Kubernetes Lab

This repository provisions a local Kubernetes lab on Docker Desktop using kind, with:

- a 3-node kind cluster
- Prometheus and Grafana via kube-prometheus-stack
- Kafka + ZooKeeper
- PostgreSQL
- a lightweight Spring Boot Java app with a web UI, DB persistence, Kafka publish/consume, and Prometheus metrics

## Prerequisites

- Docker Desktop with the Docker engine running
- kind
- kubectl
- helm
- Java 21 + Maven (only needed to rebuild the app image)

## Quick start (Single Command)

With Docker Desktop running, simply execute:

```bash
./scripts/bootstrap.sh
```

The bootstrap script automatically handles the entire end-to-end setup in one command:

1. **Pre-flight checks**: Verifies Docker Desktop is running and required tools are installed.
2. **Cluster creation**: Provisions the 3-node `sre-lab` Kind cluster and switches your `kubectl` context to `kind-sre-lab`.
3. **Observability & Core Stack**: Installs Prometheus/Grafana and deploys Kafka, ZooKeeper, and PostgreSQL.
4. **App Build & Load**: Compiles the Spring Boot app, builds the Docker image, and loads it into the cluster.
5. **Workload Rollouts**: Waits for all deployments to reach `Ready` status.
6. **Automatic Port Forwarding**: Backgrounds port-forwarding for App UI (8080), Prometheus (9090), and Grafana (3000).
7. **Health Verification**: Polls and verifies all local HTTP endpoints.

## Local access

Once `bootstrap.sh` completes, access your endpoints directly:
- App UI: http://127.0.0.1:8080/
- Health endpoint: http://127.0.0.1:8080/health
- Prometheus: http://127.0.0.1:9090/
- Grafana: http://127.0.0.1:3000/ (admin/admin)

**Or via NodePort (after bootstrap):**
- Get the control plane IP: `kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}'`
- Prometheus: `http://<control-plane-ip>:30090/`
- Grafana: `http://<control-plane-ip>:30000/`

## Verification commands

```bash
kubectl get nodes
kubectl get pods -A
kubectl get servicemonitors -n monitoring
curl http://127.0.0.1:8080/health
curl --get --data-urlencode 'query=up{job="lab-app"}' http://127.0.0.1:9090/api/v1/query
```

## State Persistence & Backup Management

SRE best practices are implemented across all stateful components:

1. **K8s Persistent Volumes (PVCs)**:
   - **PostgreSQL**: Configured with `postgres-pvc` (2Gi).
   - **Kafka**: Configured with `kafka-pvc` (5Gi) for topic logs and partition offsets.
   - **ZooKeeper**: Configured with `zookeeper-pvc` (2Gi) for cluster state.

2. **Database Snapshot Backups**:
   - Save current state: `./scripts/backup-db.sh` (saves to `backups/labdb_snapshot_<timestamp>.sql` and updates `labdb_snapshot_latest.sql`).
   - Restore state: `./scripts/restore-db.sh` (restores latest or specified `.sql` snapshot).
   - Auto-restore: `./scripts/bootstrap.sh` automatically restores `backups/labdb_snapshot_latest.sql` if present.

## Notes

- The app image is built locally and loaded into the kind cluster.
- The app persists messages in PostgreSQL and also writes a consumed copy back to PostgreSQL via the Kafka listener.
- Prometheus scrapes the app on `/actuator/prometheus`, Postgres on port `9187`, and Kafka on port `9308`.

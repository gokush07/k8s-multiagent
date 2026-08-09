#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_DIR="${ROOT_DIR}/backups"
mkdir -p "$BACKUP_DIR"

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/labdb_snapshot_${TIMESTAMP}.sql"
LATEST_LINK="${BACKUP_DIR}/labdb_snapshot_latest.sql"

echo "=== SRE Database Snapshot Backup ==="
echo "Backing up PostgreSQL 'labdb' database..."

POSTGRES_POD=$(kubectl --context kind-sre-lab get pods -n lab -l app=postgres -o jsonpath='{.items[0].metadata.name}')

kubectl --context kind-sre-lab exec -n lab "$POSTGRES_POD" -- \
    pg_dump -U labuser labdb > "$BACKUP_FILE"

cp "$BACKUP_FILE" "$LATEST_LINK"

echo "Backup successful!"
echo "Snapshot saved to: $BACKUP_FILE"
echo "Latest symlink:   $LATEST_LINK"

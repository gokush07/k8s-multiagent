#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_FILE="${1:-${ROOT_DIR}/backups/labdb_snapshot_latest.sql}"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "ERROR: Backup file '$BACKUP_FILE' not found."
    exit 1
fi

echo "=== SRE Database Snapshot Restore ==="
echo "Restoring PostgreSQL database from: $BACKUP_FILE"

POSTGRES_POD=$(kubectl --context kind-sre-lab get pods -n lab -l app=postgres -o jsonpath='{.items[0].metadata.name}')

kubectl --context kind-sre-lab exec -i -n lab "$POSTGRES_POD" -- \
    psql -U labuser -d labdb < "$BACKUP_FILE"

echo "Restore successful!"

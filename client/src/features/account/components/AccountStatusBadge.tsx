interface AccountStatusBadgeProps {
  status: string;
}

export default function AccountStatusBadge({
  status,
}: AccountStatusBadgeProps) {

  const normalizedStatus = status?.toUpperCase() ?? "UNKNOWN";

  const statusClass =
    normalizedStatus === "ACTIVE"
      ? "status-active"
      : normalizedStatus === "CLOSED"
      ? "status-closed"
      : normalizedStatus === "DORMANT"
      ? "status-dormant"
      : normalizedStatus === "FROZEN"
      ? "status-frozen"
      : normalizedStatus === "BLOCKED"
      ? "status-blocked"
      : "status-unknown";

  return (
    <span className={`account-status ${statusClass}`}>
      <span className="status-dot" />
      {normalizedStatus}
    </span>
  );
}

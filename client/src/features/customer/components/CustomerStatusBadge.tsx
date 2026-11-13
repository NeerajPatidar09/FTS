
interface CustomerStatusBadgeProps {
  status: string;
}

export default function CustomerStatusBadge({
  status,
}: CustomerStatusBadgeProps) {

  const normalizedStatus = status?.toUpperCase() ?? "UNKNOWN";

  const statusClass =
    normalizedStatus === "ACTIVE"
      ? "status-active"
      : normalizedStatus === "INACTIVE"
      ? "status-inactive"
      : normalizedStatus === "SUSPENDED"
      ? "status-suspended"
      : "status-unknown";

  return (
    <span className={`customer-status ${statusClass}`}>
      <span className="status-dot" />
      {normalizedStatus}
    </span>
  );
}


import {
  ShieldCheck,
} from "lucide-react";

import type { Permission } from "../types/permission.types";

interface PermissionTableProps {
  permissions: Permission[];
}

export default function PermissionTable({
  permissions,
}: PermissionTableProps) {

  return (
    <div className="permission-table-container">

      <table className="permission-table">

        <thead>
          <tr>
            <th>Permission</th>
            <th>Permission Code</th>
            <th>Description</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>

          {permissions.map((permission) => (

            <tr key={permission.id}>

              <td>
                <div className="permission-name-cell">

                  <div className="permission-icon">
                    <ShieldCheck size={18} />
                  </div>

                  <div className="permission-name-info">
                    <strong>{permission.permissionName}</strong>
                    <span>Permission ID: {permission.id}</span>
                  </div>

                </div>
              </td>

              <td>
                <span className="permission-code">
                  {permission.permissionCode}
                </span>
              </td>

              <td>
                <div className="permission-description">
                  {permission.description || "No description"}
                </div>
              </td>

              <td>
                <span
                  className={`permission-status ${
                    permission.active ? "active" : "inactive"
                  }`}
                >
                  <span className="permission-status-dot" />
                  {permission.active ? "Active" : "Inactive"}
                </span>
              </td>

            </tr>

          ))}

          {permissions.length === 0 && (
            <tr>
              <td colSpan={4}>
                <div className="permission-empty">
                  No permissions found.
                </div>
              </td>
            </tr>
          )}

        </tbody>

      </table>

    </div>
  );
}

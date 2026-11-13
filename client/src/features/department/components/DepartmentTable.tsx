
import {
  Building2,
  Pencil,
  Power,
} from "lucide-react";

import type { Department } from "../types/department.types";

interface DepartmentTableProps {
  departments: Department[];
  onEdit: (department: Department) => void;
  onDeactivate: (department: Department) => void;
  canUpdate: boolean;
  deactivatingId?: number | null;
}

export default function DepartmentTable({
  departments,
  onEdit,
  onDeactivate,
  canUpdate,
}: DepartmentTableProps) {

  return (
    <div className="department-table-container">

      <table className="department-table">

        <thead>
          <tr>
            <th>Department</th>
            <th>Department Code</th>
            <th>Department ID</th>
            <th>Status</th>
            {canUpdate && <th>Actions</th>}
          </tr>
        </thead>

        <tbody>

          {departments.map((department) => (

            <tr key={department.id}>

              <td>
                <div className="department-name-cell">

                  <div className="department-icon">
                    <Building2 size={18} />
                  </div>

                  <div className="department-name-info">
                    <strong>{department.departmentName}</strong>
                    <span>Department</span>
                  </div>

                </div>
              </td>

              <td>
                <span className="department-code">
                  {department.departmentCode}
                </span>
              </td>

              <td>
                #{department.id}
              </td>

              <td>
                <span
                  className={`department-status ${
                    department.active ? "active" : "inactive"
                  }`}
                >
                  <span className="department-status-dot" />
                  {department.active ? "Active" : "Inactive"}
                </span>
              </td>

              {canUpdate && (
                <td>
                  <div className="department-actions">

                    <button
                      type="button"
                      className="department-edit-btn"
                      onClick={() => onEdit(department)}
                      title="Edit department"
                    >
                      <Pencil size={15} />
                      Edit
                    </button>

                    {department.active && (
                      <button
                        type="button"
                        className="department-deactivate-btn"
                        onClick={() => onDeactivate(department)}
                        title="Deactivate department"
                      >
                        <Power size={15} />
                        Deactivate
                      </button>
                    )}

                  </div>
                </td>
              )}

            </tr>

          ))}

          {departments.length === 0 && (
            <tr>
              <td colSpan={canUpdate ? 5 : 4}>
                <div className="department-empty">
                  No departments found.
                </div>
              </td>
            </tr>
          )}

        </tbody>

      </table>

    </div>
  );
}

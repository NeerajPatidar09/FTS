
import { useEffect, useMemo, useState } from "react";

import {
  ShieldCheck,
  X,
  Search,
  CheckSquare,
  Square,
  LoaderCircle,
} from "lucide-react";

import { getAllPermissions } from "../../permission/api/permissionApi";

import type { Permission } from "../../permission/types/permission.types";

import type { Role } from "../types/role.types";

import { updateRolePermissions } from "../api/roleApi";

interface RolePermissionModalProps {
  role: Role;
  onClose: () => void;
  onSaved: () => void;
}

const getPermissionGroup = (code: string): string => {

  const prefix = code.split("_")[0];

  const groups: Record<string, string> = {
    CUSTOMER: "Customer Management",
    ACCOUNT: "Account Management",
    ALERT: "Alert Management",
    INVESTIGATION: "Investigation Management",
    USER: "User Management",
    ROLE: "Role Management",
    PERMISSION: "Permission Management",
    EMPLOYEE: "Employee Management",
    MENU: "Menu Management",
    DEPARTMENT: "Department Management",
  };

  return groups[prefix] ?? "Other";
};

export default function RolePermissionModal({
  role,
  onClose,
  onSaved,
}: RolePermissionModalProps) {

  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [initialized, setInitialized] = useState(false);

  // Load permissions and match existing role permissions
  useEffect(() => {

    const fetchPermissions = async () => {
      try {
        setLoading(true);
        setError("");
        const response = await getAllPermissions();
        setPermissions(response);
        const existingPermissionIds = response
          .filter((permission) => role.permissions?.includes(permission.permissionCode)
          ).map((permission) => permission.id);

        setSelectedIds(existingPermissionIds);
        setInitialized(true);

      } catch (error) {

        console.error("Failed to fetch permissions:", error);

        setError("Unable to load permissions.");

      } finally {

        setLoading(false);

      }
    };

    fetchPermissions();

  }, [role.id, role.permissions]);

  // Filter permissions
  const filteredPermissions = useMemo(() => {

    return permissions.filter((permission) => {

      const matchesSearch =
        `${permission.permissionCode} ${permission.permissionName}`
          .toLowerCase()
          .includes(search.toLowerCase());

      return matchesSearch;

    });

  }, [permissions, search]);

  // Group permissions
  const groupedPermissions = useMemo(() => {

    return filteredPermissions.reduce<Record<string, Permission[]>>(
      (groups, permission) => {

        const group = getPermissionGroup(permission.permissionCode);

        if (!groups[group]) {
          groups[group] = [];
        }

        groups[group].push(permission);

        return groups;

      },
      {}
    );

  }, [filteredPermissions]);

  // Toggle individual permission
  const togglePermission = (permissionId: number) => {

    setSelectedIds((previous) => {

      if (previous.includes(permissionId)) {

        return previous.filter((id) => id !== permissionId);

      }

      return [...previous, permissionId];

    });

  };

  // Select all active permissions in a group
  const toggleGroup = (groupPermissions: Permission[]) => {

    const availableIds = groupPermissions
      .filter((permission) => permission.active)
      .map((permission) => permission.id);

    const allSelected = availableIds.every((id) =>
      selectedIds.includes(id)
    );

    setSelectedIds((previous) => {

      if (allSelected) {

        return previous.filter((id) =>
          !availableIds.includes(id)
        );

      }

      return Array.from(
        new Set([...previous, ...availableIds])
      );

    });

  };

  // Save permissions
  const handleSave = async () => {

    try {

      setSaving(true);
      setError("");

      await updateRolePermissions(role.id, {
        permissionIds: selectedIds,
      });

      onSaved();

      onClose();

    } catch (error: any) {

      console.error("Failed to update role permissions:", error);

      setError(
        error.response?.data?.message ||
        "Unable to update role permissions."
      );

    } finally {

      setSaving(false);

    }

  };

  if (!initialized && loading) {

    return (
      <div className="role-modal-overlay">
        <div className="role-permission-modal">
          <div className="role-permission-loading">
            <LoaderCircle className="role-spinning" size={25} />
            Loading permissions...
          </div>
        </div>
      </div>
    );

  }

  return (

    <div
      className="role-modal-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !saving) {
          onClose();
        }
      }}
    >

      <div
        className="role-permission-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="role-permission-title"
      >

        {/* Header */}

        <div className="role-permission-header">

          <div className="role-permission-heading">

            <div className="role-permission-heading-icon">
              <ShieldCheck size={22} />
            </div>

            <div>
              <h2 id="role-permission-title">
                Manage Permissions
              </h2>

              <p>
                {role.roleName} ({role.roleCode})
              </p>
            </div>

          </div>

          <button
            className="role-modal-close"
            onClick={onClose}
            disabled={saving}
            aria-label="Close"
          >
            <X size={20} />
          </button>

        </div>

        {/* Selection summary */}

        <div className="role-permission-summary">

          <div>
            <strong>{selectedIds.length}</strong>
            <span> permissions selected</span>
          </div>

          <button
            type="button"
            onClick={() => {
              setSelectedIds(
                permissions
                  .filter((permission) => permission.active)
                  .map((permission) => permission.id)
              );
            }}
          >
            Select All
          </button>

          <button
            type="button"
            onClick={() => {
              setSelectedIds(
                permissions
                  .filter((permission) => !permission.active)
                  .map((permission) => permission.id)
              );
            }}
          >
            Clear Active
          </button>

        </div>

        {/* Search */}

        <div className="role-permission-search">

          <Search size={17} />

          <input
            placeholder="Search permissions..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />

        </div>

        {/* Error */}

        {error && (
          <div className="role-form-error">
            {error}
          </div>
        )}

        {/* Permission list */}

        <div className="role-permission-list">

          {Object.entries(groupedPermissions).map(
            ([group, groupPermissions]) => {

              const availableIds = groupPermissions
                .filter((permission) => permission.active)
                .map((permission) => permission.id);

              const allSelected =
                availableIds.length > 0 &&
                availableIds.every((id) =>
                  selectedIds.includes(id)
                );

              return (

                <div
                  className="role-permission-group"
                  key={group}
                >

                  <div className="role-permission-group-header">

                    <h3>{group}</h3>

                    <button
                      type="button"
                      onClick={() =>
                        toggleGroup(groupPermissions)
                      }
                    >
                      {allSelected
                        ? "Deselect All"
                        : "Select All"}
                    </button>

                  </div>

                  <div className="role-permission-options">

                    {groupPermissions.map((permission) => {

                      const checked = selectedIds.includes(
                        permission.id
                      );

                      return (

                        <label
                          key={permission.id}
                          className={`role-permission-option ${
                            !permission.active ? "disabled" : ""
                          }`}
                        >

                          <input
                            type="checkbox"
                            checked={checked}
                            disabled={!permission.active || saving}
                            onChange={() =>
                              togglePermission(permission.id)
                            }
                          />

                          <span className="role-custom-checkbox">
                            {checked
                              ? <CheckSquare size={18} />
                              : <Square size={18} />}
                          </span>

                          <span className="role-permission-option-info">

                            <strong>
                              {permission.permissionName}
                            </strong>

                            <small>
                              {permission.permissionCode}
                            </small>

                          </span>

                          {!permission.active && (
                            <span className="permission-inactive-label">
                              Inactive
                            </span>
                          )}

                        </label>

                      );

                    })}

                  </div>

                </div>

              );

            }
          )}

          {!loading && filteredPermissions.length === 0 && (
            <div className="role-empty">
              No permissions found.
            </div>
          )}

        </div>

        {/* Footer */}

        <div className="role-permission-footer">

          <button
            className="role-cancel-btn"
            onClick={onClose}
            disabled={saving}
          >
            Cancel
          </button>

          <button
            className="role-submit-btn"
            onClick={handleSave}
            disabled={saving || !initialized}
          >
            {saving ? "Saving..." : "Save Permissions"}
          </button>

        </div>

      </div>

    </div>

  );

}

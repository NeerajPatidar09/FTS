
import { useEffect, useMemo, useState } from "react";
import {
  Search,
  ShieldCheck,
  CheckSquare,
  Square,
  LoaderCircle,
} from "lucide-react";

import { getAllPermissions } from "../../permission/api/permissionApi";
import type { Permission } from "../../permission/types/permission.types";

interface RolePermissionSelectorProps {
  selectedIds: number[];
  onChange: (ids: number[]) => void;
  disabled?: boolean;
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

export default function RolePermissionSelector({
  selectedIds,
  onChange,
  disabled = false,
}: RolePermissionSelectorProps) {

  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  useEffect(() => {
    let cancelled = false;

    const fetchPermissions = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await getAllPermissions();

        if (!cancelled) {
          setPermissions(response);
        }

      } catch (error) {
        console.error("Failed to load permissions:", error);

        if (!cancelled) {
          setError("Unable to load permissions. Please try again.");
        }

      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    fetchPermissions();

    return () => {
      cancelled = true;
    };
  }, []);

  const filteredPermissions = useMemo(() => {
    const keyword = search.trim().toLowerCase();

    if (!keyword) return permissions;

    return permissions.filter((permission) =>
      [
        permission.permissionCode,
        permission.permissionName,
        permission.description,
      ]
        .join(" ")
        .toLowerCase()
        .includes(keyword)
    );
  }, [permissions, search]);

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

  const togglePermission = (id: number) => {
    if (disabled) return;

    const updatedIds = selectedIds.includes(id)
      ? selectedIds.filter((item) => item !== id)
      : [...selectedIds, id];

    onChange(updatedIds);
  };

  const toggleGroup = (groupPermissions: Permission[]) => {
    if (disabled) return;

    const activeIds = groupPermissions
      .filter((permission) => permission.active)
      .map((permission) => permission.id);

    const allSelected =
      activeIds.length > 0 &&
      activeIds.every((id) => selectedIds.includes(id));

    const updatedIds = allSelected
      ? selectedIds.filter((id) => !activeIds.includes(id))
      : [...new Set([...selectedIds, ...activeIds])];

    onChange(updatedIds);
  };

  const selectAll = () => {
    onChange(
      permissions
        .filter((permission) => permission.active)
        .map((permission) => permission.id)
    );
  };

  const clearAll = () => {
    onChange([]);
  };

  if (loading) {
    return (
      <div className="permission-selector-loading">
        <LoaderCircle size={22} className="permission-spinner" />
        Loading permissions...
      </div>
    );
  }

  if (error) {
    return (
      <div className="permission-selector-error">
        {error}
        <button
          type="button"
          onClick={() => window.location.reload()}
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="role-permission-selector">

      {/* Summary */}

      <div className="permission-selector-toolbar">

        <div className="permission-selector-count">
          <ShieldCheck size={18} />

          <strong>{selectedIds.length}</strong>

          <span>permissions selected</span>
        </div>

        <div className="permission-selector-actions">
          <button type="button" onClick={selectAll} disabled={disabled}>
            Select All
          </button>

          <button type="button" onClick={clearAll} disabled={disabled}>
            Clear All
          </button>
        </div>

      </div>

      {/* Search */}

      <div className="permission-selector-search">
        <Search size={18} />

        <input
          type="text"
          placeholder="Search permission by name or code..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          disabled={disabled}
        />
      </div>

      {/* Groups */}

      <div className="permission-selector-groups">

        {Object.entries(groupedPermissions).map(
          ([group, groupPermissions]) => {

            const activeIds = groupPermissions
              .filter((permission) => permission.active)
              .map((permission) => permission.id);

            const allSelected =
              activeIds.length > 0 &&
              activeIds.every((id) => selectedIds.includes(id));

            return (
              <div className="permission-selector-group" key={group}>

                <div className="permission-selector-group-header">

                  <div>
                    <h3>{group}</h3>
                    <span>
                      {activeIds.filter((id) => selectedIds.includes(id)).length}
                      {" / "}
                      {activeIds.length} selected
                    </span>
                  </div>

                  <button
                    type="button"
                    onClick={() => toggleGroup(groupPermissions)}
                    disabled={disabled || activeIds.length === 0}
                  >
                    {allSelected ? "Deselect All" : "Select All"}
                  </button>

                </div>

                <div className="permission-selector-grid">

                  {groupPermissions.map((permission) => {

                    const checked = selectedIds.includes(permission.id);

                    return (
                      <label
                        key={permission.id}
                        className={`permission-selector-item ${
                          checked ? "selected" : ""
                        } ${!permission.active ? "inactive" : ""}`}
                      >

                        <input
                          type="checkbox"
                          checked={checked}
                          disabled={disabled || !permission.active}
                          onChange={() =>
                            togglePermission(permission.id)
                          }
                        />

                        <span className="permission-selector-checkbox">
                          {checked
                            ? <CheckSquare size={19} />
                            : <Square size={19} />}
                        </span>

                        <span className="permission-selector-info">

                          <strong>
                            {permission.permissionName}
                          </strong>

                          <small>
                            {permission.permissionCode}
                          </small>

                          <span className="permission-selector-description">
                            {permission.description}
                          </span>

                        </span>

                      </label>
                    );
                  })}

                </div>

              </div>
            );
          }
        )}

        {filteredPermissions.length === 0 && (
          <div className="permission-selector-empty">
            No permissions found.
          </div>
        )}

      </div>

    </div>
  );
}


import { useEffect, useMemo, useState } from "react";

import {
  ShieldCheck,
  Search,
  RefreshCw,
  KeyRound,
  CheckCircle2,
  Layers,
} from "lucide-react";

import { getAllPermissions } from "../api/permissionApi";

import type { Permission } from "../types/permission.types";

import PermissionTable from "../components/PermissionTable";

import "./PermissionListPage.css";

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
  };

  return groups[prefix] ?? "Other";
};

export default function PermissionListPage() {

  const [permissions, setPermissions] = useState<Permission[]>([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [search, setSearch] = useState("");

  const [selectedGroup, setSelectedGroup] = useState("ALL");

  const fetchPermissions = async () => {

    try {

      setLoading(true);
      setError("");

      const response = await getAllPermissions();

      setPermissions(response);

    } catch (error) {

      console.error("Failed to fetch permissions:", error);

      setError("Unable to load permissions. Please try again.");

    } finally {

      setLoading(false);

    }
  };

  useEffect(() => {
    fetchPermissions();
  }, []);

  // Summary calculations

  const activeCount = permissions.filter(
    (permission) => permission.active
  ).length;

  const inactiveCount = permissions.length - activeCount;

  // Unique groups from backend data

  const groups = useMemo(() => {

    return Array.from(
      new Set(
        permissions.map((permission) =>
          getPermissionGroup(permission.permissionCode)
        )
      )
    ).sort();

  }, [permissions]);

  // Search and group filtering

  const filteredPermissions = useMemo(() => {

    return permissions.filter((permission) => {

      const matchesSearch =
        `${permission.permissionCode} ${permission.permissionName} ${permission.description}`
          .toLowerCase()
          .includes(search.toLowerCase());

      const matchesGroup =
        selectedGroup === "ALL" ||
        getPermissionGroup(permission.permissionCode) === selectedGroup;

      return matchesSearch && matchesGroup;

    });

  }, [permissions, search, selectedGroup]);

  return (

    <div className="permission-page">

      {/* Header */}

      <div className="permission-page-header">

        <div>
          <h1>Permission Management</h1>

          <p>
            Manage application permissions and access controls.
          </p>
        </div>

        <button
          className="permission-refresh-btn"
          onClick={fetchPermissions}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Refresh
        </button>

      </div>

      {/* Summary Cards */}

      <div className="permission-summary-grid">

        <div className="permission-summary-card">

          <div className="permission-summary-icon blue">
            <KeyRound size={21} />
          </div>

          <div>
            <h3>{permissions.length}</h3>
            <p>Total Permissions</p>
          </div>

        </div>

        <div className="permission-summary-card">

          <div className="permission-summary-icon green">
            <CheckCircle2 size={21} />
          </div>

          <div>
            <h3>{activeCount}</h3>
            <p>Active Permissions</p>
          </div>

        </div>

        <div className="permission-summary-card">

          <div className="permission-summary-icon purple">
            <Layers size={21} />
          </div>

          <div>
            <h3>{groups.length}</h3>
            <p>Permission Groups</p>
          </div>

        </div>

        <div className="permission-summary-card">

          <div className="permission-summary-icon orange">
            <ShieldCheck size={21} />
          </div>

          <div>
            <h3>{inactiveCount}</h3>
            <p>Inactive Permissions</p>
          </div>

        </div>

      </div>

      {/* Search and Filter */}

      <div className="permission-toolbar">

        <div className="permission-search">

          <Search size={18} />

          <input
            type="text"
            placeholder="Search permission..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />

        </div>

        <select
          className="permission-group-filter"
          value={selectedGroup}
          onChange={(event) => setSelectedGroup(event.target.value)}
        >

          <option value="ALL">All Groups</option>

          {groups.map((group) => (
            <option key={group} value={group}>
              {group}
            </option>
          ))}

        </select>

      </div>

      {/* Loading */}

      {loading && (
        <div className="permission-message">
          Loading permissions...
        </div>
      )}

      {/* Error */}

      {!loading && error && (

        <div className="permission-message permission-error">

          {error}

          <button onClick={fetchPermissions}>
            Retry
          </button>

        </div>

      )}

      {/* Permission Table */}

      {!loading && !error && (

        <>

          <div className="permission-results-heading">

            <h3>
              All Permissions
            </h3>

            <span>
              Showing {filteredPermissions.length} of {permissions.length}
            </span>

          </div>

          <PermissionTable
            permissions={filteredPermissions}
          />

          <div className="permission-table-footer">
            <span>
              Total records: {filteredPermissions.length}
            </span>
            <span>
              Source: Spring Boot API
            </span>
          </div>

        </>

      )}

    </div>
  );
}

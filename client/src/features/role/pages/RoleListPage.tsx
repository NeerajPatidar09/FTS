
import { useEffect, useState } from "react";
import {
  Shield, RefreshCw, Search, Users, Plus, X, Settings2,
} from "lucide-react";
import { getAllRoles, createRole, deactivateRole } from "../api/roleApi";
import type { Role, CreateRoleRequest } from "../types/role.types";
import { useAuth } from "../../auth/hooks/useAuth";
import "./RoleListPage.css";
import RolePermissionModal from "../components/RolePermissionModal";
// import CreateRoleModal from "../components/CreateRoleModal";
import { Link } from "react-router-dom";


export default function RoleListPage() {

  const { auth } = useAuth();

  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [deactivatingId, setDeactivatingId] = useState<number | null>(null);
  const [actionError, setActionError] = useState("");

  const [form, setForm] = useState<CreateRoleRequest>({
    roleCode: "",
    roleName: "",
    description: "",
  });

  // const [formError, setFormError] = useState("");

  const canCreateRole = auth?.user?.permissions?.includes("ROLE_CREATE") ?? false;

  const canUpdateRole = auth?.user?.permissions?.includes("ROLE_UPDATE") ?? false;

  // Fetch roles
  const fetchRoles = async () => {

    try {
      setLoading(true);
      setError("");

      const response = await getAllRoles();

      setRoles(response);

    } catch (error) {

      console.error("Failed to fetch roles:", error);

      setError("Unable to load roles. Please try again.");

    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  // Handle form inputs
  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {

    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]:
        name === "roleCode"
          ? value.toUpperCase().replace(/\s/g, "_")
          : value,
    }));

    setFormError("");
  };

  // Reset form
  const resetForm = () => {

    setForm({
      roleCode: "",
      roleName: "",
      description: "",
    });

    setFormError("");
  };

  // Create role
  const handleCreateRole = async (
    event: React.FormEvent<HTMLFormElement>
  ) => {

    event.preventDefault();

    setFormError("");
    setSuccess("");

    const roleCode = form.roleCode.trim().toUpperCase();
    const roleName = form.roleName.trim();
    const description = form.description.trim();

    if (!roleCode || !roleName || !description) {
      setFormError("Please fill in all fields.");
      return;
    }

    if (!/^[A-Z][A-Z0-9_]*$/.test(roleCode)) {
      setFormError(
        "Role Code must start with a letter and contain only uppercase letters, numbers or underscores."
      );
      return;
    }

    const duplicate = roles.some(
      (role) =>
        role.roleCode.toUpperCase() === roleCode
    );

    if (duplicate) {
      setFormError("This Role Code already exists.");
      return;
    }

    const request: CreateRoleRequest = {
      roleCode,
      roleName,
      description,
    };

    try {

      setSaving(true);

      await createRole(request);

      setModalOpen(false);
      resetForm();

      setSuccess("Role created successfully.");

      await fetchRoles();

    } catch (error: any) {

      console.error("Failed to create role:", error);

      setFormError(
        error.response?.data?.message ||
        "Unable to create role. Please try again."
      );

    } finally {
      setSaving(false);
    }
  };


  const handleDeactivateRole = async (role: Role) => {

    if (!role.active) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to deactivate the role "${role.roleName}"?`
    );

    if (!confirmed) {
      return;
    }

    try {

      setDeactivatingId(role.id);
      setActionError("");

      await deactivateRole(role.id);

      // Refresh role list after successful deactivation
      await fetchRoles();

    } catch (error: any) {

      console.error("Failed to deactivate role:", error);

      setActionError(
        error.response?.data?.message ||
        "Unable to deactivate role. Please try again."
      );

    } finally {

      setDeactivatingId(null);

    }
  };


  // Search
  const filteredRoles = roles.filter((role) =>
    `${role.roleCode} ${role.roleName} ${role.description}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (

    <div className="role-page">

      {/* Header */}

      <div className="role-page-header">

        <div className="role-page-title">
          <h1>Role Management</h1>

          <p>
            Manage application roles and permissions.
          </p>
        </div>

        <div className="role-header-actions">

          <button
            className="role-refresh-btn"
            onClick={fetchRoles}
            disabled={loading}
          >
            <RefreshCw size={16} />
            Refresh
          </button>

          {canCreateRole && (
            <Link
              to="/roles/create"
              className="role-create-btn"
            >
              <Plus size={17} />
              Create Role
            </Link>
          )}

        </div>

      </div>

      {/* Success Message */}

      {success && (
        <div className="role-success-message">
          {success}
          <button onClick={() => setSuccess("")}>
            <X size={16} />
          </button>
        </div>
      )}

      {/* Search */}

      <div className="role-search-container">

        <Search size={18} />

        <input
          type="text"
          placeholder="Search roles..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />

      </div>

      {/* Loading */}

      {loading && (
        <div className="role-message">
          Loading roles...
        </div>
      )}

      {/* Error */}

      {!loading && error && (
        <div className="role-message role-error">
          {error}
          <button onClick={fetchRoles}>Retry</button>
        </div>
      )}

      {/* Role Table */}

      {actionError && (
        <div className="role-form-error">
          {actionError}
        </div>
      )}

      {!loading && !error && (

        <div className="role-table-container">

          <table className="role-table">

            <thead>
              <tr>
                <th>Role</th>
                <th>Role Code</th>
                <th>Description</th>
                <th>Permissions</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>

              {filteredRoles.map((role) => (

                <tr key={role.id}>

                  <td>
                    <div className="role-name-cell">

                      <div className="role-icon">
                        <Shield size={19} />
                      </div>

                      <div className="role-name-info">
                        <strong>{role.roleName}</strong>
                        <span>Role ID: {role.id}</span>
                      </div>

                    </div>
                  </td>

                  <td>
                    <span className="role-code">
                      {role.roleCode}
                    </span>
                  </td>

                  <td>
                    <div className="role-description">
                      {role.description || "No description"}
                    </div>
                  </td>

                  <td>
                    <span className="role-permission-count">
                      {role.permissions?.length ?? 0}
                    </span>
                  </td>

                  <td>
                    <span
                      className={`role-status ${role.active ? "active" : "inactive"
                        }`}
                    >
                      <span className="role-status-dot" />
                      {role.active ? "Active" : "Inactive"}
                    </span>
                  </td>

                  <td>
                    {canUpdateRole && (
                      <button
                        type="button"
                        className="role-manage-permissions-btn"
                        onClick={() => setSelectedRole(role)}
                      >
                        <Settings2 size={15} />
                        Manage Permissions
                      </button>
                    )}
                    {/* Deactivate */}
                    {canUpdateRole && role.active && (
                      <button
                        type="button"
                        className="role-deactivate-btn"
                        onClick={() => handleDeactivateRole(role)}
                        disabled={deactivatingId === role.id}
                      >
                        {deactivatingId === role.id
                          ? "Deactivating..."
                          : "Deactivate"}
                      </button>
                    )}

                    {/* Inactive status */}
                    {!role.active && (
                      <span className="role-inactive-label">
                        Inactive
                      </span>
                    )}

                  </td>

                </tr>

              ))}


              {filteredRoles.length === 0 && (
                <tr>
                  <td colSpan={6}>
                    <div className="role-empty">
                      <Users size={30} />
                      <p>No roles found.</p>
                    </div>
                  </td>
                </tr>
              )}

            </tbody>

          </table>

          <div className="role-table-footer">
            <span>
              Showing {filteredRoles.length} of {roles.length} roles
            </span>

            <span>
              Total Roles: {roles.length}
            </span>
          </div>

        </div>

      )}
      {selectedRole && (
        <RolePermissionModal
          role={selectedRole}
          onClose={() => setSelectedRole(null)}
          onSaved={fetchRoles}
        />
      )}

    </div>
  );
}

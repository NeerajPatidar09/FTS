import { useCallback, useEffect, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import {
  Shield,
  RefreshCw,
  Search,
  Users,
  Plus,
  X,
  Settings2,
} from "lucide-react";

import { getAllRoles, createRole, deactivateRole } from "../api/roleApi";
import type { Role, CreateRoleRequest } from "../types/role.types";
import { useAuth } from "../../auth/hooks/useAuth";
import RolePermissionModal from "../components/RolePermissionModal";
import "./RoleListPage.css";

export default function RoleListPage() {
  const { auth } = useAuth();

  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
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

  const canCreateRole = auth?.permissions?.includes("ROLE_CREATE") ?? false;
  const canUpdateRole = auth?.permissions?.includes("ROLE_UPDATE") ?? false;

  const fetchRoles = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllRoles();
      setRoles(response);
    } catch (fetchError) {
      console.error("Failed to fetch roles:", fetchError);
      setError("Unable to load roles. Please try again.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void fetchRoles();
  }, [fetchRoles]);

  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]:
        name === "roleCode" ? value.toUpperCase().replace(/\s/g, "_") : value,
    }));

    setFormError("");
  };

  const resetForm = () => {
    setForm({ roleCode: "", roleName: "", description: "" });
    setFormError("");
  };

  const closeCreateModal = () => {
    if (saving) return;
    setModalOpen(false);
    resetForm();
  };

  const handleCreateRole = async (event: FormEvent<HTMLFormElement>) => {
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
        "Role Code must start with a letter and contain only uppercase letters, numbers or underscores.",
      );
      return;
    }

    const duplicate = roles.some(
      (role) => role.roleCode.toUpperCase() === roleCode,
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
    } catch (createError: unknown) {
      console.error("Failed to create role:", createError);
      const apiMessage = (
        createError as { response?: { data?: { message?: string } } }
      ).response?.data?.message;
      setFormError(apiMessage || "Unable to create role. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const handleDeactivateRole = async (role: Role) => {
    if (!role.active) return;

    const confirmed = window.confirm(
      `Are you sure you want to deactivate the role "${role.roleName}"?`,
    );
    if (!confirmed) return;

    try {
      setDeactivatingId(role.id);
      setActionError("");
      await deactivateRole(role.id);
      setSuccess(`Role "${role.roleName}" deactivated successfully.`);
      await fetchRoles();
    } catch (deactivateError: unknown) {
      console.error("Failed to deactivate role:", deactivateError);
      const apiMessage = (
        deactivateError as { response?: { data?: { message?: string } } }
      ).response?.data?.message;
      setActionError(
        apiMessage || "Unable to deactivate role. Please try again.",
      );
    } finally {
      setDeactivatingId(null);
    }
  };

  const filteredRoles = roles.filter((role) =>
    `${role.roleCode} ${role.roleName} ${role.description ?? ""}`
      .toLowerCase()
      .includes(search.toLowerCase()),
  );

  return (
    <div className="role-page">
      <div className="role-page-header">
        <div className="role-page-title">
          <h1>Role Management</h1>
          <p>Manage application roles and permissions.</p>
        </div>

        <div className="role-header-actions">
          <button
            type="button"
            className="role-refresh-btn"
            onClick={() => void fetchRoles()}
            disabled={loading}
          >
            <RefreshCw size={16} />
            Refresh
          </button>

          {canCreateRole && (
            <button
              type="button"
              className="role-create-btn"
              onClick={() => {
                resetForm();
                setFormError("");
                setModalOpen(true);
              }}
            >
              <Plus size={17} />
              Create Role
            </button>
          )}
        </div>
      </div>

      {success && (
        <div className="role-success-message" role="status">
          {success}
          <button
            type="button"
            aria-label="Dismiss success message"
            onClick={() => setSuccess("")}
          >
            <X size={16} />
          </button>
        </div>
      )}

      <div className="role-search-container">
        <Search size={18} />
        <input
          type="text"
          placeholder="Search roles..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          aria-label="Search roles"
        />
      </div>

      {loading && <div className="role-message">Loading roles...</div>}

      {!loading && error && (
        <div className="role-message role-error" role="alert">
          {error}
          <button type="button" onClick={() => void fetchRoles()}>
            Retry
          </button>
        </div>
      )}

      {actionError && (
        <div className="role-form-error" role="alert">
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
                    <span className="role-code">{role.roleCode}</span>
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
                      className={`role-status ${role.active ? "active" : "inactive"}`}
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

                    {canUpdateRole && role.active && (
                      <button
                        type="button"
                        className="role-deactivate-btn"
                        onClick={() => void handleDeactivateRole(role)}
                        disabled={deactivatingId === role.id}
                      >
                        {deactivatingId === role.id
                          ? "Deactivating..."
                          : "Deactivate"}
                      </button>
                    )}

                    {!role.active && (
                      <span className="role-inactive-label">Inactive</span>
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
            <span>Total Roles: {roles.length}</span>
          </div>
        </div>
      )}

      {modalOpen && (
        <div
          className="role-modal-overlay"
          role="presentation"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closeCreateModal();
          }}
        >
          <section
            className="role-create-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="create-role-title"
          >
            <div className="role-modal-header">
              <div>
                <h2 id="create-role-title">Create Role</h2>
                <p>Add a new application role.</p>
              </div>
              <button
                type="button"
                aria-label="Close create role dialog"
                onClick={closeCreateModal}
                disabled={saving}
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreateRole}>
              <label htmlFor="roleCode">Role Code</label>
              <input
                id="roleCode"
                name="roleCode"
                type="text"
                value={form.roleCode}
                onChange={handleChange}
                placeholder="e.g. COMPLIANCE_OFFICER"
                maxLength={50}
                required
              />

              <label htmlFor="roleName">Role Name</label>
              <input
                id="roleName"
                name="roleName"
                type="text"
                value={form.roleName}
                onChange={handleChange}
                placeholder="e.g. Compliance Officer"
                maxLength={100}
                required
              />

              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Describe the purpose of this role"
                rows={3}
                maxLength={500}
                required
              />

              {formError && (
                <div className="role-form-error" role="alert">
                  {formError}
                </div>
              )}

              <div className="role-modal-actions">
                <button
                  type="button"
                  className="role-cancel-btn"
                  onClick={closeCreateModal}
                  disabled={saving}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="role-create-btn"
                  disabled={saving}
                >
                  {saving ? "Saving..." : "Create Role"}
                </button>
              </div>
            </form>
          </section>
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

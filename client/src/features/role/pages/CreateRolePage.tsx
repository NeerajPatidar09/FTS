import { useState } from "react";
import type { FormEvent, ChangeEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import {ArrowLeft, ShieldCheck, Save, RotateCcw, LoaderCircle, Info,} from "lucide-react";
import {createRole, getAllRoles, updateRolePermissions,} from "../api/roleApi";
import type { CreateRoleRequest } from "../types/role.types";
import RolePermissionSelector from "../components/RolePermissionSelector";
import "./CreateRolePage.css";

export default function CreateRolePage() {

  const navigate = useNavigate();
  const [form, setForm] = useState<CreateRoleRequest>({
    roleCode: "",
    roleName: "",
    description: "",
  });

  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  // Retain the ID if role creation succeeds but permission assignment fails.
  const [createdRoleId, setCreatedRoleId] = useState<number | null>(null);
  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {

    const { name, value } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]:
        name === "roleCode"
          ? value.toUpperCase().replace(/\s+/g, "_")
          : value,
    }));

    setError("");
  };

  const handleReset = () => {

    if (createdRoleId) return;

    setForm({
      roleCode: "",
      roleName: "",
      description: "",
    });

    setSelectedIds([]);
    setError("");
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    const roleCode = form.roleCode.trim().toUpperCase();
    const roleName = form.roleName.trim();
    const description = form.description.trim();
    if (!roleCode || !roleName || !description) {
      setError("Please complete all required role information.");
      return;
    }
    if (!/^[A-Z][A-Z0-9_]*$/.test(roleCode)) {
      setError(
        "Role Code must start with a letter and contain only uppercase letters, numbers and underscores."
      );
      return;
    }

    try {
      setSaving(true);
      let roleId = createdRoleId;
      // STEP 1: Create the role
      if (!roleId) {
        const response = await createRole({
          roleCode,
          roleName,
          description,
        });

        roleId = response?.id ?? null;

        // Fallback if POST does not return the ID
        if (!roleId) {

          const roles = await getAllRoles();

          const createdRole = roles.find(
            (role) => role.roleCode === roleCode
          );

          roleId = createdRole?.id ?? null;
        }

        if (!roleId) {

          throw new Error(
            "Role creation may have succeeded, but its ID could not be retrieved. Check the role list before retrying to avoid creating a duplicate."
          );

        }

        setCreatedRoleId(roleId);
      }

      // STEP 2: Assign selected permissions
      if (selectedIds.length > 0) {

        await updateRolePermissions(roleId, {
          permissionIds: selectedIds,
        });

      }

      // STEP 3: Navigate to role listing
      navigate("/roles", {
        state: {
          successMessage: "Role created successfully.",
        },
      });

    } catch (error: any) {

      console.error("Create role failed:", error);

      setError(
        error.response?.data?.message ||
        error.message ||
        "Unable to create role. Please try again."
      );

    } finally {

      setSaving(false);

    }
  };

  return (

    <div className="create-role-page">

      {/* Back navigation */}

      <Link to="/roles" className="create-role-back">
        <ArrowLeft size={17} />
        Back to Roles
      </Link>

      {/* Page heading */}

      <div className="create-role-page-heading">

        <h1>
          {createdRoleId ? "Assign Role Permissions" : "Create Role"}
        </h1>

        <p>
          {createdRoleId
            ? "The role has been created. Complete its permission assignment."
            : "Create a new role and configure its application permissions."}
        </p>

      </div>

      <form onSubmit={handleSubmit}>

        {/* Error */}

        {error && (
          <div className="create-role-error">
            <Info size={18} />
            <span>{error}</span>
          </div>
        )}

        {/* Role Information */}

        <section className="create-role-card">

          <div className="create-role-card-header">

            <div className="create-role-card-icon">
              <ShieldCheck size={22} />
            </div>

            <div>
              <h2>Role Information</h2>
              <p>Enter the basic details of the application role.</p>
            </div>

          </div>

          <div className="create-role-card-body">

            <div className="create-role-form-grid">

              {/* Role Code */}

              <div className="create-role-field">

                <label htmlFor="roleCode">
                  Role Code <span>*</span>
                </label>

                <input
                  id="roleCode"
                  name="roleCode"
                  value={form.roleCode}
                  onChange={handleChange}
                  placeholder="e.g. COMPLIANCE_MANAGER"
                  maxLength={50}
                  disabled={saving || !!createdRoleId}
                  required
                />

                <small>
                  Uppercase letters, numbers and underscores.
                </small>

              </div>

              {/* Role Name */}

              <div className="create-role-field">

                <label htmlFor="roleName">
                  Role Name <span>*</span>
                </label>

                <input
                  id="roleName"
                  name="roleName"
                  value={form.roleName}
                  onChange={handleChange}
                  placeholder="Enter role name"
                  maxLength={100}
                  disabled={saving || !!createdRoleId}
                  required
                />

              </div>

              {/* Description */}

              <div className="create-role-field full-width">

                <label htmlFor="description">
                  Description <span>*</span>
                </label>

                <textarea
                  id="description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  placeholder="Enter role description"
                  rows={3}
                  maxLength={500}
                  disabled={saving || !!createdRoleId}
                  required
                />

              </div>

            </div>

          </div>

        </section>

        {/* Permission Assignment */}

        <section className="create-role-card">

          <div className="create-role-card-header">

            <div className="create-role-card-icon">
              <ShieldCheck size={22} />
            </div>

            <div>
              <h2>Permission Assignment</h2>
              <p>
                Select the permissions that this role should have.
              </p>
            </div>

          </div>

          <div className="create-role-card-body">

            <RolePermissionSelector
              selectedIds={selectedIds}
              onChange={setSelectedIds}
              disabled={saving}
            />

          </div>

        </section>

        {/* Footer Actions */}

        <div className="create-role-form-footer">

          <button
            type="button"
            className="create-role-reset-btn"
            onClick={handleReset}
            disabled={saving || !!createdRoleId}
          >
            <RotateCcw size={16} />
            Reset
          </button>

          <button
            type="submit"
            className="create-role-submit-btn"
            disabled={saving}
          >

            {saving ? (
              <>
                <LoaderCircle
                  size={17}
                  className="permission-spinner"
                />
                Processing...
              </>
            ) : (
              <>
                <Save size={17} />
                {createdRoleId
                  ? "Retry Permission Assignment"
                  : "Create Role"}
              </>
            )}

          </button>

        </div>

      </form>

    </div>
  );
}


import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  assignMenusToRole,
  getAllMenus,
  getMenuAssignableRoles,
  type MenuAssignableRole,
} from "../api/menuApi";
import type { Menu } from "../types/menu.types";
import MenuAssignmentTree from "../components/MenuAssignmentTree";
import { flattenMenus } from "../utils/menuUtils";
import "./MenuPage.css";

const collectDescendantIds = (menu: Menu): number[] => [
  menu.id,
  ...(menu.children ?? []).flatMap(collectDescendantIds),
];

export default function RoleMenuAssignmentPage() {
  const navigate = useNavigate();

  const [roles, setRoles] = useState<MenuAssignableRole[]>([]);
  const [menus, setMenus] = useState<Menu[]>([]);
  const [roleId, setRoleId] = useState("");
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError("");

        const [roleResponse, menuResponse] = await Promise.all([
          getMenuAssignableRoles(),
          getAllMenus(),
        ]);

        const activeRoles = roleResponse.filter((role) => role.active);

        setRoles(activeRoles);
        setMenus(menuResponse);

        if (activeRoles.length > 0) {
          setRoleId(String(activeRoles[0].id));
        }
      } catch (err) {
        console.error("Failed to load role menu assignment data:", err);
        setError("Unable to load roles or menus.");
      } finally {
        setLoading(false);
      }
    };

    void loadData();
  }, []);

  const selectedRole = useMemo(
    () => roles.find((role) => String(role.id) === roleId),
    [roles, roleId]
  );

  const handleToggle = (menu: Menu, checked: boolean) => {
    const affectedIds = collectDescendantIds(menu);

    setSelectedIds((current) => {
      const next = new Set(current);

      if (checked) {
        affectedIds.forEach((id) => {
          const item = flattenMenus(menus).find(
            (candidate) => candidate.id === id
          );

          if (item?.active) next.add(id);
        });
      } else {
        affectedIds.forEach((id) => next.delete(id));
      }

      return Array.from(next);
    });

    setSuccess("");
  };

  const handleSelectAll = () => {
    const activeIds = flattenMenus(menus)
      .filter((menu) => menu.active)
      .map((menu) => menu.id);

    setSelectedIds(activeIds);
    setSuccess("");
  };

  const handleClearAll = () => {
    setSelectedIds([]);
    setSuccess("");
  };

  const handleSave = async () => {
    if (!roleId) {
      setError("Please select a role.");
      return;
    }

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      await assignMenusToRole(Number(roleId), {
        menuIds: selectedIds,
      });

      setSuccess(
        `Menus assigned successfully to ${selectedRole?.roleName ?? "the selected role"}.`
      );
    } catch (err) {
      console.error("Failed to assign menus:", err);
      setError("Unable to assign menus. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="menu-page">
        <div className="menu-empty-state">Loading roles and menus...</div>
      </div>
    );
  }

  return (
    <div className="menu-page">
      <div className="menu-page-header">
        <div>
          <h1>Role Menu Assignment</h1>
          <p>Choose which application menus each role can access.</p>
        </div>

        <button
          className="menu-secondary-button"
          onClick={() => navigate("/menus")}
        >
          Back to Menus
        </button>
      </div>

      <div className="menu-form-card">
        <div className="menu-form-section">
          <h2>Select Role</h2>
          <p>Choose the role for which you want to configure menus.</p>

          <div className="menu-form-field">
            <label htmlFor="assignmentRole">Role *</label>
            <select
              id="assignmentRole"
              value={roleId}
              onChange={(event) => {
                setRoleId(event.target.value);
                setSelectedIds([]);
                setSuccess("");
                setError("");
              }}
            >
              {roles.length === 0 && (
                <option value="">No active roles available</option>
              )}

              {roles.map((role) => (
                <option key={role.id} value={role.id}>
                  {role.roleName} ({role.roleCode})
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="menu-form-section">
          <div className="assignment-heading-row">
            <div>
              <h2>Available Menus</h2>
              <p>
                Select the parent menus or individual child menus to assign.
              </p>
            </div>

            <div className="assignment-actions">
              <button
                type="button"
                className="menu-secondary-button"
                onClick={handleSelectAll}
              >
                Select All
              </button>

              <button
                type="button"
                className="menu-secondary-button"
                onClick={handleClearAll}
              >
                Clear All
              </button>
            </div>
          </div>

          <div className="assignment-selection-count">
            {selectedIds.length} menu(s) selected
          </div>

          {menus.length === 0 ? (
            <div className="menu-empty-state">
              No menus are available.
            </div>
          ) : (
            <MenuAssignmentTree
              menus={menus}
              selectedIds={selectedIds}
              onToggle={handleToggle}
            />
          )}
        </div>

        {error && <div className="menu-error">{error}</div>}

        {success && (
          <div className="assignment-success">{success}</div>
        )}

        <div className="menu-form-actions">
          <button
            type="button"
            className="menu-secondary-button"
            onClick={() => navigate("/menus")}
            disabled={saving}
          >
            Cancel
          </button>

          <button
            type="button"
            className="menu-primary-button"
            onClick={handleSave}
            disabled={saving || !roleId}
          >
            {saving ? "Saving..." : "Save Menu Assignment"}
          </button>
        </div>
      </div>
    </div>
  );
}

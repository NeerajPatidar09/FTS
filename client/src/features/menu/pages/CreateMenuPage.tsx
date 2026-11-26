
import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowLeft, Save } from "lucide-react"; // 
import {
  createMenu,
  getAllMenus,
  getMenuPermissions,
} from "../api/menuApi";
import type { Menu, CreateMenuRequest } from "../types/menu.types";
import type { MenuPermission } from "../api/menuApi";
import { flattenMenus } from "../utils/menuUtils";
import "./MenuPage.css";

type MenuType = "PARENT" | "CHILD";

const initialForm = {
  menuCode: "",
  menuName: "",
  menuIcon: "folder",
  displayOrder: "1",
  menuPath: "",
  parentId: "",
  requiredPermissionId: "",
};

export default function CreateMenuPage() {
  const navigate = useNavigate();

  const [menuType, setMenuType] = useState<MenuType>("PARENT");
  const [form, setForm] = useState(initialForm);
  const [menus, setMenus] = useState<Menu[]>([]);
  const [permissions, setPermissions] = useState<MenuPermission[]>([]);
  const [loadingOptions, setLoadingOptions] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadOptions = async () => {
      try {
        setLoadingOptions(true);
        setError("");

        const [menuResponse, permissionResponse] = await Promise.all([
          getAllMenus(),
          getMenuPermissions(),
        ]);

        setMenus(menuResponse);
        setPermissions(
          permissionResponse.filter((permission) => permission.active)
        );
      } catch (err) {
        console.error("Failed to load menu form options:", err);
        setError(
          "Unable to load parent menus or permissions. Please refresh."
        );
      } finally {
        setLoadingOptions(false);
      }
    };

    void loadOptions();
  }, []);

  const parentMenus = flattenMenus(menus).filter(
    (menu) => menu.active
  );

  const updateField = (
    field: keyof typeof initialForm,
    value: string
  ) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    if (menuType === "CHILD" && !form.parentId) {
      setError("Please select a parent menu.");
      return;
    }

    if (menuType === "CHILD" && !form.menuPath.trim()) {
      setError("Please enter the menu path.");
      return;
    }

    const displayOrder = Number(form.displayOrder);

    if (!Number.isInteger(displayOrder) || displayOrder < 0) {
      setError("Display order must be a valid non-negative number.");
      return;
    }

    const request: CreateMenuRequest = {
      menuCode: form.menuCode.trim().toUpperCase(),
      menuName: form.menuName.trim(),
      menuIcon: form.menuIcon.trim(),
      displayOrder,
    };

    if (menuType === "CHILD") {
      request.menuPath = form.menuPath.trim();
      request.parentId = Number(form.parentId);

      if (form.requiredPermissionId) {
        request.requiredPermissionId = Number(
          form.requiredPermissionId
        );
      }
    }

    try {
      setSaving(true);
      await createMenu(request);
      navigate("/menus");
    } catch (err) {
      console.error("Failed to create menu:", err);
      setError(
        "Unable to create menu. Check the menu code and other details, then try again."
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="create-page">
      <Link to="/menus" className="back">
        <ArrowLeft size={17} />
        Back to Menus
      </Link>
      <div className="create-page-heading">
        <div>
          <h1>Create Menu</h1>
          <p>Create a parent menu or a child menu for application navigation.</p>
        </div>
      </div>
      <form className="create-card" onSubmit={handleSubmit}>
        <div className="create-card-header">
          <div>
            <h2>Menu Information</h2>
            <p>Enter the basic details for this menu.</p></div>
        </div>
        <div className="create-card-body">
          <div className="create-form-grid">
            <label>Menu Type *</label>  
            <div className="menu-type-options">
              <label>
                <input
                  type="radio"
                  name="menuType"
                  value="PARENT"
                  checked={menuType === "PARENT"}
                  onChange={() => setMenuType("PARENT")}
                />
                Parent Menu
              </label>

              <label>
                <input
                  type="radio"
                  name="menuType"
                  value="CHILD"
                  checked={menuType === "CHILD"}
                  onChange={() => setMenuType("CHILD")}
                />
                Child Menu
              </label>
            </div>
          </div>

          <div className="create-card-body">
            <div className="create-form-grid">
              {/* Role Code */}
              <div className="create-field">
                <label htmlFor="menuCode">
                  Menu Code <span>*</span>
                </label>
                <input id="menuCode" value={form.menuCode} onChange={(event) =>
                  updateField("menuCode", event.target.value)
                }
                  placeholder="e.g. DESIGNATION_MANAGEMENT"
                  required
                  maxLength={100} />
                <small>
                  Uppercase letters, numbers and underscores.
                </small>
              </div>
              {/* Role Name */}
              <div className="create-field">
                <label htmlFor="menuName">
                  Menu Name <span>*</span>
                </label>
                <input id="menuName" value={form.menuName} onChange={(event) =>
                  updateField("menuName", event.target.value)
                }
                  placeholder="e.g. Designation Management"
                  required
                  maxLength={150} />
              </div>
              {/* Description */}
              <div className="create-field">
                <label htmlFor="menuIcon">
                  Menu Icon <span>*</span>
                </label>
                <input id="menuIcon" value={form.menuIcon} onChange={(event) =>
                  updateField("menuIcon", event.target.value)
                }
                  placeholder="e.g. shield, users, building"
                  required />
                <small>
                  Enter a supported icon name from your existing icon mapper.
                </small>
              </div>
              <div className="create-field">
                <label htmlFor="displayOrder">
                  Display Order <span>*</span>
                </label>
                <input id="displayOrder" type="number" min="0" value={form.displayOrder} onChange={(event) =>
                  updateField("displayOrder", event.target.value)} required />
                <small>
                  Enter a supported icon name from your existing icon mapper.
                </small>
              </div>
            </div>
          </div>
        </div>

        {menuType === "CHILD" && (
          <div className="create-card-inner">
            <div className="create-card-header">
              <div><h2>Child Menu Configuration</h2>
                <p>Choose the parent menu and configure the child route.</p></div>
            </div>

            <div className="create-card-body">
              <div>
                {loadingOptions ? (
                  <div className="menu-empty-state">
                    Loading parent menus and permissions...
                  </div>
                ) : (
                  <div className="create-card-body">
                    <div className="create-form-grid">
                      <div className="create-field">
                        <label htmlFor="parentId">Parent Menu <span>*</span></label>
                        <select
                          id="parentId"
                          value={form.parentId}
                          onChange={(event) =>
                            updateField("parentId", event.target.value)
                          }
                          required
                        >
                          <option value="">Select Parent Menu</option>
                          {parentMenus.map((menu) => (
                            <option key={menu.id} value={menu.id}>
                              {menu.menuName} ({menu.menuCode})
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="create-field">
                        <label htmlFor="menuPath">Menu Path <span>*</span></label>
                        <input
                          id="menuPath"
                          value={form.menuPath}
                          onChange={(event) =>
                            updateField("menuPath", event.target.value)
                          }
                          placeholder="e.g. /designations"
                          required
                        />
                      </div>

                      <div className="create-field">
                        <label htmlFor="requiredPermissionId">
                          Required Permission <span>*</span>
                        </label>
                        <select
                          id="requiredPermissionId"
                          value={form.requiredPermissionId}
                          onChange={(event) =>
                            updateField(
                              "requiredPermissionId",
                              event.target.value
                            )
                          }
                        >
                          <option value="">No permission selected</option>
                          {permissions.map((permission) => (
                            <option
                              key={permission.id}
                              value={permission.id}
                            >
                              {permission.permissionCode} —{" "}
                              {permission.permissionName}
                            </option>
                          ))}
                        </select>
                        <small>
                          Select the permission required to access this menu.
                        </small>
                      </div>
                    </div>
                   </div>
                )}
              </div>
            </div>
          </div>
        )}

        {error && <div className="menu-error">{error}</div>}

        <div className="create-form-footer">
          <button
            type="button"
            className="create-reset-btn"
            onClick={() => navigate("/menus")}
            disabled={saving}
          >
            Cancel
          </button>

          <button
            type="submit"
            className="create-submit-btn"
            disabled={saving || (menuType === "CHILD" && loadingOptions)}
          >
            <Save size={17} />
            {saving ? "Creating..." : "Create Menu"}
          </button>
        </div>
      </form>
    </div>
  );
}

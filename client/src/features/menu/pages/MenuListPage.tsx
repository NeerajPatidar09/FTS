
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  countActiveMenus,
  countMenus,
} from "../utils/menuUtils";
import {
  deactivateMenu,
  getAllMenus,
} from "../api/menuApi";
import type { Menu } from "../types/menu.types";
import MenuTree from "../components/MenuTree";
import "./MenuPage.css";

const filterMenuTree = (menus: Menu[], search: string): Menu[] => {
  const keyword = search.trim().toLowerCase();

  if (!keyword) return menus;

  return menus.reduce<Menu[]>((result, menu) => {
    const matchingChildren = filterMenuTree(
      menu.children ?? [],
      keyword
    );

    const menuMatches =
      menu.menuName.toLowerCase().includes(keyword) ||
      menu.menuCode.toLowerCase().includes(keyword);

    if (menuMatches || matchingChildren.length > 0) {
      result.push({
        ...menu,
        children: menuMatches
          ? menu.children ?? []
          : matchingChildren,
      });
    }

    return result;
  }, []);
};

export default function MenuListPage() {
  const navigate = useNavigate();

  const [menus, setMenus] = useState<Menu[]>([]);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState(false);

  const loadMenus = async () => {
    try {
      setError("");
      setLoading(true);

      const response = await getAllMenus();
      setMenus(response);
    } catch (err) {
      console.error("Failed to load menus:", err);
      setError("Unable to load menus. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadMenus();
  }, []);

  const filteredMenus = useMemo(() => {
    const searchedMenus = filterMenuTree(menus, search);

    if (statusFilter === "ALL") {
      return searchedMenus;
    }

    const activeValue = statusFilter === "ACTIVE";

    const filterByStatus = (items: Menu[]): Menu[] =>
      items.reduce<Menu[]>((result, menu) => {
        const children = filterByStatus(menu.children ?? []);

        if (menu.active === activeValue || children.length > 0) {
          result.push({
            ...menu,
            children,
          });
        }

        return result;
      }, []);

    return filterByStatus(searchedMenus);
  }, [menus, search, statusFilter]);

  const handleDeactivate = async (menuId: number) => {
    const confirmed = window.confirm(
      "Are you sure you want to deactivate this menu?"
    );

    if (!confirmed) return;

    try {
      setActionLoading(true);
      await deactivateMenu(menuId);
      await loadMenus();
    } catch (err) {
      console.error("Failed to deactivate menu:", err);
      setError("Unable to deactivate this menu.");
    } finally {
      setActionLoading(false);
    }
  };

  const totalMenus = countMenus(menus);
  const activeMenus = countActiveMenus(menus);
  const inactiveMenus = totalMenus - activeMenus;

  return (
    <div className="menu-page">
      <div className="menu-page-header">
        <div>
          <h1>Menu Management</h1>
          <p>
            Manage application navigation menus and their hierarchy.
          </p>
        </div>
        <div className="btn-header">
        <button
          className="menu-btn menu-create-btn"
          onClick={() => navigate("/menus/create")}
        >
          + Create Menu
        </button>
        <button
          className="menu-btn menu-assign-btn"
          onClick={() => navigate("/menus/roles/assign")}
        >
          Assign Menus to Roles
        </button></div>
      </div>

      <div className="menu-summary-grid">
        
        <div className="menu-summary">
          <div>
            <span>Total Menus</span>
            <h2>{totalMenus}</h2>
          </div>
        </div>
        <div className="menu-summary">
          <div>
            <span>Active Menus</span>
            <h2>{activeMenus}</h2>
          </div>
        </div>
        <div className="menu-summary">
          <div>
            <span>Inactive Menus</span>
            <h2>{inactiveMenus}</h2>
          </div>
        </div>
      </div>

     <section className="menu-list-card">
        <div className="menu-list-header">
          <div>
            <h2>Menu List</h2>
            <p>
              View and search registered menu.
            </p>
          </div>
        </div>
        {/* Loading */}

       <div className="menu-table-wrapper">
            <table className="menu-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Menu Name</th>
                  <th>Icon</th>
                  <th>Parent Menu</th>
                  <th>Path (URL)</th>
                  <th>Order</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredMenus.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="menu-table-empty">
                      No menus found.
                    </td>
                  </tr>
                ) : (
                  filteredMenus.map((menu, index) => (
                    <tr key={menu.id}>
                      <td>{index + 1}</td>

                      <td>
                        <div className="menu-name-cell">
                          {menu.parentId !== null && (
                            <span className="menu-child-indicator">↳</span>
                          )}
                          <span>{menu.menuName}</span>
                        </div>
                        <small className="menu-code">
                          {menu.menuCode}
                        </small>
                      </td>

                      <td>
                        <span className="menu-icon-cell">
                          {menu.menuIcon || "—"}
                        </span>
                      </td>

                      <td>
                        {menu.parentId !== null
                          ? parentNameById.get(menu.id) ?? "—"
                          : "—"}
                      </td>

                      <td>
                        <span className="menu-path">
                          {menu.menuPath || "—"}
                        </span>
                      </td>

                      <td>{menu.displayOrder}</td>

                      <td>
                        <span
                          className={`menu-status ${
                            menu.active ? "active" : "inactive"
                          }`}
                        >
                          {menu.active ? "Active" : "Inactive"}
                        </span>
                      </td>

                      <td>
                        <div className="menu-row-actions">
                          <button
                            type="button"
                            className="menu-action-button deactivate"
                            title={
                              menu.active
                                ? "Deactivate menu"
                                : "Already inactive"
                            }
                            disabled={
                              !menu.active ||
                              actionLoading === menu.id
                            }
                            onClick={() => void handleDeactivate(menu)}
                          >
                            {actionLoading === menu.id ? "..." : "Deactivate"}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

        
      </section>
    </div>
  );
}

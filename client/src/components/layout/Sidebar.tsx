
import { useState } from "react";
import {
  ChevronDown,
  ChevronRight,
  X,
  LogOut,
} from "lucide-react";
import { NavLink, useLocation } from "react-router-dom";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { filterMenusByPermission } from "../../utils/menuUtils";
import { getMenuIcon } from "../../utils/menuIcons";
import type { MenuResponse } from "../../features/auth/types/auth.types";

interface SidebarProps {
  collapsed: boolean;
  mobileOpen: boolean;
  onNavigate: () => void;
}

export default function Sidebar({
  collapsed,
  mobileOpen,
  onNavigate,
}: SidebarProps) {

  const { auth, logout } = useAuth();

  const location = useLocation();

  const [expandedMenus, setExpandedMenus] =
    useState<Record<number, boolean>>({});

  const employeeName =
  auth?.user?.employeeName ||
  auth?.user?.username ||
  "User";

  // Filter menus using backend permissions
  const visibleMenus = filterMenusByPermission(
    auth?.menus ?? [],
    {
      permissions: auth?.user.permissions ?? [],
    }
  );

  const toggleMenu = (menuId: number) => {

    setExpandedMenus((previous) => ({
      ...previous,
      [menuId]: !previous[menuId],
    }));

  };

  const renderMenu = (menu: MenuResponse) => {
    const Icon = getMenuIcon(menu.menuIcon);
    const hasChildren = menu.children.length > 0;
    const isChildActive = menu.children.some(
      (child) =>
        child.menuPath === location.pathname ||
        child.children.some(
          (nested) => nested.menuPath === location.pathname
        )
    );

    const isExpanded = expandedMenus[menu.id] ?? isChildActive;
    const isActive = menu.menuPath === location.pathname;

    // Parent menu with children
    if (hasChildren) {

      return (
        <div className="dynamic-menu-group" key={menu.id}>

          <button
            type="button"
            className={`dynamic-menu-parent ${
              isChildActive ? "active-parent" : ""
            }`}
            onClick={() => toggleMenu(menu.id)}
            title={collapsed ? menu.menuName : undefined}
            aria-expanded={isExpanded}
          >

            <Icon size={19} />

            {!collapsed && (
              <>
                <span>{menu.menuName}</span>

                {isExpanded ? (
                  <ChevronDown size={15} />
                ) : (
                  <ChevronRight size={15} />
                )}
              </>
            )}

          </button>

          {!collapsed && isExpanded && (

            <div className="dynamic-menu-children">

              {menu.children.map((child) =>
                renderMenu(child)
              )}

            </div>

          )}

        </div>
      );
    }

    // Menu without children
    if (menu.menuPath) {

      return (
        <NavLink
          key={menu.id}
          to={menu.menuPath}
          onClick={onNavigate}
          title={collapsed ? menu.menuName : undefined}
          className={({ isActive }) =>
            `navigation-link ${
              isActive ? "active" : ""
            }`
          }
        >

          <Icon size={19} />

          {!collapsed && (
            <span>{menu.menuName}</span>
          )}

        </NavLink>
      );
    }

    return null;
  };

  return (

    <aside
      className={`sidebar ${
        collapsed ? "collapsed" : ""
      } ${mobileOpen ? "mobile-open" : ""}`}
    >

      {/* Brand */}

      <div className="sidebar-brand">

        <div className="sidebar-logo">A</div>

        {!collapsed && (
          <span className="brand-name">
            Stranger
          </span>
        )}

        <button
          className="mobile-close"
          onClick={onNavigate}
          aria-label="Close navigation"
        >
          <X size={20} />
        </button>

      </div>


      {/* Dynamic Navigation */}

      <nav className="sidebar-navigation">

        {visibleMenus.length > 0 ? (

          visibleMenus.map((menu) => renderMenu(menu))

        ) : (

          <p className="no-menus">
            No accessible menus available
          </p>

        )}

      </nav>

      {/* Logged-in User */}

      <div className="sidebar-bottom">
        <div className="sidebar-user">
          <div className="user-avatar">
            {employeeName.charAt(0).toUpperCase()}
          </div>
          {!collapsed && (
            <div className="sidebar-user-info">
              <strong>{employeeName}</strong>
              <span>
                {auth?.roles?.[0] ?? "User"}
              </span>
            </div>
          )}

        

        <button
          className="sidebar-logout"
          onClick={logout}
        >

          <LogOut size={18} />

        </button>
        </div>

      </div>

    </aside>
  );
}

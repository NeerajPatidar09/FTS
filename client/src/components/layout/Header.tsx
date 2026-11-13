
import {
  Menu,
  PanelLeftClose,
  PanelLeftOpen,
  Bell,
  ChevronDown,
  LogOut,
  UserCircle,
} from "lucide-react";

import { useState } from "react";
import { useLocation } from "react-router-dom";

import { useAuth } from "../../features/auth/hooks/useAuth";

interface HeaderProps {
  collapsed: boolean;
  onToggleSidebar: () => void;
  onOpenMobileMenu: () => void;
}

export default function Header({
  collapsed,
  onToggleSidebar,
  onOpenMobileMenu,
}: HeaderProps) {

  const { auth, logout } = useAuth();

  const location = useLocation();

  const [profileOpen, setProfileOpen] = useState(false);

  const pageName =
    location.pathname.split("/").filter(Boolean).pop() || "dashboard";

  const title = pageName
    .replace(/-/g, " ")
    .replace(/\b\w/g, (letter) => letter.toUpperCase());

  const employeeName = auth?.user.employeeName || auth?.user.username || "User";

  return (
    <header className="app-header">

      <div className="header-left">

        <button
          className="header-icon desktop-menu-button"
          onClick={onToggleSidebar}
          aria-label="Toggle sidebar"
        >
          {collapsed ? (
            <PanelLeftOpen size={21} />
          ) : (
            <PanelLeftClose size={21} />
          )}
        </button>

        <button
          className="header-icon mobile-menu-button"
          onClick={onOpenMobileMenu}
          aria-label="Open sidebar"
        >
          <Menu size={22} />
        </button>

        <div className="header-page-title">
          <h2>{title}</h2>
          <p>Stranger / {title}</p>
        </div>

      </div>

      <div className="header-right">

        <button
          className="header-icon notification-button"
          aria-label="Notifications"
        >
          <Bell size={20} />
          <span className="notification-dot" />
        </button>

        <div className="profile-container">

          <button
            className="profile-button"
            onClick={() => setProfileOpen((previous) => !previous)}
            aria-expanded={profileOpen}
          >

            <div className="profile-avatar">
              {employeeName.charAt(0).toUpperCase()}
            </div>

            <div className="profile-info">
              <strong>{employeeName}</strong>
              <span>{auth?.user?.roles?.[0] ?? "User"}</span>
            </div>

            <ChevronDown size={16} />

          </button>

          {profileOpen && (
            <div className="profile-dropdown">

              <div className="profile-dropdown-info">
                <UserCircle size={18} />
                <span>{auth?.user.username}</span>
              </div>

              <button onClick={logout}>
                <LogOut size={17} />
                Logout
              </button>

            </div>
          )}

        </div>

      </div>

    </header>
  );
}

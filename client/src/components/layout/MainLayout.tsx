
import { useState } from "react";
import { Outlet } from "react-router-dom";

import Sidebar from "./Sidebar";
import Header from "./Header";

import "./MainLayout.css";

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  const toggleSidebar = () => {
    setCollapsed((previous) => !previous);
  };

  return (
    <div className="app-layout">

      <Sidebar
        collapsed={collapsed}
        mobileOpen={mobileOpen}
        onNavigate={() => setMobileOpen(false)}
      />

      {mobileOpen && (
        <div
          className="sidebar-overlay"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <div
        className={`main-content ${
          collapsed ? "sidebar-collapsed" : ""
        }`}
      >

        <Header
          collapsed={collapsed}
          onToggleSidebar={toggleSidebar}
          onOpenMobileMenu={() => setMobileOpen(true)}
        />

        <main className="page-content">
          <Outlet />
        </main>

      </div>

    </div>
  );
}

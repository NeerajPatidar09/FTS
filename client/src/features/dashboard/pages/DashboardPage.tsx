
import {
  Users,
  Landmark,
  Bell,
  Search,
  UserRound,
  ShieldCheck,
  Activity,
} from "lucide-react";

import { NavLink } from "react-router-dom";

import { useAuth } from "../../auth/hooks/useAuth";

import "./DashboardPage.css";

export default function DashboardPage() {

  const { auth } = useAuth();

  if (!auth) {
    return null;
  }

  const employeeName = auth.user.employeeName ?? auth.user.username ?? "User";

  const permissions = auth.user.permissions ?? [];
  const roles = auth.user.roles ?? [];
  const menus = auth.menus ?? [];

  const stats = [
    {
      title: "Total Customers",
      value: "—",
      icon: Users,
      color: "blue",
    },
    {
      title: "Total Accounts",
      value: "—",
      icon: Landmark,
      color: "green",
    },
    {
      title: "Active Alerts",
      value: "—",
      icon: Bell,
      color: "orange",
    },
    {
      title: "Investigations",
      value: "—",
      icon: Search,
      color: "purple",
    },
  ];

  const quickLinks = [
    {
      label: "Customers",
      path: "/customers",
      icon: Users,
    },
    {
      label: "Accounts",
      path: "/accounts",
      icon: Landmark,
    },
    {
      label: "Alerts",
      path: "/alerts",
      icon: Bell,
    },
    {
      label: "Employees",
      path: "/employees",
      icon: UserRound,
    },
  ];

  return (
    <div className="dashboard-page">

      {/* Welcome section */}

      <section className="dashboard-welcome">

        <div>
          <h1>
            Welcome back, {employeeName}
          </h1>

          <p>
            Here's an overview of your Stranger RegTech workspace.
          </p>
        </div>

        <div className="dashboard-date">
          <Activity
            size={15}
            style={{
              display: "inline",
              marginRight: "7px",
              verticalAlign: "middle",
            }}
          />
          Workspace Overview
        </div>

      </section>

      {/* Summary cards */}

      <section className="dashboard-stats">

        {stats.map((stat) => {

          const Icon = stat.icon;

          return (
            <div className="stat-card" key={stat.title}>

              <div className="stat-card-top">

                <div className={`stat-icon ${stat.color}`}>
                  <Icon size={22} />
                </div>

              </div>

              <h3>{stat.value}</h3>

              <p>{stat.title}</p>

              <small>
                Awaiting dashboard metrics API
              </small>

            </div>
          );
        })}

      </section>

      {/* Main dashboard content */}

      <section className="dashboard-main-grid">

        <div className="dashboard-panel">

          <div className="panel-header">
            <h3>Recent Activity</h3>
            <span>Activity overview</span>
          </div>

          <div className="activity-empty">

            <Activity size={35} />

            <p>No recent activity available</p>

            <span>
              Activity information will appear here
              when connected to the backend.
            </span>

          </div>

        </div>

        <div className="dashboard-panel">

          <div className="panel-header">
            <h3>Account Overview</h3>
            <span>My access</span>
          </div>

          <div className="access-summary">

            <div className="access-row">
              <span>Employee</span>
              <strong>{employeeName}</strong>
            </div>

            <div className="access-row">
              <span>Username</span>
              <strong>{auth.user.username}</strong>
            </div>

            <div className="access-row">
              <span>Employee ID</span>
              <strong>{auth.user.employeeId}</strong>
            </div>

            <div className="access-row">
              <span>Assigned roles</span>
              <strong>{roles.length}</strong>
            </div>

            <div className="access-row">
              <span>Permissions</span>
              <strong>{permissions.length}</strong>
            </div>

            <div className="access-row">
              <span>Available menu items</span>
              <strong>{menus.length}</strong>
            </div>

            <div className="access-row">
              <span>Account status</span>
              <strong style={{ color: "#21965b" }}>
                Authenticated
              </strong>
            </div>

          </div>

        </div>

      </section>

      {/* Quick access */}

      <h3 className="dashboard-section-title">
        Quick Access
      </h3>

      <section className="quick-access-grid">

        {quickLinks.map((link) => {

          const Icon = link.icon;

          return (
            <NavLink
              key={link.path}
              to={link.path}
              className="quick-access-item"
            >
              <Icon size={21} />
              <span>{link.label}</span>
            </NavLink>
          );
        })}

      </section>

    </div>
  );
}


import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import {
  Building2,
  Plus,
  Search,
  RefreshCw,
  CheckCircle2,
  XCircle,
  Pencil,
} from "lucide-react";

import {
  getAllDepartments,
  deactivateDepartment,
} from "../api/departmentApi";

import type { Department } from "../types/department.types";

import DepartmentTable from "../components/DepartmentTable";

import "./DepartmentPage.css";

interface LocationState {
  successMessage?: string;
}

export default function DepartmentListPage() {

  const navigate = useNavigate();
  const location = useLocation();

  const [departments, setDepartments] = useState<Department[]>([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [actionError, setActionError] = useState("");

  const [search, setSearch] = useState("");

  const [statusFilter, setStatusFilter] = useState("ALL");

  const [deactivatingId, setDeactivatingId] = useState<number | null>(null);

  const [successMessage, setSuccessMessage] = useState(
    (location.state as LocationState | null)?.successMessage ?? ""
  );

  const fetchDepartments = async () => {

    try {

      setLoading(true);
      setError("");

      const response = await getAllDepartments();

      setDepartments(response);

    } catch (error) {

      console.error("Failed to fetch departments:", error);

      setError("Unable to load departments. Please try again.");

    } finally {

      setLoading(false);

    }
  };

  useEffect(() => {
    fetchDepartments();
  }, []);

  // Summary

  const activeCount = departments.filter(
    (department) => department.active
  ).length;

  const inactiveCount = departments.length - activeCount;

  // Search and status filter

  const filteredDepartments = useMemo(() => {

    return departments.filter((department) => {

      const keyword = search.toLowerCase();

      const matchesSearch =
        department.departmentName.toLowerCase().includes(keyword) ||
        department.departmentCode.toLowerCase().includes(keyword);

      const matchesStatus =
        statusFilter === "ALL" ||
        (statusFilter === "ACTIVE" && department.active) ||
        (statusFilter === "INACTIVE" && !department.active);

      return matchesSearch && matchesStatus;

    });

  }, [departments, search, statusFilter]);

  // Edit navigation

  const handleEdit = (department: Department) => {
    navigate(`/departments/${department.id}/edit`);
  };

  // Deactivate

  const handleDeactivate = async (department: Department) => {

    if (!department.active) return;

    const confirmed = window.confirm(
      `Are you sure you want to deactivate "${department.departmentName}"?`
    );

    if (!confirmed) return;

    try {

      setDeactivatingId(department.id);
      setActionError("");
      setSuccessMessage("");

      await deactivateDepartment(department.id);

      await fetchDepartments();

      setSuccessMessage(
        `${department.departmentName} deactivated successfully.`
      );

    } catch (error: any) {

      console.error("Failed to deactivate department:", error);

      setActionError(
        error.response?.data?.message ||
        "Unable to deactivate department."
      );

    } finally {

      setDeactivatingId(null);

    }
  };

  return (

    <div className="department-page">

      {/* Header */}

      <div className="department-page-header">

        <div>
          <h1>Department Management</h1>
          <p>Manage organizational departments.</p>
        </div>

        <div className="department-header-actions">

          <button
            className="department-refresh-btn"
            onClick={fetchDepartments}
            disabled={loading}
          >
            <RefreshCw size={16} />
            Refresh
          </button>

          <button
            className="department-create-btn"
            onClick={() => navigate("/departments/create")}
          >
            <Plus size={17} />
            Create Department
          </button>

        </div>

      </div>

      {/* Success */}

      {successMessage && (
        <div className="department-success-message">
          <CheckCircle2 size={17} />
          {successMessage}

          <button
            type="button"
            onClick={() => setSuccessMessage("")}
          >
            ×
          </button>
        </div>
      )}

      {/* Errors */}

      {(error || actionError) && (
        <div className="department-error-message">
          {error || actionError}
          {error && (
            <button onClick={fetchDepartments}>
              Retry
            </button>
          )}
        </div>
      )}

      {/* Summary */}

      <div className="department-summary-grid">

        <div className="department-summary-card">
          <div className="department-summary-icon blue">
            <Building2 size={21} />
          </div>
          <div>
            <h3>{departments.length}</h3>
            <p>Total Departments</p>
          </div>
        </div>

        <div className="department-summary-card">
          <div className="department-summary-icon green">
            <CheckCircle2 size={21} />
          </div>
          <div>
            <h3>{activeCount}</h3>
            <p>Active Departments</p>
          </div>
        </div>

        <div className="department-summary-card">
          <div className="department-summary-icon orange">
            <XCircle size={21} />
          </div>
          <div>
            <h3>{inactiveCount}</h3>
            <p>Inactive Departments</p>
          </div>
        </div>

      </div>

      {/* Search and filter */}

      <div className="department-toolbar">

        <div className="department-search">
          <Search size={18} />

          <input
            type="text"
            placeholder="Search department name or code..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>

        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          className="department-status-filter"
        >
          <option value="ALL">All Status</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>

      </div>

      {/* Table */}

      <div className="department-results-heading">

        <h3>All Departments</h3>

        <span>
          Showing {filteredDepartments.length} of {departments.length}
        </span>

      </div>

      {loading ? (

        <div className="department-message">
          Loading departments...
        </div>

      ) : error ? (

        <div className="department-message">
          Unable to display departments.
        </div>

      ) : (

        <DepartmentTable
          departments={filteredDepartments.map((department) => ({
            ...department,
            // Keep the row actions available; disable while processing.
          }))}
          onEdit={handleEdit}
          onDeactivate={handleDeactivate}
          canUpdate={true}
        />

      )}

      <div className="department-table-footer">
        <span>Showing {filteredDepartments.length} records</span>
        <span>Source: Spring Boot API</span>
      </div>

    </div>
  );
}

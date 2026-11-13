import { useEffect, useState } from "react";
import type { FormEvent, ChangeEvent } from "react";

import {
  Link,
  useNavigate,
  useParams,
} from "react-router-dom";

import {
  ArrowLeft,
  Building2,
  Save,
  RotateCcw,
  LoaderCircle,
} from "lucide-react";

import {
  createDepartment,
  getAllDepartments,
  updateDepartment,
} from "../api/departmentApi";

import type { DepartmentRequest } from "../types/department.types";

import "./DepartmentPage.css";

export default function DepartmentFormPage() {

  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  const isEdit = Boolean(id);

  const [form, setForm] = useState<DepartmentRequest>({
    departmentCode: "",
    departmentName: "",
  });

  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  // Load department for editing
  useEffect(() => {

    if (!id) return;

    let cancelled = false;

    const loadDepartment = async () => {

      try {

        setLoading(true);
        setError("");

        const departments = await getAllDepartments();

        const department = departments.find(
          (item) => item.id === Number(id)
        );

        if (!department) {
          throw new Error("Department not found.");
        }

        if (!cancelled) {

          setForm({
            departmentCode: department.departmentCode,
            departmentName: department.departmentName,
          });

        }

      } catch (error: any) {

        if (!cancelled) {

          console.error("Failed to load department:", error);

          setError(
            error.message || "Unable to load department details."
          );

        }

      } finally {

        if (!cancelled) {
          setLoading(false);
        }

      }
    };

    loadDepartment();

    return () => {
      cancelled = true;
    };

  }, [id]);

  // Handle input changes
  const handleChange = (
    event: ChangeEvent<HTMLInputElement>
  ) => {

    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]:
        name === "departmentCode"
          ? value.toUpperCase().replace(/\s+/g, "")
          : value,
    }));

    setError("");
  };

  // Reset
  const handleReset = () => {

    if (isEdit) {

      // Reload existing department details
      window.location.reload();

    } else {

      setForm({
        departmentCode: "",
        departmentName: "",
      });

      setError("");

    }
  };

  // Submit
  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {

    event.preventDefault();

    const departmentCode = form.departmentCode.trim().toUpperCase();
    const departmentName = form.departmentName.trim();

    if (!departmentCode || !departmentName) {
      setError("Please complete all required fields.");
      return;
    }

    if (!/^[A-Z][A-Z0-9_]*$/.test(departmentCode)) {
      setError(
        "Department Code must start with a letter and contain only uppercase letters, numbers and underscores."
      );
      return;
    }

    try {

      setSaving(true);
      setError("");

      const request: DepartmentRequest = {
        departmentCode,
        departmentName,
      };

      if (isEdit) {

        await updateDepartment(Number(id), request);

      } else {

        await createDepartment(request);

      }

      navigate("/departments", {
        state: {
          successMessage: isEdit
            ? "Department updated successfully."
            : "Department created successfully.",
        },
      });

    } catch (error: any) {

      console.error("Department save failed:", error);

      setError(
        error.response?.data?.message ||
        error.message ||
        "Unable to save department."
      );

    } finally {

      setSaving(false);

    }
  };

  if (loading) {
    return (
      <div className="department-message">
        <LoaderCircle size={20} className="department-spinner" />
        Loading department details...
      </div>
    );
  }

  return (

    <div className="department-form-page">

      {/* Back */}

      <Link
        to="/departments"
        className="department-back-link"
      >
        <ArrowLeft size={17} />
        Back to Departments
      </Link>

      {/* Heading */}

      <div className="department-form-heading">

        <h1>
          {isEdit ? "Edit Department" : "Create Department"}
        </h1>

        <p>
          {isEdit
            ? "Update department information."
            : "Add a new department to the Stranger RegTech platform."}
        </p>

      </div>

      {error && (
        <div className="department-error-message">
          {error}
        </div>
      )}

      {/* Form */}

      <form onSubmit={handleSubmit}>

        <section className="department-form-card">

          <div className="department-form-card-header">

            <div className="department-form-icon">
              <Building2 size={22} />
            </div>

            <div>
              <h2>Department Information</h2>
              <p>Enter the department details below.</p>
            </div>

          </div>

          <div className="department-form-card-body">

            <div className="department-form-grid">

              <div className="department-form-field">

                <label htmlFor="departmentCode">
                  Department Code <span>*</span>
                </label>

                <input
                  id="departmentCode"
                  name="departmentCode"
                  value={form.departmentCode}
                  onChange={handleChange}
                  placeholder="e.g. COMP"
                  maxLength={20}
                  required
                  disabled={saving}
                />

                <small>
                  Use uppercase letters, numbers and underscores.
                </small>

              </div>

              <div className="department-form-field">

                <label htmlFor="departmentName">
                  Department Name <span>*</span>
                </label>

                <input
                  id="departmentName"
                  name="departmentName"
                  value={form.departmentName}
                  onChange={handleChange}
                  placeholder="Enter department name"
                  maxLength={100}
                  required
                  disabled={saving}
                />

              </div>

            </div>

          </div>

          {/* Footer */}

          <div className="department-form-footer">

            <button
              type="button"
              className="department-reset-btn"
              onClick={handleReset}
              disabled={saving}
            >
              <RotateCcw size={16} />
              Reset
            </button>

            <button
              type="submit"
              className="department-submit-btn"
              disabled={saving}
            >

              {saving ? (
                <>
                  <LoaderCircle
                    size={16}
                    className="department-spinner"
                  />
                  Saving...
                </>
              ) : (
                <>
                  <Save size={16} />
                  {isEdit
                    ? "Update Department"
                    : "Create Department"}
                </>
              )}

            </button>

          </div>

        </section>

      </form>

    </div>
  );
}

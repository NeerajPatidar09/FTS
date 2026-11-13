
import { useState } from "react";
import { Save, RotateCcw } from "lucide-react";

import type { CreatePermissionRequest } from "../types/permission.types";

interface PermissionFormProps {
  onSubmit: (values: CreatePermissionRequest) => Promise<void>;
  submitting: boolean;
}

const initialValues: CreatePermissionRequest = {
  permissionCode: "",
  permissionName: "",
  description: "",
};

export default function PermissionForm({
  onSubmit,
  submitting,
}: PermissionFormProps) {

  const [values, setValues] = useState(initialValues);

  const [validationError, setValidationError] = useState("");

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {

    const { name, value } = event.target;

    setValues((previous) => ({
      ...previous,
      [name]: value,
    }));

    setValidationError("");
  };

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>
  ) => {

    event.preventDefault();

    if (
      !values.permissionCode.trim() ||
      !values.permissionName.trim() ||
      !values.description.trim() 
    ) {
      setValidationError("Please fill in all required fields.");
      return;
    }


    await onSubmit({
      permissionCode: values.permissionCode.trim(),
      permissionName: values.permissionName.trim(),
      description: values.description.trim(),
    });
  };

  const handleReset = () => {
    setValues(initialValues);
    setValidationError("");
  };

  return (
    <form className="Permission" onSubmit={handleSubmit}>

      <div className="permission-form-grid">

        <div className="form-group">
          <label htmlFor="permissionCode">
            Permission Code *
          </label>

          <input
            id="permissionCode"
            name="permissionCode"
            value={values.permissionCode}
            onChange={handleChange}
            placeholder="ROLE_VIEW"
            required
            maxLength={100}
          />
        </div>

        <div className="form-group">
          <label htmlFor="permissionName">
            Permission Name *
          </label>

          <input
            id="permissionName"
            name="permissionName"
            value={values.permissionName}
            onChange={handleChange}
            placeholder="Enter permission name"
            required
            maxLength={150}
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">
            Description *
          </label>

          <input
            id="description"
            name="description"
            value={values.description}
            onChange={handleChange}
            placeholder="Enter Description"
            required
            maxLength={200}
          />
        </div>

      </div>

      {validationError && (
        <div className="permission-form-error">
          {validationError}
        </div>
      )}

      <div className="permission-form-actions">

        <button
          type="button"
          className="permission-reset-button"
          onClick={handleReset}
          disabled={submitting}
        >
          <RotateCcw size={16} />
          Reset
        </button>

        <button
          type="submit"
          className="permission-submit-button"
          disabled={submitting}
        >
          <Save size={16} />
          {submitting ? "Creating..." : "Create Permission"}
        </button>

      </div>

    </form>
  );
}

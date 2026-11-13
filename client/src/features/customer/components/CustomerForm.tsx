
import { useState } from "react";
import { Save, RotateCcw } from "lucide-react";

import type { CreateCustomerRequest } from "../types/customer.types";

interface CustomerFormProps {
  onSubmit: (values: CreateCustomerRequest) => Promise<void>;
  submitting: boolean;
}

const initialValues: CreateCustomerRequest = {
  customerRef: "",
  firstName: "",
  lastName: "",
  email: "",
};

export default function CustomerForm({
  onSubmit,
  submitting,
}: CustomerFormProps) {

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
      !values.customerRef.trim() ||
      !values.firstName.trim() ||
      !values.lastName.trim() ||
      !values.email.trim()
    ) {
      setValidationError("Please fill in all required fields.");
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(values.email.trim())) {
      setValidationError("Please enter a valid email address.");
      return;
    }

    await onSubmit({
      customerRef: values.customerRef.trim(),
      firstName: values.firstName.trim(),
      lastName: values.lastName.trim(),
      email: values.email.trim(),
    });
  };

  const handleReset = () => {
    setValues(initialValues);
    setValidationError("");
  };

  return (
    <form className="customer-form" onSubmit={handleSubmit}>

      <div className="customer-form-grid">

        <div className="form-group">
          <label htmlFor="customerRef">
            Customer Reference *
          </label>

          <input
            id="customerRef"
            name="customerRef"
            value={values.customerRef}
            onChange={handleChange}
            placeholder="CUST-1008"
            required
            maxLength={50}
          />
        </div>

        <div className="form-group">
          <label htmlFor="firstName">
            First Name *
          </label>

          <input
            id="firstName"
            name="firstName"
            value={values.firstName}
            onChange={handleChange}
            placeholder="Enter first name"
            required
            maxLength={100}
          />
        </div>

        <div className="form-group">
          <label htmlFor="lastName">
            Last Name *
          </label>

          <input
            id="lastName"
            name="lastName"
            value={values.lastName}
            onChange={handleChange}
            placeholder="Enter last name"
            required
            maxLength={100}
          />
        </div>

        <div className="form-group">
          <label htmlFor="email">
            Email Address *
          </label>

          <input
            id="email"
            name="email"
            type="email"
            value={values.email}
            onChange={handleChange}
            placeholder="example@gmail.com"
            required
            maxLength={254}
          />
        </div>

      </div>

      {validationError && (
        <div className="customer-form-error">
          {validationError}
        </div>
      )}

      <div className="customer-form-actions">

        <button
          type="button"
          className="customer-reset-button"
          onClick={handleReset}
          disabled={submitting}
        >
          <RotateCcw size={16} />
          Reset
        </button>

        <button
          type="submit"
          className="customer-submit-button"
          disabled={submitting}
        >
          <Save size={16} />
          {submitting ? "Creating..." : "Create Customer"}
        </button>

      </div>

    </form>
  );
}

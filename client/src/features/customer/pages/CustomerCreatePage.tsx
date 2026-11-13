
import { useState } from "react";

import { useNavigate, Link } from "react-router-dom";

import { ArrowLeft, UserPlus } from "lucide-react";

import { createCustomer } from "../api/customerApi";

import CustomerForm from "../components/CustomerForm";

import type { CreateCustomerRequest } from "../types/customer.types";

import { getApiErrorMessage } from "../../../services/api/apiError";

import "./CustomerCreatePage.css";

export default function CustomerCreatePage() {

  const navigate = useNavigate();

  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");

  const handleCreateCustomer = async (
    values: CreateCustomerRequest
  ) => {

    try {

      setSubmitting(true);
      setError("");

      await createCustomer(values);

      navigate("/customers", {
        replace: true,
        state: {
          successMessage: "Customer created successfully.",
        },
      });

    } catch (err) {

      setError(getApiErrorMessage(err));

    } finally {

      setSubmitting(false);

    }
  };

  return (

    <div className="customer-create-page">

      <div className="customer-create-heading">

        <div>
          <Link to="/customers" className="customer-back-link">
            <ArrowLeft size={16} />
            Back to Customers
          </Link>

          <h1>Create Customer</h1>

          <p>
            Add a new customer to the Stranger RegTech platform.
          </p>
        </div>

      </div>

      <section className="customer-create-card">

        <div className="customer-create-card-header">

          <div className="customer-create-icon">
            <UserPlus size={23} />
          </div>

          <div>
            <h2>Customer Information</h2>
            <p>Enter the customer details below.</p>
          </div>

        </div>

        {error && (
          <div className="customer-form-error">
            {error}
          </div>
        )}

        <CustomerForm
          onSubmit={handleCreateCustomer}
          submitting={submitting}
        />

      </section>

    </div>
  );
}

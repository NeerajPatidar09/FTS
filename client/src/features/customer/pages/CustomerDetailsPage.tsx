
import { useEffect, useState } from "react";

import { Link, useParams } from "react-router-dom";

import { ArrowLeft, RefreshCw } from "lucide-react";

import { getCustomerByRef } from "../api/customerApi";

import type { Customer } from "../types/customer.types";

import CustomerDetailsCard from "../components/CustomerDetailsCard";

import { getApiErrorMessage } from "../../../services/api/apiError";

import "./CustomerCreatePage.css";

export default function CustomerDetailsPage() {

  const { customerRef } = useParams<{ customerRef: string }>();

  const [customer, setCustomer] = useState<Customer | null>(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {

    if (!customerRef) {
      setError("Customer reference is missing.");
      setLoading(false);
      return;
    }

    const controller = new AbortController();

    const fetchCustomer = async () => {

      try {

        setLoading(true);
        setError("");

        const data = await getCustomerByRef(
          customerRef,
          controller.signal
        );

        setCustomer(data);

      } catch (err) {

        if (!controller.signal.aborted) {
          setError(getApiErrorMessage(err));
        }

      } finally {

        if (!controller.signal.aborted) {
          setLoading(false);
        }

      }
    };

    fetchCustomer();

    return () => controller.abort();

  }, [customerRef]);

  return (

    <div className="customer-create-page">

      <Link to="/customers" className="customer-back-link">
        <ArrowLeft size={16} />
        Back to Customers
      </Link>

      <div className="customer-create-heading">
        <div>
          <h1>Customer Details</h1>
          <p>View registered customer information.</p>
        </div>
      </div>

      {loading && (
        <div className="customer-message">
          <RefreshCw size={22} className="loading-icon" />
          Loading customer details...
        </div>
      )}

      {!loading && error && (
        <div className="customer-form-error">
          {error}
        </div>
      )}

      {!loading && !error && customer && (
        <CustomerDetailsCard customer={customer} />
      )}

    </div>
  );
}

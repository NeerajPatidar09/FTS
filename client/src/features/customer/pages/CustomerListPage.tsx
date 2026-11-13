import { useEffect, useMemo, useState } from "react";

import {Users, RefreshCw, ChevronLeft, ChevronRight, AlertCircle,Plus,} from "lucide-react";
import { getCustomers } from "../api/customerApi";
import type { Customer } from "../types/customer.types";
import CustomerFilters from "../components/CustomerFilters";
import CustomerTable from "../components/CustomerTable";
import { getApiErrorMessage } from "../../../services/api/apiError";
import "./CustomerListPage.css";
import { Link, useLocation } from "react-router-dom";
import {  } from "lucide-react";
const PAGE_SIZE = 10;

export default function CustomerListPage() {

  const [customers, setCustomers] = useState<Customer[]>([]);

  const [search, setSearch] = useState("");

  const [currentPage, setCurrentPage] = useState(1);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [reloadKey, setReloadKey] = useState(0);
  const location = useLocation();
  
const successMessage = location.state?.successMessage as
  | string 
  | undefined;

  // Fetch customers from Spring Boot
  useEffect(() => {
    {successMessage && (
  <div className="customer-success-message">
    {successMessage}
  </div>
)}
    const controller = new AbortController();

    const fetchCustomers = async () => {

      try {

        setLoading(true);
        setError("");

        const data = await getCustomers(controller.signal);

        setCustomers(data);

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

    fetchCustomers();

    return () => {
      controller.abort();
    };

  }, [reloadKey]);

  // Search customers
  const filteredCustomers = useMemo(() => {

    const keyword = search.trim().toLowerCase();

    if (!keyword) {
      return customers;
    }

    return customers.filter((customer) => {

      return [
        customer.customerRef,
        customer.firstName,
        customer.lastName,
        customer.fullName,
        customer.email,
        customer.status,
      ].some((value) =>
        (value ?? "").toLowerCase().includes(keyword)
      );

    });

  }, [customers, search]);

  // Pagination
  const totalPages = Math.max(
    1,
    Math.ceil(filteredCustomers.length / PAGE_SIZE)
  );

  const paginatedCustomers = useMemo(() => {

    const startIndex = (currentPage - 1) * PAGE_SIZE;

    return filteredCustomers.slice(
      startIndex,
      startIndex + PAGE_SIZE
    );

  }, [filteredCustomers, currentPage]);

  const startRecord =
    filteredCustomers.length === 0
      ? 0
      : (currentPage - 1) * PAGE_SIZE + 1;

  const endRecord = Math.min(
    currentPage * PAGE_SIZE,
    filteredCustomers.length
  );

  const handleSearch = (value: string) => {
    setSearch(value);
    setCurrentPage(1);
  };

  return (

    <div className="customer-page">

      {/* Page heading */}

      {/* <div className="customer-page-header">

        <div>
          <h1>Customer Management</h1>

          <p>
            Manage and monitor your registered customers.
          </p>
        </div>

        <button
          className="customer-refresh-button"
          onClick={() => setReloadKey((previous) => previous + 1)}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Refresh
        </button>

      </div> */}


<div className="customer-page-header">

  <div>
    <h1>Customer Management</h1>

    <p>
      Manage and monitor your registered customers.
    </p>
  </div>

  <div className="customer-header-actions">

    <button
      className="customer-refresh-button"
      onClick={() => setReloadKey((previous) => previous + 1)}
      disabled={loading}
    >
      <RefreshCw size={16} />
      Refresh
    </button>

    <Link
      to="/customers/create"
      className="customer-create-button"
    >
      <Plus size={17} />
      Create Customer
    </Link>

  </div>

</div>


      {/* Customer summary */}

      <div className="customer-summary">
        <div className="customer-summary-icon"><Users size={24} /></div>
        <div>
          <span>Total Customers</span>
          <h2>
            {loading ? "..." : customers.length}
          </h2>
        </div>
      </div>

      {/* Customer listing */}

      <section className="customer-list-card">
        <div className="customer-list-header">
          <div>
            <h2>Customer List</h2>
            <p>
              View and search registered customers.
            </p>
          </div>
          <span className="customer-count">
            {filteredCustomers.length} Records
          </span>
        </div>
        <CustomerFilters search={search} onSearchChange={handleSearch}/>
        {/* Loading */}

        {loading && (

          <div className="customer-message">
            <RefreshCw size={23} className="loading-icon" />
            <p>Loading customers...</p>
          </div>

        )}

        {/* API Error */}

        {!loading && error && (

          <div className="customer-error">

            <AlertCircle size={22} />

            <div>
              <strong>Unable to load customers</strong>
              <p>{error}</p>
            </div>

            <button
              onClick={() => setReloadKey((previous) => previous + 1)}
            >
              Retry
            </button>

          </div>

        )}

        {/* Customer Table */}

        {!loading && !error && (

          <>

            <CustomerTable
              customers={paginatedCustomers}
            />

            {/* Pagination */}

            <div className="customer-pagination">
              <span>
                Showing {startRecord}–{endRecord} of{" "}
                {filteredCustomers.length} customers
              </span>

              <div className="pagination-controls">
                <button
                  disabled={currentPage === 1}
                  onClick={() =>
                    setCurrentPage((previous) => previous - 1)
                  }
                  aria-label="Previous page"
                >
                  <ChevronLeft size={17} />
                </button>
                <span>
                  {currentPage} / {totalPages}
                </span>
                <button
                  disabled={currentPage >= totalPages}
                  onClick={() =>
                    setCurrentPage((previous) => previous + 1)
                  }
                  aria-label="Next page"
                >
                  <ChevronRight size={17} />
                </button>
              </div>
            </div>
          </>
        )}

      </section>

    </div>
  );
}

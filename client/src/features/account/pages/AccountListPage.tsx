import { useEffect, useMemo, useState } from "react";

import {Users, RefreshCw, ChevronLeft, ChevronRight, AlertCircle,Plus,} from "lucide-react";
import { getAccounts } from "../api/accountApi";
import type { Account } from "../types/account.types";
// import AccountFilters from "../components/AccountFilters";
import AccountTable from "../components/AccountTable";
import { getApiErrorMessage } from "../../../services/api/apiError";
import "./AccountListPage.css";
import { Link, useLocation } from "react-router-dom";
import {  } from "lucide-react";
const PAGE_SIZE = 10;

export default function AccountListPage() {

  const [accounts, setAccounts] = useState<Account[]>([]);

  const [search, setSearch] = useState("");

  const [currentPage, setCurrentPage] = useState(1);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [reloadKey, setReloadKey] = useState(0);
  const location = useLocation();
  
const successMessage = location.state?.successMessage as
  | string 
  | undefined;

  // Fetch accounts from Spring Boot
  useEffect(() => {
    {successMessage && (
  <div className="account-success-message">
    {successMessage}
  </div>
)}
    const controller = new AbortController();

    const fetchAccounts = async () => {

      try {

        setLoading(true);
        setError("");

        const data = await getAccounts(controller.signal);

        setAccounts(data);

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

    fetchAccounts();

    return () => {
      controller.abort();
    };

  }, [reloadKey]);

  // Search accounts
  const filteredAccounts = useMemo(() => {

    const keyword = search.trim().toLowerCase();

    if (!keyword) {
      return accounts;
    }

    return accounts.filter((account) => {

      return [
        account.accountRef,
        account.customerRef,
        account.accountType,
        account.status,
        account.currency,
      ].some((value) =>
        (value ?? "").toLowerCase().includes(keyword)
      );

    });

  }, [accounts, search]);

  // Pagination
  const totalPages = Math.max(
    1,
    Math.ceil(filteredAccounts.length / PAGE_SIZE)
  );

  const paginatedAccounts = useMemo(() => {

    const startIndex = (currentPage - 1) * PAGE_SIZE;

    return filteredAccounts.slice(
      startIndex,
      startIndex + PAGE_SIZE
    );

  }, [filteredAccounts, currentPage]);

  const startRecord =
    filteredAccounts.length === 0
      ? 0
      : (currentPage - 1) * PAGE_SIZE + 1;

  const endRecord = Math.min(
    currentPage * PAGE_SIZE,
    filteredAccounts.length
  );

  const handleSearch = (value: string) => {
    setSearch(value);
    setCurrentPage(1);
  };

  return (

    <div className="account-page">

      {/* Page heading */}

      {/* <div className="account-page-header">

        <div>
          <h1>Account Management</h1>

          <p>
            Manage and monitor your registered accounts.
          </p>
        </div>

        <button
          className="account-refresh-button"
          onClick={() => setReloadKey((previous) => previous + 1)}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Refresh
        </button>

      </div> */}


<div className="account-page-header">

  <div>
    <h1>Account Management</h1>

    <p>
      Manage and monitor your registered accounts.
    </p>
  </div>

  <div className="account-header-actions">

    <button
      className="account-refresh-button"
      onClick={() => setReloadKey((previous) => previous + 1)}
      disabled={loading}
    >
      <RefreshCw size={16} />
      Refresh
    </button>

    <Link
      to="/accounts/create"
      className="account-create-button"
    >
      <Plus size={17} />
      Create Account
    </Link>

  </div>

</div>


      {/* Account summary */}

      <div className="account-summary">
        <div className="account-summary-icon"><Users size={24} /></div>
        <div>
          <span>Total Accounts</span>
          <h2>
            {loading ? "..." : accounts.length}
          </h2>
        </div>
      </div>

      {/* Account listing */}

      <section className="account-list-card">
        <div className="account-list-header">
          <div>
            <h2>Account List</h2>
            <p>
              View and search registered accounts.
            </p>
          </div>
          <span className="account-count">
            {filteredAccounts.length} Records
          </span>
        </div>
        {/* <AccountFilters search={search} onSearchChange={handleSearch}/> */}
        {/* Loading */}

        {loading && (

          <div className="account-message">
            <RefreshCw size={23} className="loading-icon" />
            <p>Loading accounts...</p>
          </div>

        )}

        {/* API Error */}

        {!loading && error && (

          <div className="account-error">

            <AlertCircle size={22} />

            <div>
              <strong>Unable to load accounts</strong>
              <p>{error}</p>
            </div>

            <button
              onClick={() => setReloadKey((previous) => previous + 1)}
            >
              Retry
            </button>

          </div>

        )}

        {/* Account Table */}

        {!loading && !error && (

          <>

            <AccountTable
              accounts={paginatedAccounts}
            />

            {/* Pagination */}

            <div className="account-pagination">
              <span>
                Showing {startRecord}–{endRecord} of{" "}
                {filteredAccounts.length} accounts
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

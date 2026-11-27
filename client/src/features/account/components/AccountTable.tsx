import type { Account } from "../types/account.types";
import { Link } from "react-router-dom";
import AccountStatusBadge from "./AccountStatusBadge";

interface AccountTableProps {
  accounts: Account[];
}

export default function AccountTable({
  accounts,
}: AccountTableProps) {

  return (
    <div className="account-table-wrapper">
      <table className="account-table">

        <thead>
          <tr>
            <th>Account</th>
            <th>Customer Reference</th>
            <th>Account Type</th>
            <th>Status</th>
            <th>Currency</th>
          </tr>
        </thead>

        <tbody>

          {accounts.length > 0 ? (
            accounts.map((account) => (
              <tr key={account.id}>
                <td>
                  <div className="account-name-cell">
                    {/* <div className="account-avatar">
                      {(account.firstName?.charAt(0) ?? "C").toUpperCase()}
                    </div> */}


                    <div className="account-name-info">
                      <Link to={`/accounts/${encodeURIComponent(account.accountRef)}`}
                        className="account-name-link">
                        {account.accountRef}</Link>
                      <span>ID: {account.id}</span>
                    </div>
                  </div>
                </td>

                {/* <td>
                  <span className="account-reference">
                    {account.accountRef}
                  </span>
                </td> */}

                <td>
                  <span className="account-email">
                    {account.customerRef}
                  </span>
                </td>

                <td>
                  <span className="account-email">
                    {account.accountType}
                  </span>
                </td>

                <td>
                  <AccountStatusBadge
                    status={account.status}
                  />
                </td>

                <td>
                  <span className="account-email">
                    {account.currency}
                  </span>
                </td>
              </tr>

            ))

          ) : (

            <tr>
              <td colSpan={4} className="account-empty">
                No accounts found.
              </td>
            </tr>

          )}
        </tbody>
      </table>
    </div>
  );
}

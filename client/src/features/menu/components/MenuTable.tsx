import type { Menu } from "../types/menu.types";
import { Link } from "react-router-dom";
// import CustomerStatusBadge from "./CustomerStatusBadge";

interface MenuTableProps {
  menus: Menu[];
}

export default function MenuTable({
  menus,
}: MenuTableProps) {

  return (
    <div className="customer-table-wrapper">
      <table className="customer-table">

        <thead>
          <tr>
            <th>Customer</th>
            <th>Customer Reference</th>
            <th>Email Address</th>
            <th>Status</th>
          </tr>
        </thead>

        {/* <tbody>

          {customers.length > 0 ? (
            customers.map((customer) => (
              <tr key={customer.id}>
                <td>
                  <div className="customer-name-cell">
                    <div className="customer-avatar">
                      {(customer.firstName?.charAt(0) ?? "C").toUpperCase()}
                    </div>

                    <div className="customer-name-info">
                      <Link to={`/customers/${encodeURIComponent(customer.customerRef)}`}
                        className="customer-name-link">
                        {customer.fullName || `${customer.firstName} ${customer.lastName}`}</Link>
                      <span>ID: {customer.id}</span>
                    </div>
                  </div>
                </td>

                <td>
                  <span className="customer-reference">
                    {customer.customerRef}
                  </span>
                </td>

                <td>
                  <span className="customer-email">
                    {customer.email}
                  </span>
                </td>

                <td>
                  <CustomerStatusBadge
                    status={customer.status}
                  />
                </td>
              </tr>

            ))

          ) : (

            <tr>
              <td colSpan={4} className="customer-empty">
                No customers found.
              </td>
            </tr>

          )}
        </tbody> */}
      </table>
    </div>
  );
}

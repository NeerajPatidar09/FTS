
import { Mail, User, Hash, CalendarCheck } from "lucide-react";

import type { Customer } from "../types/customer.types";

import CustomerStatusBadge from "./CustomerStatusBadge";

interface CustomerDetailsCardProps {
  customer: Customer;
}

export default function CustomerDetailsCard({
  customer,
}: CustomerDetailsCardProps) {

  return (

    <div className="customer-details-card">

      <div className="customer-details-profile">

        <div className="customer-details-avatar">
          {customer.firstName?.charAt(0).toUpperCase()}
        </div>

        <div>
          <h2>{customer.fullName}</h2>
          <p>{customer.customerRef}</p>
        </div>

        <CustomerStatusBadge status={customer.status} />

      </div>

      <div className="customer-details-grid">

        <div className="customer-detail-item">
          <User size={19} />
          <div>
            <span>First Name</span>
            <strong>{customer.firstName}</strong>
          </div>
        </div>

        <div className="customer-detail-item">
          <User size={19} />
          <div>
            <span>Last Name</span>
            <strong>{customer.lastName}</strong>
          </div>
        </div>

        <div className="customer-detail-item">
          <Mail size={19} />
          <div>
            <span>Email Address</span>
            <strong>{customer.email}</strong>
          </div>
        </div>

        <div className="customer-detail-item">
          <Hash size={19} />
          <div>
            <span>Customer ID</span>
            <strong>{customer.id}</strong>
          </div>
        </div>

        <div className="customer-detail-item">
          <CalendarCheck size={19} />
          <div>
            <span>Customer Reference</span>
            <strong>{customer.customerRef}</strong>
          </div>
        </div>

      </div>

    </div>
  );
}

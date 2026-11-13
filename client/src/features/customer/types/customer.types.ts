
export interface Customer {
  id: number;
  customerRef: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  status: string;
}

export interface CreateCustomerRequest {
  customerRef: string;
  firstName: string;
  lastName: string;
  email: string;
}

export type CustomerFormValues = CreateCustomerRequest;

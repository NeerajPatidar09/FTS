import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Customer,
  CreateCustomerRequest,
} from "../types/customer.types";

// GET ALL CUSTOMERS
export const getCustomers = async (
  signal?: AbortSignal
): Promise<Customer[]> => {

  const response = await axiosInstance.get<Customer[]>(
    "/api/v1/customers",
    { signal }
  );

  return response.data;
};

// CREATE CUSTOMER
export const createCustomer = async (
  request: CreateCustomerRequest
): Promise<Customer> => {

  const response = await axiosInstance.post<Customer>(
    "/api/v1/customers",
    request
  );

  return response.data;
};

// GET CUSTOMER BY CUSTOMER REFERENCE
export const getCustomerByRef = async (
  customerRef: string,
  signal?: AbortSignal
): Promise<Customer> => {

  const response = await axiosInstance.get<Customer>(
    `/api/v1/customers/${encodeURIComponent(customerRef)}`,
    { signal }
  );

  return response.data;
};

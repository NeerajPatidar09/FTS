export interface Account {
  id: number;
  accountRef: string;
  customerRef: string;
  accountType: string;
  status: string;
  currency: string;
}

export interface CreateAccountRequest {
  accountRef: string;
  customerRef: string;
  accountType: string;
  currency: string;
  openedOn: string;
}

export type AccountFormValues = CreateAccountRequest;

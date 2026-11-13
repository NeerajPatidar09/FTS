
import axios from "axios";

export const getApiErrorMessage = (error: unknown): string => {

  if (error instanceof Error && !axios.isAxiosError(error)) {
    return error.message;
  }

  if (axios.isAxiosError(error)) {

    if (error.response) {
      const status = error.response.status;
      const serverMessage = error.response.data?.message;

      if (
        typeof serverMessage === "string" &&
        serverMessage.trim()
      ) {
        return serverMessage;
      }

      switch (status) {
        case 400:
          return "Invalid request. Please check your input.";

        case 401:
          return "Invalid username or password.";

        case 403:
          return "You don't have permission to perform this action.";

        case 404:
          return "Login API was not found.";

        case 500:
          return "Internal server error. Please try again later.";

        default:
          return `Request failed with status ${status}.`;
      }
    }

    if (error.request) {
      return "Unable to connect to the server. Please check your backend.";
    }
  }

  return "An unexpected error occurred.";
};

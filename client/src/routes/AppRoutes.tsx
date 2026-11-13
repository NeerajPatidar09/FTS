
import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";
import CustomerCreatePage from "../features/customer/pages/CustomerCreatePage";
import CustomerDetailsPage from "../features/customer/pages/CustomerDetailsPage";
import RoleListPage from "../features/role/pages/RoleListPage";
import CustomerPermissionPage from "../features/permission/pages/PermissionCreatePage";
import PermissionListPage from "../features/permission/pages/PermissionListPage";
import LoginPage from "../features/auth/pages/LoginPage";
import DashboardPage from "../features/dashboard/pages/DashboardPage";
import CustomerListPage from "../features/customer/pages/CustomerListPage";
import MainLayout from "../components/layout/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import PublicRoute from "./PublicRoute";
import PermissionRoute from "./PermissionRoute";
import UnauthorizedPage from "./UnauthorizedPage";
import CreateRolePage from "../features/role/pages/CreateRolePage";
import DepartmentListPage from "../features/department/pages/DepartmentListPage";
import DepartmentFormPage from "../features/department/pages/DepartmentFormPage";
import MenuListPage from "../features/menu/pages/MenuListPage";
import CreateMenuPage from "../features/menu/pages/CreateMenuPage";
import RoleMenuAssignmentPage from "../features/menu/pages/RoleMenuAssignmentPage";

export default function AppRoutes() {

  return (

    <Routes>

      {/* Public routes */}

      <Route element={<PublicRoute />}>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Route>

      {/* Unauthorized page */}

      <Route
        path="/unauthorized"
        element={<UnauthorizedPage />}
      />

      {/* Protected application */}

      <Route element={<ProtectedRoute />}>

        <Route element={<MainLayout />}>

          {/* Dashboard */}

          <Route
            path="/dashboard"
            element={<DashboardPage />}
          />

          {/* Customer Management */}

          <Route element={
            <PermissionRoute permissions={["CUSTOMER_VIEW"]} />
          }>
            <Route
              path="/customers"
              element={<CustomerListPage />}
            />

            {/* Customer Details */}

            <Route
              path="/customers/:customerRef"
              element={<CustomerDetailsPage />}
            />
          </Route>

          {/* Create Customer */}

          <Route element={
            <PermissionRoute permissions={["CUSTOMER_CREATE"]} />
          }>
            <Route
              path="/customers/create"
              element={<CustomerCreatePage />}
            />
          </Route>

          {/* Alert Management */}

          <Route element={
            <PermissionRoute permissions={["ALERT_VIEW"]} />
          }>
            <Route
              path="/alerts"
              element={<div>Alert List – Coming Soon</div>}
            />
          </Route>

          {/* Investigation */}

          <Route element={
            <PermissionRoute permissions={["INVESTIGATION_VIEW"]} />
          }>
            <Route
              path="/investigations"
              element={<div>Investigation List – Coming Soon</div>}
            />
          </Route>

          <Route
            element={<PermissionRoute permissions={["DEPARTMENT_VIEW"]} />}
          >
            <Route
              path="/departments"
              element={<DepartmentListPage />}
            />
          </Route>

          <Route
            element={<PermissionRoute permissions={["DEPARTMENT_CREATE"]} />}
          >
            <Route
              path="/departments/create"
              element={<DepartmentFormPage />}
            />
          </Route>

          <Route
            element={<PermissionRoute permissions={["DEPARTMENT_UPDATE"]} />}
          >
            <Route
              path="/departments/:id/edit"
              element={<DepartmentFormPage />}
            />
          </Route>

          {/* Employee Management */}

          <Route element={
            <PermissionRoute permissions={["EMPLOYEE_VIEW"]} />
          }>
            <Route
              path="/employees"
              element={<div>Employee List – Coming Soon</div>}
            />
          </Route>

          {/* User Management */}

          <Route element={
            <PermissionRoute permissions={["USER_VIEW"]} />
          }>
            <Route
              path="/users"
              element={<div>User Management – Coming Soon</div>}
            />
          </Route>

          {/* Role Management */}

          <Route element={
            <PermissionRoute permissions={["ROLE_VIEW"]} />
          }>
            <Route
              path="/roles"
              element={<RoleListPage />}
            />
          </Route>
          <Route element={<PermissionRoute permissions={["ROLE_CREATE"]} />}>
            <Route
              path="/roles/create"
              element={<CreateRolePage />}
            />
          </Route>

          {/* Permission Management */}

          <Route element={
            <PermissionRoute permissions={["PERMISSION_VIEW"]} />
          }>
            <Route
              path="/permissions"
              element={<PermissionListPage />}
            />
          </Route>
          {/* Create Permission */}

          <Route element={
            <PermissionRoute permissions={["PERMISSION_CREATE"]} />
          }>
            <Route
              path="/permissions/create"
              element={<CustomerPermissionPage />}
            />
          </Route>


          {/* Menu Management */}

          <Route element={
            <PermissionRoute permissions={["MENU_VIEW"]} />
          }>
            <Route
              path="/menus"
              element={<MenuListPage />}
            />
          </Route>

          <Route element={
            <PermissionRoute permissions={["MENU_MANAGE"]} />
          }>
            <Route
              path="/menus/create"
              element={<CreateMenuPage />}
            />
          </Route>
          <Route
            path="/menus/roles/assign"
            element={<RoleMenuAssignmentPage />}
          />
        </Route>



      </Route>

      {/* Unknown routes */}

      <Route
        path="*"
        element={<Navigate to="/dashboard" replace />}
      />

    </Routes>

  );
}

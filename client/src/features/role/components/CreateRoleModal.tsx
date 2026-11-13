
// import { useEffect, useMemo, useState } from "react";
// import type { ChangeEvent, FormEvent } from "react";

// import {
//   X,
//   Search,
//   ShieldCheck,
//   CheckSquare,
//   Square,
//   LoaderCircle,
// } from "lucide-react";

// import { createRole, getAllRoles, updateRolePermissions } from "../api/roleApi";

// import { getAllPermissions } from "../../permission/api/permissionApi";

// import type { Permission } from "../../permission/types/permission.types";

// import type { CreateRoleRequest } from "../types/role.types";

// interface CreateRoleModalProps {
//   onClose: () => void;
//   onCreated: () => void;
// }

// const getPermissionGroup = (code: string): string => {
//   const prefix = code.split("_")[0];
//   const groups: Record<string, string> = {
//     CUSTOMER: "Customer Management",
//     ACCOUNT: "Account Management",
//     ALERT: "Alert Management",
//     INVESTIGATION: "Investigation Management",
//     USER: "User Management",
//     ROLE: "Role Management",
//     PERMISSION: "Permission Management",
//     EMPLOYEE: "Employee Management",
//     MENU: "Menu Management",
//     DEPARTMENT: "Department Management",
//   };
  

//   return groups[prefix] ?? "Other";
// };

// export default function CreateRoleModal({
//   onClose,
//   onCreated,
// }: CreateRoleModalProps) {

//   const [form, setForm] = useState<CreateRoleRequest>({
//     roleCode: "",
//     roleName: "",
//     description: "",
//   });

//   const [permissions, setPermissions] = useState<Permission[]>([]);
//   const [selectedIds, setSelectedIds] = useState<number[]>([]);

//   const [search, setSearch] = useState("");
//   const [loadingPermissions, setLoadingPermissions] = useState(true);
//   const [saving, setSaving] = useState(false);

//   const [error, setError] = useState("");
//   const [createdRoleId, setCreatedRoleId] = useState<number | null>(null);

//   // Load permissions from backend
//   useEffect(() => {

//     const loadPermissions = async () => {

//       try {
//         setLoadingPermissions(true);

//         const response = await getAllPermissions();

//         setPermissions(response);

//       } catch (error) {

//         console.error("Failed to load permissions:", error);

//         setError("Unable to load permissions. Please try again.");

//       } finally {
//         setLoadingPermissions(false);
//       }
//     };

//     loadPermissions();

//   }, []);

//   // Handle input changes
//   const handleChange = (
//     event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
//   ) => {

//     const { name, value } = event.target;

//     setForm((previous) => ({
//       ...previous,
//       [name]:
//         name === "roleCode"
//           ? value.toUpperCase().replace(/\s/g, "_")
//           : value,
//     }));

//     setError("");
//   };

//   // Filter permissions
//   const filteredPermissions = useMemo(() => {

//     return permissions.filter((permission) =>
//       `${permission.permissionCode} ${permission.permissionName}`
//         .toLowerCase()
//         .includes(search.toLowerCase())
//     );

//   }, [permissions, search]);

//   // Group permissions
//   const groupedPermissions = useMemo(() => {

//     return filteredPermissions.reduce<Record<string, Permission[]>>(
//       (groups, permission) => {
//         const group = getPermissionGroup(permission.permissionCode);
//         if (!groups[group]) {
//           groups[group] = [];
//         }
//         groups[group].push(permission);
//         return groups;
//       },
//       {}
//     );
//   }, [filteredPermissions]);

//   // Toggle permission
//   const togglePermission = (id: number) => {
//     setSelectedIds((previous) => {
//       if (previous.includes(id)) {
//         return previous.filter((item) => item !== id);
//       }
//       return [...previous, id];
//     });
//   };

//   // Select or deselect a complete group
//   const toggleGroup = (groupPermissions: Permission[]) => {

//     const ids = groupPermissions
//       .filter((permission) => permission.active)
//       .map((permission) => permission.id);

//     const allSelected =
//       ids.length > 0 &&
//       ids.every((id) => selectedIds.includes(id));

//     setSelectedIds((previous) => {

//       if (allSelected) {
//         return previous.filter((id) => !ids.includes(id));
//       }

//       return [...new Set([...previous, ...ids])];
//     });

//   };

//   // Create role and assign permissions
//   const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {

//     event.preventDefault();

//     setError("");

//     const roleCode = form.roleCode.trim().toUpperCase();
//     const roleName = form.roleName.trim();
//     const description = form.description.trim();

//     if (!roleCode || !roleName || !description) {
//       setError("Please fill in all required fields.");
//       return;
//     }

//     if (!/^[A-Z][A-Z0-9_]*$/.test(roleCode)) {
//       setError("Role Code must contain uppercase letters, numbers and underscores.");
//       return;
//     }

//     if (permissions.length === 0) {
//       setError("Permissions are not loaded. Please try again.");
//       return;
//     }

//     if (
//       selectedIds.some(
//         (id) => !permissions.some(
//           (permission) => permission.id === id && permission.active
//         )
//       )
//     ) {
//       setError("One or more selected permissions are unavailable.");
//       return;
//     }

//     try {

//       setSaving(true);

//       let roleId = createdRoleId;

//       // Step A: Create role only once
//       if (!roleId) {

//         const createdRole = await createRole({
//           roleCode,
//           roleName,
//           description,
//         });

//         roleId = createdRole?.id ?? null;

//         // Fallback if POST does not return the created role ID
//         if (!roleId) {

//           const latestRoles = await getAllRoles();

//           const matchingRole = latestRoles.find(
//             (role) => role.roleCode === roleCode
//           );

//           roleId = matchingRole?.id ?? null;
//         }

//         if (!roleId) {
//           throw new Error(
//             "Role was created, but its ID could not be retrieved. Refresh the role list before retrying."
//           );
//         }

//         // Preserve the ID if permission assignment fails.
//         // A retry will assign permissions without creating a duplicate.
//         setCreatedRoleId(roleId);
//       }

//       // Step B: Assign permissions
//       if (selectedIds.length > 0) {

//         await updateRolePermissions(roleId, {
//           permissionIds: selectedIds,
//         });

//       }

//       // Step C: Refresh role listing
//       onCreated();
//       onClose();

//     } catch (error: any) {

//       console.error("Role creation/permission assignment failed:", error);

//       setError(
//         error.response?.data?.message ||
//         error.message ||
//         "Unable to complete role creation."
//       );

//     } finally {
//       setSaving(false);
//     }
//   };

//   return (

//     <div
//       className="role-modal-overlay"
//       onMouseDown={(event) => {
//         if (
//           event.target === event.currentTarget &&
//           !saving
//         ) {
//           onClose();
//         }
//       }}
//     >

//       <div
//         className="role-create-permission-modal"
//         role="dialog"
//         aria-modal="true"
//         aria-labelledby="create-role-heading"
//       >

//         {/* Header */}

//         <div className="role-permission-header">

//           <div className="role-permission-heading">

//             <div className="role-permission-heading-icon">
//               <ShieldCheck size={22} />
//             </div>

//             <div>
//               <h2 id="create-role-heading">
//                 {createdRoleId
//                   ? "Assign Permissions"
//                   : "Create New Role"}
//               </h2>

//               <p>
//                 {createdRoleId
//                   ? "The role is created. Complete its permission assignment."
//                   : "Create a role and assign its permissions."}
//               </p>
//             </div>

//           </div>

//           <button
//             type="button"
//             className="role-modal-close"
//             onClick={onClose}
//             disabled={saving}
//           >
//             <X size={20} />
//           </button>

//         </div>

//         <form onSubmit={handleSubmit}>

//           <div className="role-create-permission-body">

//             {error && (
//               <div className="role-form-error">
//                 {error}
//               </div>
//             )}

//             {/* Role details */}

//             {!createdRoleId && (

//               <div className="role-create-fields">

//                 <div className="role-form-group">

//                   <label htmlFor="newRoleCode">
//                     Role Code *
//                   </label>

//                   <input
//                     id="newRoleCode"
//                     name="roleCode"
//                     value={form.roleCode}
//                     onChange={handleChange}
//                     placeholder="e.g. COMPLIANCE_MANAGER"
//                     maxLength={50}
//                     required
//                   />

//                   <small>
//                     Automatically converted to uppercase.
//                   </small>

//                 </div>

//                 <div className="role-form-group">

//                   <label htmlFor="newRoleName">
//                     Role Name *
//                   </label>

//                   <input
//                     id="newRoleName"
//                     name="roleName"
//                     value={form.roleName}
//                     onChange={handleChange}
//                     placeholder="e.g. Compliance Manager"
//                     maxLength={100}
//                     required
//                   />

//                 </div>

//                 <div className="role-form-group">

//                   <label htmlFor="newRoleDescription">
//                     Description *
//                   </label>

//                   <textarea
//                     id="newRoleDescription"
//                     name="description"
//                     value={form.description}
//                     onChange={handleChange}
//                     placeholder="Enter role description..."
//                     rows={3}
//                     maxLength={500}
//                     required
//                   />

//                 </div>

//               </div>

//             )}

//             {/* Permission heading */}

//             <div className="role-create-permission-heading">

//               <div>
//                 <h3>Assign Permissions</h3>
//                 <p>Select the permissions for this role.</p>
//               </div>

//               <span>
//                 {selectedIds.length} selected
//               </span>

//             </div>

//             {/* Search */}

//             <div className="role-permission-search">

//               <Search size={17} />

//               <input
//                 value={search}
//                 onChange={(event) => setSearch(event.target.value)}
//                 placeholder="Search permissions..."
//               />

//             </div>

//             {/* Permissions */}

//             {loadingPermissions ? (

//               <div className="role-permission-loading">
//                 <LoaderCircle className="role-spinning" size={22} />
//                 Loading permissions...
//               </div>

//             ) : (

//               <div className="role-create-permission-list">

//                 {Object.entries(groupedPermissions).map(
//                   ([group, groupPermissions]) => {

//                     const activeIds = groupPermissions
//                       .filter((permission) => permission.active)
//                       .map((permission) => permission.id);

//                     const allSelected =
//                       activeIds.length > 0 &&
//                       activeIds.every((id) => selectedIds.includes(id));

//                     return (

//                       <div
//                         className="role-permission-group"
//                         key={group}
//                       >

//                         <div className="role-permission-group-header">

//                           <h3>{group}</h3>

//                           <button
//                             type="button"
//                             onClick={() => toggleGroup(groupPermissions)}
//                             disabled={saving}
//                           >
//                             {allSelected ? "Deselect All" : "Select All"}
//                           </button>

//                         </div>

//                         <div className="role-permission-options">

//                           {groupPermissions.map((permission) => {

//                             const checked = selectedIds.includes(permission.id);

//                             return (

//                               <label
//                                 key={permission.id}
//                                 className={`role-permission-option ${!permission.active ? "disabled" : ""
//                                   }`}
//                               >

//                                 <input
//                                   type="checkbox"
//                                   checked={checked}
//                                   disabled={!permission.active || saving}
//                                   onChange={() =>
//                                     togglePermission(permission.id)
//                                   }
//                                 />

//                                 <span className="role-custom-checkbox">
//                                   {checked
//                                     ? <CheckSquare size={18} />
//                                     : <Square size={18} />}
//                                 </span>

//                                 <span className="role-permission-option-info">

//                                   <strong>
//                                     {permission.permissionName}
//                                   </strong>

//                                   <small>
//                                     {permission.permissionCode}
//                                   </small>

//                                 </span>

//                               </label>

//                             );
//                           })}

//                         </div>

//                       </div>
//                     );
//                   }
//                 )}

//                 {filteredPermissions.length === 0 && (
//                   <div className="role-empty">
//                     No permissions found.
//                   </div>
//                 )}

//               </div>

//             )}

//           </div>

//           {/* Footer */}

//           <div className="role-permission-footer">

//             <button
//               type="button"
//               className="role-cancel-btn"
//               onClick={onClose}
//               disabled={saving}
//             >
//               Cancel
//             </button>

//             <button
//               type="submit"
//               className="role-submit-btn"
//               disabled={saving || loadingPermissions}
//             >
//               {saving
//                 ? "Processing..."
//                 : createdRoleId
//                   ? "Retry Permission Assignment"
//                   : "Create Role"}
//             </button>

//           </div>

//         </form>

//       </div>

//     </div>

//   );
// }

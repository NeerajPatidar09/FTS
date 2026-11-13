
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { ArrowLeft, UserPlus } from "lucide-react";
import { createPermission } from "../api/permissionApi";
import PermissionForm from "../components/PermissionForm";
import type { CreatePermissionRequest } from "../types/permission.types";
import { getApiErrorMessage } from "../../../services/api/apiError";
import "./PermissionCreatePage.css";

export default function PermissionCreatePage() {

    const navigate = useNavigate();
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const handleCreatePermission = async (
        values: CreatePermissionRequest
    ) => {

        try {

            setSubmitting(true);
            setError("");

            await createPermission(values);

            navigate("/permissions", {
                replace: true,
                state: {
                    successMessage: "Permission created successfully.",
                },
            });

        } catch (err) {

            setError(getApiErrorMessage(err));

        } finally {

            setSubmitting(false);

        }
    };

    return (

        <div className="permission-create-page">

            <div className="permission-create-heading">

                <div>
                    <Link to="/permission" className="permission-back-link">
                        <ArrowLeft size={16} />
                        Back to Permissions
                    </Link>

                    <h1>Create Permission</h1>

                    <p>
                        Add a new permission to the Stranger RegTech platform.
                    </p>
                </div>

            </div>

            <section className="permission-create-card">

                <div className="permission-create-card-header">

                    <div className="permission-create-icon">
                        <UserPlus size={23} />
                    </div>

                    <div>
                        <h2>Permission Information</h2>
                        <p>Enter the permission details below.</p>
                    </div>

                </div>

                {error && (
                    <div className="permission-form-error">
                        {error}
                    </div>
                )}

                <PermissionForm
                    onSubmit={handleCreatePermission}
                    submitting={submitting}
                />

            </section>

        </div>
    );
}

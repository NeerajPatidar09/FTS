
import { useNavigate } from "react-router-dom";
import { ShieldAlert, ArrowLeft } from "lucide-react";

export default function UnauthorizedPage() {

  const navigate = useNavigate();

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "#f5f5f5",
        padding: "20px",
      }}
    >

      <div
        style={{
          background: "#fff",
          padding: "50px",
          borderRadius: "16px",
          textAlign: "center",
          maxWidth: "450px",
          width: "100%",
          boxShadow: "0 4px 25px rgba(0,0,0,0.06)",
        }}
      >

        <ShieldAlert
          size={65}
          color="#dc2626"
          style={{ margin: "0 auto 20px" }}
        />

        <h1>Access Denied</h1>

        <p style={{ color: "#777", margin: "15px 0 25px" }}>
          You don't have permission to access this page.
          Please contact your administrator.
        </p>

        <button
          onClick={() => navigate("/dashboard")}
          style={{
            padding: "12px 22px",
            background: "#171717",
            color: "#fff",
            border: "none",
            borderRadius: "8px",
          }}
        >
          <ArrowLeft
            size={15}
            style={{
              display: "inline",
              verticalAlign: "middle",
              marginRight: "8px",
            }}
          />
          Back to Dashboard
        </button>

      </div>

    </div>
  );
}

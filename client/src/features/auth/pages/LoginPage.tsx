
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Code2 } from "lucide-react";

import LoginForm from "../components/LoginForm";
import { loginApi } from "../api/authApi";
import { getApiErrorMessage } from "../../../services/api/apiError";
import { useAuth } from "../hooks/useAuth";

import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async (
    username: string,
    password: string
  ) => {

    try {

      setLoading(true);
      setError("");

      const response = await loginApi({
        username,
        password,
      });


      if (!response.accessToken) {
        throw new Error("Authentication token was not received.");
      }

      // Update global authentication state
      login(response);

      // Navigate to dashboard
      navigate("/dashboard", { replace: true });

    } catch (err) {

      setError(getApiErrorMessage(err));

    } finally {

      setLoading(false);

    }
  };

  return (
    <div className="login-container">

      {/* LEFT SIDE */}
      <section className="login-left">
        <div className="login-content">

          <div className="brand">
            <div className="brand-logo">A</div>
            <span>Stranger</span>
          </div>

          <div className="login-heading">
            <h1>Sign in</h1>
            <p>Welcome back! Please enter your details.</p>
          </div>

          <LoginForm
            onSubmit={handleLogin}
            loading={loading}
          />

          {error && (
            <p className="form-error" role="alert">
              {error}
            </p>
          )}

          <div className="signup-text">
            Don't have an account?{" "}
            <span>Contact Administrator</span>
          </div>

          <div className="social-login">
            <div className="social-icon google">G</div>

            <div className="social-icon github">
              <Code2 size={25} />
            </div>

            <div className="social-icon facebook">f</div>
          </div>

          <p className="login-footer">
            © 2026 Stranger. All rights reserved.
          </p>

        </div>
      </section>

      {/* RIGHT SIDE */}
      <section className="login-right">

        <div className="right-decoration">
          <div className="large-logo">A</div>
          <div className="diagonal-line"></div>
          <div className="diagonal-line second"></div>
        </div>

        <div className="welcome-content">
          <span className="welcome-brand">Stranger</span>

          <h2>Welcome to Stranger</h2>

          <p>
            Manage your business operations with a secure,
            organized and intelligent platform. Monitor
            customers, accounts, alerts and investigations
            from one centralized dashboard.
          </p>

          <p className="community-text">
            One platform. Complete visibility. Smarter decisions.
          </p>
        </div>

        <div className="promo-card">
          <h3>
            Manage your business.
            <br />
            Stay ahead of every risk.
          </h3>

          <p>
            Experience a smarter way to manage employees,
            customers, accounts and regulatory operations.
          </p>

          <div className="promo-indicator">
            <span>SECURE</span>
            <span>RELIABLE</span>
            <span>SMART</span>
          </div>
        </div>

        <div className="right-footer">
          POWERED BY Stranger
        </div>

      </section>

    </div>
  );
}

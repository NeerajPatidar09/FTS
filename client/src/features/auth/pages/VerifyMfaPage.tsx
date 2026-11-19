import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { verifyMfaCode } from "../api/mfaApi";
import { useAuth } from "../hooks/useAuth";
import "./MfaPage.css";

interface StoredChallenge {
  challengeToken: string;
  expiresAt: number;
}

export default function VerifyMfaPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [code, setCode] = useState("");
  const [challenge, setChallenge] = useState<StoredChallenge | null>(null);
  const [remainingSeconds, setRemainingSeconds] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const stored = sessionStorage.getItem("stranger_mfa_challenge");

    if (!stored) {
      navigate("/login", { replace: true });
      return;
    }

    try {
      const parsed: StoredChallenge = JSON.parse(stored);

      if (
        !parsed.challengeToken ||
        !parsed.expiresAt ||
        parsed.expiresAt <= Date.now()
      ) {
        sessionStorage.removeItem("stranger_mfa_challenge");
        navigate("/login", { replace: true });
        return;
      }

      setChallenge(parsed);
      setRemainingSeconds(
        Math.ceil((parsed.expiresAt - Date.now()) / 1000)
      );
    } catch {
      sessionStorage.removeItem("stranger_mfa_challenge");
      navigate("/login", { replace: true });
    }
  }, [navigate]);

  useEffect(() => {
    if (!challenge) return;

    const interval = window.setInterval(() => {
      const remaining = Math.max(
        0,
        Math.ceil((challenge.expiresAt - Date.now()) / 1000)
      );

      setRemainingSeconds(remaining);

      if (remaining === 0) {
        sessionStorage.removeItem("stranger_mfa_challenge");
        setChallenge(null);
        setError("Your verification session expired. Please log in again.");
      }
    }, 1000);

    return () => window.clearInterval(interval);
  }, [challenge]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    if (!challenge || remainingSeconds <= 0) {
      setError("Your verification session has expired. Please log in again.");
      return;
    }

    if (!/^\d{6}$/.test(code)) {
      setError("Enter a valid six-digit authenticator code.");
      return;
    }

    try {
      setLoading(true);

      const response = await verifyMfaCode({
        challengeToken: challenge.challengeToken,
        code,
      });
      // Remove the temporary challenge after successful verification.
      sessionStorage.removeItem("stranger_mfa_challenge");
      // Save the final access token and user data in AuthContext.
      login(response);

      navigate("/dashboard", { replace: true });
    } catch (err) {
      console.error("MFA verification failed:", err);
      setError("Invalid OTP or verification failed. Please try again.");
      setCode("");
    } finally {
      setLoading(false);
    }
  };

  const handleBackToLogin = () => {
    sessionStorage.removeItem("stranger_mfa_challenge");
    navigate("/login", { replace: true });
  };

  return (
    <div className="mfa-auth-page">
      <div className="mfa-auth-card">
        <div className="mfa-brand">A</div>

        <div className="mfa-auth-icon">🔐</div>

        <h1>Two-Factor Authentication</h1>

        <p className="mfa-description">
          Open your authenticator application and enter the
          six-digit verification code.
        </p>

        <form onSubmit={handleSubmit}>
          <div className="mfa-field">
            <label htmlFor="mfaCode">Authenticator Code</label>

            <input
              id="mfaCode"
              type="text"
              inputMode="numeric"
              autoComplete="one-time-code"
              placeholder="000000"
              value={code}
              onChange={(event) =>
                setCode(
                  event.target.value.replace(/\D/g, "").slice(0, 6)
                )
              }
              maxLength={6}
              disabled={loading || !challenge}
              autoFocus
            />
          </div>

          <div className="mfa-timer">
            Verification expires in{" "}
            <strong>
              {Math.floor(remainingSeconds / 60)}:
              {String(remainingSeconds % 60).padStart(2, "0")}
            </strong>
          </div>

          {error && <div className="mfa-error">{error}</div>}

          <button
            type="submit"
            className="mfa-submit-button"
            disabled={
              loading ||
              !challenge ||
              remainingSeconds <= 0 ||
              code.length !== 6
            }
          >
            {loading ? "Verifying..." : "Verify & Continue"}
          </button>
        </form>

        <button
          type="button"
          className="mfa-back-button"
          onClick={handleBackToLogin}
          disabled={loading}
        >
          Back to Login
        </button>

        <div className="mfa-security-note">
          <span>🔒</span>
          Your account is protected with multi-factor authentication.
        </div>
      </div>
    </div>
  );
}

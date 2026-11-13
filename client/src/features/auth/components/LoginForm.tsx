
import { useState } from "react";
import {
  Mail,
  LockKeyhole,
  Eye,
  EyeOff,
} from "lucide-react";

interface LoginFormProps {
  onSubmit: (username: string, password: string) => void;
  loading: boolean;
}

export default function LoginForm({
  onSubmit,
  loading,
}: LoginFormProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!username.trim() || !password.trim()) {
      setError("Please enter your username and password.");
      return;
    }

    setError("");
    onSubmit(username.trim(), password);
  };

  return (
    <form onSubmit={handleSubmit} className="login-form">

      <div className="form-group">
        <label htmlFor="username">Username</label>

        <div className="input-wrapper">
          <Mail size={18} />

          <input
            id="username"
            type="text"
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            disabled={loading}
          />
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="password">Password</label>

        <div className="input-wrapper">
          <LockKeyhole size={18} />

          <input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            disabled={loading}
          />

          <button
            type="button"
            className="password-toggle"
            onClick={() => setShowPassword(!showPassword)}
            disabled={loading}
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        </div>
      </div>

      <div className="form-options">
        <label className="remember-me">
          <input
            type="checkbox"
            checked={rememberMe}
            onChange={(e) => setRememberMe(e.target.checked)}
            disabled={loading}
          />
          <span>Remember me</span>
        </label>

        <button
          type="button"
          className="forgot-password"
          disabled={loading}
          onClick={() => setError("Forgot password functionality will be added later.")}
        >
          Forgot Password?
        </button>
      </div>

      {error && <p className="form-error">{error}</p>}

      <button
        type="submit"
        className="login-button"
        disabled={loading}
      >
        {loading ? "Signing in..." : "Sign in"}
      </button>

    </form>
  );
}

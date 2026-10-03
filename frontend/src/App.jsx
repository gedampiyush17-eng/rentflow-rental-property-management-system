import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";

import { useState } from "react";

import api from "./services/api";
import { getToken, getRole } from "./utils/auth";

import TenantDashboard from "./pages/TenantDashboard";
import OwnerDashboard from "./pages/OwnerDashboard";

import Properties from "./pages/Properties";
import Units from "./pages/Units";
import UnitDetails from "./pages/UnitDetails";

import Tenants from "./pages/Tenants";
import TenantDetails from "./pages/TenantDetails";
import AddTenant from "./pages/AddTenant";

import Leases from "./pages/Leases";
import RentCycles from "./pages/RentCycles";

import Payments from "./pages/Payments";
import Receipts from "./pages/Receipts";

import OwnerLayout from "./layouts/OwnerLayout";


function Login() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");


  const handleLogin = async (e) => {

    e.preventDefault();

    try {

      const response = await api.post(
        "/auth/login",
        {
          email,
          password,
        }
      );

      localStorage.setItem(
        "token",
        response.data.token
      );

      const role = getRole();

      setMessage("Login successful!");

      window.location.href =
        role === "TENANT"
          ? "/tenant"
          : "/owner";

    } catch (error) {

      console.error(error);

      setMessage(
        error.response?.data?.message ||
        "Login failed"
      );

    }

  };


  return (
    <div className="login-page">

      <div className="login-card">

        <h1>
          RentFlow
        </h1>

        <p className="subtitle">
          Rental & Property Management System
        </p>


        <form onSubmit={handleLogin}>

          <div className="form-group">

            <label>
              Email
            </label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) =>
                setEmail(e.target.value)
              }
              required
            />

          </div>


          <div className="form-group">

            <label>
              Password
            </label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) =>
                setPassword(e.target.value)
              }
              required
            />

          </div>


          <button type="submit">
            Login
          </button>

        </form>


        {message && (
          <p
            style={{
              marginTop: "20px",
              textAlign: "center",
            }}
          >
            {message}
          </p>
        )}

      </div>

    </div>
  );
}


function App() {

  const token = getToken();
  const role = getRole();


  return (
    <BrowserRouter>

      <Routes>


        {/* LOGIN */}

        <Route
          path="/"
          element={
            token ? (
              <Navigate
                to={
                  role === "TENANT"
                    ? "/tenant"
                    : "/owner"
                }
              />
            ) : (
              <Login />
            )
          }
        />


        {/* TENANT */}

        <Route
          path="/tenant"
          element={
            token && role === "TENANT" ? (
              <TenantDashboard />
            ) : (
              <Navigate to="/" />
            )
          }
        />


        {/* OWNER */}

        <Route element={<OwnerLayout />}>


          <Route
            path="/owner"
            element={
              <OwnerDashboard />
            }
          />


          <Route
            path="/properties"
            element={
              <Properties />
            }
          />


          <Route
            path="/units"
            element={
              <Units />
            }
          />


          <Route
            path="/units/:id"
            element={
              <UnitDetails />
            }
          />


          <Route
            path="/tenants"
            element={
              <Tenants />
            }
          />


          <Route
            path="/tenants/:id"
            element={
              <TenantDetails />
            }
          />


          <Route
            path="/tenants/add"
            element={
              <AddTenant />
            }
          />


          <Route
            path="/leases"
            element={
              <Leases />
            }
          />


          <Route
            path="/rent-cycles"
            element={
              <RentCycles />
            }
          />


          {/* PAYMENTS */}

          <Route
            path="/payments"
            element={
              <Payments />
            }
          />


          {/* RECEIPTS */}

          <Route
            path="/receipts"
            element={
              <Receipts />
            }
          />


        </Route>


        {/* FALLBACK */}

        <Route
          path="*"
          element={
            <Navigate to="/" />
          }
        />


      </Routes>

    </BrowserRouter>
  );
}


export default App;
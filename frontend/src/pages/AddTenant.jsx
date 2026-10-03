import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./AddTenant.css";

function AddTenant() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
    occupation: "",
    aadhaarNumber: "",
    dateOfBirth: "",
    emergencyContactName: "",
    emergencyContactPhone: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    try {
      await api.post("/tenants", formData);

      setMessage("Tenant created successfully.");

      setTimeout(() => {
        navigate("/tenants");
      }, 1000);

    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
        "Failed to create tenant."
      );
    }
  };

  return (
    <div className="add-tenant-page">

      <div className="add-tenant-header">

        <div>
          <h1>Add Tenant</h1>

          <p>
            Create a new tenant and manage their rental information.
          </p>
        </div>

        <button
          className="back-btn"
          onClick={() => navigate("/tenants")}
        >
          ← Back to Tenants
        </button>

      </div>


      {message && (
        <div className="tenant-message success">
          {message}
        </div>
      )}


      {error && (
        <div className="tenant-message error">
          {error}
        </div>
      )}


      <div className="add-tenant-card">

        <form
          className="add-tenant-form"
          onSubmit={handleSubmit}
        >

          <div className="form-group">
            <label>First Name</label>

            <input
              type="text"
              name="firstName"
              placeholder="Enter first name"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
          </div>


          <div className="form-group">
            <label>Last Name</label>

            <input
              type="text"
              name="lastName"
              placeholder="Enter last name"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>


          <div className="form-group">
            <label>Email</label>

            <input
              type="email"
              name="email"
              placeholder="Enter email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>


          <div className="form-group">
            <label>Password</label>

            <input
              type="password"
              name="password"
              placeholder="Create login password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>


          <div className="form-group">
            <label>Phone Number</label>

            <input
              type="text"
              name="phoneNumber"
              placeholder="Enter phone number"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
            />
          </div>


          <div className="form-group">
            <label>Occupation</label>

            <input
              type="text"
              name="occupation"
              placeholder="e.g. Software Engineer"
              value={formData.occupation}
              onChange={handleChange}
            />
          </div>


          <div className="form-group">
            <label>Aadhaar Number</label>

            <input
              type="text"
              name="aadhaarNumber"
              placeholder="Enter Aadhaar number"
              value={formData.aadhaarNumber}
              onChange={handleChange}
            />
          </div>


          <div className="form-group">
            <label>Date of Birth</label>

            <input
              type="date"
              name="dateOfBirth"
              value={formData.dateOfBirth}
              onChange={handleChange}
            />
          </div>


          <div className="form-group">
            <label>Emergency Contact Name</label>

            <input
              type="text"
              name="emergencyContactName"
              placeholder="Emergency contact name"
              value={formData.emergencyContactName}
              onChange={handleChange}
            />
          </div>


          <div className="form-group">
            <label>Emergency Contact Phone</label>

            <input
              type="text"
              name="emergencyContactPhone"
              placeholder="Emergency contact phone"
              value={formData.emergencyContactPhone}
              onChange={handleChange}
            />
          </div>


          <div className="add-tenant-actions">

            <button
              type="button"
              className="cancel-btn"
              onClick={() => navigate("/tenants")}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="save-tenant-btn"
            >
              Create Tenant
            </button>

          </div>

        </form>

      </div>

    </div>
  );
}

export default AddTenant;
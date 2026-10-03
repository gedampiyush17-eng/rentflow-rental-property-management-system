import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./Units.css";

function Units() {
  const navigate = useNavigate();

  const [units, setUnits] = useState([]);
  const [properties, setProperties] = useState([]);

  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    unitNumber: "",
    monthlyRent: "",
    securityDeposit: "",
    area: "",
    notes: "",
    propertyId: "",
  });

  useEffect(() => {
    loadUnits();
    loadProperties();
  }, []);

  const loadUnits = async () => {
    try {
      const response = await api.get("/units");
      setUnits(response.data);
    } catch (error) {
      console.error("Failed to load units:", error);
      setError("Failed to load units.");
    } finally {
      setLoading(false);
    }
  };

  const loadProperties = async () => {
    try {
      const response = await api.get("/properties");
      setProperties(response.data);
    } catch (error) {
      console.error("Failed to load properties:", error);
    }
  };

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleCreateUnit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    try {
      await api.post("/units", {
        unitNumber: form.unitNumber,
        monthlyRent: Number(form.monthlyRent),
        securityDeposit: Number(form.securityDeposit),
        area: Number(form.area),
        notes: form.notes,
        propertyId: form.propertyId,
      });

      setMessage("Unit created successfully.");

      setForm({
        unitNumber: "",
        monthlyRent: "",
        securityDeposit: "",
        area: "",
        notes: "",
        propertyId: "",
      });

      setShowForm(false);

      loadUnits();

    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
        "Failed to create unit."
      );
    }
  };

  if (loading) {
    return (
      <div className="units-page">
        <h2>Loading units...</h2>
      </div>
    );
  }

  return (
    <div className="units-page">

      {/* HEADER */}

      <div className="units-header">

        <div>
          <h1>My Units</h1>

          <p>
            Manage your rental units and availability.
          </p>
        </div>

        <button
          className="add-unit-btn"
          onClick={() => {
            setShowForm(!showForm);
            setMessage("");
            setError("");
          }}
        >
          {showForm ? "✕ Close" : "+ Add Unit"}
        </button>

      </div>


      {/* SUCCESS */}

      {message && (
        <div className="success-message">
          ✓ {message}
        </div>
      )}


      {/* ERROR */}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}


      {/* CREATE UNIT FORM */}

      {showForm && (

        <div className="unit-form-card">

          <div className="form-title">

            <div>
              <h2>Add New Unit</h2>

              <p>
                Add a rental unit to one of your properties.
              </p>
            </div>

          </div>


          <form onSubmit={handleCreateUnit}>

            <div className="form-grid">


              {/* PROPERTY */}

              <div className="form-group">

                <label>Property</label>

                <select
                  name="propertyId"
                  value={form.propertyId}
                  onChange={handleChange}
                  required
                >

                  <option value="">
                    Select property
                  </option>

                  {properties.map((property) => (

                    <option
                      key={property.id}
                      value={property.id}
                    >
                      {property.propertyName}
                    </option>

                  ))}

                </select>

              </div>


              {/* UNIT NUMBER */}

              <div className="form-group">

                <label>Unit Number</label>

                <input
                  type="text"
                  name="unitNumber"
                  placeholder="e.g. 102"
                  value={form.unitNumber}
                  onChange={handleChange}
                  required
                />

              </div>


              {/* MONTHLY RENT */}

              <div className="form-group">

                <label>Monthly Rent</label>

                <input
                  type="number"
                  name="monthlyRent"
                  placeholder="18000"
                  value={form.monthlyRent}
                  onChange={handleChange}
                  min="0"
                  required
                />

              </div>


              {/* SECURITY DEPOSIT */}

              <div className="form-group">

                <label>Security Deposit</label>

                <input
                  type="number"
                  name="securityDeposit"
                  placeholder="36000"
                  value={form.securityDeposit}
                  onChange={handleChange}
                  min="0"
                  required
                />

              </div>


              {/* AREA */}

              <div className="form-group">

                <label>Area (sq ft)</label>

                <input
                  type="number"
                  name="area"
                  placeholder="950"
                  value={form.area}
                  onChange={handleChange}
                  min="0"
                  required
                />

              </div>


              {/* NOTES */}

              <div className="form-group form-group-full">

                <label>Notes</label>

                <textarea
                  name="notes"
                  placeholder="Two bedroom unit on the first floor"
                  value={form.notes}
                  onChange={handleChange}
                  rows="3"
                />

              </div>

            </div>


            <div className="form-actions">

              <button
                type="button"
                className="cancel-btn"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="save-unit-btn"
              >
                Create Unit
              </button>

            </div>

          </form>

        </div>

      )}


      {/* UNITS */}

      <div className="units-section">

        <div className="section-header">

          <div>
            <h2>Units ({units.length})</h2>

            <p>
              Your rental units
            </p>
          </div>

        </div>


        {units.length === 0 ? (

          <div className="empty-state">

            <h3>No units found</h3>

            <p>
              Create your first rental unit.
            </p>

          </div>

        ) : (

          <div className="units-list">

            {units.map((unit) => (

              <div
                className="unit-card"
                key={unit.id}
              >

                <div className="unit-main">


                  {/* UNIT INFO */}

                  <div className="unit-title">

                    <h3>
                      Unit {unit.unitNumber}
                    </h3>

                    <p>
                      Property ID: {unit.propertyId}
                    </p>

                  </div>


                  {/* UNIT DETAILS */}

                  <div className="unit-detail">

                    <span>Monthly Rent</span>

                    <strong>
                      ₹{unit.monthlyRent}
                    </strong>

                  </div>


                  <div className="unit-detail">

                    <span>Security Deposit</span>

                    <strong>
                      ₹{unit.securityDeposit}
                    </strong>

                  </div>


                  <div className="unit-detail">

                    <span>Area</span>

                    <strong>
                      {unit.area} sq ft
                    </strong>

                  </div>


                  {/* STATUS */}

                  <div className="unit-status-wrap">

                    <span className="unit-status">
                      ACTIVE
                    </span>

                  </div>

                </div>


                {/* NOTES */}

                {unit.notes && (

                  <div className="unit-notes">
                    {unit.notes}
                  </div>

                )}


                {/* ACTION */}

                <div className="unit-actions">

                  <button
                    className="view-unit-btn"
                    onClick={() =>
                      navigate(`/units/${unit.id}`)
                    }
                  >
                    View Details →
                  </button>

                </div>

              </div>

            ))}

          </div>

        )}

      </div>

    </div>
  );
}

export default Units;
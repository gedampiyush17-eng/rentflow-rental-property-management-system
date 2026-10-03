import { useEffect, useState } from "react";
import api from "../services/api";
import "./Leases.css";

function Leases() {
  const [leases, setLeases] = useState([]);
  const [tenants, setTenants] = useState([]);
  const [units, setUnits] = useState([]);

  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    tenantId: "",
    unitId: "",
    monthlyRent: "",
    securityDeposit: "",
    leaseStartDate: "",
    leaseEndDate: "",
    paymentDueDay: ""
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [leasesResponse, tenantsResponse, unitsResponse] =
        await Promise.all([
          api.get("/leases"),
          api.get("/tenants"),
          api.get("/units")
        ]);

      setLeases(leasesResponse.data);
      setTenants(tenantsResponse.data);

      // Only show vacant units when creating a lease
      setUnits(
        unitsResponse.data.filter(
          (unit) =>
            unit.occupancyStatus === "VACANT" &&
            unit.active !== false
        )
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load lease data.");
    } finally {
      setLoading(false);
    }
  };

  const getTenant = (tenantId) => {
    return tenants.find(
      (tenant) => String(tenant.id) === String(tenantId)
    );
  };

  const getUnit = (unitId) => {
    return units.find(
      (unit) => String(unit.id) === String(unitId)
    );
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleCreateLease = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");
    setSaving(true);

    try {
      await api.post("/leases", {
        tenantId: formData.tenantId,
        unitId: formData.unitId,
        monthlyRent: Number(formData.monthlyRent),
        securityDeposit: Number(formData.securityDeposit),
        leaseStartDate: formData.leaseStartDate,
        leaseEndDate: formData.leaseEndDate,
        paymentDueDay: Number(formData.paymentDueDay)
      });

      setSuccess("Lease created successfully.");

      setFormData({
        tenantId: "",
        unitId: "",
        monthlyRent: "",
        securityDeposit: "",
        leaseStartDate: "",
        leaseEndDate: "",
        paymentDueDay: ""
      });

      setShowForm(false);

      await loadData();

    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
        "Failed to create lease."
      );
    } finally {
      setSaving(false);
    }
  };

  const formatDate = (date) => {
    if (!date) return "-";

    const [year, month, day] = date.split("-");

    return `${day}-${month}-${year}`;
  };

  const formatAmount = (amount) => {
    return Number(amount || 0).toLocaleString("en-IN");
  };

  const activeLeases = leases.filter(
    (lease) => lease.leaseStatus === "ACTIVE"
  );

  const terminatedLeases = leases.filter(
    (lease) => lease.leaseStatus === "TERMINATED"
  );

  const expiredLeases = leases.filter(
    (lease) => lease.leaseStatus === "EXPIRED"
  );

  const totalRent = activeLeases.reduce(
    (total, lease) =>
      total + Number(lease.monthlyRent || 0),
    0
  );

  if (loading) {
    return (
      <div className="leases-page">
        <div className="leases-loading">
          Loading leases...
        </div>
      </div>
    );
  }

  return (
    <div className="leases-page">

      {/* HEADER */}

      <div className="leases-header">

        <div>
          <h1>Leases</h1>

          <p>
            Manage rental agreements between tenants and units
          </p>
        </div>

        <button
          className="create-lease-button"
          onClick={() => {
            setShowForm(!showForm);
            setError("");
            setSuccess("");
          }}
        >
          {showForm ? "✕ Close" : "+ Create Lease"}
        </button>

      </div>


      {/* MESSAGES */}

      {error && (
        <div className="leases-error">
          {error}
        </div>
      )}

      {success && (
        <div className="leases-success">
          {success}
        </div>
      )}


      {/* CREATE LEASE FORM */}

      {showForm && (
        <div className="lease-form-card">

          <div className="lease-form-header">

            <div>
              <h2>Create Lease</h2>

              <p>
                Create a new rental agreement between a tenant
                and a vacant unit.
              </p>
            </div>

          </div>


          <form onSubmit={handleCreateLease}>

            {/* TENANT + UNIT */}

            <div className="lease-form-section">

              <h3>Lease Assignment</h3>

              <p>
                Select the tenant and vacant unit
              </p>

              <div className="lease-form-row">

                <div className="lease-form-group">

                  <label>
                    Tenant <span>*</span>
                  </label>

                  <select
                    name="tenantId"
                    value={formData.tenantId}
                    onChange={handleChange}
                    required
                  >

                    <option value="">
                      Select Tenant
                    </option>

                    {tenants.map((tenant) => (
                      <option
                        key={tenant.id}
                        value={tenant.id}
                      >
                        {tenant.firstName} {tenant.lastName}
                      </option>
                    ))}

                  </select>

                </div>


                <div className="lease-form-group">

                  <label>
                    Unit <span>*</span>
                  </label>

                  <select
                    name="unitId"
                    value={formData.unitId}
                    onChange={handleChange}
                    required
                  >

                    <option value="">
                      Select Vacant Unit
                    </option>

                    {units.map((unit) => (
                      <option
                        key={unit.id}
                        value={unit.id}
                      >
                        {unit.unitNumber}
                      </option>
                    ))}

                  </select>

                </div>

              </div>

            </div>


            {/* FINANCIAL DETAILS */}

            <div className="lease-form-section">

              <h3>Financial Details</h3>

              <p>
                Set the monthly rent and security deposit
              </p>

              <div className="lease-form-row">

                <div className="lease-form-group">

                  <label>
                    Monthly Rent <span>*</span>
                  </label>

                  <div className="lease-input-prefix">

                    <span>₹</span>

                    <input
                      type="number"
                      name="monthlyRent"
                      placeholder="15000"
                      min="0"
                      value={formData.monthlyRent}
                      onChange={handleChange}
                      required
                    />

                  </div>

                </div>


                <div className="lease-form-group">

                  <label>
                    Security Deposit <span>*</span>
                  </label>

                  <div className="lease-input-prefix">

                    <span>₹</span>

                    <input
                      type="number"
                      name="securityDeposit"
                      placeholder="30000"
                      min="0"
                      value={formData.securityDeposit}
                      onChange={handleChange}
                      required
                    />

                  </div>

                </div>

              </div>

            </div>


            {/* LEASE PERIOD */}

            <div className="lease-form-section">

              <h3>Lease Period</h3>

              <p>
                Specify the rental agreement duration
              </p>

              <div className="lease-form-row">

                <div className="lease-form-group">

                  <label>
                    Lease Start Date <span>*</span>
                  </label>

                  <input
                    type="date"
                    name="leaseStartDate"
                    value={formData.leaseStartDate}
                    onChange={handleChange}
                    required
                  />

                </div>


                <div className="lease-form-group">

                  <label>
                    Lease End Date <span>*</span>
                  </label>

                  <input
                    type="date"
                    name="leaseEndDate"
                    value={formData.leaseEndDate}
                    onChange={handleChange}
                    required
                  />

                </div>

              </div>


              <div className="lease-form-row">

                <div className="lease-form-group">

                  <label>
                    Payment Due Day <span>*</span>
                  </label>

                  <input
                    type="number"
                    name="paymentDueDay"
                    placeholder="5"
                    min="1"
                    max="28"
                    value={formData.paymentDueDay}
                    onChange={handleChange}
                    required
                  />

                  <small>
                    Day of the month when rent is due
                  </small>

                </div>

              </div>

            </div>


            {/* ACTIONS */}

            <div className="lease-form-actions">

              <button
                type="button"
                className="lease-cancel-button"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="lease-save-button"
                disabled={saving}
              >
                {saving
                  ? "Creating..."
                  : "Create Lease"}
              </button>

            </div>

          </form>

        </div>
      )}


      {/* STATS */}

      <div className="leases-stats">

        <div className="lease-stat-card">

          <div className="lease-stat-icon">
            ▤
          </div>

          <div>
            <span>Total Leases</span>
            <strong>{leases.length}</strong>
          </div>

        </div>


        <div className="lease-stat-card">

          <div className="lease-stat-icon active">
            ✓
          </div>

          <div>
            <span>Active</span>
            <strong>{activeLeases.length}</strong>
          </div>

        </div>


        <div className="lease-stat-card">

          <div className="lease-stat-icon terminated">
            ◷
          </div>

          <div>
            <span>Terminated</span>
            <strong>{terminatedLeases.length}</strong>
          </div>

        </div>


        <div className="lease-stat-card">

          <div className="lease-stat-icon expired">
            !
          </div>

          <div>
            <span>Expired</span>
            <strong>{expiredLeases.length}</strong>
          </div>

        </div>


        <div className="lease-stat-card">

          <div className="lease-stat-icon rent">
            ₹
          </div>

          <div>
            <span>Monthly Rent</span>
            <strong>
              ₹{formatAmount(totalRent)}
            </strong>
          </div>

        </div>

      </div>


      {/* LEASE TABLE */}

      <div className="leases-card">

        <div className="leases-card-header">

          <div>
            <h2>Lease Agreements</h2>

            <p>
              Current and historical rental agreements
            </p>
          </div>

        </div>


        {leases.length === 0 ? (

          <div className="leases-empty">

            <div className="leases-empty-icon">
              ▤
            </div>

            <h3>No leases found</h3>

            <p>
              Create a lease to get started.
            </p>

          </div>

        ) : (

          <div className="leases-table-wrapper">

            <table className="leases-table">

              <thead>

                <tr>

                  <th>LEASE</th>
                  <th>TENANT</th>
                  <th>UNIT</th>
                  <th>LEASE PERIOD</th>
                  <th>MONTHLY RENT</th>
                  <th>DEPOSIT</th>
                  <th>DUE DAY</th>
                  <th>STATUS</th>

                </tr>

              </thead>


              <tbody>

                {leases.map((lease) => {

                  const tenant =
                    getTenant(lease.tenantId);

                  const unit =
                    getUnit(lease.unitId);

                  return (

                    <tr key={lease.id}>

                      <td>

                        <div className="lease-id">

                          <div className="lease-icon">
                            ▤
                          </div>

                          <div>

                            <strong>
                              {lease.id?.slice(0, 8)}...
                            </strong>

                            <span>
                              Lease
                            </span>

                          </div>

                        </div>

                      </td>


                      <td>

                        <div className="lease-tenant">

                          <div className="lease-avatar">

                            {tenant?.firstName
                              ?.charAt(0)
                              ?.toUpperCase() || "T"}

                          </div>

                          <div>

                            <strong>
                              {tenant
                                ? `${tenant.firstName} ${tenant.lastName}`
                                : "Unknown Tenant"}
                            </strong>

                            <span>
                              {tenant?.email || "No email"}
                            </span>

                          </div>

                        </div>

                      </td>


                      <td>

                        <span className="lease-unit">

                          {unit?.unitNumber || "Unknown"}

                        </span>

                      </td>


                      <td>

                        <div className="lease-period">

                          <span>
                            {formatDate(
                              lease.leaseStartDate
                            )}
                          </span>

                          <span className="lease-arrow">
                            →
                          </span>

                          <span>
                            {formatDate(
                              lease.leaseEndDate
                            )}
                          </span>

                        </div>

                      </td>


                      <td>

                        <strong className="lease-rent">

                          ₹
                          {formatAmount(
                            lease.monthlyRent
                          )}

                        </strong>

                      </td>


                      <td>

                        <span className="lease-deposit">

                          ₹
                          {formatAmount(
                            lease.securityDeposit
                          )}

                        </span>

                      </td>


                      <td>

                        <span className="lease-due-day">

                          {lease.paymentDueDay
                            ? `Day ${lease.paymentDueDay}`
                            : "-"}

                        </span>

                      </td>


                      <td>

                        <span
                          className={`lease-status ${
                            lease.leaseStatus?.toLowerCase()
                          }`}
                        >

                          <span className="lease-status-dot"></span>

                          {lease.leaseStatus}

                        </span>

                      </td>

                    </tr>

                  );

                })}

              </tbody>

            </table>

          </div>

        )}

      </div>

    </div>
  );
}

export default Leases;
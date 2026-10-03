import { useEffect, useState } from "react";
import api from "../services/api";
import "./RentCycles.css";

function RentCycles() {
  const [cycles, setCycles] = useState([]);
  const [tenants, setTenants] = useState([]);
  const [units, setUnits] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [cyclesResponse, tenantsResponse, unitsResponse] =
        await Promise.all([
          api.get("/rent-cycles"),
          api.get("/tenants"),
          api.get("/units"),
        ]);

      setCycles(cyclesResponse.data);
      setTenants(tenantsResponse.data);
      setUnits(unitsResponse.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load rent cycles.");
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

  const formatDate = (date) => {
    if (!date) return "-";

    const [year, month, day] = date.split("-");

    return `${day}-${month}-${year}`;
  };

  const formatAmount = (amount) => {
    return Number(amount || 0).toLocaleString("en-IN");
  };

  const totalCycles = cycles.length;

  const pendingCycles = cycles.filter(
    (cycle) => cycle.status === "PENDING"
  ).length;

  const partiallyPaidCycles = cycles.filter(
    (cycle) => cycle.status === "PARTIALLY_PAID"
  ).length;

  const paidCycles = cycles.filter(
    (cycle) => cycle.status === "PAID"
  ).length;

  const totalOutstanding = cycles.reduce(
    (total, cycle) =>
      total + Number(cycle.balanceDue || 0),
    0
  );

  if (loading) {
    return (
      <div className="rent-cycles-page">
        <div className="rent-cycles-loading">
          Loading rent cycles...
        </div>
      </div>
    );
  }

  return (
    <div className="rent-cycles-page">

      {/* Header */}
      <div className="rent-cycles-header">
        <div>
          <h1>Rent Cycles</h1>
          <p>Track rent periods, payments and outstanding dues</p>
        </div>
      </div>

      {/* Error */}
      {error && (
        <div className="rent-cycles-error">
          {error}
        </div>
      )}

      {/* Stats */}
      <div className="rent-cycles-stats">

        <div className="rent-cycle-stat-card">
          <div className="rent-cycle-stat-icon">
            ▤
          </div>

          <div>
            <span>Total Cycles</span>
            <strong>{totalCycles}</strong>
          </div>
        </div>

        <div className="rent-cycle-stat-card">
          <div className="rent-cycle-stat-icon pending">
            ◷
          </div>

          <div>
            <span>Pending</span>
            <strong>{pendingCycles}</strong>
          </div>
        </div>

        <div className="rent-cycle-stat-card">
          <div className="rent-cycle-stat-icon partial">
            ◐
          </div>

          <div>
            <span>Partially Paid</span>
            <strong>{partiallyPaidCycles}</strong>
          </div>
        </div>

        <div className="rent-cycle-stat-card">
          <div className="rent-cycle-stat-icon paid">
            ✓
          </div>

          <div>
            <span>Paid</span>
            <strong>{paidCycles}</strong>
          </div>
        </div>

        <div className="rent-cycle-stat-card">
          <div className="rent-cycle-stat-icon outstanding">
            ₹
          </div>

          <div>
            <span>Outstanding</span>

            <strong>
              ₹{formatAmount(totalOutstanding)}
            </strong>
          </div>
        </div>

      </div>

      {/* Main Card */}
      <div className="rent-cycles-card">

        <div className="rent-cycles-card-header">
          <div>
            <h2>Rent Cycle History</h2>
            <p>
              Payment status for all active rent cycles
            </p>
          </div>
        </div>

        {cycles.length === 0 ? (

          <div className="rent-cycles-empty">

            <div className="rent-cycles-empty-icon">
              ▤
            </div>

            <h3>No rent cycles found</h3>

            <p>
              Rent cycles will appear here once they
              are generated for a lease.
            </p>

          </div>

        ) : (

          <div className="rent-cycles-table-wrapper">

            <table className="rent-cycles-table">

              <thead>
                <tr>
                  <th>RENT CYCLE</th>
                  <th>TENANT</th>
                  <th>UNIT</th>
                  <th>RENT PERIOD</th>
                  <th>DUE DATE</th>
                  <th>AMOUNT DUE</th>
                  <th>PAID</th>
                  <th>BALANCE</th>
                  <th>STATUS</th>
                </tr>
              </thead>

              <tbody>

                {cycles.map((cycle) => {

                  const tenant = getTenant(
                    cycle.tenantId
                  );

                  const unit = getUnit(
                    cycle.unitId
                  );

                  return (
                    <tr key={cycle.id}>

                      {/* Rent Cycle */}
                      <td>
                        <div className="rent-cycle-id">

                          <div className="rent-cycle-icon">
                            ▤
                          </div>

                          <div>
                            <strong>
                              {cycle.id?.slice(0, 8)}...
                            </strong>

                            <span>
                              Rent Cycle
                            </span>
                          </div>

                        </div>
                      </td>

                      {/* Tenant */}
                      <td>
                        <div className="rent-cycle-tenant">

                          <div className="rent-cycle-avatar">
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

                      {/* Unit */}
                      <td>
                        <span className="rent-cycle-unit">
                          {unit?.unitNumber || "Unknown"}
                        </span>
                      </td>

                      {/* Period */}
                      <td>
                        <div className="rent-cycle-period">

                          <span>
                            {formatDate(
                              cycle.periodStart
                            )}
                          </span>

                          <span className="rent-cycle-arrow">
                            →
                          </span>

                          <span>
                            {formatDate(
                              cycle.periodEnd
                            )}
                          </span>

                        </div>
                      </td>

                      {/* Due Date */}
                      <td>
                        <span className="rent-cycle-due-date">
                          {formatDate(cycle.dueDate)}
                        </span>
                      </td>

                      {/* Amount Due */}
                      <td>
                        <strong className="rent-cycle-amount">
                          ₹{formatAmount(cycle.amountDue)}
                        </strong>
                      </td>

                      {/* Amount Paid */}
                      <td>
                        <span className="rent-cycle-paid">
                          ₹{formatAmount(cycle.amountPaid)}
                        </span>
                      </td>

                      {/* Balance */}
                      <td>
                        <strong
                          className={
                            Number(cycle.balanceDue) > 0
                              ? "rent-cycle-balance due"
                              : "rent-cycle-balance clear"
                          }
                        >
                          ₹{formatAmount(cycle.balanceDue)}
                        </strong>
                      </td>

                      {/* Status */}
                      <td>

                        <span
                          className={`rent-cycle-status ${cycle.status?.toLowerCase()}`}
                        >

                          <span className="rent-cycle-status-dot"></span>

                          {cycle.status
                            ?.replace("_", " ")}

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

export default RentCycles;
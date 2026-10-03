import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "./UnitDetails.css";

function UnitDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [unit, setUnit] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUnit();
  }, [id]);

  const loadUnit = async () => {
    try {
      const response = await api.get(`/units/${id}`);

      setUnit(response.data);

    } catch (error) {
      console.error(error);

    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="unit-details-page">
        Loading unit...
      </div>
    );
  }

  if (!unit) {
    return (
      <div className="unit-details-page">

        <h2>
          Unit not found
        </h2>

        <button
          onClick={() => navigate("/units")}
        >
          Back to Units
        </button>

      </div>
    );
  }

  return (
    <div className="unit-details-page">

      <button
        className="back-btn"
        onClick={() => navigate("/units")}
      >
        ← Back to Units
      </button>


      <div className="details-header">

        <div>

          <h1>
            Unit {unit.unitNumber}
          </h1>

          <p>
            Unit details and rental information
          </p>

        </div>

        <span className="status-badge">
          {unit.occupancyStatus || "ACTIVE"}
        </span>

      </div>


      <div className="details-grid">

        <div className="detail-card">

          <span>
            Unit Number
          </span>

          <strong>
            {unit.unitNumber}
          </strong>

        </div>


        <div className="detail-card">

          <span>
            Monthly Rent
          </span>

          <strong>
            ₹{unit.monthlyRent}
          </strong>

        </div>


        <div className="detail-card">

          <span>
            Security Deposit
          </span>

          <strong>
            ₹{unit.securityDeposit}
          </strong>

        </div>


        <div className="detail-card">

          <span>
            Area
          </span>

          <strong>
            {unit.area} sq ft
          </strong>

        </div>

      </div>


      <div className="unit-info-card">

        <h2>
          Additional Information
        </h2>


        <div className="info-row">

          <span>
            Notes
          </span>

          <strong>
            {unit.notes || "No notes available"}
          </strong>

        </div>


        <div className="info-row">

          <span>
            Property ID
          </span>

          <strong>
            {unit.propertyId}
          </strong>

        </div>

      </div>

    </div>
  );
}

export default UnitDetails;
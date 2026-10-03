import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./Properties.css";

function Properties() {
  const navigate = useNavigate();

  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadProperties();
  }, []);

  const loadProperties = async () => {
    try {
      const response = await api.get("/properties");

      setProperties(response.data);

    } catch (error) {
      console.error(error);

      setMessage("Failed to load properties.");

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="properties-page">

      {/* Header */}

      <div className="properties-header">

        <div>
          <h1>My Properties</h1>

          <p>
            Manage your rental properties and buildings.
          </p>
        </div>

        <button className="add-property-btn">
          <span>+</span>
          Add Property
        </button>

      </div>


      {/* Error message */}

      {message && (
        <div className="property-message error">
          {message}
        </div>
      )}


      {/* Properties section */}

      <div className="properties-section">

        <div className="section-header">

          <div>
            <h2>Properties</h2>

            <p>
              {properties.length}{" "}
              {properties.length === 1
                ? "property"
                : "properties"}
            </p>
          </div>

          <input
            className="property-search"
            type="text"
            placeholder="Search properties..."
          />

        </div>


        {/* Loading */}

        {loading && (
          <div className="property-loading">
            Loading properties...
          </div>
        )}


        {/* Empty */}

        {!loading && properties.length === 0 && (
          <div className="empty-properties">

            <h3>No properties yet</h3>

            <p>
              Add your first property to get started.
            </p>

          </div>
        )}


        {/* Property cards */}

        {!loading && properties.length > 0 && (

          <div className="property-list">

            {properties.map((property) => (

              <div
                className="property-card"
                key={property.id}
              >

                {/* Property image */}

                <div className="property-image">

                  <div className="building-icon">
                    🏢
                  </div>

                </div>


                {/* Main information */}

                <div className="property-main">

                  <div className="property-title-row">

                    <div>

                      <h3>
                        {property.propertyName}
                      </h3>

                      <p className="property-description">
                        {property.description ||
                          "Rental property"}
                      </p>

                    </div>

                    <span className="property-badge">
                      {property.propertyType}
                    </span>

                  </div>


                  {/* Location */}

                  <div className="property-location">

                    <span className="location-icon">
                      📍
                    </span>

                    <div>

                      <strong>
                        {property.addressLine1}
                      </strong>

                      <span>
                        {property.city},{" "}
                        {property.state},{" "}
                        {property.country}
                      </span>

                    </div>

                  </div>


                  {/* Bottom information */}

                  <div className="property-meta">

                    <div className="meta-item">

                      <span className="meta-label">
                        Type
                      </span>

                      <span className="meta-value">
                        {property.propertyType}
                      </span>

                    </div>


                    <div className="meta-item">

                      <span className="meta-label">
                        Location
                      </span>

                      <span className="meta-value">
                        {property.city}
                      </span>

                    </div>

                  </div>

                </div>


                {/* Right side */}

                <div className="property-actions">

                  <span className="active-status">
                    ● Active
                  </span>

                  <button
                    className="view-units-btn"
                    onClick={() => navigate("/units")}
                  >
                    View Units
                    <span>→</span>
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

export default Properties;
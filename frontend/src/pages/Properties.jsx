import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./Properties.css";

function Properties() {
  const navigate = useNavigate();

  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    propertyName: "",
    propertyType: "",
    description: "",
    addressLine1: "",
    addressLine2: "",
    city: "",
    state: "",
    country: "",
    pincode: "",
  });

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

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setSubmitting(true);
      setMessage("");

      await api.post("/properties", formData);

      setMessage("Property created successfully.");

      setFormData({
        propertyName: "",
        propertyType: "",
        description: "",
        addressLine1: "",
        addressLine2: "",
        city: "",
        state: "",
        country: "",
        pincode: "",
      });

      setShowForm(false);

      await loadProperties();
    } catch (error) {
      console.error("Failed to create property:", error);

      setMessage(
        error.response?.data?.message ||
          "Failed to create property."
      );
    } finally {
      setSubmitting(false);
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

        <button
          className="add-property-btn"
          onClick={() => {
            setShowForm(!showForm);
            setMessage("");
          }}
        >
          <span>+</span>
          {showForm ? "Cancel" : "Add Property"}
        </button>

      </div>


      {/* Message */}

      {message && (
        <div className="property-message">
          {message}
        </div>
      )}


      {/* Add Property Form */}

      {showForm && (
        <div className="property-form-card">

          <h2>Add Property</h2>

          <form onSubmit={handleSubmit}>

            {/* Property Name */}

            <div className="form-group">

              <label>
                Property Name
              </label>

              <input
                type="text"
                name="propertyName"
                value={formData.propertyName}
                onChange={handleChange}
                placeholder="Enter property name"
                required
              />

            </div>


            {/* Property Type */}

            <div className="form-group">

              <label>
                Property Type
              </label>

              <select
                name="propertyType"
                value={formData.propertyType}
                onChange={handleChange}
                required
              >

                <option value="">
                  Select property type
                </option>

                <option value="APARTMENT">
                  Apartment
                </option>

                <option value="FLAT">
                  Flat
                </option>

                <option value="PG">
                  PG
                </option>

                <option value="HOSTEL">
                  Hostel
                </option>

                <option value="SHOP">
                  Shop
                </option>

                <option value="OFFICE">
                  Office
                </option>

                <option value="WAREHOUSE">
                  Warehouse
                </option>

                <option value="LAND">
                  Land
                </option>

              </select>

            </div>


            {/* Description */}

            <div className="form-group">

              <label>
                Description
              </label>

              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Enter property description"
                rows="3"
              />

            </div>


            {/* Address */}

            <div className="form-group">

              <label>
                Address Line 1
              </label>

              <input
                type="text"
                name="addressLine1"
                value={formData.addressLine1}
                onChange={handleChange}
                placeholder="Enter address"
                required
              />

            </div>


            <div className="form-group">

              <label>
                Address Line 2
              </label>

              <input
                type="text"
                name="addressLine2"
                value={formData.addressLine2}
                onChange={handleChange}
                placeholder="Apartment, building, landmark, etc."
              />

            </div>


            {/* Location */}

            <div className="form-row">

              <div className="form-group">

                <label>
                  City
                </label>

                <input
                  type="text"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  placeholder="Enter city"
                  required
                />

              </div>


              <div className="form-group">

                <label>
                  State
                </label>

                <input
                  type="text"
                  name="state"
                  value={formData.state}
                  onChange={handleChange}
                  placeholder="Enter state"
                  required
                />

              </div>

            </div>


            <div className="form-row">

              <div className="form-group">

                <label>
                  Country
                </label>

                <input
                  type="text"
                  name="country"
                  value={formData.country}
                  onChange={handleChange}
                  placeholder="Enter country"
                  required
                />

              </div>


              <div className="form-group">

                <label>
                  Pincode
                </label>

                <input
                  type="text"
                  name="pincode"
                  value={formData.pincode}
                  onChange={handleChange}
                  placeholder="Enter pincode"
                  required
                />

              </div>

            </div>


            {/* Submit */}

            <button
              type="submit"
              className="create-property-btn"
              disabled={submitting}
            >
              {submitting
                ? "Creating..."
                : "Create Property"}
            </button>

          </form>

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
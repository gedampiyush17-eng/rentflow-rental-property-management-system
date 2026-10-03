export const getToken = () => {
  return localStorage.getItem("token");
};

export const getRole = () => {
  const token = localStorage.getItem("token");

  if (!token) {
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));

    return payload.role || null;
  } catch (error) {
    console.error("Failed to decode token:", error);
    return null;
  }
};

export const logout = () => {
  localStorage.removeItem("token");
};
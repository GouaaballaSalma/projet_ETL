import React from 'react';
import { Navigate } from 'react-router-dom';
import { authService } from '../services/authService';

interface RoleProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles: string[];
}

const RoleProtectedRoute: React.FC<RoleProtectedRouteProps> = ({ children, allowedRoles }) => {
  const isAuthorized = authService.hasAnyRole(allowedRoles);
  
  if (!isAuthorized) {
    // Redirige vers la page de recherche (ou une page "Non autorisé") si le rôle n'est pas suffisant
    return <Navigate to="/cherche-lcn" replace />;
  }

  return <>{children}</>;
};

export default RoleProtectedRoute;

import api from '../api/axiosConfig';

export const authService = {
  login: async (email: string, motDePasse: string) => {
    const response = await api.post('/api/auth/login', { email, motDePasse });
    if (response.data && response.data.token) {
      localStorage.setItem('jwt_token', response.data.token);
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('jwt_token');
  },

  isAuthenticated: (): boolean => {
    return localStorage.getItem('jwt_token') !== null;
  },

  getUserRole: (): string | null => {
    const token = localStorage.getItem('jwt_token');
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // The JWT usually stores authorities in an array under "roles" or "authorities", or a single role.
      // With Spring Security default mapped by our JwtService, let's assume "role" or check the structure.
      // Usually it's either `role` or `authorities` containing the string.
      // Let's return payload.role || (payload.authorities && payload.authorities[0]) || null;
      // Spring Security authorities are often extracted. I'll just check payload.role.
      // If it's a simple custom JwtService, we put it there.
      return payload.role || payload.authorities || null;
    } catch (e) {
      return null;
    }
  },

  hasAnyRole: (roles: string[]): boolean => {
    const userRole = authService.getUserRole();
    if (!userRole) return false;
    return roles.includes(userRole);
  }
};

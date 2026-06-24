import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authService } from './authService';
import api from '../api/axiosConfig';

vi.mock('../api/axiosConfig', () => ({
  default: {
    post: vi.fn(),
  },
}));

describe('authService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('should login and store token', async () => {
    const mockToken = 'fake-jwt-token';
    vi.mocked(api.post).mockResolvedValueOnce({ data: { token: mockToken } });

    const result = await authService.login('test@test.com', 'password');

    expect(api.post).toHaveBeenCalledWith('/api/auth/login', { email: 'test@test.com', motDePasse: 'password' });
    expect(localStorage.getItem('jwt_token')).toBe(mockToken);
    expect(result).toEqual({ token: mockToken });
  });

  it('should logout and remove token', () => {
    localStorage.setItem('jwt_token', 'token');
    authService.logout();
    expect(localStorage.getItem('jwt_token')).toBeNull();
  });

  it('should check if user is authenticated', () => {
    expect(authService.isAuthenticated()).toBe(false);
    localStorage.setItem('jwt_token', 'token');
    expect(authService.isAuthenticated()).toBe(true);
  });

  it('should get user role from valid token', () => {
    // a valid base64 payload for {"role": "ROLE_ADMIN"}
    const payload = btoa(JSON.stringify({ role: 'ROLE_ADMIN' }));
    localStorage.setItem('jwt_token', `header.${payload}.signature`);
    
    expect(authService.getUserRole()).toBe('ROLE_ADMIN');
  });

  it('should return null for user role if token is invalid', () => {
    localStorage.setItem('jwt_token', 'invalid.token.here');
    expect(authService.getUserRole()).toBeNull();
  });

  it('should verify if user has any role', () => {
    const payload = btoa(JSON.stringify({ role: 'ROLE_ADMIN' }));
    localStorage.setItem('jwt_token', `header.${payload}.signature`);

    expect(authService.hasAnyRole(['ROLE_ADMIN', 'ROLE_USER'])).toBe(true);
    expect(authService.hasAnyRole(['ROLE_USER'])).toBe(false);
  });
});

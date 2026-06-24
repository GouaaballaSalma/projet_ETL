import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { Navigate, useLocation } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    isAuthenticated: vi.fn()
  }
}));

vi.mock('react-router-dom', () => ({
  Navigate: vi.fn(() => null),
  useLocation: vi.fn(() => ({ pathname: '/test' }))
}));

describe('ProtectedRoute', () => {
  it('renders children if authenticated', () => {
    vi.mocked(authService.isAuthenticated).mockReturnValue(true);
    const { getByText } = render(<ProtectedRoute><div>Protected</div></ProtectedRoute>);
    expect(getByText('Protected')).toBeDefined();
  });

  it('navigates to login if not authenticated', () => {
    vi.mocked(authService.isAuthenticated).mockReturnValue(false);
    render(<ProtectedRoute><div>Protected</div></ProtectedRoute>);
    expect(vi.mocked(Navigate).mock.calls[0][0]).toMatchObject({ to: '/login', replace: true });
  });
});

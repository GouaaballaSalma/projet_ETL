import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { Navigate } from 'react-router-dom';
import RoleProtectedRoute from './RoleProtectedRoute';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    hasAnyRole: vi.fn()
  }
}));

vi.mock('react-router-dom', () => ({
  Navigate: vi.fn(() => null)
}));

describe('RoleProtectedRoute', () => {
  it('renders children if allowed', () => {
    vi.mocked(authService.hasAnyRole).mockReturnValue(true);
    const { getByText } = render(<RoleProtectedRoute allowedRoles={['ROLE_ADMIN']}><div>Allowed</div></RoleProtectedRoute>);
    expect(getByText('Allowed')).toBeDefined();
  });

  it('navigates to /cherche-lcn if not allowed', () => {
    vi.mocked(authService.hasAnyRole).mockReturnValue(false);
    render(<RoleProtectedRoute allowedRoles={['ROLE_ADMIN']}><div>Allowed</div></RoleProtectedRoute>);
    expect(vi.mocked(Navigate).mock.calls[0][0]).toMatchObject({ to: '/cherche-lcn', replace: true });
  });
});

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from './Login';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
  },
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual as any,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ state: null }),
  };
});

describe('Login Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderComponent = () => {
    render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
  };

  it('should render login form', () => {
    renderComponent();
    expect(screen.getByLabelText(/Adresse e-mail/i)).toBeDefined();
    expect(screen.getByLabelText(/Mot de passe/i)).toBeDefined();
    expect(screen.getAllByRole('button', { name: /Se connecter/i })[0]).toBeDefined();
  });

  it('should show error on login failure', async () => {
    vi.mocked(authService.login).mockRejectedValueOnce(new Error('Auth failed'));
    renderComponent();

    fireEvent.change(screen.getByLabelText(/Adresse e-mail/i), { target: { value: 'test@cfgbank.ma' } });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), { target: { value: 'wrongpass' } });
    fireEvent.click(screen.getAllByRole('button', { name: /Se connecter/i })[0]);

    await waitFor(() => {
      expect(screen.getByText(/Identifiants incorrects/i)).toBeDefined();
    });
  });

  it('should navigate on successful login', async () => {
    vi.mocked(authService.login).mockResolvedValueOnce({ token: 'fake-token' });
    renderComponent();

    fireEvent.change(screen.getByLabelText(/Adresse e-mail/i), { target: { value: 'test@cfgbank.ma' } });
    fireEvent.change(screen.getByLabelText(/Mot de passe/i), { target: { value: 'password' } });
    fireEvent.click(screen.getAllByRole('button', { name: /Se connecter/i })[0]);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/cherche-lcn', { replace: true });
    });
  });
});

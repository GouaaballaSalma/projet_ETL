import { render, screen, waitFor, cleanup } from '@testing-library/react';
import { describe, it, expect, vi, afterEach } from 'vitest';
import Dashboard from './Dashboard';
import api from '../../api/axiosConfig';

vi.mock('../../api/axiosConfig', () => ({
  default: {
    get: vi.fn()
  }
}));

describe('Dashboard', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('renders loading state initially', () => {
    vi.mocked(api.get).mockImplementation(() => new Promise(() => {}));
    render(<Dashboard />);
    expect(screen.getByText(/Chargement du tableau de bord/i)).toBeDefined();
  });

  it('renders dashboard with data successfully', async () => {
    vi.mocked(api.get).mockImplementation((url) => {
      if (url === '/api/admin/dashboard/stats') {
        return Promise.resolve({ data: { totalIncidents: 10, montantGlobal: 5000, repartitionTypeClient: { PP: 6, PM: 4 } } });
      }
      if (url === '/api/admin/dashboard/risk-scoring') {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error('not found'));
    });

    render(<Dashboard />);

    await waitFor(() => {
      expect(screen.getByText('10')).toBeDefined();
      expect(screen.getByText(/Tableau de Bord des Incidents/i)).toBeDefined();
    });
  });

  it('handles error fetching data', async () => {
    vi.mocked(api.get).mockRejectedValueOnce(new Error('Network error'));
    render(<Dashboard />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Chargement du tableau de bord/i)).toBeNull();
    }, { timeout: 2000 });
    expect(screen.getByText(/Tableau de Bord des Incidents/i)).toBeDefined();
  });
});

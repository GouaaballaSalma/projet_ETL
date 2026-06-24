import { describe, it, expect, vi, beforeEach } from 'vitest';
import { lcnService } from './lcnService';
import api from '../api/axiosConfig';

vi.mock('../api/axiosConfig', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
    put: vi.fn(),
  },
}));

describe('lcnService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch LCNs with cleaned params', async () => {
    const mockData = { content: [], totalElements: 0 };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockData });

    const result = await lcnService.rechercherIncidents({
      typeClient: 'PP' as any,
      nomComplet: 'John',
      identifiant: '',
    });

    expect(api.get).toHaveBeenCalledWith('/api/lcn/recherche', {
      params: { typeClient: 'PP', nomComplet: 'John' },
    });
    expect(result).toEqual(mockData);
  });

  it('should create an LCN', async () => {
    const mockData = { refImpaye: 'REF123' };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockData });

    const requestData = { typeClient: 'PP', montant: 100 };
    const result = await lcnService.creerIncidentManuel(requestData);

    expect(api.post).toHaveBeenCalledWith('/api/lcn', requestData);
    expect(result).toEqual(mockData);
  });

  it('should delete an LCN', async () => {
    vi.mocked(api.delete).mockResolvedValueOnce({});
    await lcnService.deleteLcn('REF123');
    expect(api.delete).toHaveBeenCalledWith('/api/lcn/REF123');
  });

  it('should update an LCN', async () => {
    const mockData = { refImpaye: 'REF123', montant: 200 };
    vi.mocked(api.put).mockResolvedValueOnce({ data: mockData });

    const requestData = { montant: 200 };
    const result = await lcnService.updateLcn('REF123', requestData);

    expect(api.put).toHaveBeenCalledWith('/api/lcn/REF123', requestData);
    expect(result).toEqual(mockData);
  });
});

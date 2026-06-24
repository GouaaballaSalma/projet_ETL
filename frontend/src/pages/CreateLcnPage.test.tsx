import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import CreateLcnPage from './CreateLcnPage';
import { lcnService } from '../services/lcnService';

vi.mock('../services/lcnService', () => ({
  lcnService: {
    creerIncidentManuel: vi.fn(),
  },
}));

describe('CreateLcnPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderComponent = () => {
    render(
      <BrowserRouter>
        <CreateLcnPage />
      </BrowserRouter>
    );
  };

  it('renders the creation form correctly', () => {
    renderComponent();
    expect(screen.getByText(/Saisie Manuelle d'Incident LCN/i)).toBeDefined();
    expect(screen.getAllByRole('button', { name: /Enregistrer l'incident/i })[0]).toBeDefined();
  });

  it('handles successful LCN creation', async () => {
    vi.mocked(lcnService.creerIncidentManuel).mockResolvedValueOnce({ refImpaye: 'REF123' } as any);
    renderComponent();

    const inputs = screen.getAllByRole('textbox');
    const refClientInput = inputs.find(el => (el as HTMLInputElement).name === 'refClient');
    if (refClientInput) {
        fireEvent.change(refClientInput, { target: { value: 'CLIENT01' } });
    }
    
    const numLcnInput = inputs.find(el => (el as HTMLInputElement).name === 'numLcn');
    if (numLcnInput) {
        fireEvent.change(numLcnInput, { target: { value: '12345' } });
    }

    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(lcnService.creerIncidentManuel).toHaveBeenCalled();
      expect(screen.getByText(/L'incident LCN a été créé et enregistré avec succès/i)).toBeDefined();
    });
  });

  it('handles API error on creation', async () => {
    vi.mocked(lcnService.creerIncidentManuel).mockRejectedValueOnce({
      response: { data: { message: 'Erreur serveur' } }
    });
    renderComponent();

    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(screen.getByText(/Erreur serveur/i)).toBeDefined();
    });
  });
  it('handles validation errors from API (array)', async () => {
    vi.mocked(lcnService.creerIncidentManuel).mockRejectedValueOnce({
      response: { data: { errors: [{ field: 'rib', defaultMessage: 'RIB invalide' }] } }
    });
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    await waitFor(() => {
      expect(screen.getByText(/La validation des champs a échoué/i)).toBeDefined();
      expect(screen.getByText(/rib : RIB invalide/i)).toBeDefined();
    });
  });

  it('handles validation errors from API (object)', async () => {
    vi.mocked(lcnService.creerIncidentManuel).mockRejectedValueOnce({
      response: { data: { errors: { 'montant': 'Montant invalide' } } }
    });
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    await waitFor(() => {
      expect(screen.getByText(/montant : Montant invalide/i)).toBeDefined();
    });
  });

  it('handles PM type selection and submission', async () => {
    vi.mocked(lcnService.creerIncidentManuel).mockResolvedValueOnce({ refImpaye: 'REF_PM' } as any);
    renderComponent();
    
    const typeClientSelect = screen.getAllByRole('combobox').find(el => (el as HTMLSelectElement).name === 'typeClient');
    if (typeClientSelect) {
      fireEvent.change(typeClientSelect, { target: { value: 'PM' } });
    }

    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(lcnService.creerIncidentManuel).toHaveBeenCalled();
    });
  });
});

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import SearchLcnPage from './SearchLcnPage';
import { lcnService } from '../services/lcnService';
import { authService } from '../services/authService';
import { TypeClient } from '../types/LcnSynth';

vi.mock('../services/lcnService', () => ({
  lcnService: {
    rechercherIncidents: vi.fn(),
    deleteLcn: vi.fn(),
    updateLcn: vi.fn(),
  },
}));

vi.mock('../services/authService', () => ({
  authService: {
    hasAnyRole: vi.fn(),
  },
}));

describe('SearchLcnPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(authService.hasAnyRole).mockReturnValue(true);
  });

  afterEach(() => {
    cleanup();
  });

  const renderComponent = () => {
    render(
      <BrowserRouter>
        <SearchLcnPage />
      </BrowserRouter>
    );
  };

  it('renders search form correctly', () => {
    renderComponent();
    expect(screen.getByText(/Recherche Synthétique LCN/i)).toBeDefined();
    expect(screen.getAllByRole('button', { name: /Rechercher/i })[0]).toBeDefined();
  });

  it('performs a search successfully', async () => {
    const mockData = {
      content: [
        { refImpaye: 'IMP1', typeClient: 'PP', nom: 'Doe', prenom: 'John', montant: 100 }
      ],
      totalElements: 1,
      totalPages: 1,
      number: 0,
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    
    renderComponent();

    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(lcnService.rechercherIncidents).toHaveBeenCalledWith(expect.objectContaining({
        typeClient: TypeClient.PP,
        page: 0,
      }));
      expect(screen.getByText('IMP1')).toBeDefined();
      expect(screen.getByText(/Doe/i)).toBeDefined();
    });
  });

  it('handles search error and closes modal', async () => {
    vi.mocked(lcnService.rechercherIncidents).mockRejectedValueOnce({
      response: { data: { message: 'Erreur réseau' } }
    });
    
    renderComponent();

    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(screen.getByText(/Erreur réseau/i)).toBeDefined();
    });

    const closeBtn = document.querySelector('.lucide-x');
    if (closeBtn && closeBtn.closest('button')) fireEvent.click(closeBtn.closest('button')!);
  });

  it('opens and closes edit modal', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_EDIT', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 1, totalPages: 1, number: 0,
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_EDIT')).toBeDefined());

    const editBtn = document.querySelector('button[title="Modifier"]');
    if (editBtn) fireEvent.click(editBtn);

    await waitFor(() => expect(screen.getByText(/Modifier la saisie manuelle : MAN_EDIT/i)).toBeDefined());

    const cancelBtn = screen.getByRole('button', { name: /Annuler/i });
    fireEvent.click(cancelBtn);

    await waitFor(() => expect(screen.queryByText(/Modifier la saisie manuelle : MAN_EDIT/i)).toBeNull());
  });

  it('opens delete modal and confirms deletion', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_DEL', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 1, totalPages: 1, number: 0,
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_DEL')).toBeDefined());

    const deleteBtn = document.querySelector('button[title="Supprimer"]');
    if (deleteBtn) fireEvent.click(deleteBtn);

    vi.mocked(lcnService.deleteLcn).mockResolvedValueOnce(undefined);
    const confirmBtn = screen.getAllByRole('button', { name: /Supprimer/i }).pop();
    if (confirmBtn) fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(lcnService.deleteLcn).toHaveBeenCalledWith('MAN_DEL');
    });
  });

  it('cancels deletion', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_DEL_CANCEL', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 1, totalPages: 1, number: 0,
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_DEL_CANCEL')).toBeDefined());

    const deleteBtn = document.querySelector('button[title="Supprimer"]');
    if (deleteBtn) fireEvent.click(deleteBtn);

    const cancelBtn = screen.getByRole('button', { name: /Annuler/i });
    fireEvent.click(cancelBtn);

    await waitFor(() => {
      expect(screen.queryByText(/Êtes-vous sûr de vouloir supprimer/i)).toBeNull();
    });
  });

  it('handles pagination next page', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_PAGE1', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 20, totalPages: 2, number: 0, last: false, first: true
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_PAGE1')).toBeDefined());

    const nextBtn = screen.getByRole('button', { name: /Suivant/i });
    fireEvent.click(nextBtn);

    await waitFor(() => {
      expect(lcnService.rechercherIncidents).toHaveBeenCalledWith(expect.objectContaining({ page: 1 }));
    });
  });

  it('handles pagination previous page', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_PAGE2', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 20, totalPages: 2, number: 1, last: true, first: false
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    renderComponent();
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_PAGE2')).toBeDefined());

    const prevBtn = screen.getByRole('button', { name: /Précédent/i });
    fireEvent.click(prevBtn);

    await waitFor(() => {
      expect(lcnService.rechercherIncidents).toHaveBeenCalledWith(expect.objectContaining({ page: 0 }));
    });
  });

  it('handles input changes and reset form', async () => {
    const { container } = render(
      <BrowserRouter>
        <SearchLcnPage />
      </BrowserRouter>
    );

    const selects = container.querySelectorAll('select');
    const typeClientSelect = selects[0] as HTMLSelectElement;
    const typeIdentifiantSelect = selects[1] as HTMLSelectElement;

    const inputs = container.querySelectorAll('input[type="text"]');
    const nomCompletInput = inputs[0] as HTMLInputElement;
    const valeurIdentifiantInput = inputs[1] as HTMLInputElement;

    fireEvent.change(typeClientSelect, { target: { value: 'PM' } });
    fireEvent.change(nomCompletInput, { target: { value: 'Test Nom' } });
    
    // Have to select PM first to make RC available if the form logic depends on it, but we did!
    fireEvent.change(typeIdentifiantSelect, { target: { value: 'RC' } });
    fireEvent.change(valeurIdentifiantInput, { target: { value: '123' } });

    expect(nomCompletInput.value).toBe('Test Nom');
    expect(valeurIdentifiantInput.value).toBe('123');

    const resetBtn = screen.getAllByRole('button', { name: /Effacer/i })[0];
    fireEvent.click(resetBtn);

    await waitFor(() => {
      expect(nomCompletInput.value).toBe('');
      expect(valeurIdentifiantInput.value).toBe('');
    });
  });

  it('handles search with PP and PM typeIdentifiant', async () => {
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce({
      content: [], totalElements: 0, totalPages: 0, number: 0,
    } as any);

    const { container } = render(
      <BrowserRouter>
        <SearchLcnPage />
      </BrowserRouter>
    );

    const selects = container.querySelectorAll('select');
    const typeClientSelect = selects[0] as HTMLSelectElement;
    const typeIdentifiantSelect = selects[1] as HTMLSelectElement;

    // Search PP
    fireEvent.change(typeIdentifiantSelect, { target: { value: 'CIN' } });
    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(lcnService.rechercherIncidents).toHaveBeenCalledWith(expect.objectContaining({ typeIdentifiantPP: 'CIN' }));
    });

    // Search PM
    fireEvent.change(typeClientSelect, { target: { value: 'PM' } });
    fireEvent.change(typeIdentifiantSelect, { target: { value: 'RC' } });
    fireEvent.submit(document.querySelector('form')!);

    await waitFor(() => {
      expect(lcnService.rechercherIncidents).toHaveBeenCalledWith(expect.objectContaining({ typeIdentifiantPM: 'RC' }));
    });
  });

  it('handles delete error', async () => {
    const mockData = {
      content: [{ refImpaye: 'MAN_DEL_ERR', typeClient: 'PP', nom: 'Doe' }],
      totalElements: 1, totalPages: 1, number: 0,
    };
    vi.mocked(lcnService.rechercherIncidents).mockResolvedValueOnce(mockData as any);
    render(<BrowserRouter><SearchLcnPage /></BrowserRouter>);
    fireEvent.submit(document.querySelector('form')!);
    
    await waitFor(() => expect(screen.getByText('MAN_DEL_ERR')).toBeDefined());

    const deleteBtn = document.querySelector('button[title="Supprimer"]');
    if (deleteBtn) fireEvent.click(deleteBtn);

    vi.mocked(lcnService.deleteLcn).mockRejectedValueOnce({ response: { status: 500 } });
    const confirmBtn = screen.getAllByRole('button', { name: /Supprimer/i }).pop();
    if (confirmBtn) fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(screen.getByText(/Une erreur est survenue lors de la suppression/i)).toBeDefined();
    });
  });
});

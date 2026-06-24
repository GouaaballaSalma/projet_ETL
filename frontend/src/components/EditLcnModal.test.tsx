import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EditLcnModal from './EditLcnModal';

describe('EditLcnModal', () => {
  const mockLcn = {
    refImpaye: 'IMP123',
    refClient: 'CLI123',
    typeClient: 'PP',
    nom: 'Doe',
    prenom: 'John',
    montant: 1000.5,
    statut: 'IMPAYÉ'
  };

  const mockOnClose = vi.fn();
  const mockOnSave = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders nothing if not open', () => {
    const { container } = render(<EditLcnModal isOpen={false} lcn={mockLcn as any} onClose={mockOnClose} onSave={mockOnSave} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders nothing if no lcn provided', () => {
    const { container } = render(<EditLcnModal isOpen={true} lcn={null} onClose={mockOnClose} onSave={mockOnSave} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders correctly when open with lcn', () => {
    const { container } = render(<EditLcnModal isOpen={true} lcn={mockLcn as any} onClose={mockOnClose} onSave={mockOnSave} />);
    expect(screen.getByText('Modifier la saisie manuelle : IMP123')).toBeDefined();
    const montantInput = container.querySelector('input[name="montant"]') as HTMLInputElement;
    expect(montantInput.value).toBe('1000.5');
  });

  it('handles input changes', async () => {
    const { container } = render(<EditLcnModal isOpen={true} lcn={mockLcn as any} onClose={mockOnClose} onSave={mockOnSave} />);
    const montantInput = container.querySelector('input[name="montant"]') as HTMLInputElement;
    await waitFor(() => expect(montantInput.value).toBe('1000.5'));
    fireEvent.change(montantInput, { target: { value: '2000', name: 'montant', type: 'number' } });
    expect(montantInput.value).toBe('2000');
  });

  it('calls onSave on form submit with cleaned payload', async () => {
    mockOnSave.mockResolvedValueOnce(undefined);
    const { container } = render(<EditLcnModal isOpen={true} lcn={mockLcn as any} onClose={mockOnClose} onSave={mockOnSave} />);
    
    const montantInput = container.querySelector('input[name="montant"]') as HTMLInputElement;
    await waitFor(() => expect(montantInput.value).toBe('1000.5'));

    const ribInput = container.querySelector('input[name="rib"]') as HTMLInputElement;
    fireEvent.change(ribInput, { target: { value: '123456789' } });

    const form = container.querySelector('form');
    fireEvent.submit(form!);

    await waitFor(() => {
      expect(mockOnSave).toHaveBeenCalled();
    });
    
    const payload = mockOnSave.mock.calls[0][1];
    expect(payload.rib).toBe('123456789');
  });

  it('closes on X button click', () => {
    render(<EditLcnModal isOpen={true} lcn={mockLcn as any} onClose={mockOnClose} onSave={mockOnSave} />);
    const xButton = document.querySelector('button.text-gray-400');
    if (xButton) fireEvent.click(xButton);
    expect(mockOnClose).toHaveBeenCalled();
  });
});

import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import DeleteUserModal from './DeleteUserModal';

describe('DeleteUserModal', () => {
  it('renders null when not open', () => {
    const { container } = render(<DeleteUserModal isOpen={false} userName="john" onClose={vi.fn()} onConfirm={vi.fn()} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders the modal when open and handles confirm and close', () => {
    const mockOnConfirm = vi.fn();
    const mockOnClose = vi.fn();
    render(<DeleteUserModal isOpen={true} userName="john" onClose={mockOnClose} onConfirm={mockOnConfirm} />);
    
    expect(screen.getByText(/Supprimer l'utilisateur/i)).toBeDefined();
    
    fireEvent.click(screen.getByRole('button', { name: /Supprimer/i }));
    expect(mockOnConfirm).toHaveBeenCalled();

    fireEvent.click(screen.getByRole('button', { name: /Annuler/i }));
    expect(mockOnClose).toHaveBeenCalled();
  });
});

import { type ReactNode, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

type Props = {
  children: ReactNode;
};

export default function RotaCadastro({ children }: Props) {
  const [allowed, setAllowed] = useState<boolean | null>(null);

  useEffect(() => {
    try {
      const raw = localStorage.getItem('cadastro.partial');
      setAllowed(!!raw);
    } catch (e) {
      setAllowed(false);
    }
  }, []);

  if (allowed === null) return null; // or a spinner
  if (!allowed) return <Navigate to="/cadastro" replace />;
  return <>{children}</>;
}

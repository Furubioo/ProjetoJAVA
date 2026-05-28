// cinema-frontend/src/App.jsx
// SUBSTITUIR o arquivo existente por este.
//
// Mudanças:
//  • Rotas /admin/* agora exigem dtype === 'ADMINISTRADOR'
//  • Usuários comuns que tentam acessar /admin são redirecionados para /

import { useEffect, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import ProgramacaoPage  from './pages/ProgramacaoPage';
import LoginPage        from './pages/LoginPage';
import CadastroPage     from './pages/CadastroPage';
import CompraPage       from './pages/CompraPage';
import AdminFilmesPage  from './pages/AdminFilmesPage';
import AdminSessoesPage from './pages/AdminSessoesPage';

// Protege rotas que exigem login simples
function RequireAuth({ usuario, children }) {
  if (!usuario) return <Navigate to="/login" replace />;
  return children;
}

// Protege rotas que exigem perfil de ADMINISTRADOR
function RequireAdmin({ usuario, children }) {
  if (!usuario) return <Navigate to="/login" replace />;
  const dtype = usuario?.dtype ?? usuario?.tipo ?? 'COMUM';
  if (dtype !== 'ADMINISTRADOR') return <Navigate to="/" replace />;
  return children;
}

function AppRoutes() {
  const [usuario, setUsuario] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('cinema_user') || 'null');
    } catch {
      localStorage.removeItem('cinema_user');
      return null;
    }
  });

  useEffect(() => {
    if (usuario) {
      localStorage.setItem('cinema_user', JSON.stringify(usuario));
    } else {
      localStorage.removeItem('cinema_user');
    }
  }, [usuario]);

  return (
    <Routes>
      <Route path="/"           element={<ProgramacaoPage  usuario={usuario} onLogout={() => setUsuario(null)} />} />
      <Route path="/programacao" element={<Navigate to="/" replace />} />
      <Route path="/login"      element={usuario ? <Navigate to="/" replace /> : <LoginPage  onLogin={setUsuario} />} />
      <Route path="/cadastro"   element={usuario ? <Navigate to="/" replace /> : <CadastroPage onLogin={setUsuario} />} />

      <Route
        path="/comprar/:id"
        element={
          <RequireAuth usuario={usuario}>
            <CompraPage usuario={usuario} />
          </RequireAuth>
        }
      />

      {/* ── Rotas de admin — exigem ADMINISTRADOR ── */}
      <Route
        path="/admin/filmes"
        element={
          <RequireAdmin usuario={usuario}>
            <AdminFilmesPage />
          </RequireAdmin>
        }
      />
      <Route
        path="/admin/sessoes"
        element={
          <RequireAdmin usuario={usuario}>
            <AdminSessoesPage />
          </RequireAdmin>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}
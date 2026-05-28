import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/api';

const SALA_LABEL = {
  COMUM: 'Comum',
  SALA_3D: '3D',
  XD: 'XD',
  XD_3D: 'XD/3D',
};

function normSalaTexto(v) {
  return String(v || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[_/\s-]+/g, '')
    .toUpperCase();
}

function salaNomeExibicao(sala) {
  if (!sala) return '';

  const nome = String(sala.nome || '').trim();
  const tipo = SALA_LABEL[sala.tipo] || sala.tipo || '';

  if (!nome) return '';
  if (normSalaTexto(nome) === normSalaTexto(tipo)) return '';
  if (normSalaTexto(nome) === normSalaTexto(sala.tipo)) return '';

  return nome;
}

function salaLabelCompleta(sala) {
  if (!sala) return '-';

  const nome = salaNomeExibicao(sala);
  const tipo = SALA_LABEL[sala.tipo] || sala.tipo || '';

  return nome ? `${nome} · ${tipo}` : tipo;
}

export default function AdminSessoesPage() {
  const navigate = useNavigate();

  const userRaw = (() => {
    try {
      return JSON.parse(localStorage.getItem('cinema_user') || 'null');
    } catch {
      return null;
    }
  })();

  const tipo = userRaw?.dtype ?? userRaw?.tipo ?? '';
  const isAutorizado = tipo === 'ADMINISTRADOR';

  const [filmes, setFilmes] = useState([]);
  const [salas, setSalas] = useState([]);
  const [sessoes, setSessoes] = useState([]);
  const [form, setForm] = useState({ filmeId: '', salaId: '', horario: '' });
  const [editId, setEditId] = useState(null);
  const [flash, setFlash] = useState(null);
  const [carregando, setCarregando] = useState(true);

  const msg = (texto, ok = true) => {
    setFlash({ texto, ok });
    setTimeout(() => setFlash(null), 4000);
  };

  const carregar = () => {
    setCarregando(true);

    Promise.all([
      api.get('/filmes'),
      api.get('/salas'),
      api.get('/sessoes'),
    ])
      .then(([filmesRes, salasRes, sessoesRes]) => {
        setFilmes(filmesRes.data || []);
        setSalas(salasRes.data || []);
        setSessoes(sessoesRes.data || []);
      })
      .catch(() => msg('Erro ao carregar dados de sessões.', false))
      .finally(() => setCarregando(false));
  };

  useEffect(() => {
    carregar();
  }, []);

  const handleChange = e => {
    setForm(p => ({ ...p, [e.target.name]: e.target.value }));
  };

  const limparForm = () => {
    setForm({ filmeId: '', salaId: '', horario: '' });
    setEditId(null);
  };

  const salvar = () => {
    if (!form.filmeId || !form.salaId || !form.horario.trim()) {
      msg('Escolha filme, sala e horário.', false);
      return;
    }

    const payload = {
      filmeId: Number(form.filmeId),
      salaId: Number(form.salaId),
      horario: form.horario.trim(),
    };

    const req = editId
      ? api.put(`/sessoes/${editId}`, payload)
      : api.post('/sessoes', payload);

    req
      .then(() => {
        msg(editId ? 'Sessão atualizada.' : 'Sessão criada.');
        limparForm();
        carregar();
      })
      .catch(err => msg(err.response?.data?.erro || 'Erro ao salvar sessão.', false));
  };

  const editar = sessao => {
    setEditId(sessao.id);
    setForm({
      filmeId: String(sessao.filme?.id || ''),
      salaId: String(sessao.sala?.id || ''),
      horario: sessao.horario || '',
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const remover = id => {
    if (!window.confirm('Remover esta sessão?')) return;

    api.delete(`/sessoes/${id}`)
      .then(() => {
        msg('Sessão removida.');
        carregar();
      })
      .catch(() => msg('Erro ao remover sessão.', false));
  };

  return (
    <div style={{ minHeight: '100vh', padding: '32px 24px', color: 'var(--cream)' }}>
      <div style={{ maxWidth: 980, margin: '0 auto' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 28, flexWrap: 'wrap' }}>
          <button
            className="btn-ghost"
            style={{ padding: '8px 16px', fontSize: 14 }}
            onClick={() => navigate('/')}
          >
            ← Voltar
          </button>

          <div style={{ flex: 1 }}>
            <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 28, fontStyle: 'italic' }}>
              Gerenciar Sessões
            </h1>
            <p style={{ color: 'var(--cream-dim)', fontSize: 13 }}>
              Organize horários, salas e filmes em cartaz.
            </p>
          </div>

          <Link to="/admin/filmes" className="btn-ghost" style={{ fontSize: 13 }}>
            Filmes →
          </Link>
        </div>

        {!isAutorizado && (
          <div className="flash-err" style={{ marginBottom: 20 }}>
            Acesso restrito. Apenas administradores podem gerenciar sessões.
          </div>
        )}

        {flash && (
          <p className={flash.ok ? 'flash-ok' : 'flash-err'} style={{ marginBottom: 18 }}>
            {flash.texto}
          </p>
        )}

        <div className="card" style={{ padding: '24px', marginBottom: 24 }}>
          <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 19, color: 'var(--gold)', marginBottom: 18 }}>
            {editId ? 'Editar Sessão' : 'Criar Sessão'}
          </h2>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 12 }}>
            <div>
              <label className="lbl">Filme</label>
              <select name="filmeId" className="inp" value={form.filmeId} onChange={handleChange}>
                <option value="">Selecione</option>
                {filmes.map(f => (
                  <option key={f.id} value={f.id}>{f.nome}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="lbl">Sala</label>
              <select name="salaId" className="inp" value={form.salaId} onChange={handleChange}>
                <option value="">Selecione</option>
                {salas.map(s => (
                  <option key={s.id} value={s.id}>
                    {salaLabelCompleta(s)}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="lbl">Horário</label>
              <input
                name="horario"
                className="inp"
                placeholder="ex: 20:00"
                value={form.horario}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: 10, marginTop: 18, flexWrap: 'wrap' }}>
            <button className="btn-primary" style={{ padding: '10px 22px' }} onClick={salvar}>
              {editId ? 'Salvar alterações' : '+ Criar sessão'}
            </button>

            {editId && (
              <button className="btn-ghost" style={{ padding: '10px 18px' }} onClick={limparForm}>
                Cancelar edição
              </button>
            )}
          </div>
        </div>

        <div className="card" style={{ overflow: 'hidden' }}>
          <div style={{ padding: '18px 24px 14px' }}>
            <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 19, color: 'var(--gold)' }}>
              Sessões Cadastradas
            </h2>
          </div>

          {carregando ? (
            <p style={{ padding: '20px 24px', color: 'var(--cream-muted)' }}>Carregando...</p>
          ) : sessoes.length === 0 ? (
            <p style={{ padding: '20px 24px', color: 'var(--cream-dim)' }}>Nenhuma sessão cadastrada.</p>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="tbl">
                <thead>
                  <tr>
                    {['Filme', 'Sala', 'Horário', 'Ações'].map(h => (
                      <th key={h} className="th">{h}</th>
                    ))}
                  </tr>
                </thead>

                <tbody>
                  {sessoes.map(s => (
                    <tr key={s.id}>
                      <td className="td" style={{ color: 'var(--cream)', fontWeight: 700 }}>
                        {s.filme?.nome || 'Filme removido'}
                      </td>

                      <td className="td">
                        {s.sala ? salaLabelCompleta(s.sala) : 'Sala removida'}
                      </td>

                      <td className="td" style={{ color: 'var(--gold-light)', fontWeight: 800 }}>
                        {s.horario}
                      </td>

                      <td className="td">
                        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                          <button
                            onClick={() => editar(s)}
                            style={{
                              background: 'var(--blue-dim)',
                              border: '1px solid var(--blue)',
                              color: 'var(--blue-text)',
                              borderRadius: 7,
                              padding: '6px 12px',
                              fontSize: 12,
                              fontWeight: 700,
                            }}
                          >
                            Editar
                          </button>

                          <button
                            onClick={() => remover(s.id)}
                            style={{
                              background: 'var(--red-dim)',
                              border: '1px solid var(--red)',
                              color: 'var(--red-text)',
                              borderRadius: 7,
                              padding: '6px 12px',
                              fontSize: 12,
                              fontWeight: 700,
                            }}
                          >
                            Remover
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
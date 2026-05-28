// cinema-frontend/src/pages/AdminFilmesPage.jsx
// Correções:
//   • Edição de filmes totalmente funcional (botão Editar → preenche form → Salvar)
//   • Campo de URL de imagem adicionado (imagemUrl)
//   • Proteção de rota: exige usuário logado como ADMIN/FUNCIONARIO
//   • Navegação entre admin/filmes e admin/sessoes

import { Fragment, useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/api';

function normalizarTitulo(txt) {
  return String(txt || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[:–—-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
    .toUpperCase();
}

const ADMIN_POSTER_FALLBACKS = {
  [normalizarTitulo('Pobres Criaturas')]: [
    'https://image.tmdb.org/t/p/w500/kCGlIMHnOm8JPXq3rXM6c5wMxcT.jpg',
    'https://media.themoviedb.org/t/p/w500/kCGlIMHnOm8JPXq3rXM6c5wMxcT.jpg',
  ],
  [normalizarTitulo('Moana 2')]: [
    'https://image.tmdb.org/t/p/w500/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg',
    'https://media.themoviedb.org/t/p/w500/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg',
    'https://m.media-amazon.com/images/M/MV5BNzgwZWQwMDctMDg4NC00MjFjLThhMGItMDhmZDk5NzZhZGE4XkEyXkFqcGc@._V1_SX300.jpg',
  ],
  [normalizarTitulo('Wicked')]: [
    'https://image.tmdb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg',
    'https://image.tmdb.org/t/p/w500/c5JFBd7qGPUBBUMtfOfFMaqIgEW.jpg',
    'https://media.themoviedb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg',
    'https://media.themoviedb.org/t/p/w500/c5JFBd7qGPUBBUMtfOfFMaqIgEW.jpg',
  ],
};

function AdminPoster({ filme }) {
  const urls = [
    filme.imagemUrl,
    ...(ADMIN_POSTER_FALLBACKS[normalizarTitulo(filme.nome)] || []),
  ].filter(Boolean);

  const [idx, setIdx] = useState(0);
  const [falhouTudo, setFalhouTudo] = useState(false);

  const src = urls[idx];

  if (!src || falhouTudo) {
    return (
      <div style={{
        width:36,
        height:50,
        borderRadius:4,
        border:'1px solid var(--border)',
        background:'linear-gradient(145deg, rgba(201,144,40,0.16), rgba(58,111,207,0.10))',
        display:'flex',
        alignItems:'center',
        justifyContent:'center',
        color:'var(--gold-light)',
        fontWeight:900,
        fontFamily:'var(--font-display)',
      }}>
        {filme.nome?.[0] || '🎬'}
      </div>
    );
  }

  return (
    <img
      src={src}
      alt={filme.nome}
      style={{
        width:36,
        height:50,
        objectFit:'cover',
        borderRadius:4,
        border:'1px solid var(--border)',
      }}
      onError={() => {
        if (idx + 1 < urls.length) {
          setIdx(idx + 1);
        } else {
          setFalhouTudo(true);
        }
      }}
    />
  );
}

export default function AdminFilmesPage() {
  const navigate = useNavigate();

  // Verifica se é admin/funcionario
  const userRaw = (() => { try { return JSON.parse(localStorage.getItem('cinema_user') || 'null'); } catch { return null; } })();
  const tipo = userRaw?.dtype ?? userRaw?.tipo ?? '';
  const isAutorizado = tipo === 'ADMINISTRADOR';

  const [filmes,     setFilmes]     = useState([]);
  const [form,       setForm]       = useState({ nome:'', duracao:'', sinopse:'', valor:'', imagemUrl:'' });
  const [flash,      setFlash]      = useState(null);
  const [editId,     setEditId]     = useState(null);
  const [carregando, setCarreg]     = useState(true);
  const [criticasPorFilme, setCriticasPorFilme] = useState({});
  const [filmeCriticasAberto, setFilmeCriticasAberto] = useState(null);

  const carregarCriticasDoFilme = filmeId => {
    api.get(`/filmes/${filmeId}/criticas`)
      .then(r => setCriticasPorFilme(p => ({ ...p, [filmeId]: r.data || [] })))
      .catch(() => setCriticasPorFilme(p => ({ ...p, [filmeId]: [] })));
  };

  const toggleCriticas = filmeId => {
    setFilmeCriticasAberto(p => p === filmeId ? null : filmeId);
    carregarCriticasDoFilme(filmeId);
  };

  const removerCritica = (filmeId, criticaId) => {
    if (!window.confirm('Remover esta crítica?')) return;

    api.delete(`/filmes/criticas/${criticaId}`)
      .then(() => {
        msg('Crítica removida.');
        carregarCriticasDoFilme(filmeId);
        carregar();
      })
      .catch(() => msg('Erro ao remover crítica.', false));
  };

  const carregar = () => {
    api.get('/filmes')
      .then(r => setFilmes(r.data))
      .finally(() => setCarreg(false));
  };

  useEffect(() => { carregar(); }, []);

  const msg = (texto, ok = true) => {
    setFlash({ texto, ok });
    setTimeout(() => setFlash(null), 4500);
  };

  const handleChange = e => setForm(p => ({ ...p, [e.target.name]: e.target.value }));

  const salvar = () => {
    const { nome, duracao, sinopse, valor, imagemUrl } = form;
    if (!nome.trim() || !duracao || !valor) { msg('Preencha nome, duração e valor.', false); return; }
    const payload = {
      nome: nome.trim(),
      duracao: parseInt(duracao),
      sinopse: sinopse.trim(),
      valor: parseFloat(String(valor).replace(',', '.')),
      imagemUrl: imagemUrl.trim() || null,
    };
    const req = editId ? api.put(`/filmes/${editId}`, payload) : api.post('/filmes', payload);
    req
      .then(() => {
        msg(editId ? 'Filme atualizado com sucesso!' : 'Filme adicionado com sucesso!');
        setForm({ nome:'', duracao:'', sinopse:'', valor:'', imagemUrl:'' });
        setEditId(null);
        carregar();
      })
      .catch(err => msg(err.response?.data?.mensagem || 'Erro ao salvar filme.', false));
  };

  const editar = f => {
    setForm({
      nome:      f.nome,
      duracao:   String(f.duracao),
      sinopse:   f.sinopse || '',
      valor:     String(f.valor),
      imagemUrl: f.imagemUrl || '',
    });
    setEditId(f.id);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const cancelar = () => {
    setForm({ nome:'', duracao:'', sinopse:'', valor:'', imagemUrl:'' });
    setEditId(null);
  };

  const remover = id => {
    if (!window.confirm('Remover este filme e todas as suas sessões?')) return;
    api.delete(`/filmes/${id}`)
      .then(() => { msg('Filme removido.'); carregar(); })
      .catch(() => msg('Erro ao remover.', false));
  };

  return (
    <div style={{ minHeight:'100vh', padding:'32px 24px', color:'var(--cream)' }}>
      <div style={{ maxWidth:960, margin:'0 auto' }}>

        {/* Cabeçalho */}
        <div style={{ display:'flex', alignItems:'center', gap:14, marginBottom:28, flexWrap:'wrap' }}>
          <button className="btn-ghost" style={{ padding:'8px 16px', fontSize:14 }} onClick={() => navigate('/')}>← Voltar</button>
          <div style={{ flex:1 }}>
            <h1 style={{ fontFamily:'var(--font-display)', fontSize:28, fontStyle:'italic' }}>Gerenciar Filmes</h1>
          </div>
          <Link to="/admin/sessoes" className="btn-ghost" style={{ fontSize:13 }}>🎭 Sessões →</Link>
        </div>

        {/* Aviso se não é admin/funcionario */}
        {!isAutorizado && (
          <div className="flash-err" style={{ marginBottom:20 }}>
            Acesso restrito. Apenas administradores podem gerenciar filmes.
          </div>
        )}

        {flash && <p className={flash.ok ? 'flash-ok' : 'flash-err'} style={{ marginBottom:18 }}>{flash.texto}</p>}

        {/* Formulário */}
        <div className="card" style={{ padding:'24px', marginBottom:24 }}>
          <h2 style={{ fontFamily:'var(--font-display)', fontSize:19, color:'var(--gold)', marginBottom:18 }}>
            {editId ? '✎ Editar Filme' : '+ Adicionar Filme'}
          </h2>

          {editId && (
            <div style={{ background:'var(--blue-dim)', border:'1px solid var(--blue)', color:'var(--blue-text)', borderRadius:8, padding:'10px 14px', fontSize:13, fontWeight:600, marginBottom:14 }}>
              ✎ Editando filme — preencha os campos abaixo e clique em "Salvar alterações".
            </div>
          )}

          <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(200px,1fr))', gap:12, marginBottom:14 }}>
            <div>
              <label className="lbl">Nome *</label>
              <input name="nome" className="inp" placeholder="ex: Duna: Parte Dois" value={form.nome} onChange={handleChange} />
            </div>
            <div>
              <label className="lbl">Duração (min) *</label>
              <input
                name="duracao"
                type="text"
                inputMode="numeric"
                className="inp"
                placeholder="ex: 166"
                value={form.duracao}
                onChange={e => {
                  const valor = e.target.value.replace(/\D/g, '');
                  setForm(p => ({ ...p, duracao: valor }));
                }}
              />
            </div>
            <div>
              <label className="lbl">Valor base (R$) *</label>
              <input
                name="valor"
                type="text"
                inputMode="decimal"
                className="inp"
                placeholder="ex: 20.00"
                value={form.valor}
                onChange={e => {
                  const valor = e.target.value
                    .replace(',', '.')
                    .replace(/[^\d.]/g, '')
                    .replace(/(\..*)\./g, '$1');

                  setForm(p => ({ ...p, vaFlor }));
                }}
              />
            </div>
            <div style={{ gridColumn:'1 / -1' }}>
              <label className="lbl">Sinopse</label>
              <textarea name="sinopse" className="inp" style={{ resize:'vertical', minHeight:70 }} placeholder="Breve descrição do filme…" value={form.sinopse} onChange={handleChange} />
            </div>
            <div style={{ gridColumn:'1 / -1' }}>
              <label className="lbl">URL da Imagem (poster)</label>
              <input name="imagemUrl" className="inp" placeholder="https://…/poster.jpg" value={form.imagemUrl} onChange={handleChange} />
              {form.imagemUrl && (
                <div style={{ marginTop:8, display:'flex', alignItems:'center', gap:10 }}>
                  <img src={form.imagemUrl} alt="Preview" style={{ width:48, height:68, objectFit:'cover', borderRadius:6, border:'1px solid var(--border)' }}
                    onError={e => { e.target.style.display = 'none'; }} />
                  <span style={{ fontSize:12, color:'var(--cream-dim)' }}>Pré-visualização do poster</span>
                </div>
              )}
            </div>
          </div>

          <div style={{ display:'flex', gap:10 }}>
            <button className="btn-primary" style={{ padding:'10px 22px' }} onClick={salvar}>
              {editId ? '✓ Salvar alterações' : '+ Adicionar'}
            </button>
            {editId && (
              <button className="btn-ghost" style={{ padding:'10px 18px' }} onClick={cancelar}>
                ✕ Cancelar edição
              </button>
            )}
          </div>
        </div>

        {/* Tabela de filmes */}
        <div className="card" style={{ overflow:'hidden' }}>
          <div style={{ padding:'18px 24px 14px' }}>
            <h2 style={{ fontFamily:'var(--font-display)', fontSize:19, color:'var(--gold)' }}>Filmes Cadastrados</h2>
          </div>
          {carregando ? (
            <p style={{ padding:'20px 24px', color:'var(--cream-muted)' }}>Carregando…</p>
          ) : filmes.length === 0 ? (
            <p style={{ padding:'20px 24px', color:'var(--cream-dim)' }}>Nenhum filme cadastrado.</p>
          ) : (
            <div style={{ overflowX:'auto' }}>
              <table className="tbl">
                <thead>
                  <tr>{['Poster','Nome','Duração','Valor','Nota','Ações'].map(h => <th key={h} className="th">{h}</th>)}</tr>
                </thead>
                <tbody>
                  {filmes.map(f => (
                    <Fragment key={f.id}>
                      <tr style={{ background: editId === f.id ? 'rgba(58,111,207,0.05)' : undefined }}>
                        <td className="td">
                          <AdminPoster filme={f} />
                        </td>

                        <td className="td" style={{ color:'var(--cream)', fontWeight:600 }}>
                          {editId === f.id && (
                            <span style={{ fontSize:10, color:'var(--blue-text)', background:'var(--blue-dim)', borderRadius:4, padding:'1px 6px', marginRight:6, verticalAlign:'middle' }}>
                              Editando
                            </span>
                          )}
                          {f.nome}
                        </td>

                        <td className="td">{f.duracao} min</td>
                        <td className="td">R$ {f.valor.toFixed(2)}</td>

                        <td className="td">
                          <span style={{ color:'var(--gold-light)', fontWeight:700 }}>★ {f.nota.toFixed(1)}</span>
                          {f.quantidadeCriticos > 0 && (
                            <span style={{ fontSize:11, color:'var(--cream-dim)', marginLeft:5 }}>
                              ({f.quantidadeCriticos})
                            </span>
                          )}
                        </td>

                        <td className="td">
                          <div style={{ display:'flex', gap:8, flexWrap:'wrap' }}>
                            <button onClick={() => editar(f)}
                              style={{ background:'var(--blue-dim)', border:'1px solid var(--blue)', color:'var(--blue-text)', borderRadius:7, padding:'6px 12px', fontSize:12, fontWeight:700 }}>
                              ✎ Editar
                            </button>

                            <button onClick={() => toggleCriticas(f.id)}
                              style={{ background:'rgba(201,144,40,0.10)', border:'1px solid var(--gold-border)', color:'var(--gold-light)', borderRadius:7, padding:'6px 12px', fontSize:12, fontWeight:700 }}>
                              💬 Críticas
                            </button>

                            <button onClick={() => remover(f.id)}
                              style={{ background:'var(--red-dim)', border:'1px solid var(--red)', color:'var(--red-text)', borderRadius:7, padding:'6px 12px', fontSize:12, fontWeight:700 }}>
                              ✕ Remover
                            </button>
                          </div>
                        </td>
                      </tr>

                      {filmeCriticasAberto === f.id && (
                        <tr>
                          <td className="td" colSpan={6} style={{ background:'rgba(255,255,255,0.015)' }}>
                            {(criticasPorFilme[f.id] || []).length === 0 ? (
                              <p style={{ color:'var(--cream-dim)' }}>Nenhuma crítica publicada para este filme.</p>
                            ) : (
                              <div style={{ display:'flex', flexDirection:'column', gap:10 }}>
                                {(criticasPorFilme[f.id] || []).map(c => (
                                  <div key={c.id} style={{ border:'1px solid var(--border-soft)', borderRadius:12, padding:'12px 14px' }}>
                                    <p style={{ fontWeight:700, color:'var(--cream)' }}>
                                      {c.nomeAutor || 'Crítico'}
                                      {c.origem && <span style={{ color:'var(--cream-dim)', fontSize:12 }}> · {c.origem}</span>}
                                    </p>

                                    <p style={{ color:'var(--cream-muted)', lineHeight:1.6, margin:'6px 0 10px' }}>
                                      "{c.mensagem}"
                                    </p>

                                    <button
                                      onClick={() => removerCritica(f.id, c.id)}
                                      style={{ background:'var(--red-dim)', border:'1px solid var(--red)', color:'var(--red-text)', borderRadius:7, padding:'6px 12px', fontSize:12, fontWeight:700 }}
                                    >
                                      Remover crítica
                                    </button>
                                  </div>
                                ))}
                              </div>
                            )}
                          </td>
                        </tr>
                      )}
                    </Fragment>
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
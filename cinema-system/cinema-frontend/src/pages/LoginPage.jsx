// cinema-frontend/src/pages/LoginPage.jsx
// Melhorias nesta versão:
//   • Dica de credenciais de admin REMOVIDA (era informação que devia ser oculta)
//   • Fundo com acento dourado animado mantido
//   • Ícones nos campos de input
//   • Mostrar/ocultar senha
//   • Visual premium aprimorado: borda sutil no card, separador decorativo

import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { loginUsuario } from '../api/api';

export default function LoginPage({ onLogin }) {
  const navigate = useNavigate();
  const [form, setForm]         = useState({ user: '', senha: '' });
  const [erro, setErro]         = useState('');
  const [carregando, setCarregando] = useState(false);
  const [mostrarSenha, setMostrarSenha] = useState(false);

  function handleChange(e) {
    setForm(p => ({ ...p, [e.target.name]: e.target.value }));
    setErro('');
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!form.user.trim() || !form.senha.trim()) {
      setErro('Preencha usuário e senha.'); return;
    }
    setCarregando(true);
    try {
      const res = await loginUsuario({ user: form.user, senha: form.senha });
      onLogin(res.data);
      navigate('/');
    } catch {
      setErro('Usuário ou senha incorretos.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={pg}>
      {/* Acentos de fundo */}
      <div style={{ position:'absolute', inset:0, pointerEvents:'none', overflow:'hidden' }}>
        <div style={{ position:'absolute', width:500, height:500, borderRadius:'50%', background:'radial-gradient(circle, rgba(201,144,40,0.08) 0%, transparent 70%)', top:'-15%', left:'50%', transform:'translateX(-50%)' }}/>
        <div style={{ position:'absolute', width:300, height:300, borderRadius:'50%', background:'radial-gradient(circle, rgba(56,95,206,0.06) 0%, transparent 70%)', bottom:'10%', right:'10%' }}/>
      </div>

      <div className="card" style={{
        width:'100%', maxWidth:420, padding:'48px 38px',
        boxShadow:'var(--shadow-lg), 0 0 0 1px rgba(201,144,40,0.08)',
        animation:'scaleIn 0.4s cubic-bezier(.22,.68,0,1.15) both',
        position:'relative',
      }}>
        {/* Linha decorativa dourada no topo do card */}
        <div style={{
          position:'absolute', top:0, left:'10%', right:'10%', height:2,
          background:'linear-gradient(90deg, transparent, var(--gold), transparent)',
          borderRadius:999,
        }}/>

        {/* Logo */}
        <div style={{ textAlign:'center', marginBottom:32 }}>
          <Link to="/" style={{ display:'inline-block', textDecoration:'none', marginBottom:16 }}>
            <span style={{
              fontFamily:'var(--font-display)', fontSize:34, color:'var(--gold)',
              fontStyle:'italic', display:'block',
              textShadow:'0 0 30px rgba(201,144,40,0.35)',
            }}>
              Lumière
            </span>
          </Link>
          <div style={{ width:40, height:1.5, background:'linear-gradient(90deg, transparent, var(--gold), transparent)', margin:'0 auto 20px' }}/>
          <h1 style={{ fontSize:24, fontFamily:'var(--font-display)', marginBottom:6 }}>Bem-vindo de volta</h1>
          <p style={{ color:'var(--cream-muted)', fontSize:13.5 }}>Entre com sua conta para continuar</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display:'flex', flexDirection:'column', gap:16 }}>
          {/* Campo usuário */}
          <div>
            <label className="lbl">Usuário</label>
            <div style={{ position:'relative' }}>
              <span style={{
                position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
                fontSize:16, color:'var(--cream-dim)', pointerEvents:'none',
              }}>👤</span>
              <input
                name="user" className="inp"
                placeholder="Seu nome de usuário"
                value={form.user}
                onChange={handleChange}
                autoComplete="username"
                style={{ paddingLeft:38 }}
              />
            </div>
          </div>

          {/* Campo senha */}
          <div>
            <label className="lbl">Senha</label>
            <div style={{ position:'relative' }}>
              <span style={{
                position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
                fontSize:16, color:'var(--cream-dim)', pointerEvents:'none',
              }}>🔒</span>
              <input
                name="senha"
                type={mostrarSenha ? 'text' : 'password'}
                className="inp"
                placeholder="Sua senha"
                value={form.senha}
                onChange={handleChange}
                autoComplete="current-password"
                style={{ paddingLeft:38, paddingRight:44 }}
              />
              <button
                type="button"
                onClick={() => setMostrarSenha(v => !v)}
                style={{
                  position:'absolute', right:10, top:'50%', transform:'translateY(-50%)',
                  background:'none', border:'none', color:'var(--cream-dim)',
                  cursor:'pointer', fontSize:16, padding:'4px', lineHeight:1,
                  transition:'color 0.15s',
                }}
                onMouseEnter={e => e.currentTarget.style.color='var(--cream-muted)'}
                onMouseLeave={e => e.currentTarget.style.color='var(--cream-dim)'}
                tabIndex={-1}
                aria-label={mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'}
              >
                {mostrarSenha ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          {erro && <p className="flash-err" style={{ margin:0 }}>{erro}</p>}

          <button
            type="submit" className="btn-primary"
            disabled={carregando}
            style={{ width:'100%', marginTop:4, padding:'14px', fontSize:15, opacity: carregando ? 0.6 : 1 }}
          >
            {carregando
              ? <span style={{ display:'flex', alignItems:'center', gap:8, justifyContent:'center' }}>
                  <span style={{ width:16, height:16, borderRadius:'50%', border:'2px solid rgba(0,0,0,0.3)', borderTopColor:'#0d0900', animation:'spin 0.7s linear infinite', display:'inline-block' }}/>
                  Entrando…
                </span>
              : 'Entrar →'
            }
          </button>
        </form>

        {/* Separador */}
        <div style={{ display:'flex', alignItems:'center', gap:12, margin:'20px 0 16px' }}>
          <div style={{ flex:1, height:1, background:'var(--border-soft)' }}/>
          <span style={{ fontSize:11, color:'var(--cream-dim)', letterSpacing:'0.08em' }}>OU</span>
          <div style={{ flex:1, height:1, background:'var(--border-soft)' }}/>
        </div>

        <p style={{ textAlign:'center', fontSize:13.5, color:'var(--cream-dim)', marginBottom:10 }}>
          Não tem conta?{' '}
          <Link to="/cadastro" style={{ color:'var(--gold-light)', fontWeight:700 }}>Cadastrar-se gratuitamente</Link>
        </p>

        <p style={{ textAlign:'center', fontSize:12 }}>
          <Link to="/" style={{ color:'var(--cream-dim)' }}>← Voltar à programação</Link>
        </p>

        {/* NOTA: A dica de credenciais de admin foi removida intencionalmente.
            Essa informação não deve ficar visível na tela de login. */}
      </div>
    </div>
  );
}

const pg = {
  minHeight:'100vh', display:'flex', alignItems:'center', justifyContent:'center',
  padding:'24px 16px', position:'relative', overflow:'hidden',
};
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { cadastrarUsuario } from '../api/api';
import NumberStepper from '../components/NumberStepper';


const TIPOS = [
  { value: 'COMUM',     label: 'Usuário comum' },
  { value: 'ESTUDANTE', label: 'Estudante (meia-entrada)' },
  { value: 'CRITICO',   label: 'Crítico de cinema' },
];

function apenasDigitos(valor) {
  return String(valor ?? '').replace(/[^\d]/g, '');
}

function formatarCpf(valor) {
  const d = apenasDigitos(valor).slice(0, 11);

  if (d.length <= 3) return d;
  if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`;
  if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`;

  return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9, 11)}`;
}

function cpfValido(valor) {
  const cpf = apenasDigitos(valor);
  return cpf.length === 11 && !/^(\d)\1{10}$/.test(cpf);
}

// function cpfValido(valor) {
//   const cpf = apenasDigitos(valor);

//   if (cpf.length !== 11) return false;
//   if (new Set(cpf).size === 1) return false;

//   const calcularDigito = tamanho => {
//     let soma = 0;

//     for (let i = 0; i < tamanho; i++) {
//       soma += Number(cpf[i]) * (tamanho + 1 - i);
//     }

//     const resto = soma % 11;
//     return resto < 2 ? 0 : 11 - resto;
//   };

//   const digito1 = calcularDigito(9);
//   const digito2 = calcularDigito(10);

//   return digito1 === Number(cpf[9]) && digito2 === Number(cpf[10]);
// }

function senhaValida(senha) {
  return typeof senha === 'string'
    && senha.length >= 8
    && /[a-z]/.test(senha)
    && /[A-Z]/.test(senha)
    && /\d/.test(senha);
}


export default function CadastroPage({ onLogin }) {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    user: '', cpf: '', senha: '', idade: '', sexo: '', email: '',
    nomeCartao: '', numeroCartao: '', codigoCartao: '', tipo: 'COMUM', origem: '',
  });
  const [erro, setErro]         = useState('');
  const [carregando, setCarregando] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;

    setForm(p => ({
      ...p,
      [name]: name === 'cpf' ? formatarCpf(value) : value,
    }));

    setErro('');
  }

  async function handleSubmit(e) {
    e.preventDefault();

    if (!form.user.trim() || !form.cpf.trim() || !form.senha.trim() || !form.email.trim()) {
      setErro('Preencha todos os campos obrigatórios.');
      return;
    }

  const cpfLimpo = apenasDigitos(form.cpf);

  if (!cpfValido(cpfLimpo)) {
    setErro('CPF inválido. Verifique os números digitados.');
    return;
  }

  if (!senhaValida(form.senha)) {
    setErro('A senha deve ter pelo menos 8 caracteres, com maiúscula, minúscula e número.');
    return;
  }

    setCarregando(true);

    try {
      const payload = {
        user: form.user,
        cpf: cpfLimpo,
        senha: form.senha,
        idade: parseInt(form.idade) || 0,
        sexo: form.sexo,
        email: form.email,
        nomeCartao: form.nomeCartao,
        numeroCartao: form.numeroCartao,
        codigoCartao: form.codigoCartao,
        tipo: form.tipo,
        ...(form.tipo === 'CRITICO' && { origem: form.origem }),
      };

      const res = await cadastrarUsuario(payload);
      onLogin(res.data);
      navigate('/');
    } catch (err) {
      setErro(err.response?.data?.mensagem || err.response?.data?.erro || 'Erro ao cadastrar. Tente novamente.');
    } finally {
      setCarregando(false);
    }
  }

  const campo = (label, name, type = 'text', placeholder = '', opts = {}) => (
    <div>
      <label className="lbl">{label}</label>
      <input name={name} type={type} className="inp" placeholder={placeholder}
        value={form[name]} onChange={handleChange} {...opts} />
    </div>
  );

  return (
    <div style={pg}>
      <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 60% 50% at 50% 0%, rgba(58,111,207,0.06), transparent 60%)', pointerEvents: 'none' }} />

      <div className="card" style={{ width: '100%', maxWidth: 520, padding: '44px 36px', boxShadow: 'var(--shadow-lg)', animation: 'scaleIn 0.4s ease both', position: 'relative' }}>
        <div style={{ textAlign: 'center', marginBottom: 30 }}>
          <Link to="/" style={{ display: 'inline-block', textDecoration: 'none', marginBottom: 16 }}>
            <span style={{ fontFamily: 'var(--font-display)', fontSize: 30, color: 'var(--gold)', fontStyle: 'italic' }}>Lumière</span>
          </Link>
          <div style={{ width: 40, height: 1.5, background: 'linear-gradient(90deg, transparent, var(--gold), transparent)', margin: '0 auto 18px' }} />
          <h1 style={{ fontSize: 24, fontFamily: 'var(--font-display)', marginBottom: 6 }}>Criar conta</h1>
          <p style={{ color: 'var(--cream-muted)', fontSize: 13.5 }}>Cadastre-se para comprar ingressos</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {/* Tipo */}
          <div>
            <label className="lbl">Tipo de conta</label>
            <select name="tipo" className="inp" style={{ cursor: 'pointer' }} value={form.tipo} onChange={handleChange}>
              {TIPOS.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </div>

          {form.tipo === 'ESTUDANTE' && (
            <div className="flash-ok" style={{ fontSize: 12.5 }}>🎓 Meia-entrada aplicada automaticamente em todas as suas compras.</div>
          )}
          {form.tipo === 'CRITICO' && (
            <div style={{ background: 'var(--blue-dim)', border: '1px solid var(--blue)', color: 'var(--blue-text)', borderRadius: '8px', padding: '10px 14px', fontSize: 12.5, fontWeight: 600 }}>
              🎬 Críticos têm entrada gratuita em todos os filmes.
            </div>
          )}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            {campo('Usuário *', 'user', 'text', 'ex: joaosilva')}
            {campo('Email *', 'email', 'email', 'ex: joao@email.com')}
            {campo('CPF *', 'cpf', 'text', '000.000.000-00')}
            {campo('Senha *', 'senha', 'password', 'Mínimo 6 caracteres')}
            <div>
              <label className="lbl">Idade</label>
              <NumberStepper
                value={form.idade}
                min={0}
                max={120}
                placeholder="18"
                ariaLabel="Idade"
                onChange={v => {
                  setForm(p => ({ ...p, idade: v }));
                  setErro('');
                }}
              />
            </div>
            <div>
              <label className="lbl">Sexo</label>
              <select name="sexo" className="inp" style={{ cursor: 'pointer' }} value={form.sexo} onChange={handleChange}>
                <option value="">Não informar</option>
                <option value="M">Masculino</option>
                <option value="F">Feminino</option>
                <option value="O">Outro</option>
              </select>
            </div>
          </div>

          {form.tipo === 'CRITICO' && (
            campo('Veículo / Órgão de origem', 'origem', 'text', 'ex: Folha de S.Paulo')
          )}

          <p style={{ fontSize: 11.5, color: 'var(--cream-dim)', margin: '2px 0 0', letterSpacing: '0.03em', textTransform: 'uppercase', fontWeight: 700 }}>Dados de pagamento (opcional)</p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            {campo('Nome no cartão', 'nomeCartao', 'text', 'Como aparece no cartão')}
            {campo('Número do cartão', 'numeroCartao', 'text', '0000 0000 0000 0000')}
            {campo('Código verificador', 'codigoCartao', 'text', '123')}
          </div>

          {erro && <p className="flash-err" style={{ margin: 0 }}>{erro}</p>}

          <button type="submit" className="btn-primary" disabled={carregando}
            style={{ width: '100%', padding: '13px', fontSize: 15, marginTop: 4, opacity: carregando ? 0.6 : 1 }}>
            {carregando ? 'Cadastrando…' : 'Criar conta'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: 22, fontSize: 13.5, color: 'var(--cream-dim)' }}>
          Já tem conta?{' '}
          <Link to="/login" style={{ color: 'var(--gold-light)', fontWeight: 700 }}>Entrar</Link>
        </p>
        <p style={{ textAlign: 'center', marginTop: 8, fontSize: 12 }}>
          <Link to="/" style={{ color: 'var(--cream-dim)' }}>← Voltar à programação</Link>
        </p>
      </div>
    </div>
  );
}

const pg = {
  minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
  padding: '32px 16px', position: 'relative', overflow: 'hidden',
};
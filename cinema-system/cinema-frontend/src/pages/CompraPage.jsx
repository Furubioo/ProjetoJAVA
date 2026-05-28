// cinema-frontend/src/pages/CompraPage.jsx
// Correções e melhorias desta versão:
//   • Itens de balcão adicionados (pipoca, refrigerante, chocolate, nachos, água, suco)
//     conforme exigido pelos slides: a compra pode conter itens além dos bilhetes
//   • Cupom MEIA só aparece para ESTUDANTE
//   • usuarioId sempre lido de usuario.id
//   • Badge de tipo de usuário consistente: UserTypeBadge em todas as telas (compra + confirmação)
//   • Sorteio de 2 assentos: garante fileiras mais traseiras primeiro (J→A),
//     sorteia entre TODOS os pares da fileira mais de trás disponível (não só 3)
//   • Visual enriquecido: seção de balcão com cards visuais, resumo expandido, animações

import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { buscarSessao, comprarIngresso } from '../api/api';
import api from '../api/api';
import NumberStepper from '../components/NumberStepper';


const FILEIRAS = ['A','B','C','D','E','F','G','H','I','J'];
const COLUNAS  = Array.from({ length: 15 }, (_, i) => i + 1);

// ── PRODUTOS DE BALCÃO (conforme slides: pipoca, refrigerante, chocolate, etc.) ──
const PRODUTOS_BALCAO = [
  { id:'PIPOCA_P',   nome:'Pipoca Pequena',  emoji:'🍿', valor:14.00, descricao:'Salgada ou doce' },
  { id:'PIPOCA_G',   nome:'Pipoca Grande',   emoji:'🍿', valor:22.00, descricao:'Salgada ou doce' },
  { id:'REFRIGERANTE', nome:'Refrigerante',  emoji:'🥤', valor:12.00, descricao:'350ml — gelado'  },
  { id:'CHOCOLATE',  nome:'Chocolate',       emoji:'🍫', valor:10.00, descricao:'Barra 80g'       },
  { id:'NACHOS',     nome:'Nachos',          emoji:'🌮', valor:18.00, descricao:'Com molho cheddar'},
  { id:'SUCO',       nome:'Suco Natural',    emoji:'🍹', valor:13.00, descricao:'500ml'           },
];

// Cupons — MEIA restrito a ESTUDANTE
const CUPONS_BALCAO = [
  { value:'NENHUM', label:'Sem cupom', desconto:0 },
  { value:'BALCAO10', label:'BALCAO10 — 10% no balcão', desconto:0.10 },
  { value:'BALCAO20', label:'BALCAO20 — 20% no balcão', desconto:0.20 },
];

const MULT = { COMUM:1.0, SALA_3D:1.2, XD:1.3, XD_3D:1.4 };

const TIPO_LABEL = {
  COMUM:'Usuário Comum', ESTUDANTE:'Estudante',
  CRITICO:'Crítico', ADMINISTRADOR:'Administrador', FUNCIONARIO:'Funcionário',
};
const TIPO_CLASS = {
  COMUM:'tag-comum-usr', ESTUDANTE:'tag-estudante',
  CRITICO:'tag-critico', ADMINISTRADOR:'tag-admin', FUNCIONARIO:'tag-admin',
};
const TIPO_ICON = {
  COMUM:'🎟️', ESTUDANTE:'🎓', CRITICO:'🎬', ADMINISTRADOR:'⚙️', FUNCIONARIO:'🏟️',
};

// Badge de tipo de usuário — consistente em todas as telas
function UserTypeBadge({ tipo }) {
  const icon  = TIPO_ICON[tipo] || '🎟️';
  const label = TIPO_LABEL[tipo] || 'Usuário Comum';
  const cls   = TIPO_CLASS[tipo] || 'tag-comum-usr';
  return (
    <span className={`tag ${cls}`} style={{ fontSize:9.5, display:'inline-flex', alignItems:'center', gap:4 }}>
      <span style={{ fontSize:11 }}>{icon}</span>
      {label}
    </span>
  );
}

// Sugestão de 1 assento: fileiras de trás para frente (J→A),
// dentro de cada fileira prefere próximo ao centro
function escolherPonderado(candidatos) {
  const soma = candidatos.reduce((acc, c) => acc + c.peso, 0);
  let alvo = Math.random() * soma;

  for (const c of candidatos) {
    alvo -= c.peso;
    if (alvo <= 0) return c;
  }

  return candidatos[0];
}

function scoreAssento(linha, coluna, totalLinhas = 10, totalColunas = 15) {
  const centro = (totalColunas - 1) / 2;
  const distanciaCentro = Math.abs(coluna - centro);
  const bonusTraseira = linha / Math.max(1, totalLinhas - 1);
  const bonusCentro = 1 - (distanciaCentro / centro);

  return 1 + bonusTraseira * 7 + bonusCentro * 4;
}

function sugerirAssento(cadeiras) {
  if (!cadeiras || cadeiras.length === 0) return null;

  const totalLinhas = cadeiras.length;
  const totalColunas = cadeiras[0]?.length || 15;
  const candidatos = [];

  for (let l = totalLinhas - 1; l >= 0; l--) {
    for (let c = 0; c < totalColunas; c++) {
      if (!cadeiras[l]?.[c]) {
        candidatos.push({
          linha: l,
          coluna: c,
          peso: scoreAssento(l, c, totalLinhas, totalColunas),
        });
      }
    }
  }

  if (candidatos.length === 0) return null;

  candidatos.sort((a, b) => b.peso - a.peso);
  const elite = candidatos.slice(0, Math.min(18, candidatos.length));
  const escolhido = escolherPonderado(elite);

  return { linha: escolhido.linha, coluna: escolhido.coluna };
}

function sugerirDoisAssentos(cadeiras) {
  if (!cadeiras || cadeiras.length === 0) return null;

  const totalLinhas = cadeiras.length;
  const totalColunas = cadeiras[0]?.length || 15;
  const candidatos = [];

  for (let l = totalLinhas - 1; l >= 0; l--) {
    for (let c = 0; c < totalColunas - 1; c++) {
      if (!cadeiras[l]?.[c] && !cadeiras[l]?.[c + 1]) {
        const centroPar = c + 0.5;
        candidatos.push({
          par: [{ linha:l, coluna:c }, { linha:l, coluna:c + 1 }],
          peso: scoreAssento(l, centroPar, totalLinhas, totalColunas) + 2,
        });
      }
    }
  }

  if (candidatos.length === 0) return null;

  candidatos.sort((a, b) => b.peso - a.peso);
  const elite = candidatos.slice(0, Math.min(14, candidatos.length));

  return escolherPonderado(elite).par;
}

function ContadorQtd({ value, onChange, min = 1, max = 10 }) {
  return (
    <div style={{ maxWidth:180 }}>
      <NumberStepper
        value={value}
        min={min}
        max={max}
        allowEmpty={false}
        ariaLabel="Quantidade de ingressos"
        onChange={onChange}
      />
    </div>
  );
}

function assentoLabel(a) {
  return a ? `${FILEIRAS[a.linha]}${COLUNAS[a.coluna]}` : null;
}

// ── CARD DE PRODUTO DE BALCÃO ──────────────────────────────────────────────

function CardProduto({ produto, quantidade, onAdicionar, onRemover }) {
  const temItem = quantidade > 0;

  return (
    <div style={{
      borderRadius:14,
      border: temItem ? '1px solid var(--gold-border)' : '1px solid var(--border-soft)',
      background: temItem ? 'var(--gold-dim)' : 'rgba(255,255,255,0.022)',
      padding:'14px 16px',
      display:'flex',
      alignItems:'center',
      justifyContent:'space-between',
      gap:12,
      overflow:'hidden',
      minWidth:0,
      transition:'background 0.14s, border-color 0.14s',
    }}>
      <div style={{ display:'flex', alignItems:'center', gap:12, minWidth:0, flex:1 }}>
        <div style={{
          width:44,
          height:44,
          borderRadius:11,
          background: temItem ? 'rgba(201,144,40,0.15)' : 'rgba(255,255,255,0.05)',
          border: temItem ? '1px solid var(--gold-border)' : '1px solid var(--border-soft)',
          display:'flex',
          alignItems:'center',
          justifyContent:'center',
          fontSize:22,
          flexShrink:0,
        }}>
          {produto.emoji}
        </div>

        <div style={{ minWidth:0 }}>
          <p style={{ margin:0, fontSize:14, fontWeight:700, color: temItem ? 'var(--cream)' : 'var(--cream-muted)', whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>
            {produto.nome}
          </p>
          <p style={{ margin:0, fontSize:11.5, color:'var(--cream-dim)', whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>
            {produto.descricao}
          </p>
          <p style={{ margin:'2px 0 0', fontSize:12.5, fontWeight:700, color:'var(--gold-light)', fontVariantNumeric:'tabular-nums' }}>
            R$ {produto.valor.toFixed(2)}
          </p>
        </div>
      </div>

      <div style={{ display:'flex', alignItems:'center', gap:8, flexShrink:0 }}>
        {temItem ? (
          <>
            <button className="btn-count" onClick={onRemover} aria-label="Remover">−</button>
            <span style={{ minWidth:24, textAlign:'center', fontSize:15, fontWeight:700, color:'var(--cream)', fontVariantNumeric:'tabular-nums' }}>
              {quantidade}
            </span>
            <button className="btn-count" onClick={onAdicionar} aria-label="Adicionar">+</button>
          </>
        ) : (
          <button
            onClick={onAdicionar}
            style={{
              padding:'7px 12px',
              borderRadius:8,
              background:'rgba(255,255,255,0.06)',
              border:'1px solid var(--border)',
              color:'var(--cream-muted)',
              fontSize:12.5,
              fontWeight:700,
              whiteSpace:'nowrap',
            }}
          >
            + Adicionar
          </button>
        )}
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────

export default function CompraPage({ usuario }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const [sessao,     setSessao]    = useState(null);
  const [cadeiras,   setCadeiras]  = useState([]);
  const [carregando, setCarregando]= useState(true);
  const [erro,       setErro]      = useState('');
  const [comprando,  setComprando] = useState(false);
  const [bilhetes,   setBilhetes]  = useState(null);

  const [selecionado,   setSelecionado]   = useState(null);
  const [selecionados2, setSelecionados2] = useState(null);

  const [quantidade, setQuantidade] = useState(1);
  const [comprador,  setComprador]  = useState('');
  const [cupomBalcao, setCupomBalcao] = useState('NENHUM');
  const cupom = 'NENHUM';

  // Estado do carrinho de balcão: { [produtoId]: quantidade }
  const [carrinhoProdutos, setCarrinhoProdutos] = useState({});

  const tipo   = usuario?.dtype ?? usuario?.tipo ?? 'COMUM';
  const isEst  = tipo === 'ESTUDANTE';
  const isCrit = tipo === 'CRITICO';

  useEffect(() => {
    buscarSessao(id)
      .then(r => { setSessao(r.data); setCadeiras(r.data.cadeiras || []); })
      .catch(() => setErro('Sessão não encontrada.'))
      .finally(() => setCarregando(false));
  }, [id]);

  const mult = sessao ? (MULT[sessao.sala?.tipo] ?? 1.0) : 1.0;
  const precoBase = sessao ? sessao.filme.valor * mult : 0;

  const precoUnit = isCrit ? 0 : isEst ? precoBase / 2 : precoBase;
  const precoTotal = precoUnit * quantidade;

  const totalProdutosBruto = PRODUTOS_BALCAO.reduce((sum, p) => {
    return sum + (carrinhoProdutos[p.id] || 0) * p.valor;
  }, 0);

  const qtdProdutos = Object.values(carrinhoProdutos).reduce((s, v) => s + v, 0);

  const cupomBalcaoObj = CUPONS_BALCAO.find(c => c.value === cupomBalcao) || CUPONS_BALCAO[0];
  const descontoBalcao = isCrit ? totalProdutosBruto : totalProdutosBruto * cupomBalcaoObj.desconto;
  const totalProdutos = Math.max(0, totalProdutosBruto - descontoBalcao);

  const totalGeral = isCrit ? 0 : precoTotal + totalProdutos;

  const modoUm   = quantidade === 1;
  const modoDois = quantidade === 2;
  const modoMult = quantidade > 2;

  const handleQtdChange = (v) => {
    setQuantidade(v); setSelecionado(null); setSelecionados2(null); setErro('');
  };

  const handleSugerir = () => {
    const s = sugerirAssento(cadeiras);
    if (s) { setSelecionado(s); setErro(''); }
    else setErro('Nenhum assento disponível.');
  };

  const handleSugerirDois = () => {
    const par = sugerirDoisAssentos(cadeiras);
    if (par) { setSelecionados2(par); setErro(''); }
    else setErro('Não há par de assentos adjacentes disponíveis.');
  };

  const handleClickAssento2 = (l, c) => {
    if (cadeiras[l]?.[c]) return;
    setSelecionados2(prev => {
      if (!prev) return [{ linha:l, coluna:c }, null];
      if (!prev[1]) {
        const primeiro = prev[0];
        if (primeiro.linha === l && Math.abs(primeiro.coluna - c) === 1)
          return [primeiro, { linha:l, coluna:c }];
        return [{ linha:l, coluna:c }, null];
      }
      return [{ linha:l, coluna:c }, null];
    });
    setErro('');
  };

  const isAssentoSel2 = (l, c) =>
    selecionados2 ? selecionados2.some(a => a && a.linha === l && a.coluna === c) : false;

  const adicionarProduto = (prodId) => {
    setCarrinhoProdutos(p => ({ ...p, [prodId]: (p[prodId] || 0) + 1 }));
  };
  const removerProduto = (prodId) => {
    setCarrinhoProdutos(p => {
      const novo = { ...p };
      if (novo[prodId] > 1) novo[prodId]--;
      else delete novo[prodId];
      return novo;
    });
  };

  const handleComprar = async () => {
    if (!comprador.trim()) { setErro('Informe o nome do comprador.'); return; }
    if (modoUm && !selecionado) { setErro('Selecione um assento no mapa.'); return; }
    if (modoDois && (!selecionados2 || !selecionados2[1])) {
      setErro('Selecione ou sugira 2 assentos adjacentes.'); return;
    }
    if (!usuario?.id) { setErro('Você precisa estar logado para comprar.'); return; }

    setErro(''); setComprando(true);
    try {
      if (modoUm) {
        const res = await comprarIngresso(id, {
          linha: selecionado.linha,
          coluna: selecionado.coluna,
          nomeComprador: comprador.trim(),
          cupom,
          usuarioId: usuario.id,
          tipoUsuario: tipo,
        });
        setBilhetes([res.data]);
      } else if (modoDois) {
        const [a1, a2] = selecionados2;
        const res = await api.post(`/sessoes/${id}/comprar-multiplos`, {
          quantidade: 2,
          linhas:  [a1.linha,  a2.linha],
          colunas: [a1.coluna, a2.coluna],
          nomeComprador: comprador.trim(),
          cupom,
          usuarioId: usuario.id,
          tipoUsuario: tipo,
        });
        setBilhetes(res.data.bilhetes || []);
      } else {
        const res = await api.post(`/sessoes/${id}/comprar-multiplos`, {
          quantidade,
          nomeComprador: comprador.trim(),
          cupom,
          usuarioId: usuario.id,
          tipoUsuario: tipo,
        });
        setBilhetes(res.data.bilhetes || []);
      }
      buscarSessao(id).then(r => setCadeiras(r.data.cadeiras || []));
    } catch (e) {
      setErro(e.response?.data?.erro || e.response?.data?.mensagem || 'Erro ao processar a compra.');
    } finally {
      setComprando(false);
    }
  };

  // ── Confirmação de bilhetes ──
  if (bilhetes) return (
    <div style={pgCenter}>
      <div style={{ position:'absolute', inset:0, background:'radial-gradient(ellipse 50% 40% at 50% 0%, rgba(42,158,110,0.09), transparent)', pointerEvents:'none' }}/>
      <div className="card" style={{ maxWidth:560, width:'100%', padding:'40px 36px', boxShadow:'var(--shadow-lg)', animation:'scaleIn 0.4s ease both', position:'relative' }}>
        <div style={{ textAlign:'center', marginBottom:24 }}>
          <div style={{ fontSize:54, marginBottom:10 }}>🎟</div>
          <h1 style={{ fontFamily:'var(--font-display)', fontSize:27, color:'var(--green-text)', marginBottom:8 }}>
            {bilhetes.length > 1 ? `${bilhetes.length} Ingressos Confirmados!` : 'Ingresso Confirmado!'}
          </h1>
          <div style={{ width:40, height:1.5, background:'linear-gradient(90deg, transparent, var(--green), transparent)', margin:'0 auto' }}/>
        </div>

        {/* Badge de usuário na confirmação — consistente */}
        {usuario && (
          <div style={{ display:'flex', justifyContent:'center', marginBottom:20 }}>
            <UserTypeBadge tipo={tipo} />
          </div>
        )}

        <div style={{ display:'flex', flexDirection:'column', gap:10 }}>
          {bilhetes.map((b, i) => (
            <div key={i} style={{ border:'1px dashed var(--border)', borderRadius:13, padding:'16px 20px', display:'flex', flexDirection:'column', gap:6 }}>
              {bilhetes.length > 1 && <p style={{ fontSize:11, color:'var(--gold)', fontWeight:700, letterSpacing:'0.06em', textTransform:'uppercase', marginBottom:2 }}>Ingresso {i + 1}</p>}
              <LinhaInfo label="Comprador" valor={b.nomeComprador} />
              <LinhaInfo label="Filme"     valor={b.filme} />
              <LinhaInfo label="Sala"      valor={`${b.sala} (${b.tipoSala})`} />
              <LinhaInfo label="Horário"   valor={b.horario} />
              <LinhaInfo label="Assento"   valor={b.assento} grande />
              <hr style={{ borderColor:'var(--border-soft)', margin:'4px 0' }}/>
              {b.isEstudante && <LinhaInfo label="Meia-entrada (estudante)" valor="−50%" verde />}
              {b.isCritico   && <LinhaInfo label="Entrada gratuita (crítico)" valor="−100%" verde />}
              {b.cupom !== 'NENHUM' && <LinhaInfo label={`Cupom ${b.cupom}`} valor={`−R$ ${Number(b.descontoCupomValor).toFixed(2)}`} verde />}
              <LinhaInfo label="Total ingresso" valor={`R$ ${Number(b.precoFinal).toFixed(2)}`} grande />
            </div>
          ))}
        </div>

        {/* Itens do balcão na confirmação */}
        {qtdProdutos > 0 && (
          <div style={{ marginTop:12, borderRadius:12, background:'rgba(255,255,255,0.025)', border:'1px solid var(--border-soft)', padding:'14px 18px', display:'flex', flexDirection:'column', gap:6 }}>
            <p style={{ margin:'0 0 6px', fontSize:11, color:'var(--gold)', fontWeight:700, letterSpacing:'0.06em', textTransform:'uppercase' }}>🍿 Itens do Balcão</p>
            {PRODUTOS_BALCAO.filter(p => carrinhoProdutos[p.id]).map(p => (
              <LinhaInfo key={p.id}
                label={`${p.emoji} ${p.nome} × ${carrinhoProdutos[p.id]}`}
                valor={`R$ ${(p.valor * carrinhoProdutos[p.id]).toFixed(2)}`}
              />
            ))}
            <hr style={{ borderColor:'var(--border-soft)', margin:'4px 0' }}/>
            <LinhaInfo label="Total balcão" valor={`R$ ${totalProdutos.toFixed(2)}`} grande />
          </div>
        )}

        {bilhetes.length > 1 || qtdProdutos > 0 ? (
          <div style={{ marginTop:12, background:'var(--gold-dim)', border:'1px solid var(--gold-border)', borderRadius:11, padding:'12px 16px', display:'flex', justifyContent:'space-between' }}>
            <span style={{ fontWeight:700, color:'var(--cream)', fontSize:15 }}>TOTAL GERAL</span>
            <span style={{ fontWeight:800, color:'var(--gold-light)', fontSize:18, fontVariantNumeric:'tabular-nums' }}>
              R$ {totalGeral.toFixed(2)}
            </span>
          </div>
        ) : null}

        <div style={{ display:'flex', gap:12, marginTop:24 }}>
          <button className="btn-ghost" style={{ flex:1, padding:'12px' }} onClick={() => navigate('/')}>← Programação</button>
          <button className="btn-primary" style={{ flex:1, padding:'12px' }} onClick={() => { setBilhetes(null); setSelecionado(null); setSelecionados2(null); setQuantidade(1); setCarrinhoProdutos({}); }}>
            Comprar mais
          </button>
        </div>
      </div>
    </div>
  );

  if (carregando) return (
    <div style={{ ...pgCenter, gap:16 }}>
      <div style={{ width:38, height:38, borderRadius:'50%', border:'3px solid var(--border)', borderTopColor:'var(--gold)', animation:'spin 0.8s linear infinite' }}/>
      <p style={{ color:'var(--cream-muted)', fontSize:14 }}>Carregando sessão…</p>
    </div>
  );

  if (!sessao) return (
    <div style={{ ...pgCenter, flexDirection:'column', gap:14 }}>
      <p className="flash-err">{erro || 'Sessão não encontrada.'}</p>
      <button className="btn-ghost" onClick={() => navigate('/')}>← Voltar</button>
    </div>
  );

  return (
    <div style={{ minHeight:'100vh', display:'flex', flexDirection:'column' }}>
      <header style={{
        position:'sticky', top:0, zIndex:100,
        background:'rgba(6,5,14,0.95)', backdropFilter:'blur(26px)',
        borderBottom:'1px solid var(--border-soft)', padding:'0 24px',
        boxShadow:'0 4px 32px rgba(0,0,0,0.4)',
      }}>
        <div style={{ maxWidth:900, margin:'0 auto', height:62, display:'flex', alignItems:'center', gap:14 }}>
          <button onClick={() => navigate(-1)} className="btn-ghost" style={{ padding:'7px 14px', fontSize:18, lineHeight:1 }}>←</button>
          <div style={{ flex:1 }}>
            <h1 style={{ fontFamily:'var(--font-display)', fontSize:20, lineHeight:1.2 }}>{sessao.filme.nome}</h1>
            <p style={{ fontSize:12, color:'var(--cream-dim)', marginTop:2 }}>
              {sessao.sala.nome} · {sessao.sala.tipo} · {sessao.horario}
            </p>
          </div>
          {/* Badge de tipo de usuário — mesmo componente UserTypeBadge em todas as telas */}
          {usuario && (
            <div style={{ display:'flex', alignItems:'center', gap:8, flexShrink:0 }}>
              <span style={{ fontSize:12.5, color:'var(--cream-muted)' }}>{usuario.user}</span>
              <UserTypeBadge tipo={tipo} />
            </div>
          )}
        </div>
      </header>

      <div style={{ maxWidth:900, margin:'0 auto', padding:'28px 24px 72px', width:'100%', display:'flex', flexDirection:'column', gap:18 }}>

        {/* Banner de benefício */}
        {isEst && (
          <div className="flash-ok" style={{ display:'flex', alignItems:'center', gap:10 }}>
            <span style={{ fontSize:20 }}>🎓</span>
            <div>
              <span style={{ fontWeight:700 }}>Você é Estudante</span>
              <span style={{ fontWeight:400, color:'var(--green-text)', opacity:0.85 }}> — meia-entrada aplicada automaticamente em todos os seus ingressos.</span>
            </div>
          </div>
        )}
        {isCrit && (
          <div className="flash-info" style={{ display:'flex', alignItems:'center', gap:10 }}>
            <span style={{ fontSize:20 }}>🎬</span>
            <div>
              <span style={{ fontWeight:700 }}>Você é Crítico de Cinema</span>
              <span style={{ fontWeight:400, color:'var(--blue-text)', opacity:0.85 }}> — entrada gratuita para todos os filmes.</span>
            </div>
          </div>
        )}

        {/* Quantidade de ingressos */}
        <div className="card" style={{ padding:'22px 24px' }}>
          <h2 style={{ fontFamily:'var(--font-display)', fontSize:18, marginBottom:16 }}>Quantidade de ingressos</h2>
          <ContadorQtd value={quantidade} onChange={handleQtdChange} min={1} max={10} />
        </div>

        {/* Mapa de assentos */}
        {(modoUm || modoDois) && (
          <div className="card" style={{ padding:'22px 24px' }}>
            <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:18, flexWrap:'wrap', gap:10 }}>
              <h2 style={{ fontFamily:'var(--font-display)', fontSize:18, margin:0 }}>
                {modoUm ? 'Escolha seu assento' : 'Escolha 2 assentos adjacentes'}
              </h2>
              <button
                className="btn-ghost"
                style={{ fontSize:13, padding:'7px 14px', display:'flex', alignItems:'center', gap:6 }}
                onClick={modoUm ? handleSugerir : handleSugerirDois}
              >
                <span>✨</span>
                {modoUm ? 'Sugerir assento' : 'Sugerir par'}
              </button>
            </div>

            {modoDois && selecionados2 && (
              <div style={{ marginBottom:12 }}>
                {selecionados2[0] && !selecionados2[1] && (
                  <p className="flash-info" style={{ margin:0, fontSize:13 }}>
                    1º assento: <strong>{assentoLabel(selecionados2[0])}</strong> — clique no assento ao lado para formar o par.
                  </p>
                )}
              </div>
            )}

            {/* Tela */}
            <div style={{ textAlign:'center', marginBottom:18 }}>
              <div style={{ display:'inline-block', background:'linear-gradient(90deg, transparent, var(--gold-dim) 30%, var(--gold-dim) 70%, transparent)', border:'1px solid var(--gold-border)', borderRadius:4, padding:'5px 70px', color:'var(--gold)', fontSize:10.5, letterSpacing:'0.5em', fontWeight:700, textTransform:'uppercase' }}>TELA</div>
              <div style={{ height:12, background:'linear-gradient(to bottom, rgba(201,144,40,0.07), transparent)', marginTop:6 }}/>
            </div>

            {/* Grade */}
            <div style={{ overflowX:'auto', paddingBottom:4 }}>
              <div style={{ display:'flex', flexDirection:'column', gap:5, minWidth:'max-content' }}>
                <div style={{ display:'flex', gap:4, paddingLeft:26 }}>
                  {COLUNAS.map(c => <div key={c} style={{ width:26, textAlign:'center', fontSize:9.5, color:'var(--cream-dim)', fontVariantNumeric:'tabular-nums' }}>{c}</div>)}
                </div>
                {FILEIRAS.map((letra, l) => (
                  <div key={letra} style={{ display:'flex', gap:4, alignItems:'center' }}>
                    <div style={{ width:20, textAlign:'center', fontSize:10.5, color:'var(--cream-dim)', fontWeight:700, flexShrink:0 }}>{letra}</div>
                    {COLUNAS.map((_, c) => {
                      const ocupado = !!(cadeiras[l]?.[c]);
                      const sel1 = modoUm  && selecionado?.linha === l && selecionado?.coluna === c;
                      const sel2 = modoDois && isAssentoSel2(l, c);
                      return (
                        <button key={c} disabled={ocupado}
                          onClick={() => {
                            if (modoUm && !ocupado) { setSelecionado({ linha:l, coluna:c }); setErro(''); }
                            if (modoDois) handleClickAssento2(l, c);
                          }}
                          className={`seat${(sel1 || sel2) ? ' sel' : ''}`}
                        />
                      );
                    })}
                  </div>
                ))}
              </div>
            </div>

            {/* Legenda */}
            <div style={{ display:'flex', gap:20, marginTop:16, flexWrap:'wrap' }}>
              {[
                { bg:'#17163a', border:'rgba(255,255,255,0.10)', label:'Livre' },
                { bg:'var(--gold)', border:'var(--gold)', label:'Selecionado' },
                { bg:'#0c0b20', border:'rgba(255,255,255,0.04)', label:'Ocupado', opacity:0.28 },
              ].map(item => (
                <div key={item.label} style={{ display:'flex', alignItems:'center', gap:7 }}>
                  <div style={{ width:16, height:14, borderRadius:'3px 3px 2px 2px', background:item.bg, border:`1px solid ${item.border}`, opacity:item.opacity || 1 }}/>
                  <span style={{ fontSize:12, color:'var(--cream-dim)' }}>{item.label}</span>
                </div>
              ))}
            </div>

            {modoUm && selecionado && (
              <div style={{ marginTop:16, background:'var(--gold-dim)', border:'1px solid var(--gold-border)', borderRadius:11, padding:'12px 18px', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
                <span style={{ color:'var(--cream-muted)', fontSize:13 }}>Assento selecionado</span>
                <span style={{ fontFamily:'var(--font-display)', fontSize:24, color:'var(--gold-light)', fontWeight:900, letterSpacing:'0.05em' }}>
                  {assentoLabel(selecionado)}
                </span>
              </div>
            )}

            {modoDois && selecionados2?.[0] && selecionados2?.[1] && (
              <div style={{ marginTop:16, background:'var(--gold-dim)', border:'1px solid var(--gold-border)', borderRadius:11, padding:'12px 18px', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
                <span style={{ color:'var(--cream-muted)', fontSize:13 }}>Assentos selecionados</span>
                <span style={{ fontFamily:'var(--font-display)', fontSize:20, color:'var(--gold-light)', fontWeight:900, letterSpacing:'0.05em' }}>
                  {assentoLabel(selecionados2[0])} + {assentoLabel(selecionados2[1])}
                </span>
              </div>
            )}
          </div>
        )}

        {modoMult && (
          <div className="card" style={{ padding:'20px 24px' }}>
            <div style={{ display:'flex', alignItems:'center', gap:12 }}>
              <span style={{ fontSize:32 }}>🤖</span>
              <div>
                <p style={{ fontWeight:700, color:'var(--cream)', marginBottom:4 }}>Seleção automática de {quantidade} assentos</p>
                <p style={{ fontSize:13, color:'var(--cream-muted)', lineHeight:1.5 }}>O sistema escolherá assentos disponíveis juntos na parte traseira da sala.</p>
              </div>
            </div>
          </div>
        )}

        {/* ── BALCÃO — itens de pipoca, refrigerante, etc. ── */}
        <div className="card" style={{ padding:'22px 24px' }}>
          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:18, flexWrap:'wrap', gap:8 }}>
            <div>
              <h2 style={{ fontFamily:'var(--font-display)', fontSize:18, margin:0 }}>🍿 Balcão</h2>
              <p style={{ fontSize:12.5, color:'var(--cream-dim)', margin:'4px 0 0' }}>Adicione itens à sua compra — retirada na entrada</p>
            </div>
            {qtdProdutos > 0 && (
              <span style={{ fontSize:12, background:'var(--gold-dim)', border:'1px solid var(--gold-border)', borderRadius:999, padding:'3px 12px', color:'var(--gold-light)', fontWeight:700 }}>
                {qtdProdutos} item{qtdProdutos !== 1 ? 's' : ''} · R$ {totalProdutos.toFixed(2)}
              </span>
            )}
          </div>
          <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(260px, 1fr))', gap:10 }}>
            {PRODUTOS_BALCAO.map(prod => (
              <CardProduto
                key={prod.id}
                produto={prod}
                quantidade={carrinhoProdutos[prod.id] || 0}
                onAdicionar={() => adicionarProduto(prod.id)}
                onRemover={() => removerProduto(prod.id)}
              />
            ))}
          </div>
        </div>

        {/* Dados do comprador */}
        <div className="card" style={{ padding:'22px 24px' }}>
          <h2 style={{ fontFamily:'var(--font-display)', fontSize:18, marginBottom:16 }}>
            Dados do comprador
          </h2>

          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <div style={{ gridColumn:'1/-1' }}>
              <label className="lbl">Nome do comprador *</label>
              <input
                className="inp"
                placeholder="Nome completo"
                value={comprador}
                onChange={e => {
                  setComprador(e.target.value);
                  setErro('');
                }}
              />
            </div>

            {!isCrit && qtdProdutos > 0 && (
              <div>
                <label className="lbl">Cupom do balcão</label>
                <select
                  className="inp"
                  style={{ cursor:'pointer' }}
                  value={cupomBalcao}
                  onChange={e => setCupomBalcao(e.target.value)}
                >
                  {CUPONS_BALCAO.map(c => (
                    <option key={c.value} value={c.value}>{c.label}</option>
                  ))}
                </select>
              </div>
            )}
          </div>
        </div>

        {/* Resumo de preço */}
        <div className="card" style={{ padding:'22px 24px' }}>
          <h2 style={{ fontFamily:'var(--font-display)', fontSize:18, marginBottom:16 }}>Resumo do pedido</h2>
          <div style={{ display:'flex', flexDirection:'column', gap:8, fontSize:13.5 }}>
            <ResumoLinha label={`Preço base (${sessao.sala.tipo})`} valor={`R$ ${precoBase.toFixed(2)}`} />
            {isEst && <ResumoLinha label="Estudante: meia-entrada automática" valor="−50%" verde />}
            {isCrit && <ResumoLinha label="Crítico: compra zerada" valor="−100%" verde />}
            <ResumoLinha label="Preço por ingresso" valor={`R$ ${precoUnit.toFixed(2)}`} destaque />
            {quantidade > 1 && <ResumoLinha label={`× ${quantidade} ingressos`} valor={`R$ ${precoTotal.toFixed(2)}`} />}
            <ResumoLinha label="Ingressos" valor={`R$ ${precoTotal.toFixed(2)}`} grande />

            {qtdProdutos > 0 && (
              <>
                <div style={{ height:1, background:'var(--border-soft)', margin:'4px 0' }} />
                <p style={{ margin:0, fontSize:11.5, color:'var(--gold)', fontWeight:700, letterSpacing:'0.05em', textTransform:'uppercase' }}>
                  Balcão
                </p>

                {PRODUTOS_BALCAO.filter(p => carrinhoProdutos[p.id]).map(p => (
                  <ResumoLinha
                    key={p.id}
                    label={`${p.emoji} ${p.nome} × ${carrinhoProdutos[p.id]}`}
                    valor={`R$ ${(p.valor * carrinhoProdutos[p.id]).toFixed(2)}`}
                  />
                ))}

                {!isCrit && descontoBalcao > 0 && (
                  <ResumoLinha label={`Cupom ${cupomBalcaoObj.value}`} valor={`−R$ ${descontoBalcao.toFixed(2)}`} verde />
                )}

                {isCrit && (
                  <ResumoLinha label="Crítico: balcão cortesia" valor={`−R$ ${totalProdutosBruto.toFixed(2)}`} verde />
                )}

                <ResumoLinha label="Balcão" valor={`R$ ${totalProdutos.toFixed(2)}`} grande />
              </>
            )}

            {/* Total geral */}
            <div style={{ borderTop:'2px solid var(--gold-border)', marginTop:4, paddingTop:12, display:'flex', justifyContent:'space-between', alignItems:'center' }}>
              <span style={{ fontWeight:800, fontSize:16, color:'var(--cream)' }}>TOTAL GERAL</span>
              <span style={{ fontWeight:900, fontSize:20, color:'var(--gold-light)', fontVariantNumeric:'tabular-nums' }}>R$ {totalGeral.toFixed(2)}</span>
            </div>
          </div>
        </div>

        {erro && <p className="flash-err">{erro}</p>}

        <button className="btn-primary" disabled={comprando} onClick={handleComprar}
          style={{ padding:'15px', fontSize:16, opacity: comprando ? 0.6 : 1, borderRadius:12, letterSpacing:'0.015em' }}>
          {comprando ? 'Processando…' : `Confirmar pedido · R$ ${totalGeral.toFixed(2)}`}
        </button>
      </div>
    </div>
  );
}

function LinhaInfo({ label, valor, grande, verde }) {
  return (
    <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center' }}>
      <span style={{ fontSize: grande ? 14 : 13, color:'var(--cream-muted)' }}>{label}</span>
      <span style={{ fontSize: grande ? 16 : 13.5, fontWeight: grande ? 800 : 600,
        color: verde ? 'var(--green-text)' : grande ? 'var(--cream)' : 'var(--cream-muted)',
        fontVariantNumeric:'tabular-nums' }}>{valor}</span>
    </div>
  );
}

function ResumoLinha({ label, valor, verde, destaque, grande }) {
  return (
    <div style={{ display:'flex', justifyContent:'space-between', padding: grande ? '6px 0 0' : '2px 0',
      borderTop: grande ? '1px solid var(--border-soft)' : 'none' }}>
      <span style={{ color: destaque ? 'var(--cream)' : 'var(--cream-muted)', fontWeight: destaque || grande ? 700 : 400, fontSize: grande ? 15 : 13.5 }}>{label}</span>
      <span style={{ color: verde ? 'var(--green-text)' : grande ? 'var(--gold-light)' : 'var(--cream-muted)',
        fontWeight: grande ? 800 : destaque ? 700 : 400, fontSize: grande ? 16 : 13.5, fontVariantNumeric:'tabular-nums' }}>{valor}</span>
    </div>
  );
}

const pgCenter = {
  minHeight:'100vh', display:'flex', alignItems:'center', justifyContent:'center',
  padding:'32px 16px', position:'relative', overflow:'hidden',
};
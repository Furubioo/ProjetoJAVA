import { useEffect, useState, useRef, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/api';

// ── CONSTANTES ────────────────────────────────────────────────────────────────

const MULT           = { COMUM: 1.0, SALA_3D: 1.2, XD: 1.3, XD_3D: 1.4 };
const SALA_LABEL     = { COMUM: 'Comum', SALA_3D: '3D', XD: 'XD', XD_3D: 'XD/3D' };
const SALA_TAG_CLASS = { COMUM: 'tag-comum', SALA_3D: 'tag-3d', XD: 'tag-xd', XD_3D: 'tag-xd3d' };

const TIPO_META = {
  COMUM:        { icon: '🎟️', desc: 'Usuário Comum',    cls: 'tag-comum-usr' },
  ESTUDANTE:    { icon: '🎓', desc: 'Estudante',         cls: 'tag-estudante' },
  CRITICO:      { icon: '🎬', desc: 'Crítico de Cinema', cls: 'tag-critico'   },
  ADMINISTRADOR:{ icon: '⚙️', desc: 'Administrador',     cls: 'tag-admin'     },
  FUNCIONARIO:  { icon: '🏟️', desc: 'Funcionário',       cls: 'tag-admin'     },
};

const PALETTES = [
  { from:'#0e0528', to:'#4a1580', glow:'#a855f7' },
  { from:'#060e2a', to:'#123090', glow:'#3b82f6' },
  { from:'#051a10', to:'#0d5c34', glow:'#10b981' },
  { from:'#1a0608', to:'#7a1825', glow:'#f87171' },
  { from:'#120900', to:'#6b3010', glow:'#f59e0b' },
  { from:'#09001a', to:'#4c1d95', glow:'#8b5cf6' },
  { from:'#001518', to:'#0b526a', glow:'#22d3ee' },
  { from:'#180a00', to:'#7c2d12', glow:'#fb923c' },
];

const TRAILER_IDS = {
  [trailerKey('Duna: Parte Dois')]: 'QqmbrvluQRA',
  [trailerKey('Oppenheimer')]: 'uYPbbksJxIg',
  [trailerKey('Pobres Criaturas')]: 'RlbR5N6veqw',
  [trailerKey('Missão Impossível: Acerto Final')]: 'fsQgc9pCyDU',
  [trailerKey('Missão: Impossível - O Acerto Final')]: 'fsQgc9pCyDU',
  [trailerKey('Deadpool e Wolverine')]: '73_1biulkYk',
  [trailerKey('Moana 2')]: 'DFnAWpQicqo',
  [trailerKey('Wicked')]: 'fPHsMn13aLo',
};

// Múltiplas URLs de poster por filme, tentadas em ordem via onError.
const POSTER_FALLBACKS = {
  'Duna: Parte Dois': [
    'https://media.themoviedb.org/t/p/w780/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg',
    'https://media.themoviedb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg',
  ],
  'Oppenheimer': [
    'https://media.themoviedb.org/t/p/w780/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg',
    'https://media.themoviedb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg',
  ],
  'Pobres Criaturas': [
    'https://media.themoviedb.org/t/p/w780/kCGlIMHnOm8JPXq3rXM6c5wMxcT.jpg',
    'https://media.themoviedb.org/t/p/w500/kCGlIMHnOm8JPXq3rXM6c5wMxcT.jpg',
  ],
  'Missão Impossível: Acerto Final': [
    'https://media.themoviedb.org/t/p/w780/z53D72EAOxGRqdr7KXXWp9dJiDe.jpg',
    'https://media.themoviedb.org/t/p/w500/z53D72EAOxGRqdr7KXXWp9dJiDe.jpg',
  ],
  'Deadpool e Wolverine': [
    'https://media.themoviedb.org/t/p/w780/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg',
    'https://media.themoviedb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg',
  ],
  'Moana 2': [
    'https://media.themoviedb.org/t/p/w780/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg',
    'https://media.themoviedb.org/t/p/w500/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg',
    'https://m.media-amazon.com/images/M/MV5BNzgwZWQwMDctMDg4NC00MjFjLThhMGItMDhmZDk5NzZhZGE4XkEyXkFqcGc@._V1_SX300.jpg',
  ],
  'Wicked': [
    'https://image.tmdb.org/t/p/w780/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg',
    'https://image.tmdb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg',
    'https://media.themoviedb.org/t/p/w500/xDGbZ0JJ3mYaGKy4Nzd9Kph6M9L.jpg',
    'https://media.themoviedb.org/t/p/w780/c5JFBd7qGPUBBUMtfOfFMaqIgEW.jpg',
    'https://media.themoviedb.org/t/p/w500/c5JFBd7qGPUBBUMtfOfFMaqIgEW.jpg',
  ],
};

// ── HELPERS ───────────────────────────────────────────────────────────────────

function filmePalette(nome) {
  let h = 0;
  for (let i = 0; i < nome.length; i++) h = (h * 31 + nome.charCodeAt(i)) >>> 0;
  return PALETTES[h % PALETTES.length];
}

function trailerKey(nome) {
  return String(nome || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[:–—-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
    .toUpperCase();
}


function getTrailerEmbedId(nome) {
  return TRAILER_IDS[trailerKey(nome)] ?? null;
}

function getFilmeImageUrls(filme) {
  const fallbacks = POSTER_FALLBACKS[filme.nome] || [];
  if (filme.imagemUrl) {
    return [filme.imagemUrl, ...fallbacks];
  }
  return fallbacks;
}

// ── HOOK: carregamento de imagem com múltiplos fallbacks ──────────────────────

function useImageFallback(urls) {
  const key = urls.join('|');
  const [idx,   setIdx]   = useState(0);
  const [valid, setValid] = useState(urls.length > 0);
  const prevKey = useRef(key);

  if (prevKey.current !== key) {
    prevKey.current = key;
    setIdx(0);
    setValid(urls.length > 0);
  }

  const onError = useCallback(() => {
    setIdx(i => {
      const next = i + 1;
      if (next < urls.length) return next;
      setValid(false);
      return i;
    });
  }, [urls.length]);

  // Carrega a imagem sem crossOrigin para evitar CORS
  const [probed, setProbed] = useState(null);
  useEffect(() => {
    if (!valid || !urls[idx]) { setProbed(null); return; }
    const img = new Image();
    img.onload  = () => setProbed(urls[idx]);
    img.onerror = () => {
      setIdx(i => {
        const next = i + 1;
        if (next < urls.length) return next;
        setValid(false);
        return i;
      });
      setProbed(null);
    };
    img.src = urls[idx];
    return () => { img.onload = null; img.onerror = null; };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idx, key]);

  return [valid ? probed : null, onError];
}

// ── COMPONENTES BASE ──────────────────────────────────────────────────────────

function SalaTag({ tipo }) {
  return (
    <span className={`tag ${SALA_TAG_CLASS[tipo] || 'tag-comum'}`}>
      {SALA_LABEL[tipo] || tipo}
    </span>
  );
}

function normSalaTexto(v) {
  return String(v || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[_/\s-]+/g, '')
    .toUpperCase();
}

function salaTipoLabel(tipo) {
  return SALA_LABEL[tipo] || tipo || '';
}

function salaNomeExibicao(sala) {
  if (!sala) return '';

  const nome = String(sala.nome || '').trim();
  const tipo = salaTipoLabel(sala.tipo);

  if (!nome) return '';
  if (normSalaTexto(nome) === normSalaTexto(tipo)) return '';
  if (normSalaTexto(nome) === normSalaTexto(sala.tipo)) return '';

  return nome;
}

function salaLabelCompleta(sala) {
  if (!sala) return '-';

  const nome = salaNomeExibicao(sala);
  const tipo = salaTipoLabel(sala.tipo);

  if (nome && tipo) return `${nome} · ${tipo}`;
  return nome || tipo || '-';
}

function salasUnicasDasSessoes(sessoes) {
  const vistas = new Set();

  return (sessoes || [])
    .map(s => s.sala)
    .filter(Boolean)
    .filter(sala => {
      const chave = `${sala.id || sala.nome || ''}-${sala.tipo || ''}`;

      if (vistas.has(chave)) return false;

      vistas.add(chave);
      return true;
    });
}

function Estrelas({ nota, total }) {
  const filled = Math.min(5, Math.round((nota / 10) * 5 * 2) / 2);
  return (
    <div style={{ display:'flex', alignItems:'center', gap:4 }}>
      {[1,2,3,4,5].map(i => (
        <span key={i} style={{ fontSize:13, color: i <= filled ? '#eab84a' : 'rgba(234,184,74,0.14)' }}>★</span>
      ))}
      <span style={{ fontSize:12, color:'var(--cream-muted)', marginLeft:4, fontVariantNumeric:'tabular-nums' }}>
        {nota.toFixed(1)}
        {total > 0 && (
          <span style={{ color:'var(--cream-dim)', fontSize:11 }}>
            {' '}({total} crítico{total !== 1 ? 's' : ''})
          </span>
        )}
      </span>
    </div>
  );
}

function UserTypeBadge({ tipo }) {
  const tipoNorm = (tipo || 'COMUM').toUpperCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  const meta = TIPO_META[tipoNorm] || TIPO_META.COMUM;
  return (
    <span className={`tag ${meta.cls}`} title={meta.desc}
      style={{ fontSize:9.5, display:'inline-flex', alignItems:'center', gap:4 }}>
      <span style={{ fontSize:11 }}>{meta.icon}</span>
      {meta.desc}
    </span>
  );
}

// ── LOGO SVG CARRETEL ─────────────────────────────────────────────────────────

function LogoCinema() {
  return (
    <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 13, textDecoration: 'none', flexShrink: 0 }}>
      <svg
        width="40" height="40" viewBox="0 0 40 40" fill="none"
        xmlns="http://www.w3.org/2000/svg"
        style={{
          animation: 'logo-spin 45s linear infinite, logo-reel-glow 3s ease-in-out infinite',
          transformOrigin: 'center',
          willChange: 'transform'  
        }}
      >
        <circle cx="20" cy="20" r="18.5" stroke="#c99028" strokeWidth="1.8" />
        {[0,45,90,135,180,225,270,315].map((deg, i) => {
          const r = 15.5;
          const rad = (deg * Math.PI) / 180;
          return (
            <rect
              key={i}
              x={20 + r * Math.cos(rad) - 2}
              y={20 + r * Math.sin(rad) - 2}
              width="4" height="4" rx="1"
              fill={i % 2 === 0 ? '#c99028' : 'rgba(201,144,40,0.40)'}
            />
          );
        })}
        {[0,60,120,180,240,300].map((deg, i) => {
          const rad = (deg * Math.PI) / 180;
          return (
            <line
              key={i}
              x1={20 + 5.5 * Math.cos(rad)} y1={20 + 5.5 * Math.sin(rad)}
              x2={20 + 11  * Math.cos(rad)} y2={20 + 11  * Math.sin(rad)}
              stroke="rgba(201,144,40,0.35)" strokeWidth="1.4" strokeLinecap="round"
            />
          );
        })}
        <circle cx="20" cy="20" r="5.5" fill="#c99028" />
        <circle cx="20" cy="20" r="3.0" fill="#06050e" />
        <circle cx="20" cy="20" r="1.2" fill="#c99028" />
        <circle cx="20" cy="20" r="12.5" stroke="rgba(201,144,40,0.14)" strokeWidth="0.6" />
      </svg>

      <div style={{ display: 'flex', flexDirection: 'column', lineHeight: 1, gap: 2 }}>
        <span style={{
          fontFamily: 'var(--font-display)', fontSize: 26, fontStyle: 'italic',
          letterSpacing: '-0.015em', color: 'var(--gold)',
          animation: 'gold-shimmer 6s ease-in-out infinite',
        }}>
          Lumière
        </span>
        <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
          <div style={{ width: 18, height: '1px', background: 'linear-gradient(90deg, var(--gold-border), transparent)' }} />
          <span style={{ fontSize: 7.5, letterSpacing: '0.44em', textTransform: 'uppercase', color: 'var(--cream-dim)' }}>Cinema</span>
          <div style={{ width: 18, height: '1px', background: 'linear-gradient(270deg, var(--gold-border), transparent)' }} />
        </div>
      </div>
    </Link>
  );
}

// ── MODAL SOBRE ───────────────────────────────────────────────────────────────

function ModalSobre({ onClose }) {
  useEffect(() => {
    const h = e => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', h);
    document.body.style.overflow = 'hidden';
    return () => { window.removeEventListener('keydown', h); document.body.style.overflow = ''; };
  }, [onClose]);

  return (
    <div
      style={{
        position:'fixed', inset:0, zIndex:600,
        background:'rgba(0,0,0,0.88)', backdropFilter:'blur(28px)',
        display:'flex', alignItems:'center', justifyContent:'center',
        padding:'20px', animation:'fadeIn 0.2s ease both',
      }}
      onClick={e => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div style={{
        width:'100%', maxWidth:520,
        background:'var(--bg-card)',
        border:'1px solid var(--gold-border)',
        borderRadius:24, overflow:'hidden',
        boxShadow:'var(--shadow-lg), 0 0 70px rgba(201,144,40,0.10)',
        animation:'scaleIn 0.32s cubic-bezier(.22,.68,0,1.15) both',
        position:'relative',
      }}>
        {/* Topo dourado */}
        <div style={{ height:3, background:'linear-gradient(90deg, transparent, var(--gold), var(--gold-light), var(--gold), transparent)' }}/>

        {/* Fechar */}
        <button onClick={onClose} style={{
          position:'absolute', top:14, right:14, zIndex:10,
          width:34, height:34, borderRadius:'50%',
          background:'rgba(0,0,0,0.5)', border:'1px solid var(--border)',
          color:'var(--cream-muted)', fontSize:16, cursor:'pointer',
          display:'flex', alignItems:'center', justifyContent:'center',
          transition:'all 0.15s',
        }}
          onMouseEnter={e => { e.currentTarget.style.background='rgba(180,48,74,0.55)'; e.currentTarget.style.color='#fff'; }}
          onMouseLeave={e => { e.currentTarget.style.background='rgba(0,0,0,0.5)'; e.currentTarget.style.color='var(--cream-muted)'; }}
        >✕</button>

        <div style={{ padding:'32px 36px 36px' }}>
          <div style={{ textAlign:'center', marginBottom:24 }}>
            <div style={{ fontSize:52, marginBottom:10, filter:'drop-shadow(0 0 14px rgba(201,144,40,0.44))' }}>🎬</div>
            <h2 style={{ fontFamily:'var(--font-display)', fontSize:32, fontStyle:'italic', color:'var(--gold)', marginBottom:4 }}>
              Lumière Cinema
            </h2>
            <p style={{ fontSize:11, letterSpacing:'0.24em', textTransform:'uppercase', color:'var(--cream-dim)' }}>Desde 1998</p>
            <div style={{ width:50, height:1, background:'linear-gradient(90deg, transparent, var(--gold), transparent)', margin:'16px auto' }}/>
          </div>

          <p style={{ fontSize:14, color:'var(--cream-muted)', lineHeight:1.72, marginBottom:22, textAlign:'center' }}>
            O Lumière Cinema nasceu com a missão de oferecer uma experiência cinematográfica única,
            combinando tecnologia de ponta com a magia atemporal do cinema clássico.
          </p>

          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12, marginBottom:22 }}>
            {[
              { icon:'📍', title:'Endereço',      value:'Av. Boa Viagem, 3000\nRecife – PE' },
              { icon:'🕐', title:'Funcionamento', value:'Seg–Dom\n12h às 23h' },
              { icon:'📞', title:'Contato',        value:'(81) 3000-0000' },
              { icon:'✉️', title:'E-mail',         value:'contato@lumiere.com.br' },
            ].map(item => (
              <div key={item.title} style={{
                background:'rgba(255,255,255,0.028)', border:'1px solid var(--border-soft)',
                borderRadius:12, padding:'14px 16px',
                transition:'border-color 0.15s',
              }}>
                <div style={{ fontSize:20, marginBottom:5 }}>{item.icon}</div>
                <p style={{ fontSize:10, fontWeight:700, textTransform:'uppercase', letterSpacing:'0.08em', color:'var(--cream-dim)', marginBottom:4 }}>{item.title}</p>
                <p style={{ fontSize:13, color:'var(--cream-muted)', whiteSpace:'pre-line', lineHeight:1.5 }}>{item.value}</p>
              </div>
            ))}
          </div>

          <button className="btn-primary" style={{ width:'100%', padding:'13px' }} onClick={onClose}>Fechar</button>
        </div>
      </div>
    </div>
  );
}

// ── POSTER COM FALLBACK MÚLTIPLO ──────────────────────────────────────────────

function PosterFilme({ filme, onClick, height = 280 }) {
  const urls = getFilmeImageUrls(filme);
  const [src, onError] = useImageFallback(urls);
  const [hovered, setHovered] = useState(false);
  const pal = filmePalette(filme.nome);

  return (
    <div
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        height, position:'relative', flexShrink:0, overflow:'hidden', cursor:'pointer',
        background: src ? '#000' : `linear-gradient(145deg, ${pal.from} 0%, ${pal.to} 100%)`,
        display:'flex', alignItems:'center', justifyContent:'center',
      }}
    >
      {src ? (
        <img src={src} alt={filme.nome} onError={onError}
          style={{
            width:'100%', height:'100%', objectFit:'cover',
            transition:'transform 0.6s cubic-bezier(.22,.68,0,1.2), opacity 0.3s',
            transform: hovered ? 'scale(1.09)' : 'scale(1)',
            opacity: hovered ? 1 : 0.88,
          }}
        />
      ) : (
        <>
          <div style={{ position:'absolute', width:300, height:300, borderRadius:'50%', background:pal.glow, opacity:0.13, filter:'blur(80px)', top:'50%', left:'50%', transform:'translate(-50%,-50%)' }}/>
          <span style={{ fontFamily:'var(--font-display)', fontStyle:'italic', fontSize:120, fontWeight:900, color:pal.glow, opacity:0.17, userSelect:'none', lineHeight:1, position:'relative' }}>
            {filme.nome[0]}
          </span>
        </>
      )}

      {/* Overlay hover */}
      <div style={{
        position:'absolute', inset:0,
        background: hovered ? 'rgba(0,0,0,0.58)' : 'rgba(0,0,0,0)',
        transition:'background 0.32s ease',
        display:'flex', alignItems:'center', justifyContent:'center',
      }}>
        {hovered && (
          <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap:8, animation:'fadeIn 0.18s ease both' }}>
            <div style={{
              width:64, height:64, borderRadius:'50%',
              background:'rgba(201,144,40,0.94)', backdropFilter:'blur(8px)',
              display:'flex', alignItems:'center', justifyContent:'center',
              boxShadow:'0 0 48px rgba(201,144,40,0.70)',
            }}>
              <span style={{ fontSize:26, marginLeft:4 }}>▶</span>
            </div>
            <span style={{ fontSize:11, color:'var(--cream)', fontWeight:700, letterSpacing:'0.1em', textTransform:'uppercase' }}>
              Ver detalhes
            </span>
          </div>
        )}
      </div>

      {/* Gradiente inferior */}
      <div style={{ position:'absolute', bottom:0, left:0, right:0, height:110, background:'linear-gradient(transparent, var(--bg-card))' }}/>

      {/* Badge duração */}
      <div style={{ position:'absolute', top:10, right:10 }}>
        <span style={{ fontSize:11, fontWeight:700, background:'rgba(0,0,0,0.75)', backdropFilter:'blur(8px)', border:`1px solid ${pal.glow}44`, color:pal.glow, borderRadius:999, padding:'3px 9px' }}>
          ⏱ {filme.duracao}min
        </span>
      </div>

      {/* Badge nota */}
      {filme.nota > 0 && (
        <div style={{ position:'absolute', top:10, left:10 }}>
          <span style={{ fontSize:11, fontWeight:800, background:'rgba(234,184,74,0.18)', backdropFilter:'blur(8px)', border:'1px solid rgba(234,184,74,0.40)', color:'#eab84a', borderRadius:999, padding:'3px 9px' }}>
            ★ {filme.nota.toFixed(1)}
          </span>
        </div>
      )}
    </div>
  );
}

// ── MODAL DE DETALHES ─────────────────────────────────────────────────────────

function ModalDetalhes({
  filme,
  sessoes,
  onClose,
  onComprar,
  isCritico,
  usuario,
  msgs,
  notas,
  textos,
  setNotas,
  setTextos,
  onEnviarNota,
  onEnviarCritica
}) {
  const [trailerAberto, setTrailerAberto] = useState(false);
  const [abaSelecionada, setAbaSelecionada] = useState('info');
  const [criticas, setCriticas] = useState([]);
  const [loadCriticas, setLoadCriticas] = useState(false);

  const trailerEmbedId = getTrailerEmbedId(filme.nome);
  const pal = filmePalette(filme.nome);
  const urls = getFilmeImageUrls(filme);
  const [posterSrc, onPosterError] = useImageFallback(urls);

  const embedSrc = trailerEmbedId
    ? `https://www.youtube-nocookie.com/embed/${trailerEmbedId}?autoplay=1&rel=0&modestbranding=1`
    : null;

  const filmeMsg = msgs[filme.id];

  const carregarCriticas = useCallback(() => {
    setLoadCriticas(true);
    api.get(`/filmes/${filme.id}/criticas`)
      .then(r => setCriticas(r.data || []))
      .catch(() => setCriticas([]))
      .finally(() => setLoadCriticas(false));
  }, [filme.id]);

  useEffect(() => {
    const h = e => {
      if (e.key === 'Escape') onClose();
    };

    window.addEventListener('keydown', h);
    return () => window.removeEventListener('keydown', h);
  }, [onClose]);

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = '';
    };
  }, []);

  useEffect(() => {
    if (abaSelecionada === 'criticas') {
      carregarCriticas();
    }
  }, [abaSelecionada, carregarCriticas]);

  const handlePublicarCritica = async () => {
    await onEnviarCritica(filme.id);
    carregarCriticas();
  };

  return (
    <div
      style={{
        position:'fixed',
        inset:0,
        zIndex:500,
        background:'rgba(0,0,0,0.93)',
        backdropFilter:'blur(24px)',
        display:'flex',
        alignItems:'center',
        justifyContent:'center',
        padding:'20px 16px',
        animation:'fadeIn 0.24s ease both',
        overflowY:'auto',
      }}
      onClick={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div style={{
        width:'100%',
        maxWidth:920,
        background:'var(--bg-card)',
        border:`1px solid ${pal.glow}28`,
        borderRadius:24,
        overflow:'hidden',
        boxShadow:`var(--shadow-lg), 0 0 0 1px ${pal.glow}18, 0 0 100px ${pal.glow}0a`,
        animation:'scaleIn 0.35s cubic-bezier(.22,.68,0,1.15) both',
        position:'relative',
        maxHeight:'calc(100vh - 40px)',
        display:'flex',
        flexDirection:'column',
      }}>
        <div style={{
          height:3,
          background:`linear-gradient(90deg, transparent, ${pal.glow}80, ${pal.glow}, ${pal.glow}80, transparent)`,
          flexShrink:0
        }} />

        <button
          onClick={onClose}
          style={{
            position:'absolute',
            top:18,
            right:18,
            zIndex:10,
            width:36,
            height:36,
            borderRadius:'50%',
            background:'rgba(0,0,0,0.65)',
            backdropFilter:'blur(8px)',
            border:'1px solid var(--border)',
            color:'var(--cream-muted)',
            fontSize:17,
            display:'flex',
            alignItems:'center',
            justifyContent:'center',
            cursor:'pointer',
            transition:'all 0.15s',
          }}
          onMouseEnter={e => {
            e.currentTarget.style.background = 'rgba(180,48,74,0.55)';
            e.currentTarget.style.color = '#fff';
          }}
          onMouseLeave={e => {
            e.currentTarget.style.background = 'rgba(0,0,0,0.65)';
            e.currentTarget.style.color = 'var(--cream-muted)';
          }}
        >
          ✕
        </button>

        <div style={{ display:'flex', gap:0, flexShrink:0 }}>
          <div style={{
            width:230,
            flexShrink:0,
            minHeight:300,
            background: posterSrc ? '#000' : `linear-gradient(145deg, ${pal.from} 0%, ${pal.to} 100%)`,
            position:'relative',
            overflow:'hidden',
            display:'flex',
            alignItems:'center',
            justifyContent:'center',
          }}>
            {posterSrc ? (
              <img
                src={posterSrc}
                alt={filme.nome}
                onError={onPosterError}
                style={{
                  width:'100%',
                  height:'100%',
                  objectFit:'cover',
                  position:'absolute',
                  inset:0
                }}
              />
            ) : (
              <span style={{
                fontFamily:'var(--font-display)',
                fontStyle:'italic',
                fontSize:80,
                fontWeight:900,
                color:pal.glow,
                opacity:0.22
              }}>
                {filme.nome[0]}
              </span>
            )}

            <div style={{
              position:'absolute',
              inset:0,
              background:'linear-gradient(to right, transparent 55%, var(--bg-card))'
            }} />
          </div>

          <div style={{
            flex:1,
            padding:'30px 30px 20px 26px',
            display:'flex',
            flexDirection:'column',
            gap:14,
            minWidth:0
          }}>
            <div>
              <h1 style={{
                fontFamily:'var(--font-display)',
                fontSize:28,
                lineHeight:1.16,
                fontStyle:'italic',
                marginBottom:10,
                paddingRight:44
              }}>
                {filme.nome}
              </h1>

              <Estrelas nota={filme.nota || 0} total={filme.quantidadeCriticos || 0} />
            </div>

            <div style={{ display:'flex', gap:8, flexWrap:'wrap', alignItems:'center' }}>
              <span style={{
                fontSize:12,
                color:'var(--cream-dim)',
                background:'rgba(255,255,255,0.05)',
                border:'1px solid var(--border-soft)',
                borderRadius:6,
                padding:'3px 9px'
              }}>
                ⏱ {filme.duracao} min
              </span>

              <span style={{
                fontSize:12,
                color:'var(--cream-dim)',
                background:'rgba(255,255,255,0.05)',
                border:'1px solid var(--border-soft)',
                borderRadius:6,
                padding:'3px 9px'
              }}>
                💰 R$ {filme.valor?.toFixed(2)}
              </span>

              {salasUnicasDasSessoes(sessoes).slice(0, 3).map(sala => (
                <span
                  key={`${sala.id || sala.nome}-${sala.tipo}`}
                  style={{
                    fontSize:11,
                    fontWeight:800,
                    color:'var(--gold-light)',
                    background:'var(--gold-dim)',
                    border:'1px solid var(--gold-border)',
                    borderRadius:999,
                    padding:'3px 10px',
                  }}
                >
                  {salaLabelCompleta(sala)}
                </span>
              ))}
            </div>

            <p style={{
              fontSize:13.5,
              color:'var(--cream-muted)',
              lineHeight:1.68,
              flex:1
            }}>
              {filme.sinopse}
            </p>

            {!trailerAberto && embedSrc && (
              <button
                onClick={() => setTrailerAberto(true)}
                className="btn-primary"
                style={{
                  display:'inline-flex',
                  alignItems:'center',
                  gap:10,
                  padding:'10px 22px',
                  fontSize:13.5,
                  alignSelf:'flex-start',
                  borderRadius:10
                }}
              >
                <span style={{ fontSize:20, lineHeight:1 }}>▶</span>
                Assistir trailer
              </button>
            )}

            {!trailerAberto && !embedSrc && (
              <div style={{
                display:'inline-flex',
                alignItems:'center',
                gap:8,
                fontSize:12,
                color:'var(--cream-dim)',
                background:'rgba(255,255,255,0.03)',
                border:'1px solid var(--border-soft)',
                borderRadius:8,
                padding:'8px 14px',
                alignSelf:'flex-start',
              }}>
                <span>📽️</span>
                <span>Trailer não disponível para este título</span>
              </div>
            )}
          </div>
        </div>

        {trailerAberto && embedSrc && (
          <div style={{ position:'relative', flexShrink:0, background:'#000' }}>
            <div style={{ paddingBottom:'56.25%', position:'relative' }}>
              <iframe
                src={embedSrc}
                title="Trailer"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                style={{
                  position:'absolute',
                  inset:0,
                  width:'100%',
                  height:'100%',
                  border:'none'
                }}
              />
            </div>

            <button
              onClick={() => setTrailerAberto(false)}
              style={{
                position:'absolute',
                top:10,
                right:10,
                background:'rgba(0,0,0,0.80)',
                border:'1px solid var(--border)',
                color:'var(--cream-muted)',
                borderRadius:8,
                padding:'6px 14px',
                cursor:'pointer',
                fontSize:12,
                fontWeight:700,
                backdropFilter:'blur(6px)',
              }}
            >
              ✕ Fechar trailer
            </button>
          </div>
        )}

        <div style={{ display:'flex', borderBottom:'1px solid var(--border)', flexShrink:0 }}>
          {[
            { key:'info', label:'🎬 Sessões' },
            { key:'criticas', label:`💬 Críticas${filme.quantidadeCriticos > 0 ? ` (${filme.quantidadeCriticos})` : ''}` },
          ].map(aba => (
            <button
              key={aba.key}
              onClick={() => setAbaSelecionada(aba.key)}
              style={{
                flex:1,
                padding:'14px 16px',
                border:'none',
                borderRadius:0,
                background: abaSelecionada === aba.key ? 'rgba(201,144,40,0.08)' : 'transparent',
                color: abaSelecionada === aba.key ? 'var(--gold-light)' : 'var(--cream-dim)',
                fontSize:13,
                fontWeight: abaSelecionada === aba.key ? 700 : 500,
                borderBottom: abaSelecionada === aba.key ? '2px solid var(--gold)' : '2px solid transparent',
                cursor:'pointer',
                transition:'all 0.15s',
              }}
            >
              {aba.label}
            </button>
          ))}
        </div>

        <div style={{ overflowY:'auto', flex:1, padding:'20px 24px 24px' }}>
          {abaSelecionada === 'info' && (
            <div style={{ display:'flex', flexDirection:'column', gap:10 }}>
              {sessoes.length === 0 ? (
                <p style={{ textAlign:'center', color:'var(--cream-dim)', padding:'28px 0' }}>
                  Nenhuma sessão disponível.
                </p>
              ) : sessoes.map(s => {
                const preco = (filme.valor || 0) * (MULT[s.sala?.tipo] || 1.0);
                const total = 150;
                const ocupados = Array.isArray(s.cadeiras)
                  ? s.cadeiras.flat().filter(Boolean).length
                  : null;
                const livres = ocupados !== null ? total - ocupados : null;

                return (
                  <div
                    key={s.id}
                    style={{
                      display:'flex',
                      alignItems:'center',
                      justifyContent:'space-between',
                      padding:'14px 18px',
                      borderRadius:13,
                      background:'rgba(255,255,255,0.025)',
                      border:'1px solid var(--border-soft)',
                      gap:12,
                      flexWrap:'wrap',
                      transition:'border-color 0.15s, background 0.15s',
                    }}
                    onMouseEnter={e => {
                      e.currentTarget.style.borderColor = 'var(--gold-border)';
                      e.currentTarget.style.background = 'var(--gold-dim)';
                    }}
                    onMouseLeave={e => {
                      e.currentTarget.style.borderColor = 'var(--border-soft)';
                      e.currentTarget.style.background = 'rgba(255,255,255,0.025)';
                    }}
                  >
                    <div style={{ display:'flex', alignItems:'center', gap:12, flexWrap:'wrap' }}>
                      <span style={{
                        fontSize:11,
                        fontWeight:800,
                        color:'var(--gold-light)',
                        background:'var(--gold-dim)',
                        border:'1px solid var(--gold-border)',
                        borderRadius:999,
                        padding:'3px 10px',
                      }}>
                        {salaLabelCompleta(s.sala)}
                      </span>

                      <span style={{ fontSize:13, fontWeight:700, color:'var(--cream)', fontVariantNumeric:'tabular-nums' }}>
                        {s.horario}
                      </span>

                      {livres !== null && (
                        <span style={{
                          fontSize:11,
                          color: livres < 10 ? '#f87171' : 'var(--green-text)',
                          background: livres < 10 ? 'rgba(248,113,113,0.10)' : 'rgba(40,151,106,0.10)',
                          border:`1px solid ${livres < 10 ? 'rgba(248,113,113,0.3)' : 'rgba(40,151,106,0.3)'}`,
                          borderRadius:6,
                          padding:'2px 8px',
                        }}>
                          {livres} lugar{livres !== 1 ? 'es' : ''} livres
                        </span>
                      )}
                    </div>

                    <div style={{ display:'flex', alignItems:'center', gap:14 }}>
                      <span style={{
                        fontSize:14,
                        fontWeight:800,
                        color:'var(--gold-light)',
                        fontVariantNumeric:'tabular-nums'
                      }}>
                        R$ {preco.toFixed(2)}
                      </span>

                      <button
                        className="btn-primary"
                        style={{ padding:'8px 18px', fontSize:13 }}
                        onClick={() => onComprar(s.id)}
                      >
                        Comprar →
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {abaSelecionada === 'criticas' && (
            <div style={{ display:'flex', flexDirection:'column', gap:14 }}>
              {isCritico && (
                <div style={{
                  background:'linear-gradient(135deg, rgba(201,144,40,0.06) 0%, rgba(201,144,40,0.02) 100%)',
                  border:'1px solid var(--gold-border)',
                  borderRadius:16,
                  padding:'18px 20px',
                  display:'flex',
                  flexDirection:'column',
                  gap:14,
                }}>
                  <div style={{ display:'flex', alignItems:'center', gap:10 }}>
                    <div style={{
                      width:36,
                      height:36,
                      borderRadius:'50%',
                      background:'linear-gradient(135deg, var(--gold) 0%, var(--gold-light) 100%)',
                      display:'flex',
                      alignItems:'center',
                      justifyContent:'center',
                      fontSize:16,
                      flexShrink:0,
                      boxShadow:'0 0 16px rgba(201,144,40,0.40)',
                    }}>
                      🎬
                    </div>

                    <div>
                      <p style={{ margin:0, fontWeight:700, fontSize:14, color:'var(--cream)' }}>
                        {usuario?.user}
                        <span style={{
                          marginLeft:8,
                          fontSize:10,
                          background:'var(--gold-dim)',
                          border:'1px solid var(--gold-border)',
                          borderRadius:999,
                          padding:'2px 8px',
                          color:'var(--gold-light)',
                          verticalAlign:'middle',
                        }}>
                          Crítico · {usuario?.origem || 'Independente'}
                        </span>
                      </p>

                      <p style={{ margin:0, fontSize:11.5, color:'var(--cream-dim)' }}>
                        Sua avaliação como crítico
                      </p>
                    </div>
                  </div>

                  <div>
                    <label style={{
                      display:'block',
                      fontSize:11.5,
                      fontWeight:700,
                      textTransform:'uppercase',
                      letterSpacing:'0.07em',
                      color:'var(--cream-dim)',
                      marginBottom:8,
                    }}>
                      Sua nota
                    </label>

                    <div style={{ display:'flex', alignItems:'center', gap:6 }}>
                      {[2, 4, 6, 8, 10].map(val => {
                        const atual = parseFloat(notas[filme.id]) || 0;
                        const ativo = atual >= val;

                        return (
                          <button
                            key={val}
                            onClick={() => setNotas(p => ({ ...p, [filme.id]: String(val) }))}
                            style={{
                              background:'none',
                              border:'none',
                              padding:'2px 3px',
                              cursor:'pointer',
                              fontSize:26,
                              lineHeight:1,
                              color: ativo ? '#eab84a' : 'rgba(234,184,74,0.15)',
                              transition:'transform 0.12s, color 0.12s',
                              filter: ativo ? 'drop-shadow(0 0 6px rgba(234,184,74,0.55))' : 'none',
                            }}
                            onMouseEnter={e => e.currentTarget.style.transform = 'scale(1.28)'}
                            onMouseLeave={e => e.currentTarget.style.transform = 'scale(1)'}
                            title={`${val}/10`}
                          >
                            ★
                          </button>
                        );
                      })}

                      {parseFloat(notas[filme.id]) > 0 && (
                        <span style={{
                          marginLeft:6,
                          fontSize:14,
                          fontWeight:800,
                          color:'var(--gold-light)',
                          fontVariantNumeric:'tabular-nums',
                        }}>
                          {parseFloat(notas[filme.id]).toFixed(1)}/10
                        </span>
                      )}

                      {parseFloat(notas[filme.id]) > 0 && (
                        <button
                          className="btn-primary"
                          style={{ marginLeft:'auto', padding:'8px 18px', fontSize:12.5 }}
                          onClick={() => onEnviarNota(filme.id)}
                        >
                          Registrar nota →
                        </button>
                      )}
                    </div>
                  </div>

                  <div>
                    <label style={{
                      display:'block',
                      fontSize:11.5,
                      fontWeight:700,
                      textTransform:'uppercase',
                      letterSpacing:'0.07em',
                      color:'var(--cream-dim)',
                      marginBottom:8,
                    }}>
                      Escrever crítica
                    </label>

                    <div style={{
                      background:'var(--bg-input)',
                      border:'1px solid var(--border)',
                      borderRadius:12,
                      overflow:'hidden',
                      transition:'border-color 0.15s, box-shadow 0.15s',
                    }}>
                      <textarea
                        rows={4}
                        maxLength={1000}
                        placeholder={`O que você achou de "${filme.nome}"? Compartilhe sua análise…`}
                        style={{
                          width:'100%',
                          background:'transparent',
                          border:'none',
                          color:'var(--cream)',
                          fontFamily:'var(--font-body)',
                          fontSize:13.5,
                          lineHeight:1.65,
                          padding:'14px 16px 8px',
                          resize:'none',
                          outline:'none',
                          boxSizing:'border-box',
                        }}
                        value={textos[filme.id] || ''}
                        onChange={e => setTextos(p => ({ ...p, [filme.id]: e.target.value }))}
                      />

                      <div style={{
                        display:'flex',
                        alignItems:'center',
                        justifyContent:'space-between',
                        padding:'8px 12px 10px',
                        borderTop:'1px solid var(--border-soft)',
                      }}>
                        <span style={{ fontSize:11.5, color:'var(--cream-dim)' }}>
                          {(textos[filme.id] || '').length}/1000 chars
                        </span>

                        <button
                          className="btn-primary"
                          style={{ padding:'8px 20px', fontSize:13, borderRadius:8 }}
                          disabled={!(textos[filme.id] || '').trim()}
                          onClick={handlePublicarCritica}
                        >
                          Publicar →
                        </button>
                      </div>
                    </div>
                  </div>

                  {filmeMsg && (
                    <p className={filmeMsg.ok ? 'flash-ok' : 'flash-err'} style={{ margin:0 }}>
                      {filmeMsg.texto}
                    </p>
                  )}
                </div>
              )}

              {isCritico && (
                <div style={{ display:'flex', alignItems:'center', gap:10 }}>
                  <div style={{ flex:1, height:1, background:'var(--border-soft)' }} />
                  <span style={{ fontSize:11, color:'var(--cream-dim)', letterSpacing:'0.08em' }}>
                    CRÍTICAS PUBLICADAS
                  </span>
                  <div style={{ flex:1, height:1, background:'var(--border-soft)' }} />
                </div>
              )}

              {loadCriticas ? (
                <div style={{ textAlign:'center', padding:'32px 0' }}>
                  <div style={{
                    display:'inline-block',
                    width:28,
                    height:28,
                    borderRadius:'50%',
                    border:'2px solid var(--border)',
                    borderTopColor:'var(--gold)',
                    animation:'spin 0.8s linear infinite',
                  }} />
                </div>
              ) : criticas.length === 0 ? (
                <div style={{ textAlign:'center', padding:'32px 0', color:'var(--cream-dim)' }}>
                  <p style={{ fontSize:36, marginBottom:12 }}>🎬</p>
                  <p style={{ fontSize:13.5 }}>
                    {isCritico ? 'Seja o primeiro a publicar uma crítica acima.' : 'Nenhuma crítica publicada ainda.'}
                  </p>
                </div>
              ) : criticas.map((c, i) => (
                <div
                  key={c.id || i}
                  style={{
                    background:'rgba(255,255,255,0.022)',
                    border:'1px solid var(--border-soft)',
                    borderRadius:14,
                    padding:'16px 20px',
                    transition:'border-color 0.15s',
                  }}
                  onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--border)'}
                  onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--border-soft)'}
                >
                  <div style={{ display:'flex', alignItems:'center', gap:10, marginBottom:10 }}>
                    <div style={{
                      width:36,
                      height:36,
                      borderRadius:'50%',
                      background:'linear-gradient(135deg, #1a0e38, #4a1580)',
                      border:'1px solid rgba(168,85,247,0.30)',
                      display:'flex',
                      alignItems:'center',
                      justifyContent:'center',
                      fontSize:15,
                      flexShrink:0,
                    }}>
                      🎬
                    </div>

                    <div style={{ flex:1, minWidth:0 }}>
                      <p style={{ margin:0, fontWeight:700, fontSize:13.5, color:'var(--cream)' }}>
                        {c.nomeAutor || 'Crítico'}
                      </p>

                      {c.origem && (
                        <p style={{ margin:0, fontSize:11, color:'var(--cream-dim)' }}>
                          <span style={{ marginRight:5 }}>📰</span>
                          {c.origem}
                        </p>
                      )}
                    </div>

                    <span style={{
                      fontSize:9.5,
                      fontWeight:700,
                      background:'rgba(168,85,247,0.12)',
                      border:'1px solid rgba(168,85,247,0.28)',
                      color:'#c084fc',
                      borderRadius:999,
                      padding:'3px 9px',
                      flexShrink:0,
                      display:'inline-flex',
                      alignItems:'center',
                      gap:4,
                    }}>
                      🎬 Crítico
                    </span>
                  </div>

                  <div style={{ height:1, background:'var(--border-soft)', marginBottom:10 }} />

                  <p style={{
                    fontSize:13.5,
                    color:'var(--cream-muted)',
                    lineHeight:1.70,
                    margin:0,
                    fontStyle:'italic',
                  }}>
                    "{c.mensagem}"
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ── CARD DE FILME ─────────────────────────────────────────────────────────────

function CardFilme({ filme, sessoesDisponiveis, index, onAbrirModal }) {
  const pal = filmePalette(filme.nome);
  const [hov, setHov] = useState(false);
  const temTrailer = !!getTrailerEmbedId(filme.nome);

  return (
    <div
      className="card"
      style={{
        overflow:'hidden', display:'flex', flexDirection:'column', cursor:'pointer',
        transition:'transform 0.28s cubic-bezier(.22,.68,0,1.2), box-shadow 0.28s ease',
        boxShadow:'0 8px 32px rgba(0,0,0,0.32)',
        animation:`fadeUp 0.5s ease ${index * 0.055}s both`,
        position:'relative',
      }}
      onClick={() => onAbrirModal(filme)}
      onMouseEnter={e => {
        setHov(true);
        e.currentTarget.style.transform = 'translateY(-9px)';
        e.currentTarget.style.boxShadow = `0 36px 90px rgba(0,0,0,0.60), 0 0 0 1px ${pal.glow}44`;
      }}
      onMouseLeave={e => {
        setHov(false);
        e.currentTarget.style.transform = 'translateY(0)';
        e.currentTarget.style.boxShadow = '0 8px 32px rgba(0,0,0,0.32)';
      }}
    >
      {/* Linha colorida topo */}
      <div style={{
        height:3,
        background:`linear-gradient(90deg, ${pal.glow}00, ${pal.glow}dd, ${pal.glow}00)`,
        opacity: hov ? 1 : 0.42,
        transition:'opacity 0.3s',
      }}/>

      <PosterFilme filme={filme} onClick={() => onAbrirModal(filme)} height={280} />

      <div style={{ padding:'18px 20px 22px', flex:1, display:'flex', flexDirection:'column', gap:10 }}>
        <div>
          <h2 style={{ fontFamily:'var(--font-display)', fontSize:17.5, marginBottom:6, lineHeight:1.22 }}>{filme.nome}</h2>
          <Estrelas nota={filme.nota || 0} total={filme.quantidadeCriticos || 0} />
        </div>

        <p style={{ fontSize:12.5, color:'var(--cream-dim)', lineHeight:1.57, flex:1 }}>
          {filme.sinopse?.length > 108 ? filme.sinopse.slice(0, 108) + '…' : filme.sinopse}
        </p>

        {/* Mini-prévia de sessões (até 3) */}
        {sessoesDisponiveis && sessoesDisponiveis.length > 0 && (
          <div style={{ display:'flex', gap:5, flexWrap:'wrap' }}>
            {sessoesDisponiveis.slice(0, 3).map(s => (
              <span key={s.id} style={{
                fontSize:10.5, fontVariantNumeric:'tabular-nums',
                background:'rgba(255,255,255,0.04)',
                border:'1px solid var(--border-soft)',
                borderRadius:6, padding:'2px 8px',
                color:'var(--cream-dim)',
                display:'inline-flex', alignItems:'center', gap:4,
              }}>
                <span style={{ fontSize:9 }}>{SALA_LABEL[s.sala?.tipo] || ''}</span>
                {s.horario}
              </span>
            ))}
            {sessoesDisponiveis.length > 3 && (
              <span style={{ fontSize:10.5, color:'var(--cream-dim)', padding:'2px 6px' }}>+{sessoesDisponiveis.length - 3}</span>
            )}
          </div>
        )}

        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginTop:2 }}>
          <span style={{ fontSize:14, color:'var(--gold-light)', fontWeight:800, fontVariantNumeric:'tabular-nums' }}>
            a partir de R$ {filme.valor?.toFixed(2)}
          </span>
          <span style={{
            fontSize:11.5, color:pal.glow, fontWeight:700,
            background:`${pal.glow}18`, border:`1px solid ${pal.glow}38`,
            borderRadius:999, padding:'3px 10px',
            display:'inline-flex', alignItems:'center', gap:5,
          }}>
            {temTrailer && <span style={{ fontSize:10 }}>▶</span>}
            {temTrailer ? 'Trailer + sessões' : 'Ver sessões'} →
          </span>
        </div>
      </div>
    </div>
  );
}

// ── PARTÍCULAS HERO ───────────────────────────────────────────────────────────

function HeroParticles() {
  const particles = Array.from({ length:26 }, (_, i) => ({
    id:i,
    left:`${(i * 4.9 + 2) % 96}%`,
    top:`${(i * 6.8 + 8) % 78}%`,
    delay:`${(i * 0.34) % 4.2}s`,
    dur:`${2.8 + (i % 4) * 0.7}s`,
    size: i % 5 === 0 ? 4 : i % 3 === 0 ? 3 : 2,
    opacity: 0.08 + (i % 6) * 0.038,
  }));
  return (
    <div style={{ position:'absolute', inset:0, pointerEvents:'none', overflow:'hidden' }}>
      {particles.map(p => (
        <div key={p.id} style={{
          position:'absolute', left:p.left, top:p.top,
          width:p.size, height:p.size, borderRadius:'50%',
          background:'var(--gold)', opacity:p.opacity,
          animation:`pulse ${p.dur} ease-in-out ${p.delay} infinite`,
        }}/>
      ))}
    </div>
  );
}

// ── BANNER FILME EM DESTAQUE ──────────────────────────────────────────────────

function FilmeDestaque({ filme, onAbrirModal }) {
  const pal = filmePalette(filme.nome);
  const urls = getFilmeImageUrls(filme);
  const [posterSrc, onPosterError] = useImageFallback(urls);
  const temTrailer = !!getTrailerEmbedId(filme.nome);
  const [hov, setHov] = useState(false);

  return (
    <div
      onMouseEnter={() => setHov(true)}
      onMouseLeave={() => setHov(false)}
      style={{
        position:'relative', borderRadius:22, overflow:'hidden',
        height:380, cursor:'pointer',
        background:`linear-gradient(135deg, ${pal.from} 0%, ${pal.to} 70%)`,
        border:`1px solid ${pal.glow}22`,
        boxShadow: hov ? `0 28px 90px rgba(0,0,0,0.65), 0 0 0 1px ${pal.glow}44` : '0 14px 56px rgba(0,0,0,0.50)',
        transition:'box-shadow 0.3s ease',
      }}
      onClick={() => onAbrirModal(filme)}
    >
      {/* Imagem de fundo — ocupa toda a área, com fade controlado por máscara */}
      {posterSrc && (
        <div style={{
          position:'absolute', top:0, right:0,
          height:'100%', width:'62%',
          WebkitMaskImage:'linear-gradient(to left, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.7) 35%, transparent 80%)',
          maskImage:'linear-gradient(to left, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.7) 35%, transparent 80%)',
          transition:'opacity 0.45s ease',
          opacity: hov ? 0.80 : 0.62,
        }}>
          <img src={posterSrc} alt={filme.nome} onError={onPosterError}
            style={{
              width:'100%', height:'100%',
              objectFit:'cover', objectPosition:'top center',
              display:'block',
            }}
          />
        </div>
      )}

      {/* Gradientes de composição */}
      <div style={{ position:'absolute', inset:0, background:`linear-gradient(90deg, ${pal.from}f5 28%, ${pal.from}99 55%, transparent 80%)` }}/>
      <div style={{ position:'absolute', inset:0, background:'linear-gradient(to top, rgba(0,0,0,0.72) 0%, transparent 50%)' }}/>

      {/* Brilho ambiente */}
      <div style={{ position:'absolute', top:'50%', left:'28%', width:360, height:360, borderRadius:'50%', background:pal.glow, opacity:0.07, filter:'blur(100px)', transform:'translate(-50%,-50%)' }}/>

      {/* Linha topo */}
      <div style={{ position:'absolute', top:0, left:0, right:0, height:2, background:`linear-gradient(90deg, transparent, ${pal.glow}88, ${pal.glow}, ${pal.glow}88, transparent)` }}/>

      {/* Conteúdo */}
      <div style={{ position:'relative', height:'100%', maxWidth:600, padding:'38px 48px', display:'flex', flexDirection:'column', justifyContent:'center', gap:13 }}>
        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
          <div style={{ width:28, height:1, background:`linear-gradient(90deg, ${pal.glow}, transparent)` }}/>
          <p style={{ fontSize:10, letterSpacing:'0.34em', textTransform:'uppercase', color:pal.glow, fontWeight:700, margin:0 }}>Em destaque</p>
        </div>

        <h2 style={{
          fontFamily:'var(--font-display)', fontStyle:'italic',
          fontSize:'clamp(28px, 4vw, 46px)', lineHeight:1.08,
          color:'var(--cream)',
          textShadow:'0 2px 24px rgba(0,0,0,0.55)',
        }}>
          {filme.nome}
        </h2>

        {filme.nota > 0 && <Estrelas nota={filme.nota} total={filme.quantidadeCriticos || 0} />}

        <p style={{ fontSize:14, color:'rgba(242,237,224,0.78)', lineHeight:1.62, maxWidth:420 }}>
          {filme.sinopse?.length > 145 ? filme.sinopse.slice(0, 145) + '…' : filme.sinopse}
        </p>

        <div style={{ display:'flex', gap:10, flexWrap:'wrap', marginTop:4 }}>
          <button className="btn-primary" style={{ padding:'11px 26px', fontSize:14 }}
            onClick={e => { e.stopPropagation(); onAbrirModal(filme); }}>
            Ver sessões →
          </button>
          {temTrailer && (
            <button className="btn-ghost" style={{ padding:'10px 20px', fontSize:14 }}
              onClick={e => { e.stopPropagation(); onAbrirModal(filme); }}>
              ▶ Trailer
            </button>
          )}
        </div>
      </div>

      {/* Badge */}
      <div style={{ position:'absolute', top:18, right:18 }}>
        <span style={{
          fontSize:11, fontWeight:800,
          background:'rgba(0,0,0,0.68)', backdropFilter:'blur(8px)',
          border:`1px solid ${pal.glow}55`, color:pal.glow,
          borderRadius:999, padding:'4px 12px',
        }}>★ Mais avaliado</span>
      </div>
    </div>
  );
}

// ── PÁGINA PRINCIPAL ──────────────────────────────────────────────────────────

function ModalIngressos({ filmes, sessoes, loadSessao, onClose, onComprar, usuario }) {
  const [busca,    setBusca]    = useState('');
  const [filtro,   setFiltro]   = useState('TODAS');
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    const h = e => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', h);
    document.body.style.overflow = 'hidden';
    return () => { window.removeEventListener('keydown', h); document.body.style.overflow = ''; };
  }, [onClose]);

  const TIPOS_SALA = ['TODAS', 'COMUM', 'SALA_3D', 'XD', 'XD_3D'];
  const TIPO_LABEL = { TODAS: 'Todos', COMUM: 'Comum', SALA_3D: '3D', XD: 'XD', XD_3D: 'XD/3D' };

  // Achata todas as sessões com referência ao filme
  const todasSessoes = filmes.flatMap(f =>
    (sessoes[f.id] || []).map(s => ({ ...s, _filme: f }))
  );

  const sessoesFiltradas = todasSessoes.filter(s => {
    const matchBusca  = busca.trim() === '' || s._filme.nome.toLowerCase().includes(busca.toLowerCase());
    const matchFiltro = filtro === 'TODAS' || s.sala?.tipo === filtro;
    return matchBusca && matchFiltro;
  });

  // Agrupa por filme
  const agrupado = {};
  sessoesFiltradas.forEach(s => {
    const fid = s._filme.id;
    if (!agrupado[fid]) agrupado[fid] = { filme: s._filme, sessoes: [] };
    agrupado[fid].sessoes.push(s);
  });
  const grupos = Object.values(agrupado);

  return (
    <div
      style={{
        position: 'fixed', inset: 0, zIndex: 600,
        background: 'rgba(0,0,0,0.92)', backdropFilter: 'blur(28px)',
        display: 'flex', alignItems: 'flex-start', justifyContent: 'center',
        padding: '24px 16px', overflowY: 'auto',
        animation: 'fadeIn 0.22s ease both',
      }}
      onClick={e => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div style={{
        width: '100%', maxWidth: 720,
        background: 'var(--bg-card)',
        border: '1px solid var(--gold-border)',
        borderRadius: 24, overflow: 'hidden',
        boxShadow: 'var(--shadow-lg), 0 0 80px rgba(201,144,40,0.09)',
        animation: 'scaleIn 0.32s cubic-bezier(.22,.68,0,1.15) both',
        position: 'relative',
      }}>
        {/* Linha dourada topo */}
        <div style={{ height: 3, background: 'linear-gradient(90deg, transparent, var(--gold), var(--gold-light), var(--gold), transparent)' }} />

        {/* Header do modal */}
        <div style={{ padding: '26px 28px 20px', borderBottom: '1px solid var(--border-soft)' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 18 }}>
            <div>
              <h2 style={{ fontFamily: 'var(--font-display)', fontSize: 26, fontStyle: 'italic', color: 'var(--gold)', marginBottom: 2 }}>
                🎟 Ingressos
              </h2>
              <p style={{ fontSize: 12.5, color: 'var(--cream-dim)', margin: 0 }}>
                Encontre a sessão ideal e compre diretamente
              </p>
            </div>
            <button onClick={onClose} style={{
              width: 36, height: 36, borderRadius: '50%',
              background: 'rgba(0,0,0,0.5)', border: '1px solid var(--border)',
              color: 'var(--cream-muted)', fontSize: 17, cursor: 'pointer',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              transition: 'all 0.15s',
            }}
              onMouseEnter={e => { e.currentTarget.style.background = 'rgba(180,48,74,0.55)'; e.currentTarget.style.color = '#fff'; }}
              onMouseLeave={e => { e.currentTarget.style.background = 'rgba(0,0,0,0.5)'; e.currentTarget.style.color = 'var(--cream-muted)'; }}
            >✕</button>
          </div>

          {/* Busca */}
          <div style={{ position: 'relative', marginBottom: 12 }}>
            <span style={{ position: 'absolute', left: 13, top: '50%', transform: 'translateY(-50%)', color: 'var(--cream-dim)', fontSize: 14, pointerEvents: 'none' }}>🔍</span>
            <input
              className="inp"
              placeholder="Buscar por nome do filme…"
              value={busca}
              onChange={e => setBusca(e.target.value)}
              autoFocus
              style={{ paddingLeft: 38, height: 42, fontSize: 14 }}
            />
          </div>

          {/* Filtros de sala */}
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
            {TIPOS_SALA.map(t => (
              <button key={t} onClick={() => setFiltro(t)} style={{
                fontSize: 11.5, fontWeight: filtro === t ? 700 : 500,
                padding: '5px 13px', borderRadius: 999, cursor: 'pointer',
                border: filtro === t ? '1px solid var(--gold-border)' : '1px solid var(--border-soft)',
                background: filtro === t ? 'var(--gold-dim)' : 'rgba(255,255,255,0.03)',
                color: filtro === t ? 'var(--gold-light)' : 'var(--cream-dim)',
                transition: 'all 0.14s',
              }}>
                {TIPO_LABEL[t]}
              </button>
            ))}
          </div>
        </div>

        {/* Lista de sessões agrupadas */}
        <div style={{ maxHeight: '60vh', overflowY: 'auto', padding: '16px 20px 24px' }}>
          {grupos.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '48px 0', color: 'var(--cream-dim)' }}>
              <p style={{ fontSize: 36, marginBottom: 10 }}>🎬</p>
              <p style={{ fontSize: 14 }}>Nenhuma sessão encontrada.</p>
            </div>
          ) : grupos.map(({ filme, sessoes: slist }) => {
            const aberto = expanded === filme.id;
            const pal    = filmePalette(filme.nome);
            const urls   = getFilmeImageUrls(filme);

            return (
              <div key={filme.id} style={{ marginBottom: 10 }}>
                {/* Cabeçalho do grupo — clicável */}
                <button
                  onClick={() => setExpanded(aberto ? null : filme.id)}
                  style={{
                    width: '100%', background: aberto ? `${pal.glow}12` : 'rgba(255,255,255,0.025)',
                    border: `1px solid ${aberto ? pal.glow + '44' : 'var(--border-soft)'}`,
                    borderRadius: aberto ? '12px 12px 0 0' : 12,
                    padding: '13px 16px', cursor: 'pointer',
                    display: 'flex', alignItems: 'center', gap: 12,
                    transition: 'all 0.18s',
                  }}
                >
                  {/* Mini poster colorido */}
                  <FilmeMiniPoster filme={filme} urls={urls} pal={pal} />

                  <div style={{ flex: 1, textAlign: 'left', minWidth: 0 }}>
                    <p style={{ margin: 0, fontSize: 14, fontWeight: 700, color: 'var(--cream)', fontFamily: 'var(--font-display)', fontStyle: 'italic' }}>
                      {filme.nome}
                    </p>
                    <p style={{ margin: '2px 0 0', fontSize: 12, color: 'var(--cream-dim)' }}>
                      {slist.length} sessão{slist.length !== 1 ? 'ões' : ''} disponível{slist.length !== 1 ? 'is' : ''}
                      {' · '}a partir de <span style={{ color: 'var(--gold-light)', fontWeight: 700 }}>R$ {filme.valor?.toFixed(2)}</span>
                    </p>
                  </div>

                  <span style={{
                    fontSize: 18, color: aberto ? 'var(--gold-light)' : 'var(--cream-dim)',
                    transition: 'transform 0.2s, color 0.15s',
                    transform: aberto ? 'rotate(180deg)' : 'rotate(0deg)',
                    flexShrink: 0,
                  }}>⌄</span>
                </button>

                {/* Sessões expandidas */}
                {aberto && (
                  <div style={{
                    border: `1px solid ${pal.glow}30`, borderTop: 'none',
                    borderRadius: '0 0 12px 12px', overflow: 'hidden',
                    animation: 'fadeIn 0.16s ease both',
                  }}>
                    {slist.map(s => {
                      const mult  = MULT[s.sala?.tipo] ?? 1;
                      const preco = (filme.valor * mult).toFixed(2);
                      return (
                        <div key={s.id} style={{
                          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                          padding: '11px 16px', gap: 12, flexWrap: 'wrap',
                          background: 'rgba(255,255,255,0.015)',
                          borderBottom: '1px solid var(--border-soft)',
                          transition: 'background 0.12s',
                        }}
                          onMouseEnter={e => e.currentTarget.style.background = `${pal.glow}0d`}
                          onMouseLeave={e => e.currentTarget.style.background = 'rgba(255,255,255,0.015)'}
                        >
                          <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
                            <span style={{ fontSize: 14, fontWeight: 700, fontVariantNumeric: 'tabular-nums', color: 'var(--cream)' }}>
                              {s.horario}
                            </span>

                            <span style={{
                              fontSize:11,
                              fontWeight:800,
                              color:'var(--gold-light)',
                              background:'var(--gold-dim)',
                              border:'1px solid var(--gold-border)',
                              borderRadius:999,
                              padding:'3px 10px',
                            }}>
                              {salaLabelCompleta(s.sala)}
                            </span>
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                            <span style={{ fontSize: 13.5, fontWeight: 800, color: 'var(--gold-light)', fontVariantNumeric: 'tabular-nums' }}>
                              R$ {preco}
                            </span>
                            <button
                              className="btn-primary"
                              style={{ padding: '7px 18px', fontSize: 12.5 }}
                              onClick={() => { onClose(); onComprar(s.id); }}
                            >
                              Comprar →
                            </button>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

// Mini poster usado dentro do ModalIngressos
function FilmeMiniPoster({ filme, urls, pal }) {
  const [src, onError] = useImageFallback(urls);
  return (
    <div style={{
      width: 38, height: 54, borderRadius: 6, flexShrink: 0, overflow: 'hidden',
      background: src ? '#000' : `linear-gradient(145deg, ${pal.from}, ${pal.to})`,
      border: `1px solid ${pal.glow}30`,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      {src
        ? <img src={src} alt={filme.nome} onError={onError} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
        : <span style={{ fontFamily: 'var(--font-display)', fontSize: 20, color: pal.glow, opacity: 0.6, fontStyle: 'italic' }}>{filme.nome[0]}</span>
      }
    </div>
  );
}

function ModalMinhasCriticas({ usuario, onClose }) {
  const [criticas, setCriticas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const h = e => {
      if (e.key === 'Escape') onClose();
    };

    window.addEventListener('keydown', h);
    document.body.style.overflow = 'hidden';

    api.get(`/filmes/criticas/critico/${usuario.id}`)
      .then(r => setCriticas(r.data || []))
      .catch(() => setCriticas([]))
      .finally(() => setLoading(false));

    return () => {
      window.removeEventListener('keydown', h);
      document.body.style.overflow = '';
    };
  }, [usuario.id, onClose]);

  return (
    <div
      style={{
        position:'fixed',
        inset:0,
        zIndex:650,
        background:'rgba(0,0,0,0.90)',
        backdropFilter:'blur(26px)',
        display:'flex',
        alignItems:'flex-start',
        justifyContent:'center',
        padding:'28px 16px',
        overflowY:'auto',
      }}
      onClick={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="card" style={{ width:'100%', maxWidth:760, padding:0, overflow:'hidden' }}>
        <div style={{
          padding:'24px 28px',
          borderBottom:'1px solid var(--border-soft)',
          display:'flex',
          alignItems:'center',
          justifyContent:'space-between',
          gap:16,
        }}>
          <div>
            <h2 style={{ fontFamily:'var(--font-display)', fontSize:26, color:'var(--gold)', fontStyle:'italic' }}>
              Minhas críticas
            </h2>
            <p style={{ color:'var(--cream-dim)', fontSize:13 }}>
              Seu histórico pessoal de avaliações publicadas.
            </p>
          </div>

          <button className="btn-ghost" style={{ padding:'8px 14px' }} onClick={onClose}>
            Fechar
          </button>
        </div>

        <div style={{ padding:'22px 28px 28px', display:'flex', flexDirection:'column', gap:12 }}>
          {loading ? (
            <p style={{ color:'var(--cream-dim)' }}>Carregando críticas...</p>
          ) : criticas.length === 0 ? (
            <div style={{ textAlign:'center', padding:'36px 0', color:'var(--cream-dim)' }}>
              <p style={{ fontSize:38, marginBottom:10 }}>🎬</p>
              <p>Você ainda não publicou críticas.</p>
            </div>
          ) : criticas.map(c => (
            <div key={c.id} style={{
              border:'1px solid var(--border-soft)',
              borderRadius:14,
              padding:'16px 18px',
              background:'rgba(255,255,255,0.024)',
            }}>
              <div style={{ display:'flex', justifyContent:'space-between', gap:12, marginBottom:8 }}>
                <p style={{ color:'var(--cream)', fontWeight:800 }}>
                  {c.filmeNome || 'Filme fora de catálogo'}
                </p>

                <span style={{
                  fontSize:10,
                  color:'var(--gold-light)',
                  border:'1px solid var(--gold-border)',
                  background:'var(--gold-dim)',
                  borderRadius:999,
                  padding:'3px 9px',
                  fontWeight:800,
                  flexShrink:0,
                }}>
                  Crítica publicada
                </span>
              </div>

              {c.origem && (
                <p style={{ fontSize:12, color:'var(--cream-dim)', marginBottom:8 }}>
                  {c.origem}
                </p>
              )}

              <p style={{ color:'var(--cream-muted)', lineHeight:1.7, fontStyle:'italic' }}>
                "{c.mensagem}"
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function ModalProgramacao({ filmes, sessoes, onClose, onVerGrade, onIngressos, onDestaque }) {
  const totalSessoes = Object.values(sessoes).flat().length;
  const totalCriticas = filmes.reduce((acc, f) => acc + (f.quantidadeCriticos || 0), 0);

  useEffect(() => {
    const h = e => {
      if (e.key === 'Escape') onClose();
    };

    window.addEventListener('keydown', h);
    document.body.style.overflow = 'hidden';

    return () => {
      window.removeEventListener('keydown', h);
      document.body.style.overflow = '';
    };
  }, [onClose]);

  return (
    <div
      style={{
        position:'fixed',
        inset:0,
        zIndex:640,
        background:'rgba(0,0,0,0.88)',
        backdropFilter:'blur(24px)',
        display:'flex',
        alignItems:'center',
        justifyContent:'center',
        padding:'20px 16px',
      }}
      onClick={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="card" style={{ width:'100%', maxWidth:680, padding:'28px', overflow:'hidden' }}>
        <div style={{ display:'flex', justifyContent:'space-between', gap:16, marginBottom:22 }}>
          <div>
            <p style={{
              color:'var(--gold)',
              fontSize:10,
              letterSpacing:'0.18em',
              textTransform:'uppercase',
              fontWeight:800,
              marginBottom:8,
            }}>
              Guia rápido
            </p>

            <h2 style={{ fontFamily:'var(--font-display)', fontSize:30, color:'var(--cream)', fontStyle:'italic' }}>
              Programação
            </h2>

            <p style={{ color:'var(--cream-muted)', fontSize:13.5, marginTop:6 }}>
              Escolha como você quer explorar os filmes e sessões.
            </p>
          </div>

          <button className="btn-ghost" style={{ padding:'8px 14px', alignSelf:'flex-start' }} onClick={onClose}>
            Fechar
          </button>
        </div>

        <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fit, minmax(150px, 1fr))', gap:10, marginBottom:20 }}>
          {[
            { label:'Filmes em cartaz', value:filmes.length },
            { label:'Sessões carregadas', value:totalSessoes || '...' },
            { label:'Críticas', value:totalCriticas },
          ].map(item => (
            <div key={item.label} style={{
              border:'1px solid var(--border-soft)',
              borderRadius:14,
              padding:'14px 16px',
              background:'rgba(255,255,255,0.028)',
            }}>
              <p style={{ color:'var(--cream-dim)', fontSize:10, textTransform:'uppercase', letterSpacing:'0.08em' }}>
                {item.label}
              </p>
              <p style={{ color:'var(--gold-light)', fontSize:24, fontWeight:900, fontVariantNumeric:'tabular-nums' }}>
                {item.value}
              </p>
            </div>
          ))}
        </div>

        <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fit, minmax(190px, 1fr))', gap:12 }}>
          <button className="btn-primary" style={{ padding:'14px 16px', justifyContent:'flex-start' }} onClick={onVerGrade}>
            Ver grade completa →
          </button>

          <button className="btn-ghost" style={{ padding:'14px 16px', justifyContent:'flex-start' }} onClick={onIngressos}>
            Buscar sessões e ingressos
          </button>

          <button className="btn-ghost" style={{ padding:'14px 16px', justifyContent:'flex-start' }} onClick={onDestaque}>
            Ir para destaque
          </button>
        </div>
      </div>
    </div>
  );
}

export default function ProgramacaoPage({ usuario, onLogout }) {
  const navigate = useNavigate();

  const [filmes,      setFilmes]      = useState([]);
  const [sessoes,     setSessoes]     = useState({});
  const [carregando,  setCarregando]  = useState(true);
  const [modalFilme,  setModalFilme]  = useState(null);
  const [loadSessao,  setLoadSessao]  = useState({});
  const [showToast,   setShowToast]   = useState(false);
  const [notas,       setNotas]       = useState({});
  const [textos,      setTextos]      = useState({});
  const [msgs,        setMsgs]        = useState({});
  const [busca,       setBusca]       = useState('');
  const [filtroSala,  setFiltroSala]  = useState('TODAS');
  const [modalSobre,  setModalSobre]  = useState(false);
  const [scrolled,    setScrolled]    = useState(false);
  const [showBackTop, setShowBackTop] = useState(false);
  const [modalProgramacao, setModalProgramacao] = useState(false);

  const [modalIngressos, setModalIngressos] = useState(false);
  const [modalMinhasCriticas, setModalMinhasCriticas] = useState(false);
  const tipoRaw   = usuario?.dtype ?? usuario?.tipo ?? 'COMUM';
  const tipo      = tipoRaw
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toUpperCase()
    .trim();
  const isCritico = tipo === 'CRITICO';
  const isAdmin   = tipo === 'ADMINISTRADOR';

  const irParaGradeProgramacao = () => {
    setModalProgramacao(false);
    setBusca('');
    setFiltroSala('TODAS');

    requestAnimationFrame(() => {
      document.getElementById('programacao-completa')?.scrollIntoView({
        behavior:'smooth',
        block:'start',
      });
    });
  };

  const irParaDestaque = () => {
    setModalProgramacao(false);

    requestAnimationFrame(() => {
      document.getElementById('destaque-filme')?.scrollIntoView({
        behavior:'smooth',
        block:'center',
      });
    });
  };

  const abrirIngressosPeloGuia = () => {
    setModalProgramacao(false);
    setModalIngressos(true);
  };

  // Carregar filmes
  useEffect(() => {
    api.get('/filmes')
      .then(r => setFilmes(r.data))
      .catch(console.error)
      .finally(() => setCarregando(false));
  }, []);

  // Pré-carregar sessões de todos os filmes para miniaturas
// Pré-carrega sessões em lotes de 3 para não sobrecarregar o backend
  useEffect(() => {
    if (filmes.length === 0) return;
    const pendentes = filmes.filter(f => sessoes[f.id] === undefined && !loadSessao[f.id]);
    if (pendentes.length === 0) return;

    // Processa em lotes de 3 com intervalo de 300ms entre lotes
    const LOTE = 3;
    let loteIndex = 0;

    function carregarProximoLote() {
      const lote = pendentes.slice(loteIndex, loteIndex + LOTE);
      if (lote.length === 0) return;
      loteIndex += LOTE;

      lote.forEach(f => {
        setLoadSessao(p => ({ ...p, [f.id]: true }));
        api.get(`/sessoes/filme/${f.id}`)
          .then(r => setSessoes(p => ({ ...p, [f.id]: r.data })))
          .catch(() => setSessoes(p => ({ ...p, [f.id]: [] })))
          .finally(() => {
            setLoadSessao(p => ({ ...p, [f.id]: false }));
          });
      });

      if (loteIndex < pendentes.length) {
        setTimeout(carregarProximoLote, 320);
      }
    }

    carregarProximoLote();
  }, [filmes]);

  // Scroll events
  useEffect(() => {
    const fn = () => { setScrolled(window.scrollY > 12); setShowBackTop(window.scrollY > 440); };
    window.addEventListener('scroll', fn, { passive:true });
    return () => window.removeEventListener('scroll', fn);
  }, []);

  const handleAbrirModal = useCallback((filme) => {
    setModalFilme(filme);
    // Garantir que sessões estejam carregadas
    if (sessoes[filme.id] === undefined) {
      setLoadSessao(p => ({ ...p, [filme.id]: true }));
      api.get(`/sessoes/filme/${filme.id}`)
        .then(r => setSessoes(p => ({ ...p, [filme.id]: r.data })))
        .catch(() => setSessoes(p => ({ ...p, [filme.id]: [] })))
        .finally(() => setLoadSessao(p => ({ ...p, [filme.id]: false })));
    }
  }, [sessoes]);

  const handleComprar = sessaoId => {
    if (!usuario) {
      setShowToast(true);
      setTimeout(() => setShowToast(false), 5000);
      return;
    }
    navigate(`/comprar/${sessaoId}`);
  };

  const handleNota = async filmeId => {
    const n = parseFloat(notas[filmeId]);
    if (isNaN(n) || n < 0 || n > 10) {
      setMsgs(p => ({ ...p, [filmeId]: { texto:'Nota inválida (0–10).', ok:false } }));
      return;
    }
    try {
      await api.post(`/filmes/${filmeId}/nota`, { criticoId: usuario.id, nota: n });
      setMsgs(p => ({ ...p, [filmeId]: { texto:'Nota registrada!', ok:true } }));
      const r = await api.get('/filmes');
      setFilmes(r.data);
      if (modalFilme?.id === filmeId) setModalFilme(r.data.find(f => f.id === filmeId) || modalFilme);
    } catch {
      setMsgs(p => ({ ...p, [filmeId]: { texto:'Erro ao registrar nota.', ok:false } }));
    }
  };

  const handleCritica = async filmeId => {
    const txt = (textos[filmeId] || '').trim();
    if (!txt) { setMsgs(p => ({ ...p, [filmeId]: { texto:'Escreva a crítica antes de enviar.', ok:false } })); return; }
    try {
      await api.post(`/filmes/${filmeId}/critica`, {
        criticoId: usuario.id, nomeAutor: usuario.user,
        origem: usuario.origem || '', mensagem: txt,
      });
      setTextos(p => ({ ...p, [filmeId]: '' }));
      setMsgs(p => ({ ...p, [filmeId]: { texto:'Crítica publicada!', ok:true } }));
    } catch {
      setMsgs(p => ({ ...p, [filmeId]: { texto:'Erro ao enviar crítica.', ok:false } }));
    }
  };

  // Filtros de texto + tipo de sala
  const filmesFiltrados = filmes.filter(f => {
    const matchBusca = busca.trim() === '' || f.nome.toLowerCase().includes(busca.toLowerCase());
    if (!matchBusca) return false;
    if (filtroSala === 'TODAS') return true;
    const filmeSessoes = sessoes[f.id] || [];
    return filmeSessoes.some(s => s.sala?.tipo === filtroSala);
  });

  // Filme em destaque: mais avaliado
  const filmeDestaque = filmes.length > 0
    ? [...filmes].sort((a, b) => (b.nota || 0) - (a.nota || 0))[0]
    : null;

  const scrollToGrade = () => document.getElementById('grade-filmes')?.scrollIntoView({ behavior:'smooth', block:'start' });
  const scrollToTop   = () => window.scrollTo({ top:0, behavior:'smooth' });

  // Tipos de sala disponíveis nos filmes
  const tiposSala = ['TODAS', 'COMUM', 'SALA_3D', 'XD', 'XD_3D'];
  const tiposSalaLabel = { TODAS: 'Todos os tipos', COMUM: 'Comum', SALA_3D: '3D', XD: 'XD', XD_3D: 'XD/3D' };

  return (
    <div style={{ minHeight:'100vh', display:'flex', flexDirection:'column' }}>

      {/* ── Toast login necessário ── */}
      <div style={{
        position:'fixed', top:20, left:'50%',
        transform:`translateX(-50%) translateY(${showToast ? 0 : -90}px)`,
        opacity: showToast ? 1 : 0,
        transition:'all 0.35s cubic-bezier(.22,.68,0,1.3)',
        background:'var(--bg-raised)', border:'1px solid var(--gold-border)',
        borderRadius:50, padding:'12px 22px',
        color:'var(--cream)', fontSize:13, fontWeight:500,
        display:'flex', alignItems:'center', gap:10,
        boxShadow:'var(--shadow-lg)', zIndex:1000, whiteSpace:'nowrap',
        pointerEvents: showToast ? 'auto' : 'none',
      }}>
        <span>🔒</span>
        <span>Para comprar,</span>
        <Link to="/login"    style={{ color:'var(--gold-light)', fontWeight:700 }}>entre</Link>
        <span>ou</span>
        <Link to="/cadastro" style={{ color:'var(--gold-light)', fontWeight:700 }}>cadastre-se</Link>
        <span style={{ color:'var(--cream-dim)' }}>— é grátis!</span>
      </div>

      {/* ── Modais ── */}
      {modalFilme && (
        <ModalDetalhes
          filme={modalFilme}
          sessoes={sessoes[modalFilme.id] || []}
          onClose={() => setModalFilme(null)}
          onComprar={handleComprar}
          isCritico={isCritico}
          usuario={usuario}
          msgs={msgs} notas={notas} textos={textos}
          setNotas={setNotas} setTextos={setTextos}
          onEnviarNota={handleNota}
          onEnviarCritica={handleCritica}
        />
      )}
      {modalSobre && <ModalSobre onClose={() => setModalSobre(false)} />}

      {modalIngressos && (
        <ModalIngressos
          filmes={filmes}
          sessoes={sessoes}
          loadSessao={loadSessao}
          onClose={() => setModalIngressos(false)}
          onComprar={handleComprar}
          usuario={usuario}
        />
      )}

      {modalMinhasCriticas && usuario && (
        <ModalMinhasCriticas
          usuario={usuario}
          onClose={() => setModalMinhasCriticas(false)}
        />
      )}

      {modalProgramacao && (
        <ModalProgramacao
          filmes={filmes}
          sessoes={sessoes}
          onClose={() => setModalProgramacao(false)}
          onVerGrade={irParaGradeProgramacao}
          onIngressos={abrirIngressosPeloGuia}
          onDestaque={irParaDestaque}
        />
      )}

      {/* ── Botão voltar ao topo ── */}
      <button onClick={scrollToTop} title="Voltar ao topo" style={{
        position:'fixed', bottom:28, right:28, zIndex:200,
        width:46, height:46, borderRadius:'50%',
        background:'var(--bg-raised)', border:'1px solid var(--gold-border)',
        color:'var(--gold-light)', fontSize:18,
        display:'flex', alignItems:'center', justifyContent:'center',
        boxShadow:'0 4px 24px rgba(0,0,0,0.55)',
        opacity: showBackTop ? 1 : 0,
        transform: showBackTop ? 'translateY(0) scale(1)' : 'translateY(16px) scale(0.88)',
        transition:'all 0.3s cubic-bezier(.22,.68,0,1.2)',
        pointerEvents: showBackTop ? 'auto' : 'none',
        cursor:'pointer',
      }}>↑</button>

      {/* ── HEADER ── */}
      <header style={{
        position:'sticky', top:0, zIndex:100,
        background: scrolled ? 'rgba(6,5,14,0.98)' : 'rgba(6,5,14,0.92)',
        backdropFilter:'blur(32px)',
        borderBottom:'1px solid var(--border-soft)',
        padding:'0 28px',
        boxShadow: scrolled
          ? '0 4px 56px rgba(0,0,0,0.68), 0 1px 0 rgba(201,144,40,0.14)'
          : '0 2px 20px rgba(0,0,0,0.38)',
        transition:'box-shadow 0.3s ease, background 0.3s ease',
      }}>
        <div style={{ maxWidth:1240, margin:'0 auto', height:70, display:'flex', alignItems:'center', justifyContent:'space-between', gap:14 }}>

          <LogoCinema />

          {/* Busca */}
          <div style={{ flex:1, maxWidth:300, position:'relative' }}>
            <span style={{ position:'absolute', left:12, top:'50%', transform:'translateY(-50%)', color:'var(--cream-dim)', fontSize:14 }}>🔍</span>
            <input className="inp" placeholder="Buscar filme…"
              value={busca} onChange={e => setBusca(e.target.value)}
              style={{ paddingLeft:36, height:38, fontSize:13 }}
            />
          </div>

          {/* Nav funcional */}
          <nav style={{ display:'flex', alignItems:'center', gap:2 }}>
            {[
              { label: 'Programação', action: () => setModalProgramacao(true), title: 'Abrir guia da programação' },
              { label: 'Ingressos',   action: () => setModalIngressos(true), title: 'Buscar sessões rapidamente' },
              { label: 'Sobre',       action: () => setModalSobre(true), title: 'Sobre o Lumière Cinema' },
            ].map(item => (
              <button key={item.label} onClick={item.action} title={item.title} style={{
                background: 'none', border: 'none', padding: '8px 14px', fontSize: 13,
                color: 'var(--cream-muted)', fontWeight: 500, borderRadius: 8, cursor: 'pointer',
                transition: 'color 0.15s, background 0.15s',
              }}
                onMouseEnter={e => { e.currentTarget.style.color = 'var(--cream)'; e.currentTarget.style.background = 'rgba(255,255,255,0.06)'; }}
                onMouseLeave={e => { e.currentTarget.style.color = 'var(--cream-muted)'; e.currentTarget.style.background = 'none'; }}
              >
                {item.label}
              </button>
            ))}

            {isCritico && (
              <button
                onClick={() => setModalMinhasCriticas(true)}
                title="Ver minhas críticas publicadas"
                style={{
                  background:'none',
                  border:'none',
                  padding:'8px 14px',
                  fontSize:13,
                  color:'var(--cream-muted)',
                  fontWeight:500,
                  borderRadius:8,
                  cursor:'pointer',
                  transition:'color 0.15s, background 0.15s',
                }}
                onMouseEnter={e => {
                  e.currentTarget.style.color = 'var(--cream)';
                  e.currentTarget.style.background = 'rgba(255,255,255,0.06)';
                }}
                onMouseLeave={e => {
                  e.currentTarget.style.color = 'var(--cream-muted)';
                  e.currentTarget.style.background = 'none';
                }}
              >
                Minhas críticas
              </button>
            )}
          </nav>

          {/* Área do usuário */}
          <div style={{ display:'flex', alignItems:'center', gap:10, flexShrink:0 }}>
            {usuario ? (
              <>
                <div style={{
                  display:'flex', alignItems:'center', gap:8,
                  background:'var(--bg-raised)', border:'1px solid var(--border)',
                  borderRadius:50, padding:'5px 14px 5px 10px',
                  boxShadow:'0 2px 14px rgba(0,0,0,0.30)',
                }}>
                  <span style={{ width:7, height:7, borderRadius:'50%', background:'var(--green-text)', flexShrink:0, boxShadow:'0 0 6px var(--green-text)' }}/>
                  <span style={{ fontSize:13, fontWeight:600, color:'var(--cream)' }}>{usuario.user}</span>
                  <UserTypeBadge tipo={tipo} />
                </div>
                {isAdmin && (
                  <Link to="/admin/filmes" style={{
                    fontSize:12, color:'var(--gold)', fontWeight:700,
                    border:'1px solid var(--gold-border)', borderRadius:7,
                    padding:'5px 12px', background:'var(--gold-dim)',
                    display:'inline-flex', alignItems:'center', gap:5, transition:'all 0.15s',
                  }}
                    onMouseEnter={e => e.currentTarget.style.background='rgba(201,144,40,0.22)'}
                    onMouseLeave={e => e.currentTarget.style.background='var(--gold-dim)'}
                  >⚙ Painel</Link>
                )}
                <button className="btn-ghost" style={{ padding:'7px 16px', fontSize:13 }} onClick={onLogout}>Sair</button>
              </>
            ) : (
              <>
                <Link to="/login" className="btn-ghost" style={{ padding:'7px 16px', fontSize:13 }}>Entrar</Link>
                <Link to="/cadastro" className="btn-primary" style={{ padding:'8px 18px', fontSize:13 }}>Criar conta</Link>
              </>
            )}
          </div>
        </div>
      </header>

      {/* ── HERO ── */}
      <div style={{ position:'relative', textAlign:'center', padding:'76px 24px 54px', overflow:'hidden' }}>
        <HeroParticles />
        <div style={{ position:'absolute', inset:0, background:'radial-gradient(ellipse 65% 85% at 50% -8%, rgba(201,144,40,0.14), transparent 70%)', pointerEvents:'none' }}/>
        <div style={{ position:'absolute', inset:0, background:'radial-gradient(ellipse 40% 50% at 50% 100%, rgba(56,95,206,0.07), transparent 70%)', pointerEvents:'none' }}/>

        <div style={{ position:'relative', animation:'fadeUp 0.7s ease both' }}>
          <p style={{ fontSize:11, letterSpacing:'0.28em', textTransform:'uppercase', color:'var(--gold)', marginBottom:16, fontWeight:700 }}>
            ✦ Programação em cartaz
          </p>
          <h1 style={{ fontFamily:'var(--font-display)', fontSize:'clamp(34px, 6vw, 64px)', lineHeight:1.06, fontStyle:'italic', marginBottom:12, color:'var(--cream)' }}>
            {carregando
              ? 'Carregando…'
              : filmes.length === 0
                ? 'Nenhum filme'
                : `${filmes.length} filme${filmes.length !== 1 ? 's' : ''} em cartaz`}
          </h1>
          <div style={{ width:70, height:2, margin:'16px auto', background:'linear-gradient(90deg, transparent, var(--gold), transparent)' }}/>
          <p style={{ color:'var(--cream-muted)', fontSize:15, maxWidth:460, margin:'0 auto 28px', lineHeight:1.65 }}>
            Explore a grade, confira os horários e assista ao trailer antes de garantir seu lugar.
          </p>
          <div style={{
            display:'flex',
            justifyContent:'center',
            gap:10,
            flexWrap:'wrap',
            margin:'0 auto 28px',
          }}>
            {[
              { label:'Sessões hoje', value: Object.values(sessoes).flat().length || '...' },
              { label:'Tipos de sala', value:'Comum · 3D · XD' },
              { label:'Críticas', value: filmes.reduce((acc, f) => acc + (f.quantidadeCriticos || 0), 0) },
            ].map(item => (
              <div key={item.label} style={{
                minWidth:130,
                padding:'10px 14px',
                borderRadius:14,
                background:'rgba(255,255,255,0.035)',
                border:'1px solid var(--border-soft)',
                boxShadow:'inset 0 1px 0 rgba(255,255,255,0.045)',
              }}>
                <p style={{ fontSize:10, color:'var(--cream-dim)', textTransform:'uppercase', letterSpacing:'0.08em', marginBottom:2 }}>
                  {item.label}
                </p>
                <p style={{ fontSize:15, color:'var(--gold-light)', fontWeight:800, fontVariantNumeric:'tabular-nums' }}>
                  {item.value}
                </p>
              </div>
            ))}
          </div>
          {!usuario && (
            <div style={{ display:'flex', justifyContent:'center', gap:12, flexWrap:'wrap' }}>
              <Link to="/login"    className="btn-primary" style={{ fontSize:14 }}>Entrar para comprar</Link>
              <Link to="/cadastro" className="btn-ghost"   style={{ fontSize:14 }}>Criar conta gratuita →</Link>
            </div>
          )}
        </div>
      </div>

      {/* ── FILME EM DESTAQUE ── */}
      {!carregando && filmeDestaque && !busca && (
        <section id="destaque-filme" style={{ maxWidth:1240, width:'100%', margin:'0 auto', padding:'0 24px 40px' }}>
          <FilmeDestaque filme={filmeDestaque} onAbrirModal={handleAbrirModal} />
        </section>
      )}

      {/* ── FILTROS + SEPARADOR ── */}
      {!carregando && filmes.length > 0 && (
        <div id="programacao-completa" style={{ maxWidth:1240, margin:'0 auto', width:'100%', padding:'0 24px 24px' }}>
          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', gap:16, flexWrap:'wrap' }}>
            <div style={{ display:'flex', alignItems:'center', gap:10 }}>
              <div style={{ width:32, height:1, background:'linear-gradient(90deg, transparent, var(--border))' }}/>
              <p style={{ fontSize:10.5, letterSpacing:'0.24em', textTransform:'uppercase', color:'var(--gold)', fontWeight:700, flexShrink:0 }}>
                {busca ? `Resultados para "${busca}"` : 'Todos os filmes'}
              </p>
              <div style={{ width:32, height:1, background:'var(--border)' }}/>
            </div>

            {/* Filtros de sala */}
            <div style={{ display:'flex', gap:6, flexWrap:'wrap' }}>
              {tiposSala.map(t => (
                <button key={t} onClick={() => setFiltroSala(t)} style={{
                  fontSize:11.5, fontWeight: filtroSala === t ? 700 : 500,
                  padding:'5px 12px', borderRadius:999, cursor:'pointer',
                  border: filtroSala === t ? '1px solid var(--gold-border)' : '1px solid var(--border-soft)',
                  background: filtroSala === t ? 'var(--gold-dim)' : 'rgba(255,255,255,0.03)',
                  color: filtroSala === t ? 'var(--gold-light)' : 'var(--cream-dim)',
                  transition:'all 0.15s',
                }}>
                  {tiposSalaLabel[t]}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* ── GRADE DE FILMES ── */}
      <main style={{ flex:1, maxWidth:1240, width:'100%', margin:'0 auto', padding:'0 24px 100px' }}>
        {carregando ? (
          <div style={{ textAlign:'center', padding:'64px 0' }}>
            <div style={{ display:'inline-block', width:40, height:40, borderRadius:'50%', border:'3px solid var(--border)', borderTopColor:'var(--gold)', animation:'spin 0.8s linear infinite' }}/>
          </div>
        ) : filmesFiltrados.length === 0 ? (
          <div style={{ textAlign:'center', padding:'80px 0', color:'var(--cream-muted)' }}>
            <div style={{ fontSize:58, marginBottom:16 }}>{busca ? '🔍' : '🎬'}</div>
            <p style={{ fontSize:16 }}>
              {busca ? `Nenhum filme encontrado para "${busca}".` : 'Nenhum filme em cartaz no momento.'}
            </p>
            {busca && (
              <button className="btn-ghost" style={{ marginTop:16 }} onClick={() => setBusca('')}>Limpar busca</button>
            )}
            {isAdmin && !busca && (
              <Link to="/admin/filmes" className="btn-primary" style={{ marginTop:16, display:'inline-flex' }}>+ Adicionar filmes</Link>
            )}
          </div>
        ) : (
          <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(310px, 1fr))', gap:28 }}>
            {filmesFiltrados.map((filme, index) => (
              <CardFilme
                key={filme.id}
                filme={filme}
                index={index}
                sessoesDisponiveis={sessoes[filme.id]}
                onAbrirModal={handleAbrirModal}
              />
            ))}
          </div>
        )}
      </main>

      {/* ── FOOTER ── */}
      <footer style={{ borderTop:'1px solid var(--border-soft)', padding:'46px 28px 34px', background:'rgba(6,5,14,0.88)' }}>
        <div style={{ maxWidth:1240, margin:'0 auto', display:'flex', flexDirection:'column', gap:28 }}>
          <div style={{ display:'flex', alignItems:'flex-start', justifyContent:'space-between', flexWrap:'wrap', gap:30 }}>

            {/* Logo + slogan */}
            <div>
              <div style={{ display:'flex', alignItems:'baseline', gap:8, marginBottom:7 }}>
                <span style={{ fontFamily:'var(--font-display)', color:'var(--gold)', fontStyle:'italic', fontSize:26 }}>Lumière</span>
                <span style={{ fontSize:12, color:'var(--cream-dim)' }}>Cinema</span>
              </div>
              <p style={{ fontSize:13, color:'var(--cream-dim)', lineHeight:1.65, maxWidth:220 }}>
                A magia do cinema,<br/>a cada sessão.
              </p>
            </div>

            {/* Colunas de links */}
            <div style={{ display:'flex', gap:52 }}>
              <div>
                <p style={{ fontSize:10, fontWeight:700, textTransform:'uppercase', letterSpacing:'0.12em', color:'var(--cream-dim)', marginBottom:12 }}>Navegação</p>
                {[
                  { label: 'Programação', action: scrollToGrade },
                  { label: 'Ingressos',   action: () => setModalIngressos(true) },
                  { label: 'Sobre nós',   action: () => setModalSobre(true) },
                ].map(item => (
                  <button key={item.label} onClick={item.action} style={{
                    display:'block', background:'none', border:'none', padding:'4px 0', fontSize:13,
                    color:'var(--cream-dim)', cursor:'pointer', textAlign:'left', transition:'color 0.15s',
                  }}
                    onMouseEnter={e => e.currentTarget.style.color='var(--gold-light)'}
                    onMouseLeave={e => e.currentTarget.style.color='var(--cream-dim)'}
                  >{item.label}</button>
                ))}
              </div>

              <div>
                <p style={{ fontSize:10, fontWeight:700, textTransform:'uppercase', letterSpacing:'0.12em', color:'var(--cream-dim)', marginBottom:12 }}>Conta</p>
                {usuario ? (
                  <button onClick={onLogout} style={{ display:'block', background:'none', border:'none', padding:'4px 0', fontSize:13, color:'var(--cream-dim)', cursor:'pointer', transition:'color 0.15s' }}
                    onMouseEnter={e => e.currentTarget.style.color='var(--gold-light)'}
                    onMouseLeave={e => e.currentTarget.style.color='var(--cream-dim)'}
                  >Sair</button>
                ) : (
                  [{ to:'/login', label:'Entrar' }, { to:'/cadastro', label:'Criar conta' }].map(l => (
                    <Link key={l.to} to={l.to} style={{ display:'block', padding:'4px 0', fontSize:13, color:'var(--cream-dim)', transition:'color 0.15s' }}
                      onMouseEnter={e => e.currentTarget.style.color='var(--gold-light)'}
                      onMouseLeave={e => e.currentTarget.style.color='var(--cream-dim)'}
                    >{l.label}</Link>
                  ))
                )}
              </div>

              <div>
                <p style={{ fontSize:10, fontWeight:700, textTransform:'uppercase', letterSpacing:'0.12em', color:'var(--cream-dim)', marginBottom:12 }}>Contato</p>
                {[
                  { icon:'📍', text:'Av. Boa Viagem, 3000' },
                  { icon:'📞', text:'(81) 3000-0000' },
                  { icon:'✉️', text:'contato@lumiere.com.br' },
                ].map(c => (
                  <p key={c.text} style={{ display:'flex', alignItems:'center', gap:6, fontSize:12.5, color:'var(--cream-dim)', padding:'3px 0', margin:0 }}>
                    <span style={{ fontSize:12 }}>{c.icon}</span>
                    {c.text}
                  </p>
                ))}
              </div>
            </div>
          </div>

          <div style={{ height:1, background:'linear-gradient(90deg, transparent, var(--border), transparent)' }}/>

          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', flexWrap:'wrap', gap:12 }}>
            <p style={{ fontSize:12, color:'var(--cream-dim)', margin:0 }}>
              © {new Date().getFullYear()} Lumière Cinema — Todos os direitos reservados
            </p>
            <div style={{ display:'flex', gap:6 }}>
              {['🎬','✦','🎟️','✦','🍿'].map((s, i) => (
                <span key={i} style={{ fontSize:13, opacity:0.32 }}>{s}</span>
              ))}
            </div>
          </div>
        </div>
      </footer>

      {/* ── Estilos inline para animações extras ── */}
      <style>{`
        @keyframes logo-reel-glow {
          0%, 100% { filter: drop-shadow(0 0 8px rgba(201,144,40,0.45)); }
          50%       { filter: drop-shadow(0 0 20px rgba(234,184,74,0.80)) drop-shadow(0 0 40px rgba(201,144,40,0.35)); }
        }
      `}</style>
    </div>
  );
}
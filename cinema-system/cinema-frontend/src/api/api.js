import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

export default api;

export const listarFilmes          = ()          => api.get('/filmes');
export const listarSalas           = ()          => api.get('/salas');
export const listarSessoes         = ()          => api.get('/sessoes');
export const buscarSessao          = (id)        => api.get(`/sessoes/${id}`);
export const criarSala             = (data)      => api.post('/salas', data);
export const criarSessao           = (data)      => api.post('/sessoes', data);
export const comprarIngresso       = (id, data)  => api.post(`/sessoes/${id}/comprar`, data);
export const comprarMultiplos      = (id, data)  => api.post(`/sessoes/${id}/comprar-multiplos`, data);
export const cadastrarUsuario      = (data)      => api.post('/usuarios', data);
export const loginUsuario          = (data)      => api.post('/usuarios/login', data);
export const listarSessoesPorFilme = (filmeId)   => api.get(`/sessoes/filme/${filmeId}`);
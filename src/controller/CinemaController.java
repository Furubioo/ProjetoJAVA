package controller;

import model.Bilhete;
import model.CupomPromocional;
import model.Sala;
import model.Sessao;
import model.Usuario;
import model.VendasException;

public class CinemaController {

    private Usuario usuarioLogado;

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala, int linha, int coluna) throws VendasException {
        validarCompra(sessao, linha, coluna);
        Bilhete bilhete = usuarioLogado.comprarBilhete(sala, sessao, linha, coluna);
        sessao.ocuparCadeira(linha, coluna);
        return bilhete;
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala, int linha, int coluna, CupomPromocional cupom) throws VendasException {
        Bilhete bilhete = comprarBilhete(sessao, sala, linha, coluna);
        bilhete.setValor(bilhete.getValor() * (1 - cupom.getDesconto()));
        return bilhete;
    }

    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade) throws VendasException {
        validarQuantidade(quantidade);
        validarSessao(sessao);

        int[] sugestao = sugerirCadeirasJuntas(sessao, quantidade);

        if (sugestao == null) {
            System.out.println("Nao ha cadeiras consecutivas suficientes.");
            return new Bilhete[0];
        }

        Bilhete[] bilhetes = new Bilhete[quantidade];
        int linha = sugestao[0];
        int colunaInicial = sugestao[1];

        for (int i = 0; i < quantidade; i++) {
            int coluna = colunaInicial + i;
            validarCompra(sessao, linha, coluna);
            bilhetes[i] = usuarioLogado.comprarBilhete(sala, sessao, linha, coluna);
            sessao.ocuparCadeira(linha, coluna);
        }

        return bilhetes;
    }

    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade, CupomPromocional cupom) throws VendasException {
        Bilhete[] bilhetes = comprarMultiplosBilhetes(sessao, sala, quantidade);

        for (Bilhete bilhete : bilhetes) {
            bilhete.setValor(bilhete.getValor() * (1 - cupom.getDesconto()));
        }

        return bilhetes;
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de bilhetes deve ser maior que zero.");
        }
    }

    private void validarCompra(Sessao sessao, int linha, int coluna) throws VendasException {
        validarSessao(sessao);

        if (!sessao.cadeiraDisponivel(linha, coluna)) {
            throw new VendasException(VendasException.TipoErro.POLTRONA_OCUPADA);
        }
    }

    private void validarSessao(Sessao sessao) throws VendasException {
        if (sessao == null || sessao.getFilme() == null) {
            throw new VendasException(VendasException.TipoErro.FILME_FORA_DE_CARTAZ);
        }

        if (!sessao.isEmCartaz() || sessao.horarioJaPassou()) {
            throw new VendasException(VendasException.TipoErro.SESSAO_JA_PASSOU);
        }
    }

    private int[] sugerirCadeirasJuntas(Sessao sessao, int quantidade) {
        boolean[][] cadeiras = sessao.getCadeiras();

        for (int linha = 0; linha < cadeiras.length; linha++) {
            int consecutivas = 0;
            int inicio = 0;

            for (int coluna = 0; coluna < cadeiras[linha].length; coluna++) {
                if (!cadeiras[linha][coluna]) {
                    if (consecutivas == 0) {
                        inicio = coluna;
                    }
                    consecutivas++;
                    if (consecutivas == quantidade) {
                        System.out.println("Sugestao: fileira " + (char) ('A' + linha) +
                                ", colunas " + (inicio + 1) + " a " + (coluna + 1));
                        return new int[] { linha, inicio };
                    }
                } 
                else {
                    consecutivas = 0;
                }
            }
        }
        return null;
    }

    public void encerrarSessao(Sessao sessao) {
        sessao.setEmCartaz(false);
        System.out.println("Sessao encerrada: " + sessao);
    }
}
package controller;

import Bilhete;
import CupomPromocional;
import Filme;
import Sala;
import Sessao;
import Usuario;
import VendasException;

public class CinemaController {

    private Usuario usuarioLogado;

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala, int linha, int coluna)
            throws VendasException {

        validarCompra(sessao, linha, coluna);

        double valor = usuarioLogado.calcularPrecoFinal(
                sala.calcularValorBilhete(sessao.getFilme().getValor()));

        sessao.ocuparCadeira(linha, coluna);
        String nomeCadeira = sessao.formatarCadeira(linha, coluna);

        return new Bilhete(usuarioLogado, sala, sessao, sessao.getFilme(), valor, nomeCadeira);
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala, int linha, int coluna,
                                  CupomPromocional cupom) throws VendasException {

        Bilhete bilhete = comprarBilhete(sessao, sala, linha, coluna);
        bilhete.setValor(bilhete.getValor() * (1 - cupom.getDesconto()));
        return bilhete;
    }

    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade)
            throws VendasException {

        if (sessao.getFilme() == null) {
            throw new VendasException(VendasException.TipoErro.FILME_FORA_DE_CARTAZ);
        }

        int[] sugestao = sugerirCadeirasJuntas(sessao, quantidade);
        if (sugestao == null) {
            System.out.println("Não há cadeiras consecutivas suficientes. Escolha manualmente.");
            return new Bilhete[0];
        }

        Bilhete[] bilhetes = new Bilhete[quantidade];
        int linha   = sugestao[0];
        int colunaI = sugestao[1];

        for (int i = 0; i < quantidade; i++) {
            int coluna = colunaI + i;
            validarCompra(sessao, linha, coluna);
            double valor = usuarioLogado.calcularPrecoFinal(
                    sala.calcularValorBilhete(sessao.getFilme().getValor()));
            sessao.ocuparCadeira(linha, coluna);
            String nomeCadeira = sessao.formatarCadeira(linha, coluna);
            bilhetes[i] = new Bilhete(usuarioLogado, sala, sessao,
                    sessao.getFilme(), valor, nomeCadeira);
        }
        return bilhetes;
    }

    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade,
                                              CupomPromocional cupom) throws VendasException {
        Bilhete[] bilhetes = comprarMultiplosBilhetes(sessao, sala, quantidade);
        for (Bilhete b : bilhetes) {
            b.setValor(b.getValor() * (1 - cupom.getDesconto()));
        }
        return bilhetes;
    }

    private void validarCompra(Sessao sessao, int linha, int coluna) throws VendasException {
        if (sessao.getFilme() == null) {
            throw new VendasException(VendasException.TipoErro.FILME_FORA_DE_CARTAZ);
        }

        if (!sessao.isEmCartaz()) {
            throw new VendasException(VendasException.TipoErro.SESSAO_JA_PASSOU);
        }

        if (!sessao.cadeiraDisponivel(linha, coluna)) {
            throw new VendasException(VendasException.TipoErro.POLTRONA_OCUPADA);
        }
    }

    private int[] sugerirCadeirasJuntas(Sessao sessao, int quantidade) {
        boolean[][] cadeiras = sessao.getCadeiras();
        for (int l = 0; l < cadeiras.length; l++) {
            int consecutivas = 0;
            int inicio = 0;
            for (int c = 0; c < cadeiras[l].length; c++) {
                if (!cadeiras[l][c]) {
                    if (consecutivas == 0) inicio = c;
                    consecutivas++;
                    if (consecutivas == quantidade) {
                        System.out.println("Sugestão de cadeiras: fileira "
                                + (char) ('A' + l) + ", colunas "
                                + (inicio + 1) + " a " + (c + 1));
                        return new int[]{l, inicio};
                    }
                } else {
                    consecutivas = 0;
                }
            }
        }
        return null;
    }
}
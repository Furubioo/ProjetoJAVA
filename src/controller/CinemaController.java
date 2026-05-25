package controller;

import java.util.Scanner;

import model.Administrador;
import model.Bilhete;
import model.CupomPromocional;
import model.Sala;
import model.Sessao;
import model.Usuario;
import model.VendasException;
import view.Terminal;

public class CinemaController {

    private Usuario usuarioLogado;
    private Administrador adminLogado;
    private FilmeController filmeController;

    public CinemaController(FilmeController filmeController) {
        this.filmeController = filmeController;
    }
    
    public Usuario getUsuarioLogado() { 
        return usuarioLogado; 
    }
    
    public void setUsuarioLogado(Usuario u) { 
        this.usuarioLogado = u; 
    }

    public Administrador getAdminLogado() { 
        return adminLogado; 
    }

    public void setAdminLogado(Administrador a) { 
        this.adminLogado = a; 
    }

    public boolean isAdminLogado() { 
        return adminLogado != null; 
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala,
        int linha, int coluna) throws VendasException {
        validarCompra(sessao, linha, coluna);
        Bilhete bilhete = usuarioLogado.comprarBilhete(sala, sessao, linha, coluna);
        sessao.ocuparCadeira(linha, coluna);
        return bilhete;
    }

    public Bilhete comprarBilhete(Sessao sessao, Sala sala, int linha, int coluna,
        CupomPromocional cupom) throws VendasException {
        Bilhete bilhete = comprarBilhete(sessao, sala, linha, coluna);
        if (cupom != null && cupom.getDesconto() > 0) {
            bilhete.setValor(bilhete.getValor() * (1.0 - cupom.getDesconto()));
        }
        return bilhete;
    }


    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade,
        Scanner scanner) throws VendasException {
        validarQuantidade(quantidade);
        validarSessao(sessao);

        int[] sugestao = sugerirCadeirasJuntas(sessao, quantidade);
        if (sugestao == null) {
            System.out.println("  Não há cadeiras consecutivas suficientes.");
            return new Bilhete[0];
        }

        int linha = sugestao[0];
        int colunaInicial = sugestao[1];

        System.out.printf("  Sugestão: fileira %c, colunas %d a %d%n",
        (char) ('A' + linha), colunaInicial + 1, colunaInicial + quantidade);

        System.out.print("  Confirmar estas cadeiras? (S/N): ");

        if (!scanner.nextLine().equalsIgnoreCase("S")) {
            Terminal.aviso("Compra cancelada.");
            return new Bilhete[0];
        }

        Bilhete[] bilhetes = new Bilhete[quantidade];
        for (int i = 0; i < quantidade; i++) {
            int col = colunaInicial + i;
            validarCompra(sessao, linha, col);
            bilhetes[i] = usuarioLogado.comprarBilhete(sala, sessao, linha, col);
            sessao.ocuparCadeira(linha, col);
        }
        return bilhetes;
    }

    public Bilhete[] comprarMultiplosBilhetes(Sessao sessao, Sala sala, int quantidade, CupomPromocional cupom,
        Scanner scanner) throws VendasException {
        Bilhete[] bilhetes = comprarMultiplosBilhetes(sessao, sala, quantidade, scanner);
        if (cupom != null && cupom.getDesconto() > 0) {
            for (Bilhete b : bilhetes) {
                b.setValor(b.getValor() * (1.0 - cupom.getDesconto()));
            }
        }
        return bilhetes;
    }


    public void encerrarSessao(Sessao sessao) {
        sessao.setEmCartaz(false);
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("A quantidade de bilhetes deve ser maior que zero.");
    }

    private void validarCompra(Sessao sessao, int linha, int coluna) throws VendasException {
        validarSessao(sessao);
        if (!sessao.cadeiraDisponivel(linha, coluna))
            throw new VendasException(VendasException.TipoErro.POLTRONA_OCUPADA);
    }

    private void validarSessao(Sessao sessao) throws VendasException {
        if (!sessao.isEmCartaz())
            throw new VendasException(VendasException.TipoErro.SESSAO_JA_PASSOU);

        if (sessao.getFilme() == null ||
                filmeController.buscarFilme(sessao.getFilme().getNome()) == null)
            throw new VendasException(VendasException.TipoErro.FILME_FORA_DE_CARTAZ);
    }


    private int[] sugerirCadeirasJuntas(Sessao sessao, int quantidade) {
        boolean[][] cadeiras = sessao.getCadeiras();
        int totalLinhas = cadeiras.length;

        for (int linha = totalLinhas - 1; linha >= 0; linha--) {
            int melhorInicio = -1;
            int melhorDistanciaCentro = Integer.MAX_VALUE;
            int centro = cadeiras[linha].length / 2;
            int consecutivas = 0;
            int inicio = 0;

            for (int col = 0; col < cadeiras[linha].length; col++) {
                if (!cadeiras[linha][col]) {
                    if (consecutivas == 0) inicio = col;
                    consecutivas++;
                    if (consecutivas >= quantidade) {
                        int centroBlocoAtual = inicio + quantidade / 2;
                        int distancia = Math.abs(centroBlocoAtual - centro);
                        if (distancia < melhorDistanciaCentro) {
                            melhorDistanciaCentro = distancia;
                            melhorInicio = inicio;
                        }
                    }
                } else {
                    consecutivas = 0;
                }
            }
            if (melhorInicio != -1) return new int[]{ linha, melhorInicio };
        }
        return null;
    }
}
package model;

public class VendasException extends Exception {

    public enum TipoErro {
        SESSAO_JA_PASSOU,
        POLTRONA_OCUPADA,
        FILME_FORA_DE_CARTAZ
    }

    private final TipoErro tipoErro;

    public VendasException(TipoErro tipoErro) {
        super(getMensagem(tipoErro));
        this.tipoErro = tipoErro;
    }

    public TipoErro getTipoErro() { return tipoErro; }

    private static String getMensagem(TipoErro tipo) {
        switch (tipo) {
            case SESSAO_JA_PASSOU:
                return "Não é possível comprar: o horário da sessão já passou.";
            case POLTRONA_OCUPADA:
                return "A poltrona selecionada já está ocupada. Escolha outra.";
            case FILME_FORA_DE_CARTAZ:
                return "O filme selecionado não está mais em cartaz.";
            default:
                return "Erro desconhecido na compra.";
        }
    }
}

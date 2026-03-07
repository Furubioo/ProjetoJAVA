public class Compra {
    private String[] lanches;
    private int qtdLanches;
    private Bilhete[] bilhetes;
    private int qtdBilhetes;

    public Compra(String[] lanches, int qtdlanches, Bilhete[] bilhetes, int qtdBilhetes) {
        this.lanches = new String[15];
        this.qtdLanches = qtdlanches;
        this.bilhetes = new Bilhete[15];
        this.qtdBilhetes = qtdBilhetes;
    }

    public String[] getLanches() {
        return lanches;
    }

    public int getQtdlanches() {
        return qtdLanches;
    }

    public Bilhete[] getBilhetes() {
        return bilhetes;
    }

    public int getQtdbilhetes() {
        return qtdBilhetes;
    }

    public void setLanches(String[] lanches) {
        this.lanches = lanches;
    }

    public void setQtdlanches(int qtdlanches) {
        this.qtdLanches = qtdlanches;
    }

    public void setBilhetes(Bilhete[] bilhetes) {
        this.bilhetes = bilhetes;
    }

    public void setQtdbilhetes(int qtdbilhetes) {
        this.qtdBilhetes = qtdbilhetes;
    }

    public void comprarBilhetes(Bilhete novoBilhete) {

        if (qtdBilhetes < 15) {
            this.bilhetes[qtdBilhetes] = novoBilhete;
            this.qtdBilhetes++;
            System.out.println("Bilhete(s) comprado(s): ");
        }
        else {
            System.out.println("Não é mais possível comprar bilhetes");
        }

    }

    public void adicionarLanche(String lanche) {
        if (qtdLanches < 15) {
            this.lanches[qtdLanches] = lanche;
            this.qtdLanches++;
        }
    }



}

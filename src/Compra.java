public class Compra {
    private String[] lanches;
    private int qtdlanches;
    private Bilhete[] bilhetes;
    private int qtdbilhetes;

    public Compra(String[] lanches, int qtdlanches, Bilhete[] bilhetes, int qtdBilhetes) {
        this.lanches = new String[15];
        this.qtdlanches = qtdlanches;
        this.bilhetes = new Bilhete[15];
        this.qtdbilhetes = qtdBilhetes;
    }

    public String[] getLanches() {
        return lanches;
    }

    public int getQtdlanches() {
        return qtdlanches;
    }

    public Bilhete[] getBilhetes() {
        return bilhetes;
    }

    public int getQtdbilhetes() {
        return qtdbilhetes;
    }

    public void setLanches(String[] lanches) {
        this.lanches = lanches;
    }

    public void setQtdlanches(int qtdlanches) {
        this.qtdlanches = qtdlanches;
    }

    public void setBilhetes(Bilhete[] bilhetes) {
        this.bilhetes = bilhetes;
    }

    public void setQtdbilhetes(int qtdbilhetes) {
        this.qtdbilhetes = qtdbilhetes;
    }
    
    public void adicionarLanche(String lanches) {
        
    }




}

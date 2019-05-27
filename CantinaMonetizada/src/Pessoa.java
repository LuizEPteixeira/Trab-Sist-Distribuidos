public class Pessoa {

    private String nome;
    private int credito;

    public Pessoa(String nome, int credito){
        this.nome = nome;
        this.credito = credito;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCredito() {
        return credito;
    }

    public void setCredito(int credito) {
        this.credito = credito;
    }
}

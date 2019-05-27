
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author lhries
 */
public class ChatServer extends JFrame {

    private JTextArea textoArea;
    private JTextField textoMensagem;
    private JButton botaoEnviar;
    private String nomeUsuario;
    private PrintWriter escritor;
    private Scanner leitor;

    static ArrayList<Alimento> listAlim = new ArrayList<Alimento>();

    public ChatServer(String nome) {
        super("Chat do " + nome);
        this.nomeUsuario = nome;
        this.setBounds(100, 100, 500, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        iniciaComponentes();
        iniciaConexao();
        this.setVisible(true);
    }

    public void adicionaTexto(String texto) {
        textoArea.append(texto + "\n");
    }

    public void limpaTexto() {
        textoArea.setText("");
    }

    private void iniciaComponentes() {
        textoMensagem = new JTextField(30);
        textoMensagem.addActionListener(new EnviaMensagem());
        botaoEnviar = new JButton("Enviar");
        botaoEnviar.addActionListener(new EnviaMensagem());
        JPanel painelMensagem = new JPanel();
        painelMensagem.add(new JLabel("Mensagem"));
        painelMensagem.add(textoMensagem);
        painelMensagem.add(botaoEnviar);
        this.add(painelMensagem, BorderLayout.NORTH);

        textoArea = new JTextArea();
        textoArea.setEditable(false);
        this.add(new JScrollPane(textoArea));

    }

    private void iniciaConexao() {
        try {
            ServerSocket listenSocket = new ServerSocket(6789);
            System.out.println("Esperando conexão...");
            Socket socket = listenSocket.accept();

            escritor = new PrintWriter(socket.getOutputStream());
            leitor = new Scanner(socket.getInputStream());

            new Thread(new RecebeMensagem()).start();

            System.out.println("Conectado..");
            EnviaMenu();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void EnviaMenu() {

        for (int i = 0; i < listAlim.size() ; i++) {
            String nome = listAlim.get(i).getNome();
            int preco = listAlim.get(i).getPreco();
            String p = String.valueOf(preco);

            //Envio cada item cadastrado para o usuario(cuidar para quebrar a linha entre cada mensagem)
            escritor.println("Prato " + i +" - "+ nome);
            escritor.println("Preco - " + p);
            escritor.println("----------------------");
            escritor.println("Digite o nome do seu pedido");
            escritor.flush();
            textoMensagem.setText("");
            textoMensagem.setRequestFocusEnabled(true);
            /*
            Preciso conseguir enviar o menu de forma automatica
                Hoje estou tendo que apertar no botão enviar
            System.out.println(nome);
            System.out.println(p);
             */

        }

    }



    private class EnviaMensagem implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            escritor.println(nomeUsuario + ": " + textoMensagem.getText());
            escritor.flush();
            textoMensagem.setText("");
            textoMensagem.setRequestFocusEnabled(true);
        }
    }

    private class RecebeMensagem implements Runnable {
        @Override
        public void run() {
            String texto;
            try {
                while ((texto = leitor.nextLine()) != null && !texto.contains("EXIT")) {
                    ChatServer.this.adicionaTexto(texto);

                    for (int i = 0; i < listAlim.size(); i++) {

                        if(texto.toLowerCase().trim().contains(listAlim.get(i).getNome())){
                            // verificar se cliente tem saldo
                            escritor.println("\n" + texto + "\n=================\nPedido Comprado");
                            escritor.flush();
                            textoMensagem.setText("");
                            textoMensagem.setRequestFocusEnabled(true);
                            break;
                        }else{
                            if (i == listAlim.size()-1){
                                escritor.println("\n======================\nDigite o nome do seu pedido como mostrado no menu");
                                escritor.flush();
                                textoMensagem.setText("");
                                textoMensagem.setRequestFocusEnabled(true);
                                EnviaMenu();
                            }
                            /*

                             */

                        }
                    }
                    /*
                    Preciso validar a mensagem eviada para quando for um pedido
                            If pedido valido && client tem credito
                                Debita do client, envia mensagem de pedido liberado
                                else
                                    Avisar que não tem credito
                                    Pedir para escolher outro item
                            Else
                                Avisar pedido invalido
                                Pedir apra refazer o pedido
                    */
                }
            } catch (Exception e) {
                ChatServer.this.dispose();
            }

        }
    }



    public static void main(String[] args) {
        Alimentos();
        new ChatServer("Server");

    }

    private static void Alimentos() {


        Alimento prato1 = new Alimento("hamburguer", 150);
        Alimento prato2 = new Alimento("arroz", 100);
        Alimento prato3 = new Alimento("polenta", 50);
        Alimento prato4 = new Alimento("batata", 125);
        Alimento prato5 = new Alimento("pizza", 180);
        listAlim.add(prato1);
        listAlim.add(prato2);
        listAlim.add(prato3);
        listAlim.add(prato4);
        listAlim.add(prato5);

    }
}



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {

    private final static String ENDERECO = "localhost";
    private final static int PORTA = 6789;

    private String nomeUsuario;
    private JTextField textoEnviar;
    private JTextArea textoRecebido;
    private JButton botaoEnviar;
    private PrintWriter escritor;
    private Scanner leitor;

    public ChatClient(String nome) {
        super("Chat do " + nome);
        this.nomeUsuario = nome;
        this.setBounds(100, 100, 500, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        iniciaComponentes();

        iniciaConexao();

        this.setVisible(true);
    }

    private void iniciaComponentes() {
        textoEnviar = new JTextField(30);
        textoEnviar.addActionListener(new EnviaMensagem());
        botaoEnviar = new JButton("Enviar");
        botaoEnviar.addActionListener(new EnviaMensagem());
        JPanel painelMensagem = new JPanel();
        painelMensagem.add(new JLabel("Mensagem"));
        painelMensagem.add(textoEnviar);
        painelMensagem.add(botaoEnviar);
        this.add(painelMensagem, BorderLayout.NORTH);

        textoRecebido = new JTextArea();
        textoRecebido.setEditable(false);
        JScrollPane scroll = new JScrollPane(textoRecebido);
        this.add(scroll, BorderLayout.CENTER);

    }

    private void iniciaConexao() {
        try {

            Socket socket = new Socket(ENDERECO, PORTA);
            escritor = new PrintWriter(socket.getOutputStream());
            leitor = new Scanner(socket.getInputStream());
            new Thread(new RecebeServidor()).start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class RecebeServidor implements Runnable {

        @Override
        public void run() {
            String texto;
            try {
                while ((texto = leitor.nextLine()) != null && !texto.contains("EXIT")) {
                    textoRecebido.append(texto + "\n");
                }
            } catch (Exception e) {
                ChatClient.this.dispose();
            }
        }

    }

    private class EnviaMensagem implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            escritor.println(nomeUsuario + ": " + textoEnviar.getText());
            escritor.flush();
            textoEnviar.setText("");
            textoEnviar.setRequestFocusEnabled(true);
        }

    }

    public static void main(String args[]) {
        Pessoa p1 = new Pessoa("Luiz", 200);
        Pessoa p2 = new Pessoa("eduardo", 50);
        new ChatClient(p1.getNome());
    }
}

import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.util.*;
import java.util.regex.*;

public class Cliente {
    public Cliente(String nombre, String host, int puerto, JEditorPane editor, JComboBox<String> usuarioConectado) {
        this.nombre = nombre;
        this.host = host;
        this.puerto = puerto;
        this.editor = editor;
        this.usuarioConectado = usuarioConectado;

        try {
            cliente = new MulticastSocket(puerto);
            grupo = InetAddress.getByName(host);
            cliente.joinGroup(grupo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        hiloEscucha = new EscuchaMensajes();
        escucha = new Thread(hiloEscucha);
        escucha.start();
        carpeta();
    }

    private class EscuchaMensajes implements Runnable {
        @Override
        public void run() {
            System.out.println("Escuchando Mensajes");
            try {
                DatagramPacket recibido = new DatagramPacket(new byte[6500], 6500);
                while (true) {
                    cliente.receive(recibido);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(recibido.getData()));
                    Mensaje msj = (Mensaje) ois.readObject();
                    if(msj.getTipo() == 0 && !msj.getUsuarioOrigen().equals(nombre)) {
                        byte[] bmsj = msj.getMensaje().getBytes();
                        byte[] busuario = msj.getUsuarioOrigen().getBytes();
                        String mensaje = new String(bmsj, 0, msj.getMensaje().length());
                        String usuario = new String(busuario, 0, msj.getUsuarioOrigen().length());
                        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
                        StringReader reader = new StringReader(mensaje);
                        kit.read(reader, editor.getDocument(), editor.getDocument().getLength());
                        usuarioConectado.addItem(usuario);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        msj.setTipo(3);
                        msj.setUsuarioDestino(msj.getUsuarioOrigen());
                        msj.setUsuarioOrigen(nombre);
                        oos.writeObject(msj);
                        oos.flush();
                        byte[] b = baos.toByteArray();
                        DatagramPacket re = new DatagramPacket(b, b.length, grupo, puerto);
                        cliente.send(re);
                    } else if(msj.getTipo() == 1) {
                        byte[] bmsj = msj.getMensaje().getBytes();
                        String mensaje = new String(bmsj, 0, msj.getMensaje().length());
                        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
                        StringReader reader = new StringReader(mensaje);
                        kit.read(reader, editor.getDocument(), editor.getDocument().getLength());
                    } else if(msj.getTipo() == 2) {
                        if(!msj.getUsuarioOrigen().equals(nombre))
                            recibirArchivo(msj);
                    }else if(msj.getTipo() == 3 && !msj.getUsuarioOrigen().equals(nombre) && msj.getUsuarioDestino().equals(nombre)) {
                        byte[] busuario = msj.getUsuarioOrigen().getBytes();
                        String usuario = new String(busuario, 0, msj.getUsuarioOrigen().length());
                        usuarioConectado.addItem(usuario);
                    } else if(msj.getTipo() == 4 && msj.getUsuarioDestino().equals(nombre) && !msj.getUsuarioOrigen().equals(nombre)) {
                        byte[] bmsj = msj.getMensaje().getBytes();
                        String mensaje = new String(bmsj, 0, msj.getMensaje().length());
                        mensaje = "[Privado]D:[" + nombre + "]O:" + mensaje;
                        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
                        StringReader reader = new StringReader(mensaje);
                        kit.read(reader, editor.getDocument(), editor.getDocument().getLength());
                    }
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class EnviaMensajes implements Runnable {
        private EnviaMensajes(Mensaje msj) {
            this.msj = msj;
        }

        public void run() {
            // editor.setText("<b>Enviando Mensajes</b>" + (++i));
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(msj);
                oos.flush();
                byte[] msj = baos.toByteArray();
                DatagramPacket p = new DatagramPacket(msj, msj.length, grupo, puerto);
                cliente.send(p);
                oos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Mensaje msj;
    }

    public void enviar(Mensaje msj) {

        p = Pattern.compile(":Kappa:");
        m = p.matcher(msj.getMensaje());

        if(m.find()) {
            System.out.println("Emoji Kappa");
            String img = "<img src='https://www.pngkey.com/png/full/892-8926869_1024-x-536-2-kappa-emote-png.png' width=50 height=50>";
            String cad = msj.getMensaje().replace(":Kappa:", img);
            msj.setMensaje(cad);
        }

        p = Pattern.compile(":Pog:");
        m = p.matcher(msj.getMensaje());

        if(m.find()) {
            System.out.println("Emoji PogChamp");
            String img = "<img src='https://blog.cdn.own3d.tv/resize=fit:crop,height:400,width:600/s55ezAyRT86Fe8lScgvz' width=50 height=50>";
            String cad = msj.getMensaje().replace(":Pog:", img);
            msj.setMensaje(cad);
        }

        p = Pattern.compile(":Cora:");
        m = p.matcher(msj.getMensaje());

        if(m.find()) {
            System.out.println("Emoji Cora");
            String img = "<img src='https://streamscheme.com/wp-content/uploads/2020/04/gaypride.png' width=50 height=50>";
            String cad = msj.getMensaje().replace(":Cora:", img);
            msj.setMensaje(cad);
        }

        p = Pattern.compile(":Dogo:");
        m = p.matcher(msj.getMensaje());

        if(m.find()) {
            System.out.println("Emoji Cora");
            String img = "<img src='https://i.pinimg.com/originals/6f/74/cb/6f74cb89ffde17f3470c2cb020b4f5fb.png' width=50 height=50>";
            String cad = msj.getMensaje().replace(":Dogo:", img);
            msj.setMensaje(cad);
        }

        p = Pattern.compile(":Cool:");
        m = p.matcher(msj.getMensaje());

        if(m.find()) {
            System.out.println("Emoji Cora");
            String img = "<img src='https://www.twitch.tv/p/company-assets/images/company/header/c-header-coolcat.png' width=50 height=50>";
            String cad = msj.getMensaje().replace(":Cool:", img);
            msj.setMensaje(cad);
        }

        new Thread(new EnviaMensajes(msj)).start();
    }

    public void saludo(String nombre) {
        String a = " <b>El usuario [" + nombre + "] se ha conectado</b>";
        Mensaje m = new Mensaje(a, nombre, "", 0);
        new Thread(new EnviaMensajes(m)).start();
    }

    public void carpeta() {
        File carpeta = new File("./" + nombre);
        if(!carpeta.exists()) {
            try {
                if(carpeta.mkdir())
                    System.out.println("Se creo la carpeta");
                else
                    System.out.println("No se creo la carpeta");
            }catch(SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    private class EnvioArchivos implements Runnable {
        private EnvioArchivos(File file) {
            this.file = file;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                long tamanio = dis.available();
                long enviado = 0;
                int n = 0;
                int i = 0;
                while (enviado < tamanio) {
                    Mensaje datos = new Mensaje(file.getName(), nombre, "", 2, file.length(), "", ++i);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
                    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
                    oos.flush();
                    byte[] b = new byte[4000];
                    n = dis.read(b);
                    byte[] b2 = new byte[n];
                    System.arraycopy(b, 0, b2, 0, n);
                    datos.setDatos(b2);
                    datos.setBytesEnviados(n);
                    oos.writeObject(datos);
                    oos.flush();
                    byte[] d = baos.toByteArray();
                    DatagramPacket paqueteEnvio = new DatagramPacket(d, d.length, grupo, puerto);
                    cliente.send(paqueteEnvio);
                    try {
                        Thread.sleep(500);
                    }catch (Exception e) { e.printStackTrace(); }
                    System.out.println("Numero paquete:" + i);
                    enviado += n;
                    oos.close();
                    baos.close();
                }
                byte[] bFinal = {0x02};
                Mensaje paqueteFinal = new Mensaje(file.getName(), nombre, "", 2, file.length(), "", 0);

                paqueteFinal.setDatos(bFinal);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(paqueteFinal);
                oos.flush();

                byte[] mnsj = baos.toByteArray();

                DatagramPacket dp = new DatagramPacket(mnsj,mnsj.length, grupo, puerto);
                cliente.send(dp);

                System.out.println("Archivo Enviado");

                oos.close();
                baos.close();
                //cliente.close();
                dis.close();
            } catch(Exception e) {
                e.printStackTrace();
            }//try/catch
        }

        private File file;
    }

    public void enviarArchivo(File file) {
        new Thread(new EnvioArchivos(file)).start();
    }

    private void recibirArchivo(Mensaje datos) {
        try{
            System.out.println("Numero de paquete: " + datos.getNp());
            if(datos.getNp() == 0) {
                dos = new DataOutputStream(new FileOutputStream("./" + nombre + "/" + nombreArchivo));
                for(int i = 0; i < lista.size(); i++) {
                    dos.write(lista.get(i));
                }
                dos.close();
                lista.clear();
            } else if(datos.getNp() == 1) {
                lista = new ArrayList<>();
                nombreArchivo = datos.getNombre();
                lista.add(datos.getDatos());
            } else {
                if(nombreArchivo.equals(datos.getNombre()))
                    lista.add(datos.getDatos());
            }
            Thread.sleep(500);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    private String nombre;
    private String nombreArchivo;
    private String host;
    private int puerto;
    private JEditorPane editor;
    private Thread escucha;
    private Runnable hiloEscucha;
    private MulticastSocket cliente;
    private InetAddress grupo;
    private JComboBox<String> usuarioConectado;
    private DataOutputStream dos;
    private ArrayList <byte[]> lista = null;
    private Pattern p;
    private Matcher m;
}

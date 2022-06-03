import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


public class DiscorBot {
    /** Application name. */
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "resources";    //cambiar token a resources

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);  //permisos
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DiscorBot.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        //si salta el resourcenotfound es que falta el credentials.json
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        //aqui se abre un puerto para recibir datos por eso salta el defender al ejecutar
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("848527633823-iijv72199s791agih3oh4ofd4noqgqhn.apps.googleusercontent.com");
        //returns an authorized Credential object.
        return credential;
    }
    public static void main(String[] args) {


        final String token="OTUzNjM0MjMwODI5NzI3ODM2.G-JwNE.JeVU7NDFv2f5trmHcs6ly0_fQAX_syML2aD5cA" ;
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            //conection with my drive
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            EmbedCreateSpec embed = EmbedCreateSpec.builder()

                    .title("Lion")
                    .image("https://st.depositphotos.com/2290789/3667/i/600/depositphotos_36675429-stock-photo-king-lion-aslan.jpg")
                    .build();


            gateway.on(MessageCreateEvent.class).subscribe(event -> {
                final Message message = event.getMessage();
                if ("!ping".equals(message.getContent())) {
                    final MessageChannel channel = message.getChannel().block();
                    channel.createMessage("Pong!").block();
                }
                if ("!embed".equals(message.getContent())) {
                    final MessageChannel channel = message.getChannel().block();

                    channel.createMessage(MessageCreateSpec.builder()
                            .content("content? content")

                            .addEmbed(embed)
                            .build()).subscribe();
                }
                if ("/list".equals((message.getContent()))) {
                    final MessageChannel channel = message.getChannel().block();
                    try {
                        String ruta = "C:\\Users\\yagos\\IdeaProjects\\Discord-API\\src\\main\\java\\imagenes";
                        File carpeta = new File(ruta);
                        String[] imagenes = carpeta.list();
                        String res = "";
                        for (int i = 0; i < imagenes.length; i++) {
                            res += imagenes[i] + "\n";
                        }
                        channel.createMessage(res).block();
                    } catch (NullPointerException ex) {
                        System.out.println(ex.toString());
                    }
                }
                if (message.getContent().startsWith("/get")) {
                    final MessageChannel channel = message.getChannel().block();

                    String archivo = message.getContent().substring(5, message.getContent().length());
                    EmbedCreateSpec embed2 = EmbedCreateSpec.builder()
                            .color(Color.BISMARK)
                            .title(archivo.split("\\.")[0])
                            .image("attachment://src/main/java/imagenes/"+archivo)
                            .build();

                    InputStream fileAsInputStream = null;
                    boolean exists = true;
                    try {
                        fileAsInputStream = new FileInputStream("src/main/java/imagenes/"+archivo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        exists = false;
                    }
                    if (exists == true) {
                        channel.createMessage(MessageCreateSpec.builder()
                                .addFile("src/main/java/imagenes/"+archivo, fileAsInputStream)
                                .addEmbed(embed2)
                                .build()).subscribe();
                    } else {
                        channel.createMessage("El archivo no existe").block();
                    }
                }
                if ("/listDrive".equals((message.getContent()))) {
                    final MessageChannel channel = message.getChannel().block();
                    FileList result = null;
                    try {
                        result = service.files().list()
                                .setQ("name contains 'imagenesBot' and mimeType = 'application/vnd.google-apps.folder'")   //para mostrar solo las imagenes jpeg
                                /*https://developers.google.com/drive/api/guides/search-files */
                                .setPageSize(10)  //para decir el numero que mostrar

                                .setFields("nextPageToken, files(id, name)")
                                .execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<com.google.api.services.drive.model.File> files = result.getFiles();
                    if (files == null || files.isEmpty()) {
                        System.out.println("No files found.");
                    } else {
                        String dirImagenes = null;
                        System.out.println("Files:");
                        for (com.google.api.services.drive.model.File file : files) {
                            System.out.printf("%s (%s)\n", file.getName(), file.getId());
                            dirImagenes = file.getId();     //recoge la ruta del directorio de arriba
                        }
                        // busco la imagen en el directorio
                        FileList resultImagenes = null;
                        try {
                            resultImagenes = service.files().list()
                                    .setQ("(mimeType='image/jpg' or mimeType='image/png' or mimeType='image/jpg')  AND parents in '" + dirImagenes + "'")
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name)")
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        List<com.google.api.services.drive.model.File> filesImagenes = resultImagenes.getFiles();
                        for (com.google.api.services.drive.model.File file : filesImagenes) {
                            channel.createMessage(file.getName()).block();
                        }
                    }
                }

                if ("/dwlDrive".equals((message.getContent()))) {
                    final MessageChannel channel = message.getChannel().block();
                    FileList result = null;
                    try {
                        result = service.files().list()
                                .setQ("name contains 'imagenesBot' and mimeType = 'application/vnd.google-apps.folder'")   //para mostrar solo las imagenes jpeg
                                /*https://developers.google.com/drive/api/guides/search-files */

                                .setFields("nextPageToken, files(id, name)")
                                .execute();

                        List<com.google.api.services.drive.model.File> files = result.getFiles();
                        if (files == null || files.isEmpty()) {

                            System.out.println("No files found.");
                        } else {
                            String dirImagenes = null;
                            System.out.println("Files:");
                            for (com.google.api.services.drive.model.File file : files) {
                                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                                dirImagenes = file.getId();     //recoge la ruta del directorio de arriba
                            }
                            // busco la imagen en el directorio
                            FileList resultImagenes = null;

                            resultImagenes = service.files().list()
                                    .setQ("(mimeType = 'image/png' OR mimeType = 'image/jpeg') AND parents in '" + dirImagenes + "'")
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name)")
                                    .execute();


                            List<com.google.api.services.drive.model.File> filesImagenes = resultImagenes.getFiles();
                            for (com.google.api.services.drive.model.File file : filesImagenes) {
                                channel.createMessage(file.getName()).block();
                                OutputStream outputStream = null;

                                outputStream = new FileOutputStream("C:\\Users\\yagos\\Downloads\\ralph2.jpeg");


                                service.files().get(file.getId())
                                        .executeMediaAndDownloadTo(outputStream);

                            }
                        }
                    }catch (Exception e){
                        System.out.println("Excepcion");
                    }
                }
                if("/pdf".equals((message.getContent()))) {
                    final MessageChannel channel = message.getChannel().block();
                    FileList result = null;
                    try {

                        result = service.files().list()
                                .setQ("name contains 'imagenesBot' and mimeType = 'application/vnd.google-apps.folder'")   //para mostrar solo las documentos de microsoft
                                /*https://developers.google.com/drive/api/guides/search-files */

                                .setFields("nextPageToken, files(id, name)")
                                .execute();

                        List<com.google.api.services.drive.model.File> files = result.getFiles();
                        if (files == null || files.isEmpty()) {

                            System.out.println("No files found.");
                        } else {
                            String dirDoc = null;
                            System.out.println("Files:");
                            for (com.google.api.services.drive.model.File file : files) {
                                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                                dirDoc = file.getId();     //recoge la ruta del directorio de arriba
                            }

                            FileList resultDoc = null;

                            resultDoc = service.files().list()
                                    .setQ("( mimeType='application/vnd.google-apps.document') AND parents in '" + dirDoc + "'")
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name)")

                                    .execute();


                            List<com.google.api.services.drive.model.File> filesDoc = resultDoc.getFiles();
                            for (com.google.api.services.drive.model.File file : filesDoc) {
                                channel.createMessage(file.getName()).block();

                                OutputStream outputStream = new FileOutputStream(new java.io.File("C:\\Users\\yagos\\Downloads\\yago.pdf"+file.getName()),true);

                                service.files().export(file.getId(), "application/pdf")
                                        .executeMediaAndDownloadTo(outputStream);
                                outputStream = new FileOutputStream("C:\\Users\\yagos\\Downloads\\yago.pdf");


                                service.files().get(file.getId())
                                        .executeMediaAndDownloadTo(outputStream);

                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Excepción");
                    }
                }


            });

            gateway.onDisconnect().block();
        } catch (GeneralSecurityException e) {

        } catch (IOException e) {

        }

    }
}



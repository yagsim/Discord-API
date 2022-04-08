import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class DiscorBot {

    public static void main(String[] args) {
        final String token = "OTUzNjM0MjMwODI5NzI3ODM2.YjHbFg.i13JI3vQacV7km5ZD98SbuISsbk";
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

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


            gateway.onDisconnect().block();
        });
    }
}


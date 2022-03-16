import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;


public class MyBot {
    public static void main(String[] args) {
        final String token = args[0];  //Simplemente inicializamos el token
        //Crearemos un bot(programa en computadora que nos dejara interactuar con Discord)
        //para interactuar con Discord nos ofrece DiscordClient y el punto de acceso se hara con .create
        //El boot se generara abriendo sesión y entrando en new applicattion donde nos pedira un nombre con el que poder acceder al token ,agregareremos el boot y resetearemos el token ,asi nos dara un código que sera el introducido a continuación

        final DiscordClient client = DiscordClient.create("OTUzNjM0MjMwODI5NzI3ODM2.YjHbFg.2SOCzGIau6MoIqYi_BXQLnskGF4");   //El boot
        //Creamos el gateway para el inicio de sesión ,para obtener un archivo GateweyDiscordClient y login, con inicio de sesión
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();  //para mostrar el mensaje
            if ("!ping".equals(message.getContent())) {   //comparacion y si es igual se muestra mensaje
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
        });
//desconectamos
        gateway.onDisconnect().block();
    }




}

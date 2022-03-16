import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;


public class MyBot {
    public static void main(String[] args) {
        //Crearemos un bot(programa en computadora que nos dejara interactuar con Discord)
        //para interactuar con Discord nos ofrece DiscordClient y el punto de acceso se hara con .create
        //El boot se generara abriendo sesión y entrando en new applicattion donde nos pedira un nombre con el que poder acceder al token ,agregareremos el boot y resetearemos el token ,asi nos dara un código que sera el introducido a continuación
        DiscordClient client = DiscordClient.create("OTUzNjM0MjMwODI5NzI3ODM2.YjHbFg.2SOCzGIau6MoIqYi_BXQLnskGF4");   //El boot restringe


        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

        login.block();
    }


}

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    private static final String API_KEY = "1fe338715674951128808d0e";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        while (opcion != 7) {
            try {
                System.out.println("********************************************");
                System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
                System.out.println();
                System.out.println("1) Dolar =>> Peso Argentino");
                System.out.println("2) Peso argentino =>> Dolar");
                System.out.println("3) Dolar =>> Real Brasileño");
                System.out.println("4) Real Brasileño =>> Dolar");
                System.out.println("5) Dolar =>> Peso Colombiano");
                System.out.println("6) Peso Colombiano =>> Dolar");
                System.out.println("7) Salir");
                System.out.print("Elija una opción válida: ");
                System.out.println();
                System.out.println("********************************************");

                opcion = scanner.nextInt();

                if (opcion == 7) {
                    System.out.println("Gracias por usar el conversor. ¡Adiós!");
                    break;
                }

                String from = "", to = "";

                switch (opcion) {
                    case 1: from = "USD"; to = "ARS"; break;
                    case 2: from = "ARS"; to = "USD"; break;
                    case 3: from = "USD"; to = "BRL"; break;
                    case 4: from = "BRL"; to = "USD"; break;
                    case 5: from = "USD"; to = "COP"; break;
                    case 6: from = "COP"; to = "USD"; break;
                    default:
                        throw new IllegalArgumentException("Opción inválida. Intente nuevamente.");
                }

                System.out.print("Ingrese el valor que deseas convertir: ");
                double monto = scanner.nextDouble();

                double resultado = convertirMoneda(from, to, monto);

                System.out.printf("El valor %.2f [%s] corresponde al valor final de =>> %.2f [%s]%n",
                        monto, from, resultado, to);

            } catch (InputMismatchException e) {
                System.out.println("Error: debe ingresar un número válido.");
                scanner.nextLine(); // limpiar buffer
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error al conectar con la API: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static double convertirMoneda(String from, String to, double monto) throws IOException {
        String urlStr = BASE_URL + from + "/" + to + "/" + monto;
        URL url = new URL(urlStr);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.lines().collect(Collectors.joining());
        reader.close();

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        if (!jsonResponse.get("result").getAsString().equals("success")) {
            throw new IOException("No se pudo realizar la conversión.");
        }

        return jsonResponse.get("conversion_result").getAsDouble();
    }
}

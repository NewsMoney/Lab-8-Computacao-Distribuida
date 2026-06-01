package br.mackenzie.biblioteca.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServidorBiblioteca {

    public static void main(String[] args) throws Exception {

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new BibliotecaServiceImpl())
                .build();

        server.start();

        System.out.println("Servidor da Biblioteca iniciado na porta 50051");

        server.awaitTermination();
    }
}

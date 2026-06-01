package br.mackenzie.biblioteca.client;

import br.mackenzie.biblioteca.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClienteBiblioteca {

    public static void main(String[] args) throws Exception {

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();

        BibliotecaServiceGrpc.BibliotecaServiceBlockingStub blockingStub =
                BibliotecaServiceGrpc.newBlockingStub(channel);

        BibliotecaServiceGrpc.BibliotecaServiceStub asyncStub =
                BibliotecaServiceGrpc.newStub(channel);

        System.out.println("===== CADASTRO DE LIVROS =====");

        cadastrarLivro(blockingStub,
                "Clean Code",
                "Robert Martin",
                2008,
                "111");

        cadastrarLivro(blockingStub,
                "Clean Architecture",
                "Robert Martin",
                2017,
                "222");

        cadastrarLivro(blockingStub,
                "Effective Java",
                "Joshua Bloch",
                2018,
                "333");

        System.out.println("\n===== ISBN DUPLICADO =====");

        cadastrarLivro(blockingStub,
                "Livro Duplicado",
                "Autor Teste",
                2025,
                "111");

        System.out.println("\n===== LISTAR POR AUTOR =====");

        try {

            Iterator<Livro> livros =
                    blockingStub.listarLivrosPorAutor(
                            AutorRequest.newBuilder()
                                    .setAutor("Robert Martin")
                                    .build());

            while (livros.hasNext()) {

                Livro livro = livros.next();

                System.out.println(
                        livro.getTitulo()
                                + " - "
                                + livro.getAutor());
            }

        } catch (StatusRuntimeException e) {

            System.out.println(e.getStatus());
        }

        System.out.println("\n===== AUTOR INEXISTENTE =====");

        try {

            Iterator<Livro> livros =
                    blockingStub.listarLivrosPorAutor(
                            AutorRequest.newBuilder()
                                    .setAutor("Autor Inexistente")
                                    .build());

            while (livros.hasNext()) {
                System.out.println(livros.next());
            }

        } catch (StatusRuntimeException e) {

            System.out.println(e.getStatus());
        }

        System.out.println("\n===== EMPRÉSTIMOS =====");

        CountDownLatch emprestimoLatch =
                new CountDownLatch(1);

        StreamObserver<ResumoEmprestimos> resumoObserver =
                new StreamObserver<>() {

                    @Override
                    public void onNext(
                            ResumoEmprestimos value) {

                        System.out.println(
                                "Total: "
                                        + value.getTotal());

                        System.out.println(
                                "Tempo: "
                                        + value.getTempoProcessamento());
                    }

                    @Override
                    public void onError(Throwable t) {
                        emprestimoLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        emprestimoLatch.countDown();
                    }
                };

        StreamObserver<EmprestimoRequest> emprestimos =
                asyncStub.registrarEmprestimos(
                        resumoObserver);

        for (int i = 1; i <= 5; i++) {

            emprestimos.onNext(
                    EmprestimoRequest.newBuilder()
                            .setUsuario("Usuario" + i)
                            .setLivroId(i)
                            .build());
        }

        emprestimos.onCompleted();

        emprestimoLatch.await(
                5,
                TimeUnit.SECONDS);

        System.out.println("\n===== CHAT =====");

        CountDownLatch chatLatch =
                new CountDownLatch(1);

        StreamObserver<SugestaoLivro> respostaChat =
                new StreamObserver<>() {

                    @Override
                    public void onNext(
                            SugestaoLivro value) {

                        System.out.println(
                                "Sugestão: "
                                        + value.getSugestao());
                    }

                    @Override
                    public void onError(Throwable t) {
                        chatLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        chatLatch.countDown();
                    }
                };

        StreamObserver<MensagemUsuario> chat =
                asyncStub.chatBibliotecario(
                        respostaChat);

        chat.onNext(
                MensagemUsuario.newBuilder()
                        .setMensagem("java")
                        .build());

        chat.onNext(
                MensagemUsuario.newBuilder()
                        .setMensagem("algoritmos")
                        .build());

        chat.onNext(
                MensagemUsuario.newBuilder()
                        .setMensagem("ia")
                        .build());

        chat.onCompleted();

        chatLatch.await(
                5,
                TimeUnit.SECONDS);

        channel.shutdown();
    }

    private static void cadastrarLivro(
            BibliotecaServiceGrpc.BibliotecaServiceBlockingStub stub,
            String titulo,
            String autor,
            int ano,
            String isbn) {

        try {

            LivroResponse response =
                    stub.cadastrarLivro(
                            LivroRequest.newBuilder()
                                    .setTitulo(titulo)
                                    .setAutor(autor)
                                    .setAno(ano)
                                    .setIsbn(isbn)
                                    .build());

            System.out.println(
                    response.getStatus()
                            + " | ID: "
                            + response.getId());

        } catch (StatusRuntimeException e) {

            System.out.println(
                    "Erro: "
                            + e.getStatus());
        }
    }
}

package br.mackenzie.biblioteca.server;

import br.mackenzie.biblioteca.*;
import br.mackenzie.biblioteca.model.Livro;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BibliotecaServiceImpl
        extends BibliotecaServiceGrpc.BibliotecaServiceImplBase {

    private final Map<Integer, Livro> livros = new HashMap<>();
    private final Map<String, Livro> livrosPorIsbn = new HashMap<>();

    private final AtomicInteger proximoId =
            new AtomicInteger(1);

    @Override
    public void cadastrarLivro(
            LivroRequest request,
            StreamObserver<LivroResponse> responseObserver) {

        System.out.println(
                "[Unary] cadastrarLivro: "
                        + request.getTitulo());

        if (request.getTitulo().isBlank()
                || request.getAutor().isBlank()
                || request.getIsbn().isBlank()) {

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(
                                    "Dados do livro inválidos")
                            .asRuntimeException());

            return;
        }

        if (livrosPorIsbn.containsKey(
                request.getIsbn())) {

            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription(
                                    "ISBN já cadastrado")
                            .asRuntimeException());

            return;
        }

        int id = proximoId.getAndIncrement();

        Livro livro = new Livro(
                id,
                request.getTitulo(),
                request.getAutor(),
                request.getAno(),
                request.getIsbn());

        livros.put(id, livro);
        livrosPorIsbn.put(
                request.getIsbn(),
                livro);

        LivroResponse response =
                LivroResponse.newBuilder()
                        .setId(id)
                        .setStatus("Livro cadastrado com sucesso")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listarLivrosPorAutor(
            AutorRequest request,
            StreamObserver<br.mackenzie.biblioteca.Livro> responseObserver) {

        System.out.println(
                "[Server Streaming] listarLivrosPorAutor: "
                        + request.getAutor());

        boolean encontrou = false;

        for (Livro livro : livros.values()) {

            if (livro.getAutor()
                    .equalsIgnoreCase(request.getAutor())) {

                encontrou = true;

                responseObserver.onNext(
                        br.mackenzie.biblioteca.Livro
                                .newBuilder()
                                .setId(livro.getId())
                                .setTitulo(livro.getTitulo())
                                .setAutor(livro.getAutor())
                                .setAno(livro.getAno())
                                .setIsbn(livro.getIsbn())
                                .build()
                );
            }
        }

        if (!encontrou) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(
                                    "Autor não encontrado")
                            .asRuntimeException());

            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<EmprestimoRequest>
    registrarEmprestimos(
            StreamObserver<ResumoEmprestimos> responseObserver) {

        System.out.println(
                "[Client Streaming] registrarEmprestimos");

        long inicio = System.currentTimeMillis();

        return new StreamObserver<>() {

            int total = 0;

            @Override
            public void onNext(EmprestimoRequest value) {

                total++;

                System.out.println(
                        value.getUsuario()
                                + " -> Livro "
                                + value.getLivroId());
            }

            @Override
            public void onError(Throwable t) {

                System.out.println(
                        "Erro no stream: "
                                + t.getMessage());
            }

            @Override
            public void onCompleted() {

                long fim =
                        System.currentTimeMillis();

                ResumoEmprestimos resumo =
                        ResumoEmprestimos
                                .newBuilder()
                                .setTotal(total)
                                .setTempoProcessamento(
                                        (fim - inicio) + " ms")
                                .build();

                responseObserver.onNext(resumo);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MensagemUsuario>
    chatBibliotecario(
            StreamObserver<SugestaoLivro> responseObserver) {

        System.out.println(
                "[Bidirectional] chatBibliotecario");

        Map<String, String> sugestoes =
                Map.of(
                        "java", "Effective Java",
                        "algoritmos", "Introduction to Algorithms",
                        "ia", "Artificial Intelligence: A Modern Approach",
                        "redes", "Computer Networking"
                );

        return new StreamObserver<>() {

            @Override
            public void onNext(MensagemUsuario value) {

                String chave =
                        value.getMensagem().toLowerCase();

                String resposta =
                        sugestoes.getOrDefault(
                                chave,
                                "Nenhuma sugestão encontrada");

                responseObserver.onNext(
                        SugestaoLivro.newBuilder()
                                .setSugestao(resposta)
                                .build());
            }

            @Override
            public void onError(Throwable t) {

                System.out.println(
                        "Erro no chat: "
                                + t.getMessage());
            }

            @Override
            public void onCompleted() {

                responseObserver.onCompleted();
            }
        };
    }
}

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileSystemSimulator {

    public static void main(String[] args) {
        System.out.println("Sistema de arquivos formatado como EXT4 (simulado no nome)\n");


        criarDiretorio("projeto");
        criarDiretorio("projeto/documentos");
        criarDiretorio("projeto/dados");
        criarDiretorio("projeto/resultados");


        criarArquivo("projeto/documentos/relatorio.txt", 1500);
        criarArquivo("projeto/dados/amostra1.csv", 2500);
        criarArquivo("projeto/dados/amostra2.csv", 3000);
        criarArquivo("projeto/resultados/resultado_final.txt", 4000);


        aplicarPermissaoWindows("projeto\\documentos", "R");  // Somente leitura
        aplicarPermissaoWindows("projeto\\dados", "RW");      // Leitura e escrita
        aplicarPermissaoWindows("projeto\\resultados", "F");  // Total (rwx)


        System.out.println("\nTentando escrever em 'projeto/documentos/relatorio.txt'...");
        tentarEscrita("projeto/documentos/relatorio.txt");

        System.out.println("\nTentando escrever em 'projeto/dados/amostra1.csv'...");
        tentarEscrita("projeto/dados/amostra1.csv");

        System.out.println("\nTentando executar 'projeto/resultados/resultado_final.txt'...");
        tentarExecucao("projeto/resultados/resultado_final.txt");

        // Listar permissões estilo 'ls -l'
        System.out.println("\nListagem estilo 'ls -l' para o diretório /projeto:");
        listarPermissoesEstiloUnix("projeto");
    }

    private static void criarDiretorio(String caminho) {
        File dir = new File(caminho);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Diretório criado: " + caminho);
            } else {
                System.out.println("Erro ao criar diretório: " + caminho);
            }
        }
    }

    private static void criarArquivo(String caminho, int tamanhoBytes) {
        try (FileOutputStream fos = new FileOutputStream(caminho)) {
            byte[] dados = new byte[tamanhoBytes];
            new Random().nextBytes(dados);
            fos.write(dados);
            System.out.println("Arquivo criado: " + caminho + " (" + tamanhoBytes + " bytes)");
        } catch (IOException e) {
            System.out.println("Erro ao criar arquivo: " + caminho);
        }
    }


    private static void aplicarPermissaoWindows(String caminho, String modo) {
        try {

            String comando = "cmd /c icacls \"" + caminho + "\" /inheritance:r /grant:r %USERNAME%:" + modo;
            Process p = Runtime.getRuntime().exec(comando);
            p.waitFor();
            System.out.println("Permissões aplicadas via icacls em: " + caminho + " com modo: " + modo);


            comando = "cmd /c icacls \"" + caminho + "\" /grant:r %USERNAME%:" + modo + " /T";
            p = Runtime.getRuntime().exec(comando);
            p.waitFor();
            System.out.println("Permissões recursivas aplicadas para arquivos dentro de: " + caminho);

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao aplicar permissões no Windows: " + e.getMessage());
        }
    }


    private static void tentarEscrita(String caminho) {
        File file = new File(caminho);
        if (file.canWrite()) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write("\nLinha de teste adicionada.");
                System.out.println("Escrita realizada com sucesso em: " + caminho);
            } catch (IOException e) {
                System.out.println("Erro de I/O ao escrever: " + caminho);
            }
        } else {
            System.out.println("Permissão negada para escrita em: " + caminho);
        }
    }

    private static void tentarExecucao(String caminho) {
        File file = new File(caminho);
        if (file.canExecute()) {
            System.out.println("Arquivo " + caminho + " é marcado como executável.");
        } else {
            System.out.println("Permissão negada para executar o arquivo: " + caminho);
        }
    }


    private static void listarPermissoesEstiloUnix(String caminhoDir) {
        File dir = new File(caminhoDir);
        File[] arquivos = dir.listFiles();

        if (arquivos == null) {
            System.out.println("Diretório não encontrado ou vazio.");
            return;
        }

        for (File arquivo : arquivos) {
            String tipo = arquivo.isDirectory() ? "d" : "-";
            String permissoes =
                    (arquivo.canRead() ? "r" : "-") +
                            (arquivo.canWrite() ? "w" : "-") +
                            (arquivo.canExecute() ? "x" : "-");

            long tamanho = arquivo.length();
            String nome = arquivo.getName();

            System.out.printf("%s%s %10d %s\n", tipo, permissoes, tamanho, nome);
        }
    }
}

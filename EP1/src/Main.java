import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

class BCP{
    private int pc;
    private String status; //Executando, Bloqueado ou Pronto (Finalizado?)
    private int prioridade;
    private int creditos;
    private String X;
    private String Y;
    Programa programa;
    int indiceTabela;
    public BCP(int prioridade, Programa programa){
        pc = -1;
        this.prioridade = prioridade;
        creditos = prioridade;
        this.programa = programa;
    }
    void executa(){
        creditos = creditos - 1;
        this.status = "Executando";
    }
    void inserenaTabela(int indiceTabela){
        this.indiceTabela = indiceTabela;
    }
    String devolveInstrucao_processo(){
        pc++;
        return programa.devolveIntrucao(pc);
    }
    void apronta(){
        this.status = "Pronto";
    }
    void bloqueia(){
        this.status = "Bloqueado";
    }
    void finaliza(){
        this.status = "Finalizado";
    } //talvez nao seja mais util
    void reiniciaCred(){
        creditos = prioridade;
    }
    void armazenaX(String X){
        this.X = X;
    }
    void armazenaY(String Y){
        this.Y = Y;
    }
    String devolveStatus(){
        return status;
    }
    int devolvePrioridade(){
        return prioridade;
    }
    int devolveIndice_tabela(){
        return indiceTabela;
    }
    String devolveNome_processo(){
        return programa.devolveNome();
    }
    int devolveTamanho_processo(){
        return programa.devolveTamanho();
    }

    int devolveCreditos(){
        return creditos;
    }
    int devolvePC(){
        return pc;
    }
    void printBCP(){
        System.out.printf("programa: %S | indice: %d\n", devolveNome_processo(), devolveIndice_tabela());
        /*System.out.println("pc = "+pc);
        System.out.println("status = "+status);
        System.out.println("prioridade = "+prioridade);
        System.out.println("creditos = "+creditos);
        System.out.println("X = "+X);
        System.out.println("Y = "+Y);
        System.out.println("Programa: "+devolveNome_processo());
        System.out.println("");*/
    }
}

class Programa{
    private String nome;
    private String [] instrucoes;
    int indice;
    public Programa(String nome, int maxinstucoes){
        this.nome = nome;
        instrucoes = new String[maxinstucoes];
        indice = 0;

    }
    void addIntrucao(String intrucao){
        instrucoes[indice] = intrucao;
        indice++;

    }
    String devolveNome(){
        return nome;
    }
    String devolveIntrucao(int pc){
        return instrucoes[pc];
    }
    int devolveTamanho(){
        return indice;
    }

    //acho que vai sair um null, usar indice - 1 se for o caso - > EREEEI
    void printPrograma(){
        System.out.println(nome);
        System.out.println("Tamanho: "+devolveTamanho());
        for(int i = 0; i < indice; i++){
            System.out.printf("instrucao numero %d: %S\n", i, instrucoes[i]);
        }
    }
}

//colocar prontos e bloquados na tabela ou numa classe escalonador?
//para que serve essa tabela?
class Tabeladeprocessos {
    private BCP[] tabela;
    int indice;
    public Tabeladeprocessos(int numprogramas){
        tabela = new BCP[numprogramas];
        indice = 0;
    }
    //quantos processos estão rodando
    int devolveTamanho(){
        return indice;
    }
    void adicionaProcesso(BCP processo){
        processo.inserenaTabela(indice);
        tabela[indice] = processo;
        indice++; //proxima posicao livre
    }
    void removeProcesso(int indiceTabela){
        tabela[indiceTabela].finaliza();

        for(int i = indiceTabela; i < indice; i++){
            if(i == indice - 1){
                tabela[i] = null;
                indice = indice - 1;
                break;
            }
            tabela[i] = tabela [i + 1];
            tabela[i].inserenaTabela(i);
        }
    }

    void verificaCreditos(){
        int programasprobres = 0;
        for (int i = 0; i < indice; i++){
            if(tabela[i].devolveCreditos() == 0) programasprobres++;
        }
        if(programasprobres == devolveTamanho()) reditribuiCreds();
    }
    void reditribuiCreds(){
        for(int i = 0; i < indice; i++){
            tabela[i].reiniciaCred();
        }
    }
    void printTabela(){
        System.out.println("INDICE: "+indice);
        for(int i = 0; i < indice; i++){
            tabela[i].printBCP();
        }
        System.out.println("");
    }

}

class Listaprontos{
    private BCP [] prontos;
    private int indice;
    public Listaprontos(int numprogramas){
        prontos = new BCP[numprogramas];
        indice = 0;
    }
    void adicionaPronto(BCP processo) {
        BCP atualprocesso, novoprocesso;
        processo.apronta();
        novoprocesso = processo;
        indice++;//proxima posicao livre
        for (int i = 0; i < indice; i++) {
            atualprocesso = prontos[i];
                if (atualprocesso == null) {
                    prontos[i] = novoprocesso;
                    break;
                } else if (atualprocesso.devolveCreditos() < novoprocesso.devolveCreditos()) {
                    prontos[i] = novoprocesso;
                    novoprocesso = atualprocesso;
                }
        }
    }
    BCP removePronto(){
        BCP removido = prontos[0];

        for(int i = 0; i < indice; i++){
            if(i == indice - 1 || prontos[i + 1] == null){
                prontos[i] = null;
                indice = indice -1;//posicao livre
                break;
            }
            prontos[i] = prontos [i + 1];
        }
        return removido;
    }
    int devolveTamanho(){
        return indice;
    }
    void printProntos(){
        System.out.println("Lista de processos prontos");
        System.out.println("");
        for(int i = 0; i < indice; i++){
            System.out.printf("Nome: %S | Prioridade: %d\n", prontos[i].devolveNome_processo(), prontos[i].devolvePrioridade());
        }
        System.out.println("");
    }
}
class Bloqueado{
    private BCP processo;
    private int espera;
    public Bloqueado(BCP processo, int espera){ //bloquear
        processo.bloqueia();
        this.processo = processo;
        this.espera = espera;
    }
    void reduzEspera(){
        espera = espera - 1;
    }
    int devolveEspera(){
        return espera;
    }
    String devolveNome_processo() {
        return processo.devolveNome_processo();
    }

    BCP devovolveProcesso(){
        return processo;
    }
}
class Listabloqueados{
    private Bloqueado [] bloqueados;
    private int indice; //posicao livre
    public Listabloqueados(int numprogramas){
        bloqueados = new Bloqueado[numprogramas];
        indice = 0;
    }
    void adicionaBloquado(Bloqueado programa){
        bloqueados[indice] = programa;
        indice++;
    }
    void reduzEspera_bloqueado(Listaprontos lista){
        for(int i = 0; i < indice; i++){
            bloqueados[i].reduzEspera();
            if(bloqueados[i].devolveEspera() == 0){
                removeBloquado(lista);
            }
        }
    }
    void removeBloquado(Listaprontos lista){
        BCP removido = bloqueados[0].devovolveProcesso();
        lista.adicionaPronto(removido);

        for(int i = 0; i < indice; i++){
            if(i == indice - 1){
                bloqueados[i] = null;
                indice = indice - 1;
                break;
            }
            bloqueados[i] = bloqueados [i + 1];
        }
    }
    int devolveTamanho(){
        return indice;
    }
    void printBloqueados(){
        System.out.println("Lista de processos bloquados");
        System.out.println("");
        for(int i = 0; i < indice; i++){
            System.out.printf("Nome: %S\n", bloqueados[i].devolveNome_processo());
        }
        System.out.println("");
    }
}


    public class Main {
        public static void main(String[] args) {
            //System.out.println("Hello world!");

            //  C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas
            File prioridadesfile = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\prioridades.txt");
            File quantumfile = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\quantum.txt");
            File pfile[] = new File[10];
            pfile[0] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\01.txt");
            pfile[1] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\02.txt");
            pfile[2] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\03.txt");
            pfile[3] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\04.txt");
            pfile[4] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\05.txt");
            pfile[5] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\06.txt");
            pfile[6] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\07.txt");
            pfile[7] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\08.txt");
            pfile[8] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\09.txt");
            pfile[9] = new File("C:\\USP\\SO\\Trabalhos\\EP1\\EP1\\src\\programas\\10.txt");

            Programa p[] = new Programa[10];
            BCP bcp[] = new BCP[10];
            int quantum = 0;
            try {
                Scanner program, priority, qt;
                qt = new Scanner(quantumfile);
                quantum = qt.nextInt();
                priority = new Scanner(prioridadesfile);
                for (int i = 0; i < 10; i++) {
                    program = new Scanner(pfile[i]);
                    p[i] = new Programa(program.nextLine(), 21);
                    while (program.hasNextLine()) {
                        p[i].addIntrucao(program.nextLine());
                    }
                    bcp[i] = new BCP(priority.nextInt(), p[i]);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Não encontrei um dos arquivos solicitados");
            }
            Tabeladeprocessos tabela = new Tabeladeprocessos(10);
            Listaprontos lprontos = new Listaprontos(10);
            Listabloqueados lblock = new Listabloqueados(10);
            int programaspobres = 0;
            for (int i = 0; i < 10; i++) {
                tabela.adicionaProcesso(bcp[i]);
                bcp[i].apronta();
                lprontos.adicionaPronto(bcp[i]);
            }
        /*BCP teste = lprontos.removePronto();
        while (true){
            String t1 = teste.devolveInstrucao_processo();
            System.out.println(t1);
            if (t1.contains("SAIDA")) break;
        }*/
            BCP proxprogram;
            while (tabela.devolveTamanho() > 0) {
                tabela.verificaCreditos();
                while (lprontos.devolveTamanho() == 0) {
                    lblock.reduzEspera_bloqueado(lprontos);
                }
                proxprogram = lprontos.removePronto();
                proxprogram.executa();
                lblock.reduzEspera_bloqueado(lprontos);
                String instrucao = "NADA";
                System.out.println("Status Antes:"+proxprogram.devolveStatus());
                for (int i = 0; i < quantum; i++) {
                    //System.out.println("Status Antes:"+proxprogram.devolveStatus());
                    instrucao = proxprogram.devolveInstrucao_processo();
                    //System.out.printf("PROGRAMA: %S | CREDITOS %d| NUMERO DE INTRUCOES: %d | PC: %d | INSTRUCAO: %S\n", proxprogram.devolveNome_processo(), proxprogram.devolveCreditos(), proxprogram.devolveTamanho_processo(), proxprogram.devolvePC(), instrucao);
                    System.out.printf("PROGRAMA: %S | INSTRUCAO: %S\n", proxprogram.devolveNome_processo(), instrucao);
                    if (instrucao.contains("SAIDA")) {
                        tabela.removeProcesso(proxprogram.devolveIndice_tabela());
                        break;
                    } else if (instrucao.contains("E/S")) {
                        Bloqueado block = new Bloqueado(proxprogram, 2);
                        lblock.adicionaBloquado(block);
                        break;
                    } else if (instrucao.contains("X")) {
                        proxprogram.armazenaX(instrucao);
                    } else if (instrucao.contains("Y")) {
                        proxprogram.armazenaY(instrucao);
                    }
                }
                System.out.println("Status Depois:"+proxprogram.devolveStatus());
                if(proxprogram.devolveStatus().contains("Executando")) {
                    System.out.println("processo reincerido em prontos: "+proxprogram.devolveNome_processo());
                    lprontos.adicionaPronto(proxprogram);
                }
                System.out.println("");
            }
        }
    }
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Calculo;

import javax.swing.JOptionPane;

/**
 *
 * @author Bruno
 */
public class MetodoSimplex {
    
    /*PASSOS:   - Deve rodar enquanto os valores dos produtos não forem negativos
              1 - Igualar a função objetivo a 0 e igualar as restrições.
              2 - Formar uma tabela colocando com o numero de colunas sendo igual a quantidade de restrições + 1 (1 que é da função objetivo) e (produtos*2 + 2) linhas (Pois tem as variáveis de folga e o Z e B(que é o valor depois do igual))
              3 - Na tabela, verificar enre os produtos qual o maior valor em módulo. Marcar a coluna onde estava esse valor.
              4 - Dividir valores do b(Valor que fica depois do igual na função objetivo e restrições) pelos valores da coluna marcada, exceto pela linha da função objetivo.
              5 - Ver qual o menor resultado dessa divisão feita no passo 4. E então marcar a linha da divisão que deu esse resultado.
              6 - Achar a linha pivô: Dividir a linha marcada pela intersecção entre a linha e a coluna marcada.
              7 - O resultado da divisão feita no passo 6 é a nova linha pivô (NVP) que será usada nos próximos passos.
              8 - Achar nova linha 1: Multiplicar a linha pivô (valor por valor) pelo valor invertido da linha 1 e coluna que foi marcada no passo 3.
              9 - Somar o resultado dessa multiplicação (valor por valor) pela antiga primeira linha da tabela (em ordem, valor 1 do resultado da multiplicação com valor 1 da primeira linha...)
              10 - Para achar as outras linhas, repetir o passo 8 e 9, mas dessa vez fazendo os calculos (multiplicando e depois somando o resultado da multiplicação) com a linha da qual vai ser achada, se for a linha 2, vai multiplicar pelo valor invertido da linha 2, e assim vai...
              11 - Depois de ter calculado todas as linhas, formar uma nova tabela, com as linhas que foram achadas (primeira, segunda, pivô...)
              12 - Verificar se o valor dos produtos (X1,X2...) na primeira linha, ainda estão negativos, se sim, repetir do passo 3 em diante, se não, mostrar o resultado (b), da primeira linha (onde o Z é = 1) e as variáveis básicas e não básicas.)
    */
    
    //Variáveis globais que podem ser acessadas em todos os métodos da classe.
    private Double tabela[][];
    private int linhaDestacada,colunaDestacada;

    public String aplicarMetodoSimplex(int quantidadeProdutos, int quantidadeRestricoes, int tipoFuncao){
        int qtdLinhas = 1+quantidadeRestricoes; //A linha tem a função objetivo + as restrições.
        int qtdColunas = 1 + quantidadeProdutos + quantidadeRestricoes + 1; // A coluna tem o Z + quantidade de produtos + variáveis de folga(qtdRestricoes) + b (depois da igualdade).
        tabela = new Double[qtdLinhas][qtdColunas]; //Matriz criada para formar a tabela com os valores da função objetivo e restrições.
        
        String resultadoColeta = formarTabela(quantidadeProdutos, quantidadeRestricoes, tipoFuncao); //Esse método pega os valores, passa os valores depois do igual da função objetivo para a esquerda e iguala as restrições.
        
        boolean calculoConcluido = false;
        do{
            
            for(int y = 1; y<=quantidadeProdutos; y++){ //Deve continuar o processo até que os valores dos produtos sejam positivos.
                System.out.println("calculoConcluido = "+calculoConcluido+" | Vezes repetidas "+y+" vezes. ");
                if(tabela[0][y] < 0){
                    calculoConcluido = false;
                    break;  //Pois, se pelo menos 1 produto não estiver positivo, o processo já deve ser repetido.
                } else {
                    calculoConcluido = true;    //Põe true, mas não para a repetição, pois pode ainda haver algum produto que esteja com o valor negativo.
                }
            }
            
            if(calculoConcluido == false){
            
                destacarLinhasColunas(quantidadeProdutos, qtdLinhas, qtdColunas);

                calcularLinhaPivo(qtdColunas);

                calcularNovaLinha(qtdLinhas, qtdColunas);
            }

        } while (calculoConcluido == false);
        
        String resultado = montarResultado(quantidadeProdutos, quantidadeRestricoes, qtdColunas);
        return resultado;
    }
    
    public String formarTabela(int quantidadeProdutos, int quantidadeRestricoes, int tipoFuncao){
        String resultadoColeta = "";
            
            //Forma a função objetivo pedindo lucro por produto e já vai montando a tabela.
            double valorPorProduto = 0;
            String funcaoObjetivo="Z + ";
            tabela[0][0] = 1.0; //Preenche coluna do Z para a linha da função objetivo.
            for(int i=1; i<=quantidadeProdutos; i++){ //Preenche as colunas dos produtos com os valores do usuário para a linha da função objetivo.
                valorPorProduto = Double.parseDouble(JOptionPane.showInputDialog("<html>Igualando a <b>Função objetivo</b> a 0: "+funcaoObjetivo+"<br><br>Digite o lucro para o <b>produto "+(i)+"</b></html>"));
                funcaoObjetivo += "("+valorPorProduto*(-1)+"X"+(i)+") + ";   //Apenas para mostrar no JOptionPane para o usuário ter uma ideia sobre o que já digitou.
                tabela[0][i] = valorPorProduto*(-1);    //Vai adicionando a tabela com os valores já invertidos (Pois eles são passados do 2º membro da equação para o 1º membro)
            }
            //Completa os espaços não preenchidos da tabela na linha da função objetivo.
            for (int i=(quantidadeProdutos+1); i<=(quantidadeProdutos+quantidadeRestricoes+1); i++){    //Começa na posição onde parou + 1 e vai até a quantidade de variáveis de folga (qtdRestricoes) + 1(b que é o que tem depois da igualdade)
                tabela[0][i] = 0.0;
            }
        funcaoObjetivo = funcaoObjetivo.substring(0, funcaoObjetivo.length()-2)+"= 0";    //Tira o "+ " que sobrou no fim da string e substitui por = 0.
        resultadoColeta += "Função objetivo: "+funcaoObjetivo+"\n";
        
            //Forma as restrições, pedindo o valor por produto e já vai montando a tabela.
            String linhaRestricao;
            String restricoesStr="";
            Double valorDepoisIgualdade;
            for(int x=1; x<=quantidadeRestricoes; x++){
                tabela[x][0] = 0.0; //Preenche a coluna do Z para a linha da restrição.
                linhaRestricao = "";
                for (int y=1; y<=quantidadeProdutos; y++){
                    valorPorProduto = Double.parseDouble(JOptionPane.showInputDialog("<html><b>Restrição</b>: "+linhaRestricao+"<br><br>Digite a quantidade de produtos para a <b>restrição "+(x)+" produto "+(y)+"</b></html>"));
                    linhaRestricao += "("+valorPorProduto+"X"+y+") + ";
                    tabela[x][y] = valorPorProduto;    //Vai adicionando a tabela
                }
                valorDepoisIgualdade = Double.parseDouble(JOptionPane.showInputDialog("<html>Digite o que tem <b>depois do sinal</b> de < ou > ou <= ou >= ou = na <b>restrição</b> "+x+".</html>")); //Para preencher a coluna do B que é a ultima da tabela.
                tabela[x][quantidadeProdutos+quantidadeRestricoes+1] = valorDepoisIgualdade; //Preenche a coluna do b na tabela. Que é a última, fica depois do Z, produtos e variáveis de folga.
                restricoesStr  += "Restrição "+x+": "+linhaRestricao+" F"+x+" = "+valorDepoisIgualdade+"\n";  //Põe a variável de folga e iguala ao valor depois da igualdade.
            }
            //Preenche as variáveis de folga, que ainda não foram preenchidas.
            for (int x=1; x<=(quantidadeRestricoes); x++){    //Começa na linha da primiera restrição e vai até a última restrição
                tabela[x][quantidadeProdutos+x] = 1.0;    //Para poder colocar 1 onde tem variável de folga. Passa então as colunas equivalente a quantidade de produtos + x (x que equivale a qual restrição está). Com isso consegue saber em qual variável colocar a folga.
                System.out.println("x = "+x+" quantidadeProdutos+x = "+(quantidadeProdutos+x));
                for(int y=quantidadeProdutos+1; y<=quantidadeRestricoes+quantidadeProdutos; y++){  //Começa da coluna onde parou e vai até a penúltima coluna (para poder preencher apenas as variáveis de folga)
                    if(tabela[x][y] != tabela[x][quantidadeProdutos+x])
                        tabela[x][y] = 0.0;
                }
            }
        resultadoColeta += "Restrições: "+restricoesStr;
        
        JOptionPane.showMessageDialog(null, resultadoColeta);
        return resultadoColeta; //Caso queira mostrar tudo o que foi feito
    }
    
    public void destacarLinhasColunas(int qtdProdutos, int qtdLinhas, int qtdColunas){
        double maiorValor = 0;
        //Verifica qual o maior valor em módulo entre os valores dos produtos da função objetivo.
        for(int y = 1; y<=qtdProdutos; y++){
            System.out.println("DESTACAR COLUNA: "+Math.abs(tabela[0][y])+" > "+maiorValor+" ? Coluna destacada anteriormente = "+colunaDestacada);
            if(Math.abs(tabela[0][y])>=maiorValor){  //Pois deve ser checado o valor em módulo. Então, usa do método da classe Math para por em módulo.
                maiorValor = Math.abs(tabela[0][y]);
                colunaDestacada = y;
            }
        }
        //Verifica qual o menor resultado da divisão do b pelos valores da coluna marcada.
        double menorValor = 0;
        menorValor = tabela[1][qtdColunas-1]/tabela[1][colunaDestacada];    //Divide o valor de b (última coluna) da linha 1 (Pois não conta a da função objetivo) pelo valor que está na linha 1 e na coluna destacada. Para que a variável menorValor já possa começar com a primeira divisão.
        for(int x=1; x<qtdLinhas; x++){
            System.out.println("DESTACAR LINHA: ("+tabela[x][qtdColunas-1]+"/"+tabela[x][colunaDestacada]+") = ("+(tabela[x][qtdColunas-1]/tabela[x][colunaDestacada])+") < "+menorValor+" ? Linha destacada anteriormente = "+linhaDestacada);
            if((tabela[x][qtdColunas-1]/tabela[x][colunaDestacada]) <= menorValor){    //Se o valor da divisão de b pela coluna destacada for menor que o menor valor encontrado até aquele momento, então atualiza a variável menorValor com o valor da divisão e salva a linhaDestacada até o momento.
                menorValor = (tabela[x][qtdColunas-1]/tabela[x][colunaDestacada]);
                linhaDestacada = x;
            }
        }
        JOptionPane.showMessageDialog(null, "Coluna destacada = "+colunaDestacada+" | Linha destacada = "+linhaDestacada);
    }
    
    public String calcularLinhaPivo(int qtdColunas){
        String strLinhaPivo="Linha pivô: "; //É formada apenas para mostrar qual linha pivô foi formada.
        
        //Forma a nova linha pivô, e ja vai substituindo na linha destacada
        double valorInterseccao = tabela[linhaDestacada][colunaDestacada];
        for(int y=0; y<qtdColunas; y++){
            System.out.println("tabela["+linhaDestacada+"]["+y+"] = "+tabela[linhaDestacada][y]+"/"+valorInterseccao+" = "+(tabela[linhaDestacada][y]/valorInterseccao));
            tabela[linhaDestacada][y] = tabela[linhaDestacada][y]/valorInterseccao;  //Divide a linha destacada pela intersecção da linha destacada com a coluna destacada.
            strLinhaPivo += tabela[linhaDestacada][y]+" | ";
        }
        
        JOptionPane.showMessageDialog(null, strLinhaPivo);
        return strLinhaPivo;    //Caso queira mostrar a linha pivô que foi formada
    }
    
    public String calcularNovaLinha(int qtdLinhas, int qtdColunas){
        String novaLinha = "";
        double valorDestacado = 0.0;
        for(int x=0; x<qtdLinhas; x++){
            valorDestacado = tabela[x][colunaDestacada]*(-1);
            if(x!=linhaDestacada){  //Não deve fazer isso com a linha pivô.
                novaLinha += "Nova linha "+x+" = ";
                for(int y=0; y<qtdColunas; y++){
                    System.out.println("linhaDestacada = ="+linhaDestacada+" Y = "+y+" |"+" | tabela[x][y] =  ("+tabela[linhaDestacada][y]+"*"+valorDestacado+") + "+tabela[x][y]+" = "+((tabela[linhaDestacada][y]*valorDestacado)+ tabela[x][y]));
                    tabela[x][y] = ((tabela[linhaDestacada][y]*valorDestacado) + tabela[x][y]); //Multiplica linha pivô pelo valor invertido da linha que está sendo trabalha e da coluna destacada e por fim soma pelo valor que estava na linha antiga.
                    novaLinha += ""+tabela[x][y]+" | ";
                }
            }
            novaLinha += "\n";
        }
        
        JOptionPane.showMessageDialog(null, novaLinha);
        return novaLinha;    //Caso queira mostrar a nova linha que foi formada
    }
    
    public String montarResultado(int qtdProdutos,int qtdRestricoes, int qtdColunas){
        String resultado="<html>";
        
        // Como identificar qual é a variável básica e não básica: https://youtu.be/OD0BVZbDieY?t=2292
        
        //Mostra a tabela final.
        String teste="A tabela final ficou dessa forma:\n";
        for(int x=0; x<=qtdProdutos; x++){
            for(int y=0; y<qtdColunas; y++){
                teste += ""+tabela[x][y]+" |";
            }
                teste += "\n";
        }
        JOptionPane.showMessageDialog(null, teste);
        
        //Variáveis básicas -> Procura as colunas que tem apenas 1 e 0. Mostra o b (última coluna) do que tem 1.
        //Variáveis não básicas -> As variáveis não básicas são as que sobraram depois de achado as variáveis básicas. Elas deevm ser mostradas como tendo resultado 0.
        boolean varBasica = true;
        String variaveisBasicas="";
        String variaveisNaoBasicas="";
        String auxiliarVariaveis=""; //Para poder pegar qual a variável e seu valor, temporariamente.
        
        //Identificando variáveis básicas e não básicas das colunas dos produtos
        for(int y=1; y<=qtdProdutos; y++){  //Percorre apenas as colunas dos produtos
            varBasica = true;
            
              for(int x=1; x<=qtdProdutos; x++){    //Com exceção da primeira linha (da função objetivo), percorre todas as outras.
                System.out.println("Em produtos: ["+x+"]["+y+"] :"+tabela[x][y]+" != 0 e de 1?");
                if(tabela[x][y] != 0 && tabela[x][y] != 1){ //Permite verificar se a coluna só tem valores iguais a 1 e 0.
                    System.out.println("Em produtos: Coluna "+y+" Tem valor diferente de 1 e de 0");
                    varBasica = false;
                    auxiliarVariaveis = "X"+y+" = 0"; //Se algum lugar da coluna tinha 1 e ele ja tinha pêgo o valor de b dessa linha, refaz e põe apenas o nome da variável para dizer que é não básica.
                }
                
                if(varBasica == true){   //Pois, se for igual a false, já não é uma coluna de só zero e um, então não deve percorrer as condições abaixo.
                    if(tabela[x][y] == 1){
                        auxiliarVariaveis = "X"+y+" = "+tabela[x][qtdColunas-1];    //Pega o resultado que está na coluna b (última coluna)
                    }
                }    
            }
            //Guarda a variável no seu tipo correspondente
            if(varBasica == true){ //Quer dizer que a coluna analisada anteriormente tinha apenas 1 e 0. E ele pegou o resultado da linha dessa coluna onde tinha 1.
                variaveisBasicas += auxiliarVariaveis+"; "; 
            } else {    //Se não, quer dizer que a coluna analisada anteriormente não tinha apenas 1 e 0. Então, é variável não básica.
                variaveisNaoBasicas += auxiliarVariaveis+"; ";
            }
        }
        
        //Identificando variáveis básicas e não básicas da colunas das variáveis de folga
        for(int y=qtdProdutos+1; y<qtdColunas-1; y++){  //Percorre apenas as colunas das variáveis de folga.
            varBasica = true;
            
            for(int x=1; x<=qtdProdutos; x++){  //Com exceção da primeira linha (da função objetivo), percorre todas as outras.
                System.out.println("Em variáveis de folga: ["+x+"]["+y+"] :"+tabela[x][y]+" != 0 e de 1?");
                if(tabela[x][y] != 0 && tabela[x][y] != 1){ //Permite verificar se a coluna só tem valores iguais a 1 e 0.
                    System.out.println("Em variáveis de folga: Coluna "+y+" Tem valor diferente de 1 e de 0");
                    varBasica = false;
                    auxiliarVariaveis = "FX"+(y-qtdProdutos)+" = 0"; //Se algum lugar da coluna tinha 1 e ele ja tinha pêgo o valor de b dessa linha, refaz e põe apenas o nome da variável para dizer que é não básica.
                }
                
                if(varBasica == true){   //Pois, se for igual a false, já não é uma coluna de só zero e um, então não deve percorrer as condições abaixo.
                    if(tabela[x][y] == 1){
                        auxiliarVariaveis = "Fx"+(y-qtdProdutos)+" = "+tabela[x][qtdColunas-1];    //Põe Fx e o valor de y menos a quantidade de produtos. Exemplo: Se ele tiver no Y=3, e tem 2 produtos, ele vai dizer que é a variável de folga 1. Igual ao valor da última coluna
                    }
                }    
            }
            //Guarda a variável no seu tipo correspondente
            if(varBasica == true){ //Quer dizer que a coluna analisada anteriormente tinha apenas 1 e 0. E ele pegou o resultado da linha dessa coluna onde tinha 1.
                variaveisBasicas += ""+auxiliarVariaveis+"; "; 
            } else {    //Se não, quer dizer que a coluna analisada anteriormente não tinha apenas 1 e 0. Então, é variável não básica.
                variaveisNaoBasicas += ""+auxiliarVariaveis+"; ";
            }
        }
        
        resultado += "<b>Variáveis básicas</b>: "+variaveisBasicas;
        resultado += "<br><b>Variáveis não básicas</b>: "+variaveisNaoBasicas;

        //Z
        resultado += "<br><b>Z </b>= "+tabela[0][qtdColunas-1]+""; //Pega o valor de b da linha da função objetivo e última coluna.
        resultado += "</html>";
        return resultado;
    }
    
}

package Ooooof;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Oooof {
    
    public static void main(String[] args) {
        String fileName = "code.txt"; // Название файла с кодом
        
        // Считывание файла в список fileLines
        ArrayList<String> fileLines = reader(fileName); 
        System.out.println(fileLines);
        
        //Лексический анализ в список fLexemList
        ArrayList<fLexem> fLexemList = fLexer(fileLines);        
        System.out.print("\nЛексер: ");
        for (int i = 0; i < fLexemList.size(); i++) {
            System.out.print(fLexemList.get(i).getFlexemValue() + " ");
        }
        
        //Перевод в обратную польскую нотацию
        System.out.print("\nПОЛИЗ: ");
        ArrayList<fLexem> polizeList = polize(fLexemList);
        for (int i = 0; i < polizeList.size(); i++) {
            System.out.print(polizeList.get(i).value + " ");
        }
        System.out.print("\nПарсинг: ");
        parsingCalc(polizeList);
    }
    // Метод чтения из файла в список
    public static ArrayList<String> reader (String fileName) {
        // Список строк для получения из файла 
        ArrayList<String> fileLines = new ArrayList<String>(); 
        try { // Читаем файл и записываем в список fileLines
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                fileLines.add(fileLine);
            }
            br.close();
        } catch (IOException err) {
            System.out.println("Err " + err);
        }
        return fileLines;
    }
    // Класс лексем
    public static class fLexem{
        String type;
        String value;
        int weight;        
        public fLexem (String type, String value) {
            this.type = type;
            this.value = value;
        }
        public fLexem (String type, String value, int weight) {
            this.type = type;
            this.value = value;
            this.weight = weight;
        }
        public String getFlexemType (){
            return type;
        }
        public String getFlexemValue (){
            return value;
        }
        public int getFlexemWeight (){
            return weight;
        }
    }
    // Метод лексического анализа
    public static ArrayList<fLexem> fLexer (ArrayList<String> fileLines) {
        // Список для найденных лексем
        ArrayList<fLexem> flexems = new ArrayList<fLexem>();
        // Буфер для многосимвольных чисел и слов
        StringBuilder buffer = new StringBuilder();
        // Лексический анализ
        for (String line : fileLines) {
            int i = 0;
            boolean wordDigit = false, wordLetter = false;
            while (i < line.length()){
        //---------------------------числа или слова----------------------------
                //Если текущий символ является числом или символом,
                if (Character.isLetterOrDigit(line.charAt(i))) {
                    // то добавляем его в буфер,
                    buffer.append(line.charAt(i));
                    if (Character.isDigit(line.charAt(i))){
                        wordDigit = true; // Если символ - число
                    } else {
                        wordLetter = true; } // Если символ - буква
                    if (i+1>=line.length()){ // Если это конец строки,
                        // то содержимое буфера добавляем в список лексем
                        if (wordDigit && !wordLetter){
                            flexems.add(new fLexem("NUMB",buffer.toString()));
                        } //Если в буфере есть цифры и нет букв, то тип лексемы - NUMB
                        if (wordLetter){ 
                            flexems.add(new fLexem("WORD",buffer.toString()));
                        } //Если в буфере есть буквы, то тип лексемы - WORD
                        buffer.delete(0, buffer.length()); // и очищаем буфер
                        wordDigit = false; // и сбрасываем показатели
                        wordLetter = false;
                    }
                } 
                //Если текущий символ не число и не буква и буфер непустой
                if (!(Character.isLetterOrDigit(line.charAt(i))) && buffer.length()!=0){
                    // то содержимое буфера добавляем в список лексем
                    if (wordDigit && !wordLetter){
                            flexems.add(new fLexem("NUMB",buffer.toString()));
                        } //Если в буфере есть цифры и нет букв, то тип лексемы - NUMB
                        if (wordLetter){
                            flexems.add(new fLexem("WORD",buffer.toString()));
                        } //Если в буфере есть буквы, то тип лексемы - WORD
                        buffer.delete(0, buffer.length()); // и очищаем буфер
                        wordDigit = false; // и сбрасываем показатели
                        wordLetter = false;
                }                
            //----------------------------операторы-----------------------------
                switch (line.charAt(i)) {
                    case '=': flexems.add(new fLexem("OP","=")); break;
                    case '<': flexems.add(new fLexem("OP","<")); break;
                    case '>': flexems.add(new fLexem("OP",">")); break;
                    case '+': flexems.add(new fLexem("OP","+",1)); break;
                    case '-': flexems.add(new fLexem("OP","-",1)); break;
                    case '*': flexems.add(new fLexem("OP","*",2)); break;
                    case '/': flexems.add(new fLexem("OP","/",2)); break;
                    case '(': flexems.add(new fLexem("BRO","(",-1)); break;
                    case ')': flexems.add(new fLexem("BRC",")")); break;
                    case ';': flexems.add(new fLexem("CLS",";")); break;
                    default: break;
                }   
                if (i+1>=line.length()){ // Если это конец строки
                    flexems.add(new fLexem("ENDL","|")); // Добавим в список лексем
                }                                      // обозначение конца строки
                i++;
            }
        }
        return flexems;
    }
    // Метод перевода в обратную польскую нотацию
    public static ArrayList<fLexem> polize (ArrayList<fLexem> fLexemList) {
        int i = 0;
        ArrayList<fLexem> polizeOut = new ArrayList<fLexem>();
        ArrayList<fLexem> polizeStack = new ArrayList<fLexem>();
        while (i < fLexemList.size()) {
            if (fLexemList.get(i).value == "(") { //Если лексема - открывающая скобка
                polizeStack.add(fLexemList.get(i)); // добавляем ее в стек
            }
            if (fLexemList.get(i).value == ")") { //Если лексема - закрывающая скобка
                // пока верхним элементом стека не будет являтся открывающая скобка
                while (polizeStack.get(polizeStack.size()-1).value != "("){
                    //добавляем в выходной список верхний элемент стека
                    polizeOut.add(polizeStack.get(polizeStack.size()-1));
                    //удаляем верхний элемент стека
                    polizeStack.remove(polizeStack.size()-1);
                }
                //удаляем открывающую скобку из стека
                polizeStack.remove(polizeStack.size()-1); 
            }
            if (fLexemList.get(i).type == "NUMB"||fLexemList.get(i).type =="WORD") { // Если лексема - число
                polizeOut.add(fLexemList.get(i)); // добавляем его в список
            }
            if (fLexemList.get(i).type == "OP") { // Если лексема - оператор
                if (polizeStack.size() != 0) { // Если стек не пуст
                    // и если приоритет оператора выше или равен приоритету
                    // верхнего элемента в стеке
                    if (fLexemList.get(i).weight >= polizeStack.get(polizeStack.size()-1).weight) {
                        polizeStack.add(fLexemList.get(i)); //добавляем его в стек
                    }
                    // если приоритет оператора ниже приоритета верхнего элемента в стеке
                    if (fLexemList.get(i).weight < polizeStack.get(polizeStack.size()-1).weight) {
                        // добавляем верхний элемент стека в выходной список
                        polizeOut.add(polizeStack.get(polizeStack.size()-1));
                        // удаляем верхний элемент стека
                        polizeStack.remove(polizeStack.size()-1);
                        //затем добавляем оператор в выходной список
                        polizeStack.add(fLexemList.get(i));
                    }
                } else { //Если стэк пуст, добавляем оператор в стэк
                    polizeStack.add(fLexemList.get(i));
                }
            }
            if (fLexemList.get(i).type == "ENDL") { // Если это последний элемент списка лексем
                while (polizeStack.size() != 0){ 
                    // поочередно убираем элементы из стека в выходной список
                    polizeOut.add(polizeStack.get(polizeStack.size()-1));
                    polizeStack.remove(polizeStack.size()-1);
                }
                // и добавляем в выходной список элемент окончания строки
                polizeOut.add(fLexemList.get(i));
            }
            i++;            
        }        
        return polizeOut;
    }
    // Метод синтаксического анализа 
    public static void parsingCalc (ArrayList<fLexem> polizeList) {
        int i = 0;
        float result = 0;
        ArrayList<Float> parsingCalcStack = new ArrayList<Float>(); // Стэк
        while (polizeList.get(i).value != "|") { //Пока не конец строки
            if (polizeList.get(i).type == "OP"){ // Если текущий элемент - оператор
                float arg2 = parsingCalcStack.get(parsingCalcStack.size()-1);
                float arg1 = parsingCalcStack.get(parsingCalcStack.size()-2);
                // производим соответствующую операцию между двумя верхними
                // элементами в стеке
                switch (polizeList.get(i).value) {
                    case "+": result = arg1 + arg2; break;
                    case "-": result = arg1 - arg2; break;
                    case "*": result = arg1 * arg2; break;
                    case "/": result = arg1 / arg2; break;
                } // удаляем из стека верхние два элемента
                parsingCalcStack.remove(parsingCalcStack.size()-1); 
                parsingCalcStack.remove(parsingCalcStack.size()-1);
                // добавляем в стек результат операции
                parsingCalcStack.add(result);
            }
            if (polizeList.get(i).type == "NUMB"){ // Если текущий элемент - число
                // добавляем это число в стэк
                parsingCalcStack.add(Float.parseFloat(polizeList.get(i).value));
            }
            i++;
        }
        System.out.println("Результат = " + result);
        
    }
}


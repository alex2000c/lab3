package bsu.rfe.java.group6.lab3.Dorozhko.varA6;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.*;


@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    // Константы с исходным размером окна приложения
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    
 // Массив коэффициентов многочлена 
    private Double[] coefficients;

    // Объект диалогового окна для выбора файлов.
    // Компонент не создается изначально, т.к. может и не понадобиться
    // пользователю если тот не собирается сохранять данные в файл
    private JFileChooser fileChooser = null;

    // Элементы меню вынесены в поля данных класса, так как ими необходимо
    // манипулировать из разных мест
    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private Box hBoxResult; //
    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
    private bsu.rfe.java.group6.lab3.Dorozhko.varA6.GornerTableModel data;  // модель данных с результами вычислений

    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");// Обязательный вызов конструктора предка
        this.coefficients = coefficients;
        setSize(WIDTH, HEIGHT);// Установить размеры окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2, // Отцентрировать окно приложения на экране
                (kit.getScreenSize().height - HEIGHT)/2);

        JMenuBar menuBar = new JMenuBar();  //Создать полосу меню и установить ее в верхнюю часть фрейма
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);
        JMenu SpravkaMenu = new JMenu("Справка");
        menuBar.add(SpravkaMenu);

        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction(
                "Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                // Если экземпляр диалогового окна "Открыть файл" еще не создан,
                // то создать его и инициализировать текущей директорией
                if (fileChooser==null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }

                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION)
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        saveToTextMenuItem.setEnabled(false);
        
        
    
     	

        // Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this,
                        "Введите значение для поиска", "Поиск значения",
                        JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }
        };

        searchValueMenuItem = tableMenu.add(searchValueAction);
        searchValueMenuItem.setEnabled(false);



        Action aboutProgrammAction = new AbstractAction("О программе") {
            public void actionPerformed(ActionEvent event) {
                JLabel info = new JLabel("Автор: Дорожко Александр Валерьевич, 2 курс 6 группа");
                JOptionPane.showMessageDialog(MainFrame.this, info,
                        "О программе", JOptionPane.PLAIN_MESSAGE);
            }
        };

        SpravkaMenu.add(aboutProgrammAction);
        
     // Создать область с полями ввода для границ отрезка и шага
// Создать подпись для ввода левой границы отрезка
        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        // Создать подпись для ввода левой границы отрезка
        JLabel labelForTo = new JLabel("до:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 1.0
        textFieldTo = new JTextField("1.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        // Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 1.0
        textFieldStep = new JTextField("0.1", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
        // Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        Box hboxRange = Box.createHorizontalBox();
        // Задать для контейнера тип рамки "объѐмная"
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));
        // Добавить "клей" C1-H1
        hboxRange.add(Box.createHorizontalGlue());
        // Добавить подпись "От"
        hboxRange.add(labelForFrom);
        // Добавить "распорку" C1-H2 
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "От"
        hboxRange.add(textFieldFrom);
        // Добавить "распорку" C1-H3 
        hboxRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "До" 
        hboxRange.add(labelForTo);
        // Добавить "распорку" C1-H4
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "До"
        hboxRange.add(textFieldTo); 
        // Добавить "распорку" C1-H5 
        hboxRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "с шагом" 
        hboxRange.add(labelForStep);
        // Добавить "распорку" C1-H6
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле для ввода шага табулирования 
        hboxRange.add(textFieldStep);
        // Добавить "клей" C1-H7
        hboxRange.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному
        // минимальному, чтобы при компоновке область совсем не сдавили

        hboxRange.setPreferredSize(new Dimension(
        		new Double(hboxRange.getMaximumSize().getWidth()).intValue(),
        		new Double(hboxRange.getMinimumSize().getHeight()).intValue()*2));
        
     // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hboxRange, BorderLayout.NORTH);
       
     
        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                   
                    // На основе считанных данных создать экземпляр модели таблицы
                    data = new bsu.rfe.java.group6.lab3.Dorozhko.varA6.GornerTableModel(from, to, step, coefficients);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для класса Double
                    //разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обернутую" в панель
                    //с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Перерасположить компоненты в hBoxResult и выполнить
                    //перерисовку
                    hBoxResult.revalidate();
                    // Сделать ряд элементов меню доступными
                    saveToTextMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об
                    //ошибке
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в формате записи числа с плавающей точкой",
                            "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера hBoxResult
                hBoxResult.removeAll();
                // Перерисовать сам hBoxResult
                hBoxResult.repaint();
                // Сделать ряд элементов меню недоступными
                saveToTextMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
            }
        });

        // Поместить созданные кнопки в контейнер
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createEtchedBorder());
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области больше минимального, чтобы
        // при компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(
                new Double(hboxButtons.getMaximumSize().getWidth()).intValue(),
                new Double(hboxButtons.getMinimumSize().getHeight()*1.5).intValue()));
        // Разместить контейнер с кнопками в нижней (южной) области граничной
//компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);

        // Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        // Установить контейнер hBoxResult в главной (центральной) области
//граничной компоновки
        getContentPane().add(hBoxResult);
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            // Создать новый символьный поток вывода, направленный в  указанный файл
            PrintStream out = new PrintStream(selectedFile);

            // Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования функции:");
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo() +
                    " с шагом " + data.getStep());
            out.println("====================================================");

            // Записать в поток вывода значения в точках
            for (int i = 0; i<data.getRowCount(); i++)
                out.println("Значение в точке " + data.getValueAt(i,0)  + " равно " +
                        data.getValueAt(i,1));

            // Закрыть поток
            out.close();
        } catch (FileNotFoundException e) {
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаем, а не открываем
        }
    }

    public static void main(String[] args){
    	// Если не задано ни одного аргумента командной строки -
    	// Продолжать вычисления невозможно, коэффиценты неизвестны 
    	if (args.length==0) {
    		System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
    		System.exit(-1); 
    		} // Зарезервировать места в массиве коэффициентов столько, сколько аргументов командной строки
    		Double[] coefficients = new Double[args.length];
    		int i = 0;
    		try { // Перебрать аргументы, пытаясь преобразовать их в Double 
    			for (String arg: args) {
    				coefficients[i++] = Double.parseDouble(arg);
    				} 
    			} catch (NumberFormatException ex) {
    				// Если преобразование невозможно - сообщить об ошибке и завершиться 
    				System.out.println("Ошибка преобразования строки '" + args[i] + "' в число типа Double");
    				System.exit(-2);
    				}
    		
        // Создать экземпляр главного окна
    	MainFrame frame = new MainFrame(coefficients);
        // Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Показать главное окно приложения
        frame.setVisible(true);
    }
}

